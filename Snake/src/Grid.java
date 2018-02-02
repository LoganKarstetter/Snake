import java.awt.*;
import java.util.ArrayList;

/**
 * @author Logan Karstetter
 * Date: 01/30/2018
 */
public class Grid
{
    /** The pixel width of a single square on the grid */
    public static final int SQUARE_WIDTH = 30;
    /** The pixel height of a single square on the grid */
    public static final int SQUARE_HEIGHT = 30;

    /** The height of the grid in squares */
    private int gridWidth;
    /** The width of the grid in squares */
    private int gridHeight;

    /** The 2D array of booleans representing a grid of squares. The snake and apple will only be
     * able to move/spawn onto the squares that are open. The status of the boolean determines whether or
     * not a tile is open. */
    private boolean[][] gridMap;

    /** The color used to draw the grid on the screen */
    private Color translucentGray;

    /** A reference to the fruit object in this game of Snake */
    private Fruit fruit;

    /**
     * Create a grid of squares from the given width and height. The dimensions of each square are
     * calculated based on the Grid class static values SQUARE_WIDTH and SQUARE_HEIGHT. Thus if the
     * inputted with and height are not cleanly divisible by those values there may be extra unused
     * space in the display. Each square contains a boolean value determining if it is open or not.
     * @param width The width in pixels of the area in which the grid can be drawn.
     * @param height The height in pixels of the area in which the grid can be drawn.
     */
    public Grid(int width, int height)
    {
        //Calculate the width and height of the gridMap (in squares)
        gridWidth = width/SQUARE_WIDTH;
        gridHeight = height/SQUARE_HEIGHT;

        //Create the gridMap
        gridMap = new boolean[gridWidth][gridHeight]; //booleans are set to false by default

        //Create the translucent gray color
        translucentGray = new Color(69, 69, 69, 25);
    }

    /**
     * Determine if the square on the grid located at the x and y grid coordinates is occupied
     * or open (not occupied). If the square contains the fruit the snake is allowed to move onto
     * the square to eat the fruit. If the square is out of the bounds of the grid then the snake has
     * hit a wall and the square is considered occupied.
     * @param x The x coordinate of the square on the grid.
     * @param y The y coordinate of the square on the grid.
     * @return True if the square is occupied, false if it is not (open).
     */
    public boolean isOccupied(int x, int y)
    {
        //Check that the x and y coordinates are valid within the grid
        if (!(x < 0 || x >= gridWidth || y < 0 || y >= gridHeight)) //If the x,y coordinates are valid
        {
            //See if the square on the grid is open
            if (gridMap[x][y]) //If the square not open
            {
                //Check for the fruit
                if (fruit != null && (x == fruit.gridX && y == fruit.gridY))
                {
                    fruit.setIsEaten(true);  //Eat the fruit
                    return false;
                }
                return true; //The square is not open
            }
            return false; //The square is open
        }
        //The square is out of bounds, game over
        return true;
    }

    /**
     * Get an ArrayList of all the open Points (x, y) on the grid.
     * @return The ArrayList of open points.
     */
    public ArrayList<Point> getOpenSquares()
    {
        //Initialize the array list
        ArrayList<Point> points = new ArrayList<>();

        //Loop through the squares array and make an array of points
        for (int x = 0; x < gridWidth; x++)
        {
            for (int y = 0; y < gridHeight; y++)
            {
                //If the square is open store the point
                if (!isOccupied(x, y))
                {
                    points.add(new Point(x, y));
                }
            }
        }

        //Return the array list
        return points;
    }

    /**
     * Set the status of the square on the grid located at x, y.
     * @param x The x coordinate of the square on the grid.
     * @param y The y coordinate of the square on the grid.
     * @param isOccupied The boolean determining if this square is occupied (false) or closed (true).
     */
    public void setSquare(int x, int y, boolean isOccupied)
    {
        gridMap[x][y] = isOccupied;
    }

    /**
     * Get the x and y grid coordinates of center square (or close to it).
     * @return A Point containing the grid x and y values.
     */
    public Point getCenter()
    {
        return new Point(gridWidth/2, gridHeight/2);
    }

    /**
     * Draw the grid.
     * @param dbGraphics The Graphics object used to draw the grid.
     */
    public void draw(Graphics dbGraphics)
    {
        dbGraphics.setColor(translucentGray);
        for (int i = 0; i < gridWidth; i++)
        {
            for (int j = 0; j < gridHeight; j++)
            {
                dbGraphics.drawRect(SQUARE_WIDTH * i,
                        SQUARE_HEIGHT * j, SQUARE_WIDTH, SQUARE_HEIGHT);
            }
        }
    }

    /**
     * Set the fruit field stored by the grid to the fruit used in the game. This is necessary
     * since the grid needs to know the location of the fruit in order for the snake to eat it.
     * @param fruit The Fruit used in this game of Snake.
     */
    public void setFruit(Fruit fruit)
    {
        this.fruit = fruit;
    }
}
