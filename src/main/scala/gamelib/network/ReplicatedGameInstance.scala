package gamelib.network

import gamelib.core.{GameObject, GameInstance}
import gamelib.util.BidirectionalHashMap
import org.reflections.Reflections
import com.twitter.chill.{Input, Output}
import com.esotericsoftware.kryo.Kryo
import scala.collection.JavaConversions._
import scala.collection.mutable

class ReplicatedGameInstance(packages: String*) extends GameInstance
{
    private val replicatedObjects = new BidirectionalHashMap[Int, ReplicatedGameObject]
    private var replicatedObjectIndex = 0

    private val localObjects = new mutable.HashSet[ReplicatedGameObject]

    private val recentCreations = new mutable.LinkedHashSet[ReplicatedGameObject]
    private val recentDeletions = new mutable.LinkedHashSet[ReplicatedGameObject]

    //Index all subclasses of Replicated. Their position in the sequence is their unique id used for new instance creation.
    private val classIds = new BidirectionalHashMap[Int, Class[_ <: ReplicatedGameObject]]
    for(p <- packages)
    {
        val classes = new Reflections(p).getSubTypesOf(classOf[ReplicatedGameObject]).toList
        for(c <- classes) classIds.addPair(classIds.size, c)
    }

    private val objectOutputBuffer = new Output(128)
    private val objectInputBuffer = new Input(128)

    override def addObject(newObject: GameObject) = newObject match
    {
        case repObject: ReplicatedGameObject => addObject(repObject, -1)
        case _ => super.addObject(newObject)
    }

    private def addObject(repObject: ReplicatedGameObject, id: Int)
    {
        super.addObject(repObject)

        recentCreations += repObject

        if(id < 0)
        {
            replicatedObjects.addPair(replicatedObjectIndex, repObject)
            localObjects += repObject
        }
        else
            replicatedObjects.addPair(id, repObject)

        replicatedObjectIndex += 1
    }

    protected override def updateObject(gameObject: GameObject, deltaTime: Double) = gameObject match
    {
        case repObject: ReplicatedGameObject => 
            if(localObjects.contains(repObject)) super.updateObject(repObject, deltaTime)
            else repObject.updateLocal(this, deltaTime)
        case _ => super.updateObject(gameObject, deltaTime)
    }

    protected override def removeObject(gameObject: GameObject)
    {
        super.removeObject(gameObject)
        gameObject match
        {
            case repObject: ReplicatedGameObject =>
                replicatedObjects.removePair(replicatedObjects.getKey(repObject), repObject)
                if(localObjects.contains(repObject)) localObjects -= repObject
                recentDeletions += repObject
            case _ =>
        }
    }

    override def update(deltaTime: Double)
    {
        recentCreations.clear()
        recentDeletions.clear()
        super.update(deltaTime)
    }

    def writeCreateMessages(kryo: Kryo) =
    {
        for(repObject <- recentCreations) yield
        {
            objectOutputBuffer.clear()
            objectOutputBuffer.writeInt(classIds.getKey(repObject.getClass))
            repObject.write(objectOutputBuffer, kryo)
            ReplicationMessage(CreateMessage, replicatedObjects.getKey(repObject), objectOutputBuffer.toBytes)
        }
    }

    def writeUpdateMessages(kryo: Kryo) =
    {
        for((id, repObject) <- replicatedObjects; if repObject.replicationNeeded) yield
        {
            objectOutputBuffer.clear()
            repObject.writeUpdate(objectOutputBuffer, kryo)
            ReplicationMessage(UpdateMessage, replicatedObjects.getKey(repObject), objectOutputBuffer.toBytes)
        }
    }

    def writeDestroyMessages(kryo: Kryo) = for(repObject <- recentDeletions) yield ReplicationMessage(DestroyMessage, replicatedObjects.getKey(repObject), null)

    def applyMessages(messages: Iterable[ReplicationMessage], kryo: Kryo)
    {
        for(message <- messages)
        {
            objectInputBuffer.setBuffer(message.data)
            message.messageType match
            {
                case CreateMessage =>
                    val newObject = classIds.getValue(objectInputBuffer.readInt()).newInstance()
                    newObject.read(objectInputBuffer, kryo)
                    addObject(newObject, message.objectId)
                case UpdateMessage => replicatedObjects.getValue(message.objectId).read(objectInputBuffer, kryo)
                case DestroyMessage => replicatedObjects.getValue(message.objectId).setDead()
            }
        }
    }
}