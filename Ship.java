import java.awt.*;
import java.util.ArrayList;

public class Ship extends Polygon {
    private final ArrayList<Missile> missiles = new ArrayList<Missile>();
    private final Point pull = new Point(0, 0);
    private int boosterAnimation = 0;
    private boolean boosterOn = false;

    public Ship() {
        // use Game.SCREEN_WIDTH and Game.SCREEN_HEIGHT to set the initial position of the ship
        super(
                new Point[]{
                        new Point(10, 10),
                        new Point(0, 25),
                        new Point(0, 35),
                        new Point(20, 35),
                        new Point(20, 25)
                }
                , new Point((double) Game.SCREEN_WIDTH / 2, (double) Game.SCREEN_HEIGHT / 2), 0);
        // change color to black
        setColor(Color.WHITE);
        setFill(false);
    }

    public void reset() {
        // reset the position of the ship to the center of the screen
        position.x = (double) Game.SCREEN_WIDTH / 2;
        position.y = (double) Game.SCREEN_HEIGHT / 2;
        // reset the rotation of the ship to 0
        rotation = 0;
        // reset the pull of the ship to 0
        pull.x = 0;
        pull.y = 0;
    }

    public void shoot() {
//        if (missiles.size() >= 5) {
//            return;
//        }
        // create a new missile and add it to the list of missiles
        missiles.add(new Missile(new Point[]{
                new Point(0, 0),
                new Point(0, 5),
                new Point(5, 5),
                new Point(5, 0)
        }, new Point(position.x, position.y), rotation));
    }

    public ArrayList<Missile> getMissiles() {
        return missiles;
    }

    public void removeMissile(Missile missile) {
        missiles.remove(missile);
    }

    public void paint(Graphics brush) {
        if (boosterOn) {
            brush.setColor(Color.RED);
            // calculate the position of the booster based on the ship's position and orientation
            double boosterX = position.x - (Math.sin(Math.toRadians(rotation)) * 14);
            double boosterY = position.y + (Math.cos(Math.toRadians(rotation)) * 14);
            Point[] booster = {
                    new Point(boosterX, boosterY),
                    new Point(boosterX - 10, boosterY - 10),
                    new Point(boosterX - 5, boosterY),
                    new Point(boosterX - 10, boosterY + 10)
            };
            // create new polygon for the booster
            Polygon boosterPoly = new Polygon(booster, new Point(boosterX, boosterY), rotation - 90);
            boosterPoly.setColor(Color.RED);
            // draw the booster
            boosterPoly.paint(brush);

            boosterAnimation--;
            if (boosterAnimation == 0) {
                boosterOn = false;
            }
        }

        super.paint(brush);

        for (Missile missile : missiles) {
            missile.paint(brush);
        }
    }

    private int[] convertToYArray(Point[] points) {
        // return only the y values of the points in the array
        int[] yArray = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            yArray[i] = (int) points[i].y;
        }
        return yArray;
    }

    private int[] convertToXArray(Point[] points) {
        // return only the x values of the points in the array
        int[] xArray = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            xArray[i] = (int) points[i].x;
        }
        return xArray;
    }


    public void accelerate(double acceleration) {
        pull.x += (acceleration * Math.cos(Math.toRadians(rotation + 270)));
        pull.y += (acceleration * Math.sin(Math.toRadians(rotation + 270)));

        boosterOn = true;
        boosterAnimation = 10; // 10 frames (paint decrements this)
    }

    // what should I name the method that is called every frame to apply the acceleration?
    public void move() {
        pull.x *= 0.99;
        pull.y *= 0.99;

        Point newPos = new Point(position.x + pull.x, position.y + pull.y);

        if (newPos.x > Game.SCREEN_WIDTH) {
            newPos = new Point(newPos.x % Game.SCREEN_WIDTH, newPos.y);
        } else if (newPos.x < 0) {
            newPos = new Point(newPos.x + Game.SCREEN_WIDTH, newPos.y);
        } else if (newPos.y > Game.SCREEN_HEIGHT) {
            newPos = new Point(newPos.x, newPos.y % Game.SCREEN_HEIGHT);
        } else if (newPos.y < 0) {
            newPos = new Point(newPos.x, newPos.y + Game.SCREEN_HEIGHT);
        }
        position = newPos;
    }
}
