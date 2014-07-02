package gamelib.network

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.Kryo

class ReplicatedField[+A](getValue: () => A, setValue: A => Unit)
{
    def writeValue(out: Output, kryo: Kryo) = kryo.writeObject(out, getValue())
    def readValue(in: Input, kryo: Kryo) = setValue(kryo.readObject(in, getValue().getClass))
}
