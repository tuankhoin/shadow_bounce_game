import bagel.Image;
import bagel.Window;

/**
 * Dark matter that absorbs ball when touched. Appears once every turn
 */
public class Dark extends PowerUp {
    /**
     * Constructor
     */
    public Dark(){
        super();
        this.setIsOn(true);
        this.image = new Image("res/dark.png");
    }

    @Override
    public void effect(Ball ball) {
        //Setting the ball off screen and disappears
        ball.setY(Window.getHeight());
        this.disappear();
    }
}
