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
    final static int ASTEROID_SPEED = 1;
    final static int MISSILE_SPEED = 3;
    final static double SHIP_SPEED = 0.4;
    final static double ROTATION_SPEED = 5;
    final static int SCREEN_WIDTH = 1000;
    final static int SCREEN_HEIGHT = 750;
    //AP CS - Once you have created your Ship class, switch theShip from a Polygon to a Ship variable
    private final Ship ship;
    private final ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
    private final Color[] titleCardColors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA};
    private final Color[] infoCardColors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA};
    protected boolean on = true;
    //private Ship theShip;
    //APCS - Once you have created your Asteroid class, uncomment this section
    //private ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
    protected int width, height;
    protected Image buffer;
    private int score = 0;
    private int lives = 3;
    private int infoCardColor = 0;
    // int for title card height, set to bottom of screen
    private Point titleCardLocation = new Point((double) Game.SCREEN_WIDTH / 2, (double) Game.SCREEN_HEIGHT / 2);
    private int titleCardDirection = 75;
    private int titleCardColor = 0;
    private Point infoCardLocation = new Point((double) Game.SCREEN_WIDTH / 2, (double) Game.SCREEN_HEIGHT / 2);
    private int infoCardDirection = 75 + 180;
    private boolean titleScreen = true;
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


    public void drawUI(Graphics brush) {
        // draw the score
        brush.setColor(Color.LIGHT_GRAY);
        brush.setFont(new Font("Arial", Font.BOLD, 30));
        String titleText = "Score: " + score;
        brush.drawString(titleText, 10, 50);

        Point[] shipShape = {
                new Point(10, 10),
                new Point(0, 25),
                new Point(0, 35),
                new Point(20, 35),
                new Point(20, 25)
        };

        // draw the lives
        for (int i = 0; i < lives; i++) {
            // draw the ships on the top left corner of the screen, and increment the x position by 30 each time
            brush.setColor(Color.WHITE);
            Polygon outline = new Polygon(shipShape, new Point(25 + (i * 30), 80), 0);
            outline.setColor(Color.WHITE);
            outline.setFill(false);
            outline.paint(brush);
        }
    }

    public void paint(Graphics brush) {

        // game over screen
        if (lives <= 0) {
            brush.setColor(Color.BLACK);
            brush.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            brush.setColor(Color.WHITE);
            brush.setFont(new Font("Arial", Font.BOLD, 30));
            String titleText = "Game Over";
            brush.drawString(titleText, 10, 50);
            brush.setFont(new Font("Arial", Font.BOLD, 20));
            String titleText2 = "Score: " + score;
            brush.drawString(titleText2, 10, 100);
            // escape to quit
            brush.setFont(new Font("Arial", Font.BOLD, 20));
            String titleText3 = "Press escape to quit";
            brush.drawString(titleText3, 10, 150);

            return;
        }


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
            if (asteroid.collides(ship) && !titleScreen) {
                // remove the asteroid
                asteroids.remove(asteroid);
                // reset the ship
                ship.reset();
                // decrement the lives
                lives--;
            }

            // check for collisions between the missiles and the asteroids

            // loop backwards through the missiles
            for (int j = ship.getMissiles().size() - 1; j >= 0; j--) {
                if (asteroid.collides(ship.getMissiles().get(j))) {
                    // remove the missile and the asteroid
                    ship.removeMissile(ship.getMissiles().get(j));
                    asteroids.remove(asteroid);
                    // add a new asteroid
                    asteroids.add(generateAsteroid());
                    // increment the score, based on the size of the asteroid
                    // the smaller the asteroid, the more points it's worth
                    double area = asteroid.findArea();
                    score += (int) (500 / area);
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

        if (titleScreen) {
            // add grey rectangle to cover screen
            brush.setColor(new Color(0, 0, 0, 150));
            brush.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            // draw title card, and bounce it around the screen like a dvd screen saver

            // draw title card
            brush.setColor(titleCardColors[titleCardColor]);
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
                // change color
                titleCardColor++;
                if (titleCardColor >= titleCardColors.length) {
                    titleCardColor = 0;
                }
            } else if (newPos.x < textWidth / 2) {
                newPos = new Point(textWidth / 2 + 1, newPos.y);
                titleCardDirection = -titleCardDirection;
                titleCardColor++;
                if (titleCardColor >= titleCardColors.length) {
                    titleCardColor = 0;
                }
            } else if (newPos.y > Game.SCREEN_HEIGHT - textHeight / 2) {
                newPos = new Point(newPos.x, Game.SCREEN_HEIGHT - textHeight / 2 - 1);
                titleCardDirection = 180 - titleCardDirection;
                titleCardColor++;
                if (titleCardColor >= titleCardColors.length) {
                    titleCardColor = 0;
                }
            } else if (newPos.y < textHeight / 2) {
                newPos = new Point(newPos.x, textHeight / 2 + 1);
                titleCardDirection = 180 - titleCardDirection;
                titleCardColor++;
                if (titleCardColor >= titleCardColors.length) {
                    titleCardColor = 0;
                }
            }
            titleCardLocation = newPos;

            // do all the above again for the instructions
            brush.setColor(infoCardColors[infoCardColor]);
            brush.setFont(new Font("Arial", Font.BOLD, 20));
            String instructionsText = "Press any key to start";
            fontMetrics = brush.getFontMetrics();
            textWidth = fontMetrics.stringWidth(instructionsText);
            textHeight = fontMetrics.getHeight();
            brush.drawString(instructionsText, (int) (infoCardLocation.x - textWidth / 2), (int) (infoCardLocation.y + textHeight / 2));

            // move the instructions
            newPos = new Point(infoCardLocation.x + (5 * Math.sin(Math.toRadians(infoCardDirection))),
                    infoCardLocation.y - (5 * Math.cos(Math.toRadians(infoCardDirection))));
            if (newPos.x > Game.SCREEN_WIDTH - textWidth / 2) {
                newPos = new Point(Game.SCREEN_WIDTH - textWidth / 2 - 1, newPos.y);
                infoCardDirection = -infoCardDirection;
                infoCardColor++;
            } else if (newPos.x < textWidth / 2) {
                newPos = new Point(textWidth / 2 + 1, newPos.y);
                infoCardDirection = -infoCardDirection;
                infoCardColor++;
            } else if (newPos.y > Game.SCREEN_HEIGHT - textHeight / 2) {
                newPos = new Point(newPos.x, Game.SCREEN_HEIGHT - textHeight / 2 - 1);
                infoCardDirection = 180 - infoCardDirection;
                infoCardColor++;
            } else if (newPos.y < textHeight / 2) {
                newPos = new Point(newPos.x, textHeight / 2 + 1);
                infoCardDirection = 180 - infoCardDirection;
                infoCardColor++;
            }
            infoCardLocation = newPos;

        } else {
            drawUI(brush);
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
        if (titleScreen) {
            titleScreen = false;
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