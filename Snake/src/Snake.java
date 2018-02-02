import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Logan Karstetter
 * Date: 01/30/2018
 */
public class Snake
{
    /** The flag/constant value for moving the snake up */
    public static final int UP = 0;
    /** The flag/constant value for moving the snake right */
    public static final int RIGHT = 1;
    /** The flag/constant value for moving the snake down */
    public static final int DOWN = 2;
    /** The flag/constant value for moving the snake left */
    public static final int LEFT = 3;

    /** The current direction the snake's head should move according to the user's input */
    private int currentDirection;

    /** The ArrayList of nodes used to store the snake */
    private ArrayList<SnakeNode> nodes;

    /** The array of serpent head images ordered relative to the direction constants */
    private BufferedImage[] serpHeadImages;
    /** The image used for the snake's body */
    private BufferedImage serpBody;
    /** The array of serpent tail images ordered relative to the direction constants */
    private BufferedImage[] serpTailImages;

    /** A reference to the SnakePanel */
    private SnakePanel sPanel;
    /** A reference to the Grid this snake lives on */
    private Grid grid;

    /**
     * Create a new Snake to be used in a game of Snake.
     * @param sPanel The SnakePanel this snake resides within.
     * @param grid The Grid this snake moves/lives on.
     * @param imageLoader The ImageLoader used to load images for this game.
     */
    public Snake(SnakePanel sPanel, Grid grid, ImageLoader imageLoader)
    {
        //Store the sPanel and grid
        this.sPanel = sPanel;
        this.grid = grid;

        //Initialize the images arrays
        serpHeadImages = new BufferedImage[4];
        serpTailImages = new BufferedImage[4];

        //Load the relevant image(s)
        serpHeadImages[UP] = imageLoader.getImage("Serpent Head Up");
        serpHeadImages[RIGHT] = imageLoader.getImage("Serpent Head Right");
        serpHeadImages[DOWN] = imageLoader.getImage("Serpent Head Down");
        serpHeadImages[LEFT] = imageLoader.getImage("Serpent Head Left");
        serpBody = imageLoader.getImage("Serpent Body");
        serpTailImages[UP] = imageLoader.getImage("Serpent Tail Up");
        serpTailImages[RIGHT] = imageLoader.getImage("Serpent Tail Right");
        serpTailImages[DOWN] = imageLoader.getImage("Serpent Tail Down");
        serpTailImages[LEFT] = imageLoader.getImage("Serpent Tail Left");

        //Create the nodes array list
        nodes = new ArrayList<>(); //An array list allows the snake to grow to fit any grid size
        nodes.add(new SnakeNode(grid)); //Create the head node
        nodes.add(new SnakeNode(grid, nodes.get(0))); //Create the tail node

        //Set the initial direction of the snake
        currentDirection = LEFT;
    }

    /**
     * Update the snake's position according to the current direction. The snake's body is moved first
     * in reverse order which allows each node to move to the location of its parent node and
     * copy its direction before the parents themselves move. If possible, snake's head is moved
     * next in the direction specified. If the snake's head was unable to move, then the head either
     * ran into a wall or its own body and the game will end.
     */
    public void update()
    {
        //Move the tail first since it has the special condition of setting its location to open
        nodes.get(nodes.size() - 1).moveNode(true);

        //Move the rest of the snake's body
        for (int i = nodes.size() - 2; i > 0; i--)
        {
            nodes.get(i).moveNode(false);
        }

        //Move the snake's head last
        if (!nodes.get(0).moveNode(currentDirection)) //The head could not be moved, we hit something
        {
            //The snake has crashed and burned (it ran into something), stop the game
            sPanel.gameOver();
        }
    }

    /**
     * Grow the snake by adding a new SnakeNode.
     */
    public void grow()
    {
        //Add a new node to the snake and set its parent as the current tail node
        nodes.add(new SnakeNode(grid, nodes.get(nodes.size() - 1)));
    }

    /**
     * Draw the snake.
     * @param dbGraphics The Graphics object used to draw the snake.
     */
    public void draw(Graphics dbGraphics)
    {
        //Set the color to green
        dbGraphics.setColor(Color.GREEN);

        //Draw the snakes head in the head's current direction
        //This is tricky because the currentDirection variable may change between the last update and
        //the call to this draw method. Thus the nodes direction must be used, not the one stored in
        //the Snake class variable. If the Snake class currentDirection is used the head may be drawn
        //facing a direction it is not yet moving in
        nodes.get(0).draw(dbGraphics, serpHeadImages[nodes.get(0).getDirection()]);

        //Draw the snake's body
        for (int i = 1; i < nodes.size() - 1; i++) //Skip the head
        {
            nodes.get(i).draw(dbGraphics, serpBody);
        }

        //Draw the snake's tail in the direction of its parent node (nodes.size() - 2)
        //This is done to prevent the tail from appearing detached from the rest of the snake
        nodes.get(nodes.size() - 1).draw(dbGraphics,
                serpTailImages[nodes.get(nodes.size() - 2).getDirection()]);
    }

    /**
     * Set the direction the snake is currently traveling.
     * @param newDirection The new direction for the snake to travel.
     */
    public void setCurrentDirection(int newDirection)
    {
        this.currentDirection = newDirection;
    }

}
