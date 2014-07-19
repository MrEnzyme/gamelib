package gamelib.network

import java.util.concurrent.LinkedBlockingQueue
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import gamelib.core.GameInstanceThread
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryonet.Connection
import scala.collection.mutable

class ReplicatedGameInstanceThread(gameInstance: ReplicatedGameInstance, kryo: Kryo) extends GameInstanceThread(gameInstance)
{
    private val messageInbox = new LinkedBlockingQueue[ReplicationMessage]
    private val messages = new ListBuffer[ReplicationMessage]
    private val connections = new mutable.LinkedHashSet[Connection]

    def addConnection(c: Connection, sendCreations: Boolean = false)
    {
        connections += c
        if(sendCreations) for(m <- gameInstance.getAllCreateMessages(kryo)) c.sendTCP(m)
    }
    def removeConnection(c: Connection) = connections -= c
    def putMessage(m: ReplicationMessage) = messageInbox.add(m)

    protected override def update(deltaTime: Double)
    {
        //get network input
        messages.clear()
        messageInbox.drainTo(messages)
        gameInstance.applyMessages(messages, kryo)

        //perform normal update operations
        super.update(deltaTime)

        //send network output
        messages.clear()
        messages ++= gameInstance.getCreateMessages(kryo)
        messages ++= gameInstance.getDestroyMessages(kryo)
        for(m <- messages; c <- connections) c.sendTCP(m)
        messages.clear()
        messages ++= gameInstance.getUpdateMessages(kryo)
        for(m <- messages; c <- connections) c.sendUDP(m)
    }
}