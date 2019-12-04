/**
 *
 * ShadowBounce
 * SWEN20003 Project 2B
 *
 * @author: Tuan Khoi Nguyen - 1025294
 *
 */

import bagel.*;
import bagel.Window;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 *  Operating game class
 */
public class ShadowBounce extends AbstractGame {

    private static final int INITIAL = 0;
    private static final int X_COL = 1; //.csv column order
    private static final int Y_COL = 2; //.csv column order
    private static final int RED_FRACTION = 5; // 1/5 of the blue pegs will become red
    private static final int NUM_LEVELS = 5; // number of levels. Exceeding this means that you've won the game
    private static final int TURNS = 20; // the default number of given turns
    private static final int INTERVAL = 10; // the power up's appearance turn interval
    private static final double DIAGONAL = 10*Math.cos(45); // the initial velocity of the bonus balls
    private Ball ball;
    private Bucket bucket;
    private Plate plate;
    private PowerUp powerUp;
    private Laser laser;
    private Dark dark;
    private ArrayList<Ball> bonusBall = new ArrayList<Ball>();
    private ArrayList<BluePeg> bluePeg = new ArrayList<BluePeg>();
    private ArrayList<String> bluePegType = new ArrayList<String>();
    private ArrayList<RedGreenPeg> redPeg = new ArrayList<RedGreenPeg>();
    private ArrayList<GreyPeg> greyPeg = new ArrayList<GreyPeg>();
    private RedGreenPeg greenPeg;
    private int numRedPeg;
    private int numTurns; // number of turns available, to determine your win-lose status
    private int numTurnsPassed; // number of actual turns played, used for getting power up appearing turn
    private int powerUpTurn; // the turn that the power up will appear
    private int laserTurn;  // the turn that the laser will appear
    private int level; // the level you are in

    /**
     * Constructor
     */
    public ShadowBounce() {
        this.level = INITIAL;
        generate(level);
    }

    /**
     *  Main method
     */
    public static void main(String[] args) {
        ShadowBounce game = new ShadowBounce();
        game.run();
    }

    /**
     *  Use for generating a new level
     * @param level Number of the generated level
     */
    private void generate(int level){

        // Restarting the attributes
        this.numTurns = TURNS;
        this.numTurnsPassed = INITIAL;
        bonusBall = new ArrayList<Ball>();
        bluePeg = new ArrayList<BluePeg>();
        bluePegType = new ArrayList<String>();
        redPeg = new ArrayList<RedGreenPeg>();
        greyPeg = new ArrayList<GreyPeg>();

        // Generating pegs
        try (BufferedReader br =
                     new BufferedReader(new FileReader("res/"+ level +".csv"))) {

            String text;

            while ((text = br.readLine()) != null) {
                // .csv column separation
                String[] lin = text.split(",");
                // the initial column, split to see if the peg is grey
                String[] pegInfo = lin[INITIAL].split("_");
                if ("grey".equals(pegInfo[INITIAL])) {
                    greyPeg.add(new GreyPeg(Double.parseDouble(lin[X_COL]),
                            Double.parseDouble(lin[Y_COL]), "res/" + getImgName(lin[INITIAL]) + ".png"));
                } else { // in case of the default blue peg
                    bluePeg.add(new BluePeg(Double.parseDouble(lin[X_COL]),
                            Double.parseDouble(lin[Y_COL]), "res/" + getImgName(lin[INITIAL]) + ".png"));
                    bluePegType.add(lin[INITIAL]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // red pegs is a fifth of the blue pegs in number
        this.numRedPeg = bluePeg.size()/RED_FRACTION;

        // random seed for setting red and green pegs
        Random r = new Random();

        //Setting red pegs
        // the image of the peg will be stacked on its position. In other words, it's just coloring the given blue pegs
        int bluePos; //position of the blue peg being colored
        for(int i=0; i< bluePeg.size()/RED_FRACTION;i++){
            bluePos = r.nextInt(bluePeg.size()-1);
            // If the generated position is already a red peg, get another one until a new position is found
            while (bluePeg.get(bluePos).getRedPeg()){
                bluePos = r.nextInt(bluePeg.size()-1);
            }
            // now overriding the red color on it, and add a key which is the order of the colored blue peg
            redPeg.add(new RedGreenPeg(bluePos, bluePeg.get(bluePos).getX(), bluePeg.get(bluePos).getY(),
                    "res/red-" + getImgName(bluePegType.get(bluePos)) + ".png"));
            bluePeg.get(bluePos).setRedPeg(true);
        }

        System.out.println(bluePeg.size()-1);
        //Setting the green peg, same way as red peg
        bluePos = r.nextInt(bluePeg.size()-1);
        // If the green position clashes a red one, get another position until finding a suitable one
        while (bluePeg.get(bluePos).getRedPeg()){
            bluePos = r.nextInt(bluePeg.size()-1);
        }
        // coloring
        setGreenPeg(bluePos);

        ball = new Ball(Window.getWidth()/2, Window.getHeight(), INITIAL, INITIAL, false);
        bucket = new Bucket();
        powerUpTurn = r.nextInt(INTERVAL-1); // power up appears every 1/10 turns
        laserTurn = r.nextInt(INTERVAL-1); //same for laser
        powerUp = new PowerUp();
        dark = new Dark();
        laser = new Laser();
        plate = new Plate();
    }

    /**
     * The green peg coloring function, and add a key which is the order of the colored blue peg
     * @param key   The array index of the blue peg position that is turning green
     */
    public void setGreenPeg(int key){
        greenPeg = new RedGreenPeg(key, bluePeg.get(key).getX(), bluePeg.get(key).getY(),
                "res/green-" + getImgName(bluePegType.get(key)) + ".png");
    }

    @Override
    public void update(Input input) {

        // To make it easier to see how many lives you have left. If you don't like it, you can clear the 4 lines below
        // They will appear on top-left of screen
        Image lives = new Image("res/ball.png");
        for (int i=1; i <= numTurns; i++){
            lives.draw(lives.getWidth()*i, lives.getHeight()/2);
        }

        // Press Escape for fast exit
        if (input.isDown(Keys.ESCAPE)) {
            Window.close();
        }

        // CHEAT CODEs ;))
        if (input.isDown(Keys.RIGHT_CTRL) || input.isDown(Keys.LEFT_CTRL)){
            if (input.wasPressed(Keys.N)){
                    this.level++;
                    // If you have cleared all levels
                    if (this.level == NUM_LEVELS){
                        Window.close();
                        System.out.println("WINNER WINNER CHICKEN DINNER!!!");
                        return;
                    }
                    // If there are still further levels to be cleared, proceed to the next one
                    generate(this.level);
                    return;
            }
        }

        if(input.isDown(Keys.NUM_6)){
            if(input.wasPressed(Keys.NUM_9)){
                generate(69);
            }
        }


        bucket.update(input);
        plate.update(input);

        checkPowerUp();
        checkLaser();
        powerUp.update(input);
        laser.update(input);
        dark.update(input);

        ball.updateVelocity(input);
        ball.update(input);
        effectCheck(ball);

        // When all red peg have been destroyed, you finish a level
        if (numRedPeg == 0){
            this.level++;
            // If you have cleared all levels
            if (this.level == NUM_LEVELS){
                Window.close();
                System.out.println("WINNER WINNER CHICKEN DINNER!!!");
                return;
            }
            // If there are still further levels to be cleared, proceed to the next one
            generate(this.level);
            return;
        }

        //If there is bonus ball from activation of green peg on screen, make an update on it as well
        if (!bonusBall.isEmpty()){
            int i = 0;
            // Update loop for each ball
            while (i<bonusBall.size()){
                bonusBall.get(i).update(input);
                //Remove the bonus ball if it has fallen out of the screen
                if (bonusBall.get(i).getY() > Window.getHeight()){
                    bonusBall.remove(i);
                    // Size is reduced by 1, and so is the order, so no need for i++
                    // Plus, a fallen out ball definitely cannot do any effect, so skip to the next one
                    continue;
                }
                effectCheck(bonusBall.get(i));
                i++;
            }
        }

        // When all balls have fallen down, it is the end of turn
        if ((ball.getInTurn() && ball.isOutBound()) && bonusBall.isEmpty()){
            ball.setInTurn(false);
            newTurn();
            // turning off the power up or laser if its in its turn, which has ended
            if (powerUp.getIsOn()){
                powerUp.setIsOn(false);
            }
            if (laser.getIsOn()){
                laser.setIsOn(false);
            }
        }
    }

    /**
     * Making changes after each new turn beginning
     */
    public void newTurn(){
        numTurns--; // you lose a life
        numTurnsPassed++; // you entered a new turn
        ball.setBucketActivated(false); // if bucket is activated last turn, make it available for the new turn
        ball.setOnFire(false); // ball returns to normal in new turn
        ball.setLaser(false);
        dark.setIsOn(true);

        // If you ran out of balls, you lose
        if (this.numTurns == INITIAL){
            Window.close();
            System.out.println("NOOOOOOOB!!!!!!!!!");
            return;
        }

        // and if there are still blue pegs on screen, one of them will turn green next turn
        if(!isRedPegsOnly(bluePeg)){
            // Setting new position for the green peg
            Random r = new Random();
            int key = r.nextInt(bluePeg.size()-1);
            // the position must not be a red peg and must be available. If not, go on to find another suitable key
            while (bluePeg.get(key).getRedPeg() || !bluePeg.get(key).getIsOn()){
                key = r.nextInt(bluePeg.size()-1);
            }
            setGreenPeg(key);
        }

    }



    /**
     * For checking if there are blue pegs left on screen
     * @param peg   The peg array being checked
     * @return      true if there is only red pegs left on board
     *              false otherwise
     */
    public boolean isRedPegsOnly(ArrayList<BluePeg> peg){
        for (BluePeg value : peg) {
            if (value.getIsOn() && !value.getRedPeg()) {
                return false;
            }
        }
        // if there is no blue peg
        return true;
    }

    /**
     * Function that checks if it is time for the power up to show up
     */
    public void checkPowerUp(){
        // If a ball is on fire, it means that the ball has already touched the power up
        boolean isActivated = false;
        //Check if the bonus balls have touched the power up, if there is any
        if (!bonusBall.isEmpty()){
            for (Ball value : bonusBall) {
                if (value.getOnFire()) {
                    isActivated = true;
                    break;
                }
            }
        }

        // In its turn, the power up only turns on if untouched.
        // Also, the turn number should match the power up turn.
        if ((Math.floorMod(numTurnsPassed, INTERVAL)==powerUpTurn) && !ball.getOnFire() && !isActivated){
            powerUp.setIsOn(true);
        }
    }

    /**
     * Function that checks if it is time for the power up to show up, same logic as checkPowerUp
     */
    public void checkLaser(){
        boolean isActivated = false;
        if (!bonusBall.isEmpty()){
            for (Ball value : bonusBall) {
                if (value.isLaser()) {
                    isActivated = true;
                    break;
                }
            }
        }
        if ((Math.floorMod(numTurnsPassed, INTERVAL)==laserTurn) && !ball.isLaser() && !isActivated){
            laser.setIsOn(true);
        }
    }
    /**
     * Checking for effect activations of the chosen ball
     * @param ball      The ball being checked on
     */
    public void effectCheck(Ball ball){

        // Power up
        if (powerUp.intersects(ball) && powerUp.getIsOn()){
            powerUp.effect(ball);
        }

        if(dark.intersects(ball) && dark.getIsOn()){
            dark.effect(ball);
        }

        if(laser.intersects(ball) && laser.getIsOn()){
            laser.effect(ball);
        }

        // Bucket will only work once when being intersected, hence the ball.isBucketActivated boolean
        if (bucket.intersects(ball) && ball.getInTurn() && !ball.isBucketActivated()){
            numTurns++;
            bucket.setAvailable(true, ball);
        }

        if (plate.intersects(ball)){
            if(!ball.isPlateActivated){
                plate.effect(ball);
            }
        }
        else {
            ball.isPlateActivated = false;
        }

        // Pegs checking
        checkPeg(bluePeg, ball);
        checkGreyPeg(greyPeg,ball);
        checkRedGreenPeg(bluePeg, redPeg, greenPeg);
    }

    /**
     * This is used for checking default pegs with the chosen ball, which are blue ones
     * @param pegs      The peg array being checked
     * @param ball      The ball which the pegs will put effect on
     */
    public void checkPeg(ArrayList<BluePeg> pegs, Ball ball){
        double maxArea = INITIAL; // maximum area of the effective peg
        int effectKey = pegs.size(); // key of the peg that will bounce the ball
        for (int i = pegs.size() - 1; i >= 0; i--){
            // If ball ran into an existing peg
            if (pegs.get(i).getIsOn() && ball.intersects(pegs.get(i))) {
                // If peg is touched, it disappears
                pegs.get(i).disappear();
                // In case of a ball intersecting 2 pegs in a frame, choose the peg with higher intersecting area
                double area = areaOfIntersect(ball, pegs.get(i));
                if (area>maxArea){
                    maxArea = area;
                    effectKey = i;
                }
                // In case of a fireball. A defensive boolean is included in the function
                ball.fireBallDestruction(pegs, i);
            }
        }
        // If effectKey is still equals pegs.size(), it means that the ball did not touch any peg
        if (effectKey != pegs.size()){
            // Else, ball will bounce off the chosen peg
            pegs.get(effectKey).effect(ball);
        }
        // Drawing the alive pegs
        renderPegs(pegs);
    }

    /**
     * Use for checking the grey pegs effect on a ball.
     * @param pegs      The grey peg array
     * @param ball      The ball which effect will apply on
     */
    public void checkGreyPeg(ArrayList<GreyPeg> pegs, Ball ball){
        // Logic for checking of a ball intersecting 2 pegs is the same as blue peg
        double maxArea = INITIAL;
        int effectKey = pegs.size();
        for (int i = pegs.size() - 1; i >= 0; i--){
            // If ball ran into an existing peg
            if (ball.intersects(pegs.get(i))) {
                // See if the grey peg is already touched by the ball or not
                if (pegs.get(i).isInEffect()){
                    // If ball is untouched, now it will
                    if(ball.isTouchable()){
                        double area = areaOfIntersect(ball, pegs.get(i));
                        if (area>maxArea){
                            maxArea = area;
                            effectKey = i;
                        }
                    }

                }
            }
            // If pegs and ball does not touch each other, they should be available again
            else {
                pegs.get(i).setInEffect(true);
                ball.setTouchable(true);
            }
        }
        if (effectKey != pegs.size()){
            // The chosen peg will bounce the ball
            pegs.get(effectKey).effect(ball);
            // and peg is not available for bouncing until the ball no longer intersects it
            pegs.get(effectKey).setInEffect(false);
            ball.setTouchable(false);
        }
        // Drawing the pegs
        renderPegs(pegs);
    }

    /**
     * Check if there is any red/green peg position that is destroyed, then activating the corresponding effects
     * and draw the alive ones. Mentioning again that green and red pegs are overdrawn on a blue peg body
     * @param bluePeg       The blue peg array
     * @param redPeg        The red peg array
     * @param greenPeg      The green peg
     */
    public void checkRedGreenPeg(ArrayList<BluePeg> bluePeg, ArrayList<RedGreenPeg> redPeg, RedGreenPeg greenPeg){
        for (int i = bluePeg.size() - 1; i >= 0; i--){
            for (int j = redPeg.size() - 1; j >= 0; j--){
                // If a red peg position has just found to be destroyed
                if (redPeg.get(j).getIsOn() && (i==redPeg.get(j).getKey()) && !bluePeg.get(i).getIsOn()){
                    numRedPeg--;
                    redPeg.get(j).setIsOn(false);
                    // Matching key is found and carried out, so no need to check on other positions
                    break;
                }
            }

            // Draw the alive ones
            renderPegs(redPeg);

            // Same key checking procedure for green peg, but including effect activation
            if (greenPeg.getIsOn() && i==greenPeg.getKey() && !(bluePeg.get(i).getIsOn())){
                greenPeg.effect(ball);
                // Adding the 2 bonus balls
                bonusBall.add(new Ball(ball.getX(), ball.getY(), DIAGONAL, -DIAGONAL, ball.getOnFire()));
                bonusBall.add(new Ball(ball.getX(), ball.getY(), -DIAGONAL, -DIAGONAL, ball.getOnFire()));
            }

            // Draw the green peg
            if (greenPeg.getIsOn()) {
                greenPeg.render();
            }
        }
    }

    /**
     * Drawing function for peg array lists
     * @param pegs      The array of pegs to be rendered
     */
    public void renderPegs(ArrayList<? extends Peg> pegs){
        for (int j = pegs.size() - 1; j >= 0; j--){
            // Only draw alive ones
            if (pegs.get(j).getIsOn()){
                pegs.get(j).render();
            }
        }
    }

    /**
     * Function to generate image name that matches the peg type
     * @param type      The String that represents peg type
     * @return          The String of the corresponding image name
     */
    public String getImgName(String type){
        final int SHAPE = 2;
        final int COLOR = 0;
        //type comes in the form of "color_peg_shape"
        //info[] will be color, peg, shape respectively
        String[] info = type.split("_");
        if (info[COLOR].equals("blue")) {
            //Default blue pegs doesn't have color in its image file name
            if (info.length==SHAPE) {
                // If the shape is default, which is round
                return "peg";
            } else {
                return info[SHAPE] + "-peg";
            }
        }

        //In case of other pegs, just reorder the type names as set
        else {
            if (info.length==SHAPE) {
                return info[COLOR] + "-peg";
            }
            else {
                return info[COLOR] + "-" + info[SHAPE] + "-peg";
            }

        }
    }

    /**
     * Getting the area of intersection between a chosen ball and a chosen peg by calculating area created by
     * two intersection corners
     * @param ball      The chosen ball
     * @param peg       The chosen peg
     * @return          The area of intersection between the two parameter's bounding rectangle
     */
    public double areaOfIntersect(Ball ball, Peg peg){
        Rectangle rect1 = ball.bound;
        Rectangle rect2 = peg.bound;
        if (rect2.intersects(rect1.topRight())){
            return area(rect2.bottomLeft(), rect1.topRight());
        }
        if (rect2.intersects(rect1.topLeft())){
            return area(rect2.bottomRight(), rect1.topLeft());
        }
        if (rect2.intersects(rect1.bottomRight())){
            return area(rect2.topLeft(), rect1.bottomRight());
        }
        if (rect2.intersects(rect1.bottomLeft())){
            return area(rect2.topRight(), rect1.bottomLeft());
        }
        return 0;
    }

    /**
     * Calculating area of a rectangle with 2 diagonal head point p1 and p2
     * @param p1        The first point
     * @param p2        The second point
     * @return          The area of rectangle with p1 and p2 being the 2 diagonal edges
     */
    public double area(Point p1, Point p2){
        return Math.abs(p1.x-p2.x)*Math.abs(p1.y-p2.y);
    }
}
