package gamelib.network

import gamelib.core.GameInstance
import org.scalatest.{OneInstancePerTest, FunSuite}

class MockReplicatedGameObject extends ReplicatedGameObject
{
    @replicate("a") var a = 5

    def update(gameInstance: GameInstance, deltaTime: Double) = List()
    def updateLocal(gameInstance: GameInstance, deltaTime: Double) = List()
}

class ReplicatedGameInstanceTest extends FunSuite with OneInstancePerTest
{
    val hostInstance = new ReplicatedGameInstance("gamelib")
	val clientInstance = new ReplicatedGameInstance("gamelib")
    val kryo = KryoRegistrar.makeNewKryo()

    test("tracks newly added ReplicatedGameObjects")
    {
        val repObject = new MockReplicatedGameObject
        hostInstance.addObject(repObject)

        val messages = hostInstance.getCreateMessages(kryo)
        assert(messages.size == 1)
        assert(messages.head.messageType == CreateMessage)
    }

    test("replicates object creations")
    {
        val repObject = new MockReplicatedGameObject
        hostInstance.addObject(repObject)

        val creationMessages = hostInstance.getCreateMessages(kryo)
        clientInstance.applyMessages(creationMessages, kryo)
        clientInstance.update(0)
        assert(clientInstance.getObjects.length == 1)
        assert(clientInstance.getObjects.head.isInstanceOf[MockReplicatedGameObject])
        assert(clientInstance.getObjects.head.asInstanceOf[MockReplicatedGameObject].a == 5)
    }

    test("replicates object deletions")
    {
        val repObject = new MockReplicatedGameObject
        hostInstance.addObject(repObject)

        //make a client and send the creation message to it
        val creationMessages = hostInstance.getCreateMessages(kryo)
        clientInstance.applyMessages(creationMessages, kryo)

        //call update to add the object to the world
        hostInstance.update(0)
        clientInstance.update(0)

        assert(clientInstance.getObjects.length == 1)
        repObject.setDead()

        //call update on the host to push out the object removal
        hostInstance.update(0)
        val destroyMessages = hostInstance.getDestroyMessages(kryo)

        //client gets the destroy message and sets the object as dead
        clientInstance.applyMessages(destroyMessages, kryo)

        //client cleans up the dead object during update
        clientInstance.update(0)

        assert(clientInstance.getObjects.length == 0)
    }

	test("replicates object updates")
	{
		val repObject = new MockReplicatedGameObject
		hostInstance.addObject(repObject)

		val creationMessages = hostInstance.getCreateMessages(kryo)
		clientInstance.applyMessages(creationMessages, kryo)

		hostInstance.update(0)
		clientInstance.update(0)

		assert(clientInstance.getObjects.head.asInstanceOf[MockReplicatedGameObject].a == 5)

		repObject.a = 10
		repObject.updateFields("a")

		val updateMessages = hostInstance.getUpdateMessages(kryo)
		clientInstance.applyMessages(updateMessages, kryo)

		assert(clientInstance.getObjects.head.asInstanceOf[MockReplicatedGameObject].a == 10)
	}
}
