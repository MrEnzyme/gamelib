package gamelib.networking

import org.scalatest.{OneInstancePerTest, FunSuite}
import com.esotericsoftware.kryo.io.{Input, Output}

class ReplicatedFieldTest extends FunSuite with OneInstancePerTest
{
	test("field propogates changes to its variable")
	{
		var x = 0
		val field = new ReplicatedField[Int](() => x, x = _)
		assert(x == 0)
		val out = new Output(128)
		out.writeInt(5)
		field.readValue(new Input(out.toBytes))
		assert(x == 5)
	}

	test("field tracks changes to its variable")
	{
		var x = 0
		val field = new ReplicatedField[Int](() => x, x = _)
		assert(x == 0)
		x = 5
		val out = new Output(128)
		field.writeValue(out)
		val in = new Input(out.toBytes)
		assert(in.readInt == 5)
	}
}
