package gamelib.core

import java.util.concurrent.LinkedBlockingQueue
import gamelib.network.InputEvent
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

class GameInstanceThread(gameInstance: GameInstance) extends Runnable
{
	private val timePerFrame = 20L

	private val inputEvents = new LinkedBlockingQueue[InputEvent]
	private val inputEventBuffer = new ListBuffer[InputEvent]

	def addInputEvent(evt: InputEvent) = inputEvents.add(evt)

	def start() = new Thread(this).start()
	def run() = while(true)
	{
		val start = System.currentTimeMillis
		update(timePerFrame / 1000.0)
		val diff = System.currentTimeMillis - start
		if (timePerFrame - diff > 0) Thread.sleep(timePerFrame - diff)
	}
	protected def update(deltaTime: Double)
	{
		if(!inputEvents.isEmpty)
		{
			inputEventBuffer.clear()
			inputEvents.drainTo(inputEventBuffer)
			for(evt <- inputEventBuffer) gameInstance.updateInputState(evt)
		}
		gameInstance.update(timePerFrame)
	}
}
