package gamelib.ui

import java.awt.{KeyboardFocusManager, KeyEventDispatcher, Toolkit}
import java.awt.event.{KeyEvent, WindowEvent}
import javax.swing.{JPanel, BoxLayout, JFrame}

class GameWindow
{
    class DrawThread(window: GameWindow) extends Thread
    {
        setDaemon(true)
        override def run()
        {
            val timePerFrame = 20L
            while(true)
            {
                window.draw()
                Thread.sleep(timePerFrame)
            }
        }
    }

    val frame = new JFrame()
    {
        def close()
        {
            val wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING)
            Toolkit.getDefaultToolkit.getSystemEventQueue.postEvent(wev)
        }
    }
    val keyDispatcher = new KeyEventDispatcher
    {
        override def dispatchKeyEvent(e: KeyEvent) =
        {
            if (e.getID == KeyEvent.KEY_PRESSED && e.getKeyChar == KeyEvent.VK_ESCAPE) frame.close()
            false
        }
    }
    KeyboardFocusManager.getCurrentKeyboardFocusManager.addKeyEventDispatcher(keyDispatcher)

    val mainView = new JPanel
    mainView.setLayout(new BoxLayout(mainView, BoxLayout.Y_AXIS))

    frame.add(mainView)

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    //f.setUndecorated(true)

    frame.setSize(1200, 800)
    frame.setLocationRelativeTo(null)
    frame.setVisible(true)

    new DrawThread(this).start()

    def draw() = frame.repaint()

    def getMousePos = frame.getMousePosition()

    def addPanel(panel: JPanel)
    {
        mainView.add(panel)
        mainView.validate()
    }
}
