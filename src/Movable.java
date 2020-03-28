/**
 * Interfaces for objects that can move in the game. All of them have bounds which will change the
 * moving direction when exceeded.
 */
public interface Movable {
    public void move(double dx, double dy); //Moving function
    public void setNewDirection(); //New direction set for the object
    public boolean isOutBound(); // Check if object is off its screen bound
    public boolean isTouchSides(); // Check if object is off its side bounds
}
