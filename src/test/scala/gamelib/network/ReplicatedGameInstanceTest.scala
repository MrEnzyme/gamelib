package gamelib.network

import gamelib.core.GameInstance
import org.scalatest.{OneInstancePerTest, FunSuite}

class MockReplicatedGameObject extends ReplicatedGameObject
{
    @replicate("a") var a = 5

    def update(gameInstance: GameInstance, deltaTime: Double) =
    {
        a += 1
        List()
    }
    def updateLocal(gameInstance: GameInstance, deltaTime: Double) =
    {
        a -= 1
        List()
    }
}

class ReplicatedGameInstanceTest extends FunSuite with OneInstancePerTest
{
    val gameInstance = new ReplicatedGameInstance("gamelib")
    val kryo = KryoRegistrar.makeNewKryo()

    test("tracks newly added ReplicatedGameObjects")
    {
        val repObject = new MockReplicatedGameObject
        gameInstance.addObject(repObject)

        val messages = gameInstance.writeCreateMessages(kryo)
        assert(messages.size == 1)
        assert(messages.head.messageType == CreateMessage)
    }

    test("replicates object creations")
    {
        val repObject = new MockReplicatedGameObject
        gameInstance.addObject(repObject)

        val clientGameInstance = new ReplicatedGameInstance("gamelib")
        val creationMessages = gameInstance.writeCreateMessages(kryo)
        clientGameInstance.applyMessages(creationMessages, kryo)
        clientGameInstance.update(0)
        assert(clientGameInstance.getObjects.length == 1)
        assert(clientGameInstance.getObjects.head.isInstanceOf[MockReplicatedGameObject])
        assert(clientGameInstance.getObjects.head.asInstanceOf[MockReplicatedGameObject].a == 4)
    }

    test("replicates object deletions")
    {
        val repObject = new MockReplicatedGameObject
        gameInstance.addObject(repObject)

        //make a client and send the creation message to it
        val clientGameInstance = new ReplicatedGameInstance("gamelib")
        val creationMessages = gameInstance.writeCreateMessages(kryo)
        clientGameInstance.applyMessages(creationMessages, kryo)

        //call update to add the object to the world
        gameInstance.update(0)
        clientGameInstance.update(0)

        assert(clientGameInstance.getObjects.length == 1)
        repObject.setDead()

        //call update on the host to push out the object removal
        gameInstance.update(0)
        val destroyMessages = gameInstance.writeDestroyMessages(kryo)

        //client gets the destroy message and sets the object as dead
        clientGameInstance.applyMessages(destroyMessages, kryo)

        //client cleans up the dead object during update
        clientGameInstance.update(0)

        assert(clientGameInstance.getObjects.length == 0)
    }
}
