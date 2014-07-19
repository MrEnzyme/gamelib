package gamelib.network

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.Kryo
import org.reflections.ReflectionUtils._
import java.lang.reflect.Field
import scala.collection.mutable

trait Replicated
{
    class ReplicatedField(instanceField: Field)
    {
        def writeValue(out: Output, kryo: Kryo) = kryo.writeObject(out, instanceField.get(Replicated.this))
        def readValue(in: Input, kryo: Kryo) = instanceField.set(Replicated.this, kryo.readObject(in, instanceField.getType))
    }

    private lazy val fields = fieldNames.values.toSeq
    private lazy val fieldNames = initFields()
    private val fieldsToUpdate = new mutable.LinkedHashSet[ReplicatedField]

    def initFields(): Map[String, ReplicatedField] =
    {
        val newFields = getAllFields(getClass, withAnnotation(classOf[replicate]))
        val namePairs = for(f <- newFields.toArray) yield
        {
            val field = f.asInstanceOf[Field]
            field.setAccessible(true)
            val newField = new ReplicatedField(field)
            field.getAnnotation(classOf[replicate]).value -> newField
        }
        namePairs.toMap
    }

    final def updateFields(fields: String*) = for(f <- fields) fieldsToUpdate += fieldNames(f)
    final def clearUpdates() = fieldsToUpdate.clear()

    private def writeField(out: Output, kryo: Kryo, field: ReplicatedField)
    {
        out.writeByte(fields.indexOf(field))
        field.writeValue(out, kryo)
    }

    final def write(out: Output, kryo: Kryo) = for(field <- fields) writeField(out, kryo, field)
    final def writeUpdate(out: Output, kryo: Kryo) = for(field <- fields) if(fieldsToUpdate.contains(field)) writeField(out, kryo, field)
    final def read(in: Input, kryo: Kryo) = while(in.available > 0) fields(in.readByte()).readValue(in, kryo)

    final def replicationNeeded = fieldsToUpdate.nonEmpty
}
