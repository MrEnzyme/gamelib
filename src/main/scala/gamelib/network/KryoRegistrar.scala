package gamelib.network

import com.esotericsoftware.kryo.Kryo
import com.twitter.chill.ScalaKryoInstantiator

/**
 * Creates kryo instances with the needed classes pre-registered
 */
object KryoRegistrar
{
    def makeNewKryo() =
    {
        val kryo = (new ScalaKryoInstantiator).newKryo()
        registerOnKryo(kryo)
        kryo
    }
    def registerOnKryo(kryo: Kryo)
    {
        kryo.register(classOf[ReplicationMessage])
    }
}
