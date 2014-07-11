package gamelib.network

import gamelib.core.GameInstance
import org.reflections.Reflections
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import com.twitter.chill.{Input, Output}
import com.esotericsoftware.kryo.Kryo

class ReplicatedGameInstance(packages: String*) extends GameInstance
{
    private val replicatedObjects = new ListBuffer[ReplicatedGameObject]

    //Index all subclasses of Replicated. Their position in the sequence is their unique id used for new instance creation.
    private val classIds: Seq[Class[_ <: Replicated]] = (for(p <- packages) yield new Reflections(p).getSubTypesOf(classOf[Replicated]).toList).flatten

    private val objectOutputBuffer = new Output(1024)
    private val objectInputBuffer = new Input(1024)

    def addObject(gameObject: ReplicatedGameObject)
    {
        super.addObject(gameObject)
        replicatedObjects += gameObject
    }

    def writeUpdateMessages(kryo: Kryo) =
    {
        for(o <- replicatedObjects; if o.replicationNeeded) yield
        {
            objectOutputBuffer.clear()
            o.writeUpdate(objectOutputBuffer, kryo)
            ReplicationMessage(UpdateMessage, objectOutputBuffer.toBytes)
        }
    }
}