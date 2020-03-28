import bagel.Input;
import bagel.Keys;
import bagel.Window;
import bagel.util.Vector2;

/**
 * Plate that bounces back, just like Atari Breakout
 */
public class Plate extends GameComponents implements Movable, TouchEffect{
    private static final double SPEED = 8;
    private static final double Y_DISTANCE = 60; // Distance from screen bottom
    private static final double DIAGONAL = 45;
    private static final double MID = 24;



    /**
     * Constructor that sets the bucket to its default position
     */
    public Plate(){
        super(Window.getWidth()/2, Window.getHeight()-Y_DISTANCE,"res/plate.png");
    }

    @Override
    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }


    @Override
    public void setNewDirection() {
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
        if(input.isDown(Keys.LEFT) && (this.bound.left()>0)){
            this.move(-SPEED, 0);
        }
        if(input.isDown(Keys.RIGHT) && (this.bound.right()<Window.getWidth())){
            this.move(SPEED, 0);
        }
        super.render();
    }

    @Override
    public void disappear() {

    }

    @Override
    public void effect(Ball ball) {
        double dis = ball.bound.centre().x-this.bound.centre().x;
        ball.isPlateActivated = true;

        //If ball falls into middle zone of plate
        if(Math.abs(dis)<=MID){
            ball.setDy(-(ball.getDy()+ball.getV()));
            ball.setV(Ball.DEFAULT_V);
        }
        //If not, ball will fall at a new velocity, same magnitude, but diagonally
        else {
            Vector2 vector = new Vector2(ball.getDx(), ball.getDy()+ball.getV());
            ball.setDx(dis/Math.abs(dis) * vector.length() * Math.cos(DIAGONAL));
            ball.setDy(-vector.length() * Math.sin(DIAGONAL));
            ball.setV(Ball.DEFAULT_V);
        }
    }
}
