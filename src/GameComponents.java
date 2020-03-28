import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Superclass for gaming components of the board
 */
public abstract class GameComponents {
    protected double x; // x location
    protected double y; // y location
    protected Image image; // object image
    protected Rectangle bound; // object bounding box

    /**
     * Superclass constructor
     * @param x          x-location
     * @param y          y-location
     * @param imageSrc    Corresponding image file
     */
    public GameComponents(double x, double y, String imageSrc){
        this.x = x;
        this.y = y;
        this.image = new Image(imageSrc);
        Point point = new Point(x,y);
        this.bound = image.getBoundingBoxAt(point);
        }

    /**
     *  Drawing the component on screen
     */
        public void render() {
            image.draw(this.x,this.y);
            this.bound = image.getBoundingBoxAt(new Point(x,y));
        }

    /**
     * Check if two distinct components intersect each other on screen
     * @param other     The other component to be checked
     * @return          true if they do intersect
     *                  false if not
     */
    public boolean intersects(GameComponents other) {
            return bound.intersects(other.bound);
        }

    /**
     * Getter of the object's x-location
     * @return      x-location of object
     */
    public double getX() {
            return this.x;
        }

    /**
     * Getter of the object's y-location
     * @return      y-location of object
     */
    public double getY() {
            return this.y;
        }

    public void setY(double y) {
        this.y = y;
    }

    /**
     * Make updates on object each frame. The update will depends on the object
     * @param input     Input
     */
    public void update(Input input){
        // To be overridden
        }

}
