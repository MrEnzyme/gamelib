package gamelib.networking

import scala.collection.mutable.{LinkedHashMap, Queue}

trait Replicated
{
	private val replicatedFields = new LinkedHashMap[Symbol, ReplicatedField[Any]]
	private val fieldIds = new Queue[Symbol]
	private val fieldsToUpdate = new LinkedHashMap[Symbol, Boolean]

	def registerField[A](name: Symbol, get: () => A, set: A => Unit)
	{
		val field = new ReplicatedField(get, set)
		replicatedFields(name) = field
		fieldIds += name
		fieldsToUpdate(name) = false
	}
}
