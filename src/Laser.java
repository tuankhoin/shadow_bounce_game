import bagel.Image;
import bagel.Window;

/**
 * Laser that makes ball penetrable for the destructible pegs
 */
public class Laser extends PowerUp {
    /**
     * Constructor
     */
    public Laser(){
        super();
        this.image = new Image("res/laser.png");
    }

    @Override
    public void effect(Ball ball) {
        //Setting the laser ball and disappears
        ball.setLaser(true);
        this.disappear();
    }
}
