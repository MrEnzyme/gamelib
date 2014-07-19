package gamelib.network

import com.esotericsoftware.kryonet.{KryoSerialization, Connection, Listener, Server}
import scala.collection.mutable.ListBuffer
import scala.collection.mutable

class GameServer extends Server(16384, 2048, new KryoSerialization(KryoRegistrar.makeNewKryo()))
{
    private val instanceThreads = new ListBuffer[ReplicatedGameInstanceThread]
    private val connectionInstances = new mutable.HashMap[Connection, ReplicatedGameInstanceThread]

    private class ServerListener extends Listener
    {
        override def connected(connection: Connection)
        {
            val defaultInstance = instanceThreads.head
            connectionInstances(connection) = defaultInstance
            defaultInstance.addConnection(connection)
        }

        override def disconnected(connection: Connection)
        {
            connectionInstances(connection).removeConnection(connection)
            connectionInstances.remove(connection)
        }

        override def received(connection: Connection, obj: Object) = obj match
        {
            case input: InputEvent => connectionInstances(connection).addInputEvent(input)
            case _ =>
        }
    }

    addListener(new ServerListener)

    def addGameInstance(gameInstance: ReplicatedGameInstance)
    {
        val newThread = new ReplicatedGameInstanceThread(gameInstance, getKryo)
        instanceThreads += newThread
        newThread.start()
    }
}
