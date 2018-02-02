import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Logan Karstetter
 * Date: 01/30/2018
 */
public class Fruit
{
    /** The grid x position of this fruit */
    public int gridX;
    /** The grid y position of this fruit */
    public int gridY;
    /** The diameter of the fruit (it's a sphere) */
    private int diameter = 20; //This is basically just backup if the images don't load

    /** Determines whether the fruit has been eaten and needs to be repositioned */
    private boolean isEaten = false;

    /** The image used to display the fruit in the game */
    private BufferedImage fruitImage;
    /** The random number generator used to generate locations for the fruit to spawn */
    private Random rng;

    /** A reference to the SnakePanel */
    private SnakePanel sPanel;
    /** A reference to the grid this fruit is on */
    private Grid grid;
    /** A reference to the snake this fruit is eaten by */
    private Snake snake;


    /**
     * Create a fruit to be eaten by a Snake.
     * @param sPanel The SnakePanel that contains this fruit.
     * @param grid The Grid the fruit is on.
     * @param snake The Snake that eats the fruit.
     * @param imageLoader The ImageLoader used to load images for this game.
     */
    public Fruit(SnakePanel sPanel, Grid grid, Snake snake, ImageLoader imageLoader)
    {
        //Store the sPanel, grid, and snake references
        this.sPanel = sPanel;
        this.grid = grid;
        this.snake = snake;

        //Load the relevant image(s)
        fruitImage = imageLoader.getImage("Apple");

        //Find a random position to place the fruit
        rng = new Random();
        findPosition();
    }

    /**
     * Update the fruit, but only if it has been eaten by the snake. If it has been eaten the current
     * square on the grid occupied by the fruit will be opened, the snake's grow method will be
     * called, the fruitsEaten counter will increment, and the fruit repositioned on a random open square
     * on the grid. If the fruit has not been eaten, nothing will happen.
     */
    public void update()
    {
        //If the fruit has been eaten, update
        if (isEaten)
        {
            //Set the fruit position to open
            grid.setSquare(gridX, gridY, false);

            //Grow the snake
            snake.grow();

            //Increment fruitsEaten
            sPanel.fruitEaten();

            //Find a new position for the fruit
            findPosition();

            //Reset isEaten to false
            isEaten = false;
        }

    }

    /**
     * Find an open point on the grid to place the fruit.
     */
    private void findPosition()
    {
        //Get an ArrayList of open points on the grid
        ArrayList<Point> points = grid.getOpenSquares();

        //Check if the points array list is empty
        if (!points.isEmpty())
        {
            //Pick a random point out of the array list
            int index = rng.nextInt(points.size());
            gridX = points.get(index).x;
            gridY = points.get(index).y;

            //Set the square to occupied
            grid.setSquare(gridX, gridY, true);

            //System.out.println("Placed at " + gridX + ", " + gridY);
        }
        else //The player won wow
        {
            sPanel.gameOver();
        }
    }

    /**
     * Set the isEaten boolean for this fruit. When the fruit is eaten, its update method will free its
     * current square on the grid, grow the snake, and find a new position for the fruit on the grid.
     * @param fruitIsEaten True if the fruit is eaten, false if it is not.
     */
    public void setIsEaten(boolean fruitIsEaten)
    {
        isEaten = fruitIsEaten;
        //System.out.println("Eaten at " + gridX + ", " + gridY);
    }

    /**
     * Draw the fruit on the grid.
     * @param dbGraphics The Graphics object used to draw the fruit.
     */
    public void draw(Graphics dbGraphics)
    {
        //Draw the fruit
        if (fruitImage != null)
        {
            dbGraphics.drawImage(fruitImage, Grid.SQUARE_WIDTH * gridX,
                    Grid.SQUARE_HEIGHT * gridY, null);
        }
        else //If the fruitImage is null
        {
            dbGraphics.setColor(Color.RED);
            dbGraphics.fillOval(Grid.SQUARE_WIDTH * gridX + (diameter/4),
                    Grid.SQUARE_HEIGHT * gridY + (diameter/4), diameter, diameter);
        }
    }
}
