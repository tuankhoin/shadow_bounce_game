
/**
 * Interface for objects that can put effect on the ball that touches them. Some of them may disappear after touch
 */
public interface TouchEffect {
    public void effect(Ball ball); // The effect that it has on the ball
    public void disappear(); // The disappearing effect if it has
}
