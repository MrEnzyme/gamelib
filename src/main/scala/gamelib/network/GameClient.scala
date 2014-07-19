package gamelib.network

import com.esotericsoftware.kryonet.{Connection, Listener, Client}
import scala.collection.mutable

class GameClient(gameInstance: ReplicatedGameInstance) extends Client
{
    private val gameThread = new ReplicatedGameInstanceThread(gameInstance, getKryo)

	KryoRegistrar.registerOnKryo(getKryo)

	private class ClientListener extends Listener
	{
        override def connected(connection: Connection) = gameThread.addConnection(connection)
        override def disconnected(connection: Connection) = gameThread.removeConnection(connection)
        override def received(connection: Connection, obj: Object) = obj match
		{
			case s: StateUpdate => for(m <- s.replicationMessages) gameThread.putMessage(m)
			case _ =>
		}
	}

	addListener(new ClientListener)

    override def start()
    {
        gameThread.start()
        super.start()
    }
}
