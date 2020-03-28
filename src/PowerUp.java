import bagel.Input;
import bagel.Window;
import java.util.Random;

/**
 * The power up of the game that turns a ball fireball
 */
public class PowerUp extends GameComponents implements Movable, TouchEffect {
    private static final double SPEED = 3;
    private static final double CLOSE = 5;
    private static final double SQUARE = 2;
    private boolean isOn = false; // only true if the power up is in its appearance turn
    private double dx, dy;
    private double xEnd;
    private double yEnd;

    /**
     * Constructor
     */
    public PowerUp(){
        // The power up will appear from a random location
        super(new Random().nextDouble()* Window.getWidth(),
                new Random().nextDouble()* Window.getHeight(), "res/powerup.png");
        this.setNewDirection();
    }

    /**
     * Getter for isOn
     * @return      true if the power up is appearing on screen
     *              false otherwise
     */
    public boolean getIsOn(){
        return this.isOn;
    }

    /**
     * Set the appearance condition for the power up
     * @param change        The new condition to be set
     */
    public void setIsOn(boolean change){
        this.isOn = change;
    }

    @Override
    public void setNewDirection() {
        // (xEnd,yEnd) is the random chosen location where the power up move towards
        this.xEnd = new Random().nextDouble()* Window.getWidth();
        this.yEnd = new Random().nextDouble()* Window.getHeight();
        // Same mathematical method as ball speed calculation, see updateVelocity function in Ball.java for details
        double theta = Math.atan(Math.abs(yEnd-y)/Math.abs(xEnd-x));
        this.dx = SPEED * Math.cos(theta) * (Math.abs(xEnd-x)/(xEnd-x));
        this.dy = SPEED * Math.sin(theta) * (Math.abs(yEnd-y)/(yEnd-y));
    }

    @Override
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public boolean isOutBound() {
        return false;
    }

    @Override
    public boolean isTouchSides() {
        return false;
    }

    @Override
    public void effect(Ball ball) {
        //Setting the ball on fire and disappears
        ball.setOnFire(true);
        this.disappear();
    }

    @Override
    public void disappear() {
        this.setIsOn(false);
    }

    @Override
    public void update(Input input) {
        if (this.isOn){
            this.move(dx, dy);
            super.render();

            // When the power up is within 5 pixels of its destination, set a new destination
            if (Math.sqrt(Math.pow(x-xEnd,SQUARE)+Math.pow(y-yEnd,SQUARE))<=CLOSE){
                this.setNewDirection();
            }
        }
    }

}
