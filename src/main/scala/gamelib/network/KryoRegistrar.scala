package gamelib.network

import com.twitter.chill.ScalaKryoInstantiator

/**
 * Creates kryo instances with the needed classes pre-registered
 */
object KryoRegistrar
{
    def makeNewKryo() =
    {
        val kryo = (new ScalaKryoInstantiator).newKryo()
        kryo.register(classOf[ReplicationMessage])
        kryo
    }
}
