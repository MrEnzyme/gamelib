package gamelib.network

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.Kryo

class ReplicatedField[+A](get: => A, set: A => Unit)
{
    def writeValue(out: Output, kryo: Kryo) = kryo.writeObject(out, get)
    def readValue(in: Input, kryo: Kryo) = set(kryo.readObject(in, get.getClass))
}
