package gamelib.network

import scala.collection.mutable.{ListBuffer, LinkedHashMap, LinkedHashSet}
import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.Kryo

trait Replicated
{
    private val fields = new ListBuffer[ReplicatedField[Any]]
    private val fieldNames = new LinkedHashMap[Symbol, ReplicatedField[Any]]
    private val fieldsToUpdate = new LinkedHashSet[ReplicatedField[Any]]

    final def registerField[A](name: Symbol, get: () => A, set: A => Unit)
    {
        val field = new ReplicatedField(get, set)
        fields += field
        fieldNames(name) = field
        fieldsToUpdate(field) = false
    }

    final def replicateFields(fields: Symbol*) = for(f <- fields) fieldsToUpdate += fieldNames(f)
    final def clearUpdates() = fieldsToUpdate.clear()

    private def writeField(out: Output, kryo: Kryo, field: ReplicatedField[Any])
    {
        out.writeByte(fields.indexOf(field))
        field.writeValue(out, kryo)
    }

    final def write(out: Output, kryo: Kryo) = for(field <- fields) writeField(out, kryo, field)
    final def writeUpdate(out: Output, kryo: Kryo) = for(field <- fields) if(fieldsToUpdate.contains(field)) writeField(out, kryo, field)
    final def read(in: Input, kryo: Kryo) = while(in.available > 0) fields(in.readByte()).readValue(in, kryo)

    final def replicationNeeded = !fieldsToUpdate.isEmpty
}
