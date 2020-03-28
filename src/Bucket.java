import bagel.Input;
import bagel.Window;

/**
 * The bucket that gives 1 more shot every time a ball hits it
 */
public class Bucket extends GameComponents implements Movable{
    private static final double SPEED = 4;
    private static final double Y_DISTANCE = 24; // Distance from screen bottom
    private double dx = -SPEED;
    private double dy = 0;

    /**
     * Constructor that sets the bucket to its default position
     */
    public Bucket() {
        super(Window.getWidth()/2, Window.getHeight()-Y_DISTANCE,"res/bucket.png");
    }

    /**
     * When a ball intersects the bucket, it can be intersected for many frames, but the bucket is only activated once
     * This function will set the availability of the bucket to the chosen ball
     * @param available     The availability to be set
     * @param ball          The ball being set on
     */
    public void setAvailable(boolean available, Ball ball) {
        ball.setBucketActivated(available);
    }

    @Override
    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }


    @Override
    public void setNewDirection() {
        // Change direction of horizontal movement if bucket reaches window sides
        if (this.isTouchSides()) {
            dx = -dx;
        }
    }

    @Override
    public boolean isTouchSides(){
        // Boolean value to check if the bucket touches 2 sides of frame
        return (this.bound.left()<0) || (this.bound.right()>Window.getWidth());
    }

    @Override
    public boolean isOutBound(){
        // Boolean value to check if the bucket is fallen out of the frame, temporary implementation
        return (this.getY()<0) || (this.getY()>Window.getHeight());
    }

    @Override
    public void update(Input input){

        // In-bound check and direction change if necessary
        this.setNewDirection();

        // Now move !
        this.move(dx, dy);
        super.render();
    }

}
