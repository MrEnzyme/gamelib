package gamelib.network

import org.scalatest.FunSuite
import com.esotericsoftware.kryo.io.{Input, Output}
import com.twitter.chill.ScalaKryoInstantiator

class ReplicatedTest extends FunSuite
{
    class MockReplicated extends Replicated
    {
        var boolean: Boolean = true
        var byte: Byte = 0
        var short: Short = 0
        var integer: Integer = 0
        var double: Double = 0.0
        registerField[Boolean]('boolean, () => boolean, boolean = _)
        registerField[Byte]('byte, () => byte, byte = _)
        registerField[Short]('short, () => short, short = _)
        registerField[Int]('integer, () => integer, integer = _)
        registerField[Double]('double, () => double, double = _)
    }

    val instantiator = new ScalaKryoInstantiator
    instantiator.setRegistrationRequired(false)
    val kryo = instantiator.newKryo()

    test("can correctly read and write all fields to kryo streams")
    {
        //set a bunch of fields in mock1
        val out = new Output(128)
        val mock1 = new MockReplicated
        mock1.boolean = false
        mock1.byte = 1
        mock1.short = 2
        mock1.integer = 3
        mock1.double = 4
        mock1.writeAllFields(out, kryo)

        //ensure they get replicated to mock2
        val mock2 = new MockReplicated
        mock2.readFields(new Input(out.toBytes), kryo)
        assert(mock2.boolean == false)
        assert(mock2.byte == 1)
        assert(mock2.short == 2)
        assert(mock2.integer == 3)
        assert(mock2.double == 4)
    }
}
