import bagel.*;
import bagel.util.Rectangle;
import bagel.util.Side;
import bagel.util.Vector2;

/**
 * Abstract class of pegs in the board, object that bring various effects that differs by type on ball
 */
public abstract class Peg extends GameComponents implements TouchEffect{
    private boolean isOn; // Check if a peg is still alive to be drawn on screen

    /**
     * Super constructor
     * @param x         x-location
     * @param y         y-location
     * @param imageSrc  Corresponding image file
     */
    public Peg(double x, double y, String imageSrc) {
        super(x,y,imageSrc);
        this.isOn = true;
    }

    /**
     * Getter to check if peg is still alive to be drawn on screen
     * @return      false if peg is destroyed already
     *              true otherwise
     */
    public boolean getIsOn(){
        return this.isOn;
    }

    /**
     * Setter of peg's alive status
     * @param change    The change of status
     */
    public void setIsOn(boolean change){
        this.isOn = change;
    }

    @Override
    public void disappear() {
        this.setIsOn(false);
    }

    @Override
    public void effect(Ball ball) {
        // A default peg will make the ball bounce off from where it intersected
        Side side = getIntersectionSide(ball, this.bound);

        switch (side){
            case LEFT:
            case RIGHT:
                ball.setDx(-ball.getDx());
                break;
            case TOP:
            case BOTTOM:
                ball.setDy(-(ball.getDy()+ball.getV()));
                /* since this is not a physics assignment, I'll just assume
                 the new velocity as the opposite of total velocity*/
                ball.setV(Ball.DEFAULT_V);// restart the velocity again
                break;
            default:
                break;
        }
    }

    /**
     * Getting the side of intersection between a ball and a peg
     * @param ball      The intersecting ball
     * @param peg       The intersecting peg
     * @return          The side of the peg's bound that the ball's bound intersected on
     */
    public Side getIntersectionSide(Ball ball, Rectangle peg){
        // Laser ball don't bounce
        if (ball.isLaser()){
            return Side.NONE;
        }
        // Get the moving vector of ball for more precise result
        Vector2 vector = new Vector2(ball.getDx(), ball.getDy()+ball.getV());
        //Checking which corner of the ball bound intersects the peg bound. This will give a more precise result
            if (peg.intersects(ball.bound.bottomLeft())){
                return peg.intersectedAt(ball.bound.bottomLeft(), vector);
            }
            if (peg.intersects(ball.bound.bottomRight())){
                return peg.intersectedAt(ball.bound.bottomRight(), vector);
            }
            if (peg.intersects(ball.bound.topLeft())){
                return peg.intersectedAt(ball.bound.topLeft(), vector);
            }
            if (peg.intersects(ball.bound.topRight())){
                return peg.intersectedAt(ball.bound.topRight(), vector);
            }
        return Side.NONE;
    }
}
