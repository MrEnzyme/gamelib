package gamelib.network

import com.esotericsoftware.kryonet.{Connection, Listener, Server}
import scala.collection.mutable.ListBuffer
import scala.collection.mutable

class GameServer extends Server
{
	private val instanceThreads = new ListBuffer[ReplicatedGameInstanceThread]
	private val connectionInstances = new mutable.HashMap[Connection, ReplicatedGameInstanceThread]

	private var instanceIndex = 0

	KryoRegistrar.registerOnKryo(getKryo)

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
		val newThread = new ReplicatedGameInstanceThread(gameInstance, instanceIndex, getKryo)
		instanceIndex += 1
		instanceThreads += newThread
		newThread.start()
	}
}
