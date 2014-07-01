package gamelib.networking

import com.esotericsoftware.kryo.io.{Input, Output}

class ReplicatedField[+A](getValue: () => A, setValue: A => Unit)
{
	def writeValue(out: Output) = getValue() match
	{
		case v: Boolean => out.writeBoolean(v)
		case v: Byte => out.writeByte(v)
		case v: Short => out.writeShort(v)
		case v: Int => out.writeInt(v)
		case v: Long => out.writeLong(v)
		case v: Double => out.writeDouble(v)
		case v: String => out.writeString(v)
	}

	def readValue(in: Input) = getValue() match
	{
		case v: Boolean => setValue(in.readBoolean.asInstanceOf[A])
		case v: Byte => setValue(in.readByte.asInstanceOf[A])
		case v: Short => setValue(in.readShort.asInstanceOf[A])
		case v: Int => setValue(in.readInt.asInstanceOf[A])
		case v: Long => setValue(in.readLong.asInstanceOf[A])
		case v: Double => setValue(in.readDouble.asInstanceOf[A])
		case v: String => setValue(in.readString.asInstanceOf[A])
	}
}
