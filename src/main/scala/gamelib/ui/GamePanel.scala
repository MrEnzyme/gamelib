package gamelib.ui

import java.awt.{Color, Graphics2D, Graphics}
import javax.swing.JPanel
import gamelib.core.GameInstance
import gamelib.util.math.Point

class GamePanel(gameInstance: GameInstance) extends JPanel
{
    override def paint(graphics: Graphics) =
    {
        val g = graphics.asInstanceOf[Graphics2D]

        g.setColor(Color.black)
        g.fillRect(0, 0, getWidth, getHeight)

        for(gameObj <- gameInstance.getObjects) gameObj.render(g)
    }

    def getMousePos: Point =
    {
        try
        {
            val mousePos = getMousePosition()
            return Point(mousePos.getX.toInt, mousePos.getY.toInt)
        }
        catch {case _: Throwable => }
        null
    }
}
