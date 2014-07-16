package gamelib.network

import com.twitter.chill.Output
import scala.collection.JavaConversions._

trait BaseTrait

class Creature extends Replicated
{
    @replicate("life") var life = 100
    @replicate("speed") var speed = 0
}

class Goon extends Creature
{
    @replicate("shots") var shots = 55
}

object Test
{
    def main(args: Array[String])
    {
        val out = new Output(5666)
        out.writeInt(133)
        for(b <- out.toBytes) print(b)
    }
}
