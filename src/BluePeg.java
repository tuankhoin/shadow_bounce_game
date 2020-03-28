/**
 * Blue peg is the default peg type. It can be overdrawn by a red peg and green peg
 */
public class BluePeg extends Peg{
    private boolean isRedPeg = false; // Check if the peg position is overdrawn by a red peg

    /**
     * Constructor
     * @param x          x-location
     * @param y          y-location
     * @param imgSrc    Corresponding image file
     */
    public BluePeg(double x, double y, String imgSrc) {
        super(x,y,imgSrc);
    }

    /**
     * This is to check if this peg position is overdrawn by a red peg
     * @return      true if this position is a red peg
     *              false otherwise
     */
    public  boolean getRedPeg(){
        return this.isRedPeg;
    }

    /**
     * Setter of isRedPeg
     * @param change        The change
     */
    public void setRedPeg(boolean change){
        this.isRedPeg = change;
    }

}
