package gamelib.core

import scala.collection.mutable
import gamelib.util.math.Point
import gamelib.network.{KeyInput, MouseButton, MouseMove, InputEvent}

class InputState
{
    private val keys = new mutable.HashMap[Char, Boolean]
    private val mouseButtons = new mutable.HashMap[Int, Boolean]
    private var mousePos = Point(0,0)

    for(i <- 0 to 255) keys(i.toChar) = false
    for(i <- 0 to 10) mouseButtons(i) = false

    def getKey(key: Char) = keys(key)
    def getMouseButton(button: Int) = mouseButtons(button)
    def getMousePos = mousePos

    def processInputEvent(evt: InputEvent) = evt match
    {
        case mm: MouseMove => mousePos = mm.mousePos
        case mb: MouseButton => mouseButtons(mb.button) = mb.newState
        case ki: KeyInput => keys(ki.key) = ki.newState
    }
}
