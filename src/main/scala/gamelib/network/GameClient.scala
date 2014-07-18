package gamelib.network

import com.esotericsoftware.kryonet.{Connection, Listener, Client}
import scala.collection.mutable

class GameClient extends Client
{
	private val gameInstances = new mutable.HashMap[Int, ReplicatedGameInstanceThread]

	KryoRegistrar.registerOnKryo(getKryo)

	private class ClientListener extends Listener
	{
		override def received(connection: Connection, obj: Object) = obj match
		{
			case s: StateUpdate => for(m <- s.replicationMessages) gameInstances(s.instanceId).putMessage(m)
			case _ =>
		}
	}

	addListener(new ClientListener)
}
