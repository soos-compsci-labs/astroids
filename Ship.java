import java.awt.*;
import java.util.ArrayList;

public class Ship extends Polygon {
    private final ArrayList<Missile> missiles = new ArrayList<Missile>();
    private final Point pull = new Point(0, 0);

    // forwards booster and backwards booster
    private int fbAnimationDuration = 0;
    private boolean fbOn = false;
    private int bBAnimationDuration = 0;
    private boolean bBOn = false;

    private boolean shootCooldown = false;
    private int shootCooldownDuration = 0;


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
        if (shootCooldown) {
            return;
        }

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

        shootCooldown = true;
        shootCooldownDuration = 20;

        // recoil
        // accelerate without triggering the booster
        pull.x -= (1 * Math.cos(Math.toRadians(rotation + 270)));
        pull.y -= (1 * Math.sin(Math.toRadians(rotation + 270)));
    }

    public ArrayList<Missile> getMissiles() {
        return missiles;
    }

    public void removeMissile(Missile missile) {
        missiles.remove(missile);
    }

    public void paint(Graphics brush) {
        // shoot cool down
        if (shootCooldown) {
            shootCooldownDuration--;
            if (shootCooldownDuration == 0) {
                shootCooldown = false;
            }
        }


        if (fbOn) {
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
            // draw the booster
            boosterPoly.paint(brush);

            fbAnimationDuration--;
            if (fbAnimationDuration == 0) {
                fbOn = false;
            }
        }

        if (bBOn) {
            // calculate the position of the booster based on the ship's position and orientation
            double boosterX = position.x - (Math.sin(Math.toRadians(rotation)) * -10);
            double boosterY = position.y + (Math.cos(Math.toRadians(rotation)) * -10);
            // same as above but with the opposite rotation, and a little bit smaller
            Point[] booster = {
                    new Point(boosterX, boosterY),
                    new Point(boosterX + 10, boosterY - 10),
                    new Point(boosterX + 5, boosterY),
                    new Point(boosterX + 10, boosterY + 10)
            };
            // create new polygon for the booster
            Polygon boosterPoly = new Polygon(booster, new Point(boosterX, boosterY), rotation - 90);
            // draw the booster
            boosterPoly.paint(brush);

            bBAnimationDuration--;
            if (bBAnimationDuration == 0) {
                bBOn = false;
            }
        }


        for (Missile missile : missiles) missile.paint(brush);
        super.paint(brush);
    }


    public void accelerate(double acceleration) {
        // max speed is 10
        if (Math.abs(pull.x) > 10 || Math.abs(pull.y) > 10) {
            return;
        }


        pull.x += (acceleration * Math.cos(Math.toRadians(rotation + 270)));
        pull.y += (acceleration * Math.sin(Math.toRadians(rotation + 270)));


        if (acceleration > 0) {
            fbOn = true;
            fbAnimationDuration = 20;
        } else if (acceleration < 0) {
            bBOn = true;
            bBAnimationDuration = 20;
        }
    }

    // what should I name the method that is called every frame to apply the acceleration?
    public void move() {
        pull.x *= 0.999;
        pull.y *= 0.999;

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
