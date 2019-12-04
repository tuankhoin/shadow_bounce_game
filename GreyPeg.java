/**
 * A grey peg does not really do anything besides standing there and making the balls bounce off
 */
public class GreyPeg extends Peg{
    private boolean isInEffect = true;

    /**
     * Constructor
     * @param x          x-location
     * @param y          y-location
     * @param imgSrc    Corresponding image file
     */
    public GreyPeg(double x, double y, String imgSrc) {
        super(x,y,imgSrc);
    }

    /**
     * A grey peg might intersect a ball for many consecutive turns causing strange move. This will turns false when a
     * ball intersects it, and will only return true again when it moves away from the peg.
     * @return  true if peg is available for ball bouncing
     *          false otherwise
     */
    public boolean isInEffect() {
        return this.isInEffect;
    }

    /**
     * Setter of inEffect status
     * @param inEffect  status to be set
     */
    public void setInEffect(boolean inEffect) {
        this.isInEffect = inEffect;
    }

}
