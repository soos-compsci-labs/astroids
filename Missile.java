import java.awt.*;

public class Missile extends Polygon {

    public Missile(Point[] inShape, Point inPosition, double inRotation) {
        super(inShape, inPosition, inRotation);
        this.setWrap(false);
        this.setColor(Color.BLUE);
    }

    public boolean isOutOfBounds() {
        return position.x > Game.SCREEN_WIDTH || position.x < 0 || position.y > Game.SCREEN_HEIGHT || position.y < 0;
    }


}
