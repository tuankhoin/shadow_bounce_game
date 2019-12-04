
/**
 * Red/Green peg will be overdrawn on a blue peg position, to maintain the default effects that a blue one has
 * They will just change in image
 */
public class RedGreenPeg extends Peg {
    private int key; // The blue peg position that it lies

    /**
     * Constructor
     * @param key            The array index of the corresponding blue peg
     * @param x              x-location
     * @param y              y-location
     * @param imgSrc         Corresponding image file
     */
    public RedGreenPeg(int key, double x, double y, String imgSrc){
        super(x,y,imgSrc);
        this.key = key;
    }

    /**
     * Get the key of corresponding blue peg position
     * @return      Array index of the corresponding blue peg
     */
    public int getKey() {
        return this.key;
    }

    @Override
    public void effect(Ball ball) {
        // Does not make the ball bounces off, because it is set in a blue peg, which has already bounced the ball off
        // It will just disappears
        this.disappear();
    }

}
