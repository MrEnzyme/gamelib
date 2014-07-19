package gamelib.util

import scala.collection.mutable

/*
    Mutable collection that works as a two-way map
 */
class BidirectionalHashMap[A, B] extends Traversable[(A, B)]
{
    private val AtoB = new mutable.LinkedHashMap[A, B]
    private val BtoA = new mutable.LinkedHashMap[B, A]

    def getValue(a: A) = AtoB(a)
    def getKey(b: B) = BtoA(b)

    def hasKey(a: A) = AtoB.contains(a)
    def hasValue(b: B) = BtoA.contains(b)

    def addPair(a: A, b: B)
    {
        AtoB(a) = b
        BtoA(b) = a
    }
    def removePair(a: A, b: B)
    {
        AtoB.remove(a)
        BtoA.remove(b)
    }

    def clear() =
    {
        AtoB.clear()
        BtoA.clear()
    }

    def foreach[C](f: ((A, B)) => C): Unit = AtoB.foreach(f)

    override def size = AtoB.size
}
