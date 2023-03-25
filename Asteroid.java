import java.awt.*;

public class Asteroid extends Polygon {
    public Asteroid(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
        super.setColor(Color.DARK_GRAY);
    }

}
