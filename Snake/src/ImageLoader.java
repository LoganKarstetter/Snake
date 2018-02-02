import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

/**
 * @author Logan Karstetter
 * Date: 1/27/2018
 */
public class ImageLoader
{
    /** The location of the file to load images from */
    private String directory = "Images/";

    /**
     * The HashMap used to store loaded images. The key is the image name as
     * it appeared in the file, and the object store is a BufferedImage.
     */
    private HashMap imagesMap;

    /** The graphics configuration describing the characteristics of the user's display */
    private GraphicsConfiguration graphicsConfiguration;

    /**
     * * Create an ImageLoader for loading images from a file located in the local Images/ directory.
     */
    public ImageLoader()
    {
        //Create the imagesMap and get the graphicsConfiguration
        imagesMap = new HashMap();
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsConfiguration = graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();
    }
    /**
     * Create an ImageLoader for loading images from a file located in some directory.
     * @param directory The local directory to load files from.
     */
    public ImageLoader(String directory)
    {
        //Call the constructor
        this();

        //Set the directory
        this.directory = directory; //Set to Images/ by default
    }

    /**
     * Load the single images located within the given file under the given directory
     * (Images/ by default). The format of the file is as follows [image]. The key/name
     * of the images in the imagesMap will be set to the [image] minus the .ext (if it even
     * has an extension). Lines that do not adhere to this format will be skipped. Lines
     * beginning with // will be regarded as comments and blank lines will also be skipped.
     * @param fileName The name of the file to load images from.
     */
    public void loadImagesFromFile(String fileName)
    {
        //Inform the user of the file reading
        System.out.println("Reading file: " + directory + fileName);
        try
        {
            //Created an InputStream and BufferedReader to read the file
            InputStream inputStream = this.getClass().getResourceAsStream(directory + fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            //Loop until the end of the file is reached
            String line;
            while ((line = br.readLine()) != null)
            {
                //Determine what action to take based off the line read
                if (line.startsWith("//") || (line.length() == 0)) //This line is a comment or blank line
                {
                    continue;
                } else //The line is not a comment/blank
                {
                    loadImage(line);
                }
            }

            //Close the BufferedReader
            br.close();

            //Inform the user the ImageLoader is done reading
            System.out.println("Finished reading file: " + directory + fileName);
        }
        catch (IOException e) {
            System.out.println("Error reading file: " + directory + fileName + " " + e);
        }
    }

    /**
     * Load the specified image from the line, and store it in the imagesMap.
     * @return True or false (success or fail)
     */
    private boolean loadImage(String line)
    {
        //Get the image name (remove an .extension, if any)
        String imageName = line; //Set the imageName to the line by default
        if (line.contains("."))
        {
            imageName = line.substring(0, line.indexOf('.'));
        }

        //Check that the imagesMap does not already contain this file or one using this name
        if (imagesMap.containsKey(imageName))
        {
            System.out.println("ImagesMap already contains: " + imageName);
            return false;
        }

        //Load in the image
        try
        {
            //Read in the image and store it in a new BufferedImage
            BufferedImage readImage = ImageIO.read(getClass().getResource(directory + line));

            //Create a new copy of the image to ensure it becomes a managed image
            int transparency = readImage.getColorModel().getTransparency();
            BufferedImage copy = graphicsConfiguration.createCompatibleImage(readImage.getWidth(),
                    readImage.getHeight(), transparency);
            //Create a graphics context to draw the image onto
            Graphics2D g2d = copy.createGraphics();
            g2d.drawImage(readImage, 0, 0, null);
            g2d.dispose();

            //Store the new image in the imagesMap if it is not null
            if (copy != null)
            {
                imagesMap.put(imageName, copy);
                System.out.println("Stored " + imageName + " [" + line + "]");
                return true;
            }

            //The copy was null
            return false;
        }
        catch (IOException e)
        {
            System.out.println("Error loading image [" + line + "]");
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("Unable to find image [" + line + "]");
        }

        //Something went wrong
        return false;
    }

    /**
     * Get an image from the imagesMap using it's key/name.
     * @param key The key (name) of the image.
     * @return The BufferedImage associated with the passed key.
     */
    public BufferedImage getImage(String key)
    {
        //Get the image and check if its null before returning
        BufferedImage image = (BufferedImage) imagesMap.get(key);
        if (image == null)
        {
            System.out.println("No image found under '" + key + "'");
        }
        return image;
    }
}
