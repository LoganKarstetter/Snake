import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author Logan Karstetter
 * Date: 01/28/2018
 */
public class SnakeGame extends JFrame implements WindowListener
{
    /** The desired FPS/UPS for SnakeGame */
    private static int DEFAULT_FPS = 6; //This is an average speed, increase the FPS for a challenge

    /** The SnakePanel used to play SnakeGame */
    private SnakePanel sPanel;

    /**
     * A single-player SnakeGame game.
     * @param FPS The desired FPS.
     */
    public SnakeGame(int FPS)
    {
        super("SnakeGame");

        //Create the SnakePanel and add it to the contentPane
        sPanel = new SnakePanel(FPS);
        getContentPane().add(sPanel);

        //Add a window listener to handle pausing
        addWindowListener(this);
        pack();
        setResizable(false);
        setVisible(true);
    }

    /**
     * Resumes the game when the window is activated/invoked.
     * @param e A WindowEvent
     */
    public void windowActivated(WindowEvent e)
    {
        sPanel.resumeGame();
    }

    /**
     * Pauses the game when the window is deactivated.
     * @param e A WindowEvent
     */
    public void windowDeactivated(WindowEvent e)
    {
        sPanel.pauseGame();
    }

    /**
     * Resumes the game when the window is deiconified/invoked.
     * @param e A WindowEvent
     */
    public void windowDeiconified(WindowEvent e)
    {
        sPanel.resumeGame();
    }

    /**
     * Pauses the game when the window is iconified.
     * @param e A WindowEvent
     */
    public void windowIconified(WindowEvent e)
    {
        sPanel.pauseGame();
    }

    /**
     * Stops the game when the window is closed.
     * @param e A WindowEvent
     */
    public void windowClosing(WindowEvent e)
    {
        sPanel.stopGame();
    }

    /**
     * This method does nothing.
     * @param e A WindowEvent
     */
    public void windowOpened(WindowEvent e)
    {
        //Do nothing
    }

    /**
     * This method does nothing.
     * @param e A WindowEvent
     */
    public void windowClosed(WindowEvent e)
    {
        //Do nothing
    }

    /**
     * Launches a game of SnakeGame.
     * @param args Command line arguments are not used.
     */
    public static void main(String[] args)
    {
        new SnakeGame(DEFAULT_FPS);
    }
}
