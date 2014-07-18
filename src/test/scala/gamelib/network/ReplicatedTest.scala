package gamelib.network

import org.scalatest.FunSuite
import com.twitter.chill.{Input, Output}

class ReplicatedTest extends FunSuite
{
    class MockReplicated extends Replicated
    {
        @replicate("bool") var boolean: Boolean = true
        @replicate("byte") var byte: Byte = 0
        @replicate("short") var short: Short = 0
        @replicate("integer") var integer: Integer = 0
        @replicate("double") var double: Double = 0.0
    }

    val kryo = KryoRegistrar.makeNewKryo()

    test("read and write all fields to kryo streams")
    {
        //set a bunch of fields in mock1
        val out = new Output(128)
        val mock1 = new MockReplicated
        mock1.boolean = false
        mock1.byte = 1
        mock1.short = 2
        mock1.integer = 3
        mock1.double = 4
        mock1.write(out, kryo)

        //ensure they get replicated to mock2
        val mock2 = new MockReplicated
        mock2.read(new Input(out.toBytes), kryo)
        assert(mock2.boolean == false)
        assert(mock2.byte == 1)
        assert(mock2.short == 2)
        assert(mock2.integer == 3)
        assert(mock2.double == 4)
    }

    test("read and write specific fields to kryo streams")
    {
        //set a bunch of fields in mock1
        val out = new Output(128)
        val mock1 = new MockReplicated
        mock1.boolean = false
        mock1.byte = 1
        mock1.short = 2
        mock1.integer = 3
        mock1.double = 4
        mock1.updateFields("bool", "short", "double")
        mock1.writeUpdate(out, kryo)

        //ensure they get replicated to mock2
        val mock2 = new MockReplicated
        mock2.read(new Input(out.toBytes), kryo)
        assert(mock2.boolean == false)
        assert(mock2.byte != 1)
        assert(mock2.short == 2)
        assert(mock2.integer != 3)
        assert(mock2.double == 4)
    }
}
