package gamelib.networking

import scala.collection.mutable.LinkedHashMap

trait Replicated
{
	private val replicatedFields = new LinkedHashMap[Symbol, ReplicatedField[Any]]


}
