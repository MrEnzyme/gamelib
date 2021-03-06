package gamelib.network

import org.scalatest.FunSuite
import com.twitter.chill.{Input, Output, ScalaKryoInstantiator}

class ReplicationMessageTest extends FunSuite
{
    test("kryo can serialize and read the class")
    {
        val kryo = KryoRegistrar.makeNewKryo()

        val writeMessage = ReplicationMessage(CreateMessage, 1, Array[Byte](1,2,3,4))
        val out = new Output(64)
        kryo.writeObject(out, writeMessage)
        val readMessage = kryo.readObject(new Input(out.toBytes), classOf[ReplicationMessage])

        assert(writeMessage.messageType == readMessage.messageType)
        assert(writeMessage.data.toList == readMessage.data.toList)
    }
}
