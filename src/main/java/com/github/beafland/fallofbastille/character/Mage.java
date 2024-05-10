package com.github.beafland.fallofbastille.character;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Set;

public class Mage extends Player {
    //player images
    private final Image playerStandImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/Mage/Mage.png")));
    private final Image attackImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/Mage/mageAttack.png")));
    private final Image useFireImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/Mage/mageFire.gif")));
    private Image playerCurrImage = playerStandImage;

    //initialize player
    private static PlayerController controller;
    private int SPEED = 4;
    private final int JUMP_FORCE = 18;
    private final HealthBarUI healthBar;
    private Player enemy;

    //Fireball components
    private FireBall fireball;
    private boolean isAttack = false;
    private boolean usedFireBall = false;
    private final Timeline attackTimeline;

    public Mage(int x, int y) {
        super(x, y, 100);
        controller = new PlayerController(this, SPEED, JUMP_FORCE);
        healthBar = new HealthBarUI(100);

        attackTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> isAttack = false));
        attackTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    public void update(Set<KeyCode> keysPressed) {
        for (KeyCode keyCode : keysPressed) {
            switch (keyCode) {
                case A -> {
                    controller.moveLeft();
                    setFacingLeft(false);
                }
                case D -> {
                    controller.moveRight();
                    setFacingLeft(true);
                }
                case G -> {
                    useFireBall();
                }
            }
        }
        //Change character status image
        if (keysPressed.contains(KeyCode.G) && !isAttack) setStatus(12);
        else setStatus(0);

        //update character position
        controller.update();

        //check fireball collision if used
        if (usedFireBall && fireball != null) checkCollison();
    }

    public void render(GraphicsContext gc) {
        setPlayerImage();

        if (!isFacingLeft()) {
            gc.save(); // Save the current canvas state
            gc.translate(getX() + WIDTH, getY()); // Move the drawing start point to the attack position
            gc.scale(-1, 1); // Horizontal Flip
            gc.drawImage(playerCurrImage, 0, 0, WIDTH, HEIGHT);
            gc.restore(); // Restore the canvas state to the most recent save point
        } else {
            // If facing left, draw normally
            gc.drawImage(playerCurrImage, getX(), getY(), WIDTH, HEIGHT);
        }

        healthBar.draw(gc, getHealth(), false);

        if (usedFireBall && fireball != null) {
            //Horizontal flip or not depending on the orientation of the player's character
            fireball.render(gc);
            if (fireball.getX() > 2000 || fireball.getX() < 0) {
                usedFireBall = false;
                fireball = null;
            }
        }
    }

    public void Jump() {
        controller.Jump();
    }

    public void useFireBall() {
        if(!isAttack && !usedFireBall) {
            //Slow Speed
            SPEED = 2;
            //create a new fireball if null
            //increase the fireball instead
            if (fireball == null) {
                fireball = new FireBall();
                fireball.fireBallIncrease();
            } else {
                fireball.fireBallIncrease();
            }
        }

    }

    public void FireBallRelease() {
        if (fireball != null) {
            if(!usedFireBall)
                fireball.release(getX(), getY(), isFacingLeft());

            //attack marks
            isAttack = true;
            usedFireBall = true;
            attackTimeline.play();
            //reset speed
            SPEED = 4;
        }
    }

    private void checkCollison() {
        double mechanX = enemy.getX(); // Get the x-coordinate of the mechan
        double mechanY = enemy.getY(); // Get the y-coordinate of the mechan

        double fireBallX = fireball.getX(); // Get the x-coordinate of the fireball
        double fireBallY = fireball.getY(); // Get the y-coordinate of the fireball

        // Check for overlap on the x-axis
        boolean overlapX = Math.abs(fireBallX - mechanX) < (fireball.getFireBallSize() + WIDTH / 2.0);

        // Check for overlap on the y-axis
        // (given that the board origin is in the upper left corner, the y-coordinate needs to be inverted)
        boolean overlapY = Math.abs(fireBallY - mechanY) < (fireball.getFireBallHeight() * 0.8 + HEIGHT) / 2;

        // Collisions occur if they overlap on both the x and y axes
        if (overlapX && overlapY) {
            // Calculate the damage done to the character
            enemy.damage((int) (fireball.getFireBallSize() * 0.1));
            usedFireBall = false;
            fireball = null;
        }
    }

    //updating character image
    private void setPlayerImage() {
        switch (getStatus()) {
            case 0 -> playerCurrImage = playerStandImage;
            case 11 -> playerCurrImage = attackImage;
            case 12 -> playerCurrImage = useFireImage;
        }
    }
}
