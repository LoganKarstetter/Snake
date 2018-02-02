import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Logan Karstetter
 * Date: 01/30/2018
 */
public class SnakeNode
{
    /** The grid x position of this node */
    private int gridX;
    /** The grid y position of this node */
    private int gridY;
    /** The direction this node is currently moving */
    private int direction;

    /** The parent node of this node */
    private SnakeNode parent;

    /** A reference to the Grid this node is on */
    private Grid grid;

    /**
     * Create a new SnakeNode without a parent node and set its initial position
     * to the center of the SnakePanel. This method should only be
     * used to create the node representing the head of the snake.
     * @param grid The grid this node is on.
     */
    public SnakeNode(Grid grid)
    {
        //Store the grid
        this.grid = grid;

        //Set the position of the node to the center of the grid
        gridX = grid.getCenter().x;
        gridY = grid.getCenter().y;
        //Don't bother with direction here, it will be set by the snake's update method
    }

    /**
     * Create a new SnakeNode with a parent node.
     * @param grid The grid this node is on.
     * @param parent The parent snake node that leads this node.
     */
    public SnakeNode(Grid grid, SnakeNode parent)
    {
        //Store the grid and parent node
        this.grid = grid;
        this.parent = parent;

        //Set the node's direction to that of its parent
        gridX = parent.gridX;
        gridY = parent.gridY;
        direction = parent.direction;

    }

    /**
     * Move a node by using the given current direction to determine offsets to apply to the node's
     * gridX and gridY values. If the square located on the grid at the new coordinates is open then
     * it will be set to occupied. If it is not open then this node has run into something.
     * @param currentDirection The current direction the user wishes the snake to move.
     * @return True if this move was successful, false if this node tried to move into an occupied square.
     */
    public boolean moveNode(int currentDirection)
    {
        //Update the nodes stored direction
        direction = currentDirection;

        //Set the current square as not occupied
        grid.setSquare(gridX, gridY, false);

        //Set the new position of the node by adding a change in the gridX or gridY
        //depending on the direction (gridX and gridY never both need to be changed)
        switch (direction)
        {
            case Snake.UP:
                gridY = gridY - 1;
                break;
            case Snake.RIGHT:
                gridX = gridX + 1;
                break;
            case Snake.DOWN:
                gridY = gridY + 1;
                break;
            case Snake.LEFT:
                gridX = gridX - 1;
                break;
        }

        //Check if the square is open
        if (!grid.isOccupied(gridX, gridY)) //If the spot is open
        {
            //Set the square as occupied
            grid.setSquare(gridX, gridY, true);
            return true;
        }

        //The spot was not open and the node ran into something
        return false;
    }

    /**
     * Move a node using the current position and direction of its parent node. Before the move is
     * made, if this node is the tail of the snake, the square this node currently occupies is set
     * to open. The node is moved to the same location as its parent. The position is then set to
     * occupied regardless of whether it was already occupied or not.
     * @param isTail Determines whether or not this is the tail node of the snake.
     */
    public void moveNode(boolean isTail)
    {
        //Set the current node to not occupied, but only if this is the tail
        if (isTail)
        {
            grid.setSquare(gridX, gridY, false);
        }

        //Move the node based on the location of its parent node
        gridX = parent.gridX;
        gridY = parent.gridY;

        //Update the direction of the node to its parent's direction
        direction = parent.direction;

        //Set the tile to occupied
        grid.setSquare(gridX, gridY, true);
    }

    /**
     * Draw the snake node at its current position on the grid.
     * @param dbGraphics The Graphics object used to draw the node.
     * @param image The BufferedImage used to display this node.
     */
    public void draw(Graphics dbGraphics, BufferedImage image)
    {
        //Draw the node
        if (image != null)
        {
            dbGraphics.drawImage(image, Grid.SQUARE_WIDTH * gridX,
                    Grid.SQUARE_HEIGHT * gridY, null);
        }
        else //If the image is null then just draw a green rectangle
        {
            //The snake's draw method sets the color to green once, so it's not nee
            dbGraphics.fillRect(Grid.SQUARE_WIDTH * gridX,
                    Grid.SQUARE_HEIGHT * gridY, Grid.SQUARE_WIDTH, Grid.SQUARE_HEIGHT);
        }
    }

    /**
     * Get the direction this node is currently moving in.
     * @return The direction this node is currently moving in.
     */
    public int getDirection()
    {
        return direction;
    }
}
