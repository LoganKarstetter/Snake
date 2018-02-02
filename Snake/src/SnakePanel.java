import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * @author Logan Karstetter
 * Date: 01/28/2018
 */
public class SnakePanel extends JPanel implements Runnable
{
    /** The width of the SnakePanel */
    public static final int SWIDTH = 690; //Make these clean multiples of the Grid.SQUARE_WIDTH/HEIGHT
    /** The height of the SnakePanel */
    public static final int SHEIGHT = 690;

    /** The thread that runs the game loop */
    private Thread animator;
    /** Determines whether the animator thread is running */
    private volatile boolean isRunning = false;
    /** Determines whether the game is paused */
    private volatile boolean isPaused = false;
    /** Determines whether the game is over */
    private volatile boolean gameOver = false;

    /** The desired FPS/UPS */
    private int FPS;
    /** The amount of time allocated for each cycle of the game loop (in nanos) */
    private long loopPeriod;
    /** The time the game started (in nanos) */
    private long gameStartTime;
    /** The amount of time spent playing the game (in secs) */
    private int timeSpentInGame;

    /** The max number of times the animator thread can loop without sleeping
     * before it is forced to sleep/yield to let other threads execute. */
    private static final int NUM_DELAYS_FOR_YIELD = 16;
    /** The max number of frames that can be skipped before the game is forced to render */
    private static final int MAX_FRAMES_SKIPPED = 5;

    /** The Graphics used to double buffer/render offscreen */
    private Graphics dbGraphics;
    /** The image that is created/rendered offscreen and later painted to the screen */
    private Image dbImage;

    /** The font used to display messages to the user */
    private Font snakeFont;
    /** The font metrics used to help render the font messages */
    private FontMetrics fontMetrics;

    /** The ImageLoader used to load the game images */
    private ImageLoader imageLoader;
    /** The background image painted on the screen */
    private BufferedImage backgroundImage;

    /** The grid used to divide the panel */
    private Grid grid;

    /** The snake used to play this game Snake */
    private Snake snake;

    /** The fruit used in this game of Snake */
    private Fruit fruit;

    /** The number of fruits eaten by the player (score) */
    private int fruitsEaten = 0;

    /**
     * Create a new SnakePanel for playing Snake. The panel is responsible for running the game loop
     * which updates, renders, and draws the game at the desired FPS/UPS.
     */
    public SnakePanel(int FPS)
    {
        //Calculate the loopPeriod
        this.FPS = FPS;
        loopPeriod = 1000000000/FPS; //secs -> nanos

        //Set the background color and size of the PongPanel
        setDoubleBuffered(false);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(SWIDTH, SHEIGHT));

        //Create the font, font metrics and color
        snakeFont = new Font("", Font.PLAIN, 20);
        fontMetrics = this.getFontMetrics(snakeFont);

        //Request focus to the SnakePanel so it can receive key events
        setFocusable(true);
        requestFocus();
        initKeyListener();

        //Initialize the imageLoader and load the game images
        imageLoader = new ImageLoader();
        imageLoader.loadImagesFromFile("ImagesConfig.txt");
        backgroundImage = imageLoader.getImage("Snake Background"); //690x690 image

        //Create the Grid
        grid = new Grid(SWIDTH, SHEIGHT);

        //Create the Snake
        snake = new Snake(this, grid, imageLoader);

        //Create the Fruit
        fruit = new Fruit(this, grid, snake, imageLoader);
        grid.setFruit(fruit); //The grid needs a reference to the location of the fruit
    }

    /**
     * Notifies this component that it now has a parent component.
     * This method informs the SnakePanel that it has been added to a
     * parent container such as a JFrame. Once notified it starts the
     * game. This prevents the game starting before the user can see it.
     */
    public void addNotify()
    {
        super.addNotify();
        startGame();
    }

    /**
     * Initialize the animator thread and start the game.
     */
    private void startGame()
    {
        //If the game is not already started
        if (animator == null || !isRunning)
        {
            //Initialize the animator thread
            animator = new Thread(this);
            animator.start();
        }
    }

    /**
     * Pause the game.
     */
    public void pauseGame()
    {
        isPaused = true;
    }

    /**
     * Resume the game.
     */
    public void resumeGame()
    {
        isPaused = false;
    }

    /**
     * Stop the game, set isRunning to false.
     */
    public void stopGame()
    {
        isRunning = false;
    }

    /**
     * Repeatably update, render, paint, and sleep such that the game loop takes close to the amount of
     * time allotted by the desired FPS (loopPeriod).
     */
    public void run()
    {
        //The time before the current loop/cycle begins
        long beforeTime;
        //The current time after the gameUpdate, gameRender, and paintScreen methods execute
        long afterTime;
        //The time taken for the gameUpdate, gameRender, and paintScreen methods to execute
        long timeDifference;

        //The amount of time left in the current loopPeriod that the thread can sleep for
        //(loopPeriod - timeDifference) - overSleepTime
        long sleepTime;
        //The amount of time the thread overslept
        long overSleepTime = 0L;

        //The number of times the thread looped/cycled without sleeping (sleepTime was <= 0)
        int numDelays = 0;
        //The total amount of excess time the methods took to execute (overTime = actual - loopPeriod)
        long overTime = 0L;

        //Get the current time before the first loop
        gameStartTime = System.nanoTime();
        beforeTime = gameStartTime;

        //Game loop
        isRunning = true;
        while (isRunning)
        {
            //Update, render, and paint the screen
            gameUpdate();
            gameRender();
            paintScreen(); //active rendering

            //Get the current time after the methods executed
            afterTime = System.nanoTime();
            timeDifference = afterTime - beforeTime; //The time it took to update, render, and paint

            //Calculate how much time is left for sleeping in this loopPeriod (1000000000/FPS)
            sleepTime = (loopPeriod - timeDifference) - overSleepTime;

            //Sleep
            if (sleepTime > 0)
            {
                try
                {
                    Thread.sleep(sleepTime/1000000); //nanos -> ms
                }
                catch (InterruptedException e)
                {
                    //Do nothing
                }
                //Check if the animator overslept, overSleepTime will be deducted from the next sleepTime
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            }
            else //If we didn't get a chance to sleep this loopPeriod (sleepTime <= 0)
            {
                overTime = overTime - sleepTime; //Store the excess time (- because sleepTime is <= 0)
                overSleepTime = 0L; //Reset the oversleep time

                //See if the animator thread needs to yield
                if (++numDelays >= NUM_DELAYS_FOR_YIELD) //(it hasn't slept for NUM_DELAYS_FOR_YIELD cycles)
                {
                    Thread.yield();
                    numDelays = 0;
                }
            }

            //Get the beforeTime for the next cycle
            beforeTime = System.nanoTime();

            //If rendering and animation are taking too long, update the game without rendering it
            //This will get the UPS closer to the desired FPS
            int skips = 0;
            while ((overTime > loopPeriod) && (skips < MAX_FRAMES_SKIPPED))
            {
                //Update x times without rendering, won't be noticeable if MAX_SKIPPED_FRAMES is small
                overTime = overTime - loopPeriod;
                gameUpdate();
                skips++;
            }
        }
        //Running is false, so exit
        System.exit(0);
    }

    /**
     * Update the elements of the game as long as the game is not over or paused.
     */
    private void gameUpdate()
    {
        //If the game is not over or paused, update
        if (!gameOver && !isPaused)
        {
            //Update the game
            fruit.update(); //Update the fruit first, if it is eaten the snake will grow 'naturally'
            snake.update();
        }
    }

    /**
     * Render the game using double buffering. If it does not already exist, this
     * method creates an Image the size of the PongPanel and draws to it offscreen.
     * Drawing offscreen prevents flickering and then allows the paintScreen() method
     * to draw the entire screen as an image rather than in layers.
     */
    private void gameRender()
    {
        //If the dbImage (double buffered image) has not been created
        if (dbImage == null)
        {
            //Make an image the size of the panel
            dbImage = createImage(SWIDTH, SHEIGHT);
            if (dbImage == null)
            {
                return;
            }
            else
            {
                //Get the graphics context to draw to the dbImage
                dbGraphics = dbImage.getGraphics();

                //Set the font once
                dbGraphics.setFont(snakeFont);
            }
        }

        //Draw the background
        if (backgroundImage != null)
        {
            //Draw the background image to the screen
            dbGraphics.drawImage(backgroundImage, 0, 0, null);
        }
        else //If the image hasn't been loaded or couldn't be found draw a black background
        {
            dbGraphics.setColor(Color.BLACK);
            dbGraphics.fillRect(0, 0, SWIDTH, SHEIGHT);
        }

        //Draw the grid
        grid.draw(dbGraphics);

        //Draw the snake and fruit
        fruit.draw(dbGraphics);
        snake.draw(dbGraphics); //So the snake appears to 'eat' the fruit

        //Print the game stats
        printStats(dbGraphics);
    }

    /**
     * Actively render/draw the dbImage (created in gameRender()) onto the screen/PongPanel.
     */
    private void paintScreen()
    {
        //Declare a graphics object
        Graphics g;

        try
        {
            //Get the graphics context from the SnakePanel so we can to draw to the panel
            g = this.getGraphics();

            if ((g != null) && (dbImage != null))
            {
                g.drawImage(dbImage, 0, 0, null);
            }
            Toolkit.getDefaultToolkit().sync(); //Sync the display (only applies to odd systems)
            g.dispose();

        }
        catch (NullPointerException e)
        {
            System.out.println("Graphics context error: " + e);
        }
    }

    /**
     * Print the game statistics onto the screen.
     */
    private void printStats(Graphics dbGraphics)
    {
        //Set the color to white
        dbGraphics.setColor(Color.WHITE);

        //Write the score
        dbGraphics.drawString("Fruits eaten - " + fruitsEaten, SWIDTH - 180, 20);

        //Calculate the time playing as long as the game isn't over
        if (!gameOver)
        {
            timeSpentInGame = (int) ((System.nanoTime() - gameStartTime)/1000000000L);  // ns --> secs
            //Write out the time spent in game
            dbGraphics.drawString("Game time - " + timeSpentInGame, 5, 20);
        }
        else
        {
            //Print the final game time
            dbGraphics.drawString("GameTime - " + timeSpentInGame, 5, 20);

            //Write the game over message to the screen
            int msgX = (SWIDTH - fontMetrics.stringWidth("Game Over!"))/2;
            int msgY = (SHEIGHT - fontMetrics.getHeight())/2;
            dbGraphics.setColor(Color.WHITE);
            dbGraphics.drawString("Game Over!", msgX, msgY);

            //Write the created by message to the screen
            msgX = (SWIDTH - fontMetrics.stringWidth("Code and Graphics by: Logan Karstetter"))/2;
            msgY = (SHEIGHT - fontMetrics.getHeight())/2;
            dbGraphics.drawString("Code and Graphics by: Logan Karstetter", msgX, msgY + fontMetrics.getHeight());
        }
    }

    /**
     * Initialize a new KeyListener for this SnakePanel. The KeyListener listens
     * for the esc, and arrow keys being pressed.
     */
    private void initKeyListener()
    {
        //Add a KeyListener to receive key events
        addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                super.keyPressed(e);

                //Listen for the esc key to quit
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    //Quit the game
                    stopGame();
                }

                //Make the game play keys unavailable if the game is paused or over
                if (!isPaused && !gameOver)
                {
                    //Listen for direction changes
                    if (e.getKeyCode() == KeyEvent.VK_UP)
                    {
                        //Move the snake up
                        snake.setCurrentDirection(Snake.UP);
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    {
                        //Move the snake right
                        snake.setCurrentDirection(Snake.RIGHT);
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                    {
                        //Move the snake down
                        snake.setCurrentDirection(Snake.DOWN);
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    {
                        //Move the snake left
                        snake.setCurrentDirection(Snake.LEFT);
                    }
                }
            }
        });
    }

    /**
     * Ends the game, sets gameOver to true.
     */
    public void gameOver()
    {
        gameOver = true;
    }

    /**
     * Increment the number of fruits eaten (the score).
     */
    public void fruitEaten()
    {
        fruitsEaten++;
    }
}
