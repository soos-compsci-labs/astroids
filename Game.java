/*
CLASS: Game
DESCRIPTION:  Game is mostly in the paint method.
NOTE: This class is the metaphorical "main method" of your program,
      it is your control center.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Game extends Canvas implements KeyListener {

    //Game settings
    final static int INIT_ASTEROIDS = 15;
    final static int ASTEROID_SPEED = 3;
    final static int MISSILE_SPEED = 1;
    final static int SHIP_SPEED = 3;
    final static int ROTATION_SPEED = 8;
    final static int MAX_LIVES = 3;
    final static int FREE_LIFE_THRESHOLD = 3;
    final static int SCREEN_WIDTH = 1000;
    final static int SCREEN_HEIGHT = 750;
    //APCS - Once you have created your Ship class, switch theShip from a Polygon to a Ship variable
    private final Ship ship;
    private final int collisionCt = 0;
    private final int newCollisionWith = -1;
    private final ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
    protected boolean on = true;
    //private Ship theShip;
    //APCS - Once you have created your Asteroid class, uncomment this section
    //private ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
    protected int width, height;
    protected Image buffer;
    // int for title card height, set to bottom of screen
    private Point titleCardLocation = new Point((double) Game.SCREEN_WIDTH / 2, (double) Game.SCREEN_HEIGHT / 2);
    private int titleCardDirection = 75;
    private boolean titleCard = true;
    private BufferStrategy strategy;
    // variables to keep track of what keys are pressed
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean spacePressed = false;

    public Game() {

        //Code from Game that creates the window for the display
        JFrame frame = new JFrame("Asteroids Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel contentPane = new JPanel();
        contentPane.setBackground(Color.BLACK);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(this);
        frame.setContentPane(contentPane);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setVisible(true);
        frame.setResizable(false);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });


        ship = new Ship();

        //Create some asteroids
        for (int i = 0; i < INIT_ASTEROIDS; i++) {
            Asteroid asteroid = generateAsteroid();
            asteroids.add(asteroid);
        }
        render();
        addKeyListener(this);

        int delay = 30; //milliseconds - started at 1000
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //...Perform a task...
                render();
            }
        };
        new Timer(delay, taskPerformer).start();

        requestFocusInWindow();
    }

    public static void main(String[] args) {
        new Game();

    }

    public void paint(Graphics brush) {

        // paint background to black
        brush.setColor(Color.BLACK);
        brush.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        // print number of missiles
        if (ship == null) return;

        ship.paint(brush);
        ship.move();


        // loop backwards through the asteroids
        for (int i = asteroids.size() - 1; i >= 0; i--) {
            Asteroid asteroid = asteroids.get(i);
            if (asteroid == null) continue;

            asteroid.move(ASTEROID_SPEED);
            asteroid.paint(brush);

            // check for collisions between the ship and the asteroids
            if (asteroid.collides(ship) && !titleCard) {
                // remove the asteroid
                asteroids.remove(asteroid);
                // reset the ship
                ship.reset();
            }

            // check for collisions between the missiles and the asteroids

            // loop backwards through the missiles
            for (int j = ship.getMissiles().size() - 1; j >= 0; j--) {
                if (asteroid.collides(ship.getMissiles().get(j))) {
                    // remove the missile and the asteroid
                    ship.removeMissile(ship.getMissiles().get(j));
                    asteroids.remove(asteroid);
                } else {
                    if (ship.getMissiles().get(j).isOutOfBounds()) {
                        ship.removeMissile(ship.getMissiles().get(j));
                        continue;
                    }
                    // move the missile
                    ship.getMissiles().get(j).move(MISSILE_SPEED);

                }
            }

        }


        // title card!

        if (titleCard) {
            // add grey rectangle to cover screen
            brush.setColor(new Color(0, 0, 0, 230));
            brush.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            // draw title card, and bounce it around the screen like a dvd screen saver

            // draw title card
            brush.setColor(Color.WHITE);
            brush.setFont(new Font("Arial", Font.BOLD, 50));
            String titleText = "ASTEROIDS";
            FontMetrics fontMetrics = brush.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(titleText);
            int textHeight = fontMetrics.getHeight();
            brush.drawString(titleText, (int) (titleCardLocation.x - textWidth / 2), (int) (titleCardLocation.y + textHeight / 2));

            // move title card
            Point newPos = new Point(titleCardLocation.x + (5 * Math.sin(Math.toRadians(titleCardDirection))),
                    titleCardLocation.y - (5 * Math.cos(Math.toRadians(titleCardDirection))));
            if (newPos.x > Game.SCREEN_WIDTH - textWidth / 2) {
                newPos = new Point(Game.SCREEN_WIDTH - textWidth / 2 - 1, newPos.y);
                titleCardDirection = -titleCardDirection;
            } else if (newPos.x < textWidth / 2) {
                newPos = new Point(textWidth / 2 + 1, newPos.y);
                titleCardDirection = -titleCardDirection;
            } else if (newPos.y > Game.SCREEN_HEIGHT - textHeight / 2) {
                newPos = new Point(newPos.x, Game.SCREEN_HEIGHT - textHeight / 2 - 1);
                titleCardDirection = 180 - titleCardDirection;
            } else if (newPos.y < textHeight / 2) {
                newPos = new Point(newPos.x, textHeight / 2 + 1);
                titleCardDirection = 180 - titleCardDirection;
            }
            titleCardLocation = newPos;

        }


        // move/rotate the ship based on the keys pressed
        if (leftPressed) {
            ship.rotate(-ROTATION_SPEED);
        }
        if (rightPressed) {
            ship.rotate(ROTATION_SPEED);
        }
        if (upPressed) {
            ship.accelerate(SHIP_SPEED);
        }
        if (downPressed) {
            ship.accelerate(-SHIP_SPEED);
        }
        if (spacePressed) {
            ship.shoot();
        }

    }

    public void render() {
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        Graphics g = null;
        do {
            try {
                g = strategy.getDrawGraphics();
            } finally {
                assert g != null;
                paint(g);
            }
            strategy.show();
            g.dispose();
        } while (strategy.contentsLost());
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
        if (titleCard) {
            titleCard = false;
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spacePressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spacePressed = false;
        }
    }

    public Asteroid generateAsteroid() {
        //Set up asteroid shapes - four different shapes randomly chosen.
        Point[] aShape;
        int numPts = (int) (Math.random() * 4) + 5;
        if (numPts == 5) {
            aShape = new Point[]{new Point(10, 10), new Point(5, 15), new Point(12, 20), new Point(19, 14), new Point(12, 13)};
        } else if (numPts == 6) {
            aShape = new Point[]{new Point(10, 10), new Point(22, 4), new Point(24, 18), new Point(33, 23), new Point(7, 38), new Point(15, 25)};
        } else if (numPts == 7) {
            aShape = new Point[]{new Point(3, 17), new Point(12, 2), new Point(22, 4), new Point(18, 13), new Point(25, 15), new Point(11, 31), new Point(12, 18)};
        } else {
            aShape = new Point[]{new Point(15, 5), new Point(24, 15), new Point(35, 10), new Point(28, 27), new Point(23, 45), new Point(17, 32), new Point(6, 30), new Point(14, 21)};
        }

        //Start the asteroid at a random location, but not within 100 of the center
        int xLoc = ((int) (Math.random() * (SCREEN_WIDTH / 2 - 109)) + 10) + ((SCREEN_WIDTH / 2 + 110) * ((int) (Math.random() * 2)));
        //Same for yLoc
        int yLoc = ((int) (Math.random() * (SCREEN_HEIGHT / 2 - 109)) + 10) + ((SCREEN_HEIGHT / 2 + 110) * ((int) (Math.random() * 2)));

        return new Asteroid(aShape, new Point(xLoc, yLoc), (int) (Math.random() * 360));
    }
}