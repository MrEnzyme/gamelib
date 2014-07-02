package gamelib.networking

import scala.collection.mutable.{LinkedHashMap, LinkedHashSet, Queue}
import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.Kryo

trait Replicated
{
    private val replicatedFields = new LinkedHashMap[Symbol, ReplicatedField[Any]]
    private val fieldIds = new Queue[ReplicatedField[Any]]
    private val fieldsToUpdate = new LinkedHashSet[Symbol]

    def registerField[A](name: Symbol, get: () => A, set: A => Unit)
    {
        val field = new ReplicatedField(get, set)
        replicatedFields(name) = field
        fieldIds += field
        fieldsToUpdate(name) = false
    }

    def replicateFields(fields: Symbol*) = for(f <- fields) fieldsToUpdate += f
    final def clearUpdates() = fieldsToUpdate.clear()

    private def writeField(out: Output, kryo: Kryo, field: ReplicatedField[Any])
    {
        out.writeByte(fieldIds.indexOf(field))
        field.writeValue(out, kryo)
    }

    final def writeAllFields(out: Output, kryo: Kryo) = for((fieldName, field) <- replicatedFields) writeField(out, kryo, field)
    final def writeUpdatedFields(out: Output, kryo: Kryo) = for((fieldName, field) <- replicatedFields) if(fieldsToUpdate.contains(fieldName)) writeField(out, kryo, field)
    final def readFields(in: Input, kryo: Kryo) = while(in.available > 0) fieldIds(in.readByte()).readValue(in, kryo)

    final def replicationNeeded = !fieldsToUpdate.isEmpty
}
