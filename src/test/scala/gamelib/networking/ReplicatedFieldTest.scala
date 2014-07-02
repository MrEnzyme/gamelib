package gamelib.networking

import org.scalatest.FunSuite
import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.Kryo

class ReplicatedFieldTest extends FunSuite
{
    val kryo = new Kryo()

    test("can read and write correctly from kryo streams")
    {
        var x = 0
        var y = 5
        val xfield = new ReplicatedField[Int](() => x, x = _)
        val yfield = new ReplicatedField[Int](() => y, y = _)
        val out = new Output(128)
        xfield.writeValue(out, kryo)
        yfield.readValue(new Input(out.toBytes), kryo)
        assert(y == 0)
    }
}
