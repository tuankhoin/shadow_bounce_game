import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.Image;
import bagel.util.Vector2;

import java.util.ArrayList;

/**
 * The ball class of the game. It will destroy the pegs in the game
 */
public class Ball extends GameComponents implements Movable{
    private static final double DEFAULT_X = 512;
    private static final double DEFAULT_Y = 32;
    static final double DEFAULT_V = 0.15;
    private static final double SPEED = 10;
    private static final double RANGE = 70; // Fireball range
    private boolean isTouchable = true; // See if ball is able to be touch (has not touched a peg yet)
    private boolean isOnFire; // Check if ball is a fireball
    private boolean isLaser;
    private boolean isInTurn = false; // If false, it means that a turn has ended
    private boolean isBucketActivated = false; // This boolean is to check if bucket is activated for the ball
    boolean isPlateActivated = false; // Same use as isBucketActivated, but for the plate instead
    private double v;
    private double dx, dy;

    /**
     * Constructor
     * @param x         x-position
     * @param y         y-position
     * @param dx        x-coordinate of velocity vector
     * @param dy        y-coordinate of velocity vector, gravity not included
     * @param isOnFire  Boolean to check if ball is a fireball
     */
    public Ball(double x, double y, double dx, double dy, boolean isOnFire) {
        super(x, y, "res/ball.png");
        v = DEFAULT_V;
        this.dx = dx;
        this.dy = dy;
        this.setOnFire(isOnFire);
    }

    /**
     * x-velocity getter
     * @return      Velocity in x direction
     */
    public double getDx() {
        return dx;
    }

    /**
     * y-velocity getter
     * @return      Velocity in y direction
     */
    public double getDy() {
        return dy;
    }

    /**
     * Gravity getter
     * @return      Ball's gravity
     */
    public double getV() {
        return v;
    }

    /**
     * Setter of x-velocity
     * @param dx        Value dx to be set
     */
    public void setDx(double dx) {
        this.dx = dx;
    }

    /**
     * Setter of y-velocity
     * @param dy        Value dy to be set
     */
    public void setDy(double dy) {
        this.dy = dy;
    }

    /**
     * Setter of gravity velocity
     * @param v         Value v to be set
     */
    public void setV(double v) {
        this.v = v;
    }

    /**
     * Getter of isTouchable, to check if ball is simultaneously colliding any 2 pegs in the same frame
     * @return      true if ball is untouched yet
     *              false otherwise
     */
    public boolean isTouchable() {
        return isTouchable;
    }

    /**
     * Setter of ball's touchable status
     * @param touchable     The touchable status to be set
     */
    public void setTouchable(boolean touchable) {
        isTouchable = touchable;
    }

    /**
     * Getter of boolean that checks if ball is fireball
     * @return      true if ball is fireball
     *              false if not
     */
    public boolean getOnFire(){
        return this.isOnFire;
    }

    /**
     * Setter that either turn on or off the fireball mode
     * @param isOnFire      The fire status to be set
     */
    public void setOnFire(boolean isOnFire){
        this.isOnFire = isOnFire;
        // Make image changes where necessary
        if (this.isOnFire){
            this.image = new Image("res/fireball.png");
        }
        else {
            this.image = new Image("res/ball.png");
        }
    }

    public boolean isLaser() {
        return isLaser;
    }

    public void setLaser(boolean laser) {
        isLaser = laser;
        if (this.isLaser){
            this.image = new Image("res/laserball.png");
        }
        else {
            this.image = new Image("res/ball.png");
        }
    }

    /**
     * Getter of boolean that checks if ball is already activated by the bucket in its corresponding turn
     * @return      true if ball is already activated in the turn
     *              false otherwise
     */
    public boolean isBucketActivated() {
        return isBucketActivated;
    }

    /**
     * Setter of ball's bucket activation status
     * @param bucketActivated       The status to be set
     */
    public void setBucketActivated(boolean bucketActivated){
        this.isBucketActivated = bucketActivated;
    }

    /**
     * Getter to check if ball is visible in its turn
     * @return      true if ball is appearing in its turn
     *              false otherwise
     */
    public boolean getInTurn(){
        return this.isInTurn;
    }

    /**
     * Setter of ball's visibility in turn
     * @param isInTurn      Status to be set
     */
    public void setInTurn(boolean isInTurn){
        this.isInTurn = isInTurn;
    }

    @Override
    public void move(double dx, double dy) {
        // Moving function for the ball
        this.x += dx;
        this.y += dy;
        // Including gravity's velocity, adding 0.15 more to it each frame
        this.y += v;
        v += DEFAULT_V;
    }

    /**
     * Check if the ball is out of frame, if so restart the values before proceeding
     */
    public void restartRender(){
        if(this.isOutBound()){
            setNewDirection();
        }
    }

    /**
     * Function that destroy surrounding pegs in case of a fireball
     * @param pegs      The array of pegs on board
     * @param hit       The array index of the struck peg
     */
    public void fireBallDestruction(ArrayList<? extends Peg> pegs, int hit){
        // Only execute the code if ball is a fireball
        if (this.getOnFire()){
            for (int i = pegs.size() - 1; i >= 0; i--) {
                // vector from struck peg's center to the processed peg's center
                Vector2 vector = new Vector2(pegs.get(i).bound.centre().x-pegs.get(hit).bound.centre().x,
                        pegs.get(i).bound.centre().y-pegs.get(hit).bound.centre().y);
                // Check if the peg is within 70 pixels
                if (vector.length() <= RANGE){
                    pegs.get(i).disappear();
                }
            }
        }
    }

    @Override
    public void setNewDirection() {
        // Restarting a fallen out ball to default
        this.x=DEFAULT_X;
        this.y=DEFAULT_Y;
        this.v=DEFAULT_V;
    }

    @Override
    public boolean isTouchSides(){
        // Boolean value to check if the ball is on 2 sides of frame
        return (this.bound.left()<0) || (this.bound.right()>Window.getWidth());
    }


    @Override
    public boolean isOutBound(){
        // Boolean value to check if the ball is fallen out of the frame
        /* Note: Project 1 says that ball can be summoned as long as it is out of the frame, not specify if it is
            just being fallen down, so to make it more convenient to calculate turns, I assumed so, and made the boolean
            to be true only when the ball has fallen out.*/
        return this.getY()>Window.getHeight();
    }


    @Override
    public void update(Input input){

        // Change direction of horizontal movement if ball reaches window sides
        if (this.isTouchSides()) {
            this.dx = -dx;
        }

        //Bounce back if ball touches the top
        if (this.bound.top()<0){
            this.dy = -(dy+v);
            this.v = DEFAULT_V;
        }

        // Now move !
        this.move(dx, dy);
        super.render();
    }

    /**
     * Check if there is any click to retrieve the ball, and renew its velocity if there is
     * @param input Input to check for mouse-click
     */
    public void updateVelocity(Input input){
        // Calculating new velocity
        // Only do this if ball is out of frame
        if (input.wasPressed(MouseButtons.LEFT) && !this.isInTurn) {
            // In-bound check and restart if necessary
            this.restartRender();
            // Set a new turn (ball has fallen out)
            this.setInTurn(true);
            // A velocity vector is made of x and y components, followed by a theta angle of x axis and the vector
            // Some cool vector math
            double theta = Math.atan(Math.abs(input.getMouseY() - this.getY()) /
                    Math.abs(input.getMouseX() - this.getX()));

            // We will have the magnitude by getting x and y components of the speed
            // Then just need to check what direction the ball will go, by checking the sign of displacement
            // A sign of a number k can be found by abs(k)/k
            // Formula will be magnitude*sign_of_displacement

            dx = SPEED * Math.cos(theta) * Math.abs(input.getMouseX() - this.getX())/(input.getMouseX() - this.getX());
            dy = SPEED * Math.sin(theta) * Math.abs(input.getMouseY() - this.getY())/(input.getMouseY() - this.getY());


        }
    }
}

