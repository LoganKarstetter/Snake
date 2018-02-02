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
        setResizable(false);
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
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
     * Launches a game of Snake. A single integer value can be specified as a command line argument to
     * set the FPS for the game. If no value is provided it will run at the default FPS (6).
     * @param args An integer specifying the requested FPS.
     */
    public static void main(String[] args)
    {
        //Check for command line arguments
        if (args.length > 0)
        {
            //Cast the first argument to an integer
            try
            {
                //Start a game with the requested FPS
                int FPS = Integer.valueOf(args[0]);
                System.out.println("Running Snake with FPS: " + FPS);
                new SnakeGame(FPS);
            }
            catch (Exception e) //Horrible practice, but error catching isn't useful here
            {
                System.out.println("Unable to set requested FPS value: " + args[0] + "\nPlease enter only integers.  Exiting...");
                System.exit(0);
            }
        }
        else //Use the default FPS
        {
            System.out.println("Running Snake with default FPS: " + DEFAULT_FPS);
            new SnakeGame(DEFAULT_FPS);
        }
    }
}
