package com.github.beafland.fallofbastille.character;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Set;

public class Mechan extends Player{
    //player images
    private final Image playerStandImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanician.png")));
    private final Image playerMoveImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanicianMove.gif")));
    private final Image playerJumpImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanicianJump.gif")));
    private Image playerCurrImage = playerStandImage;
    private final Image gunFire = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/gunFire.png")));

    //initialize player
    private static PlayerController controller;
    private final int attackRange = 150;
    private final int SPEED = 5;
    private final int JUMP_FORCE = 20;
    private final HealthBarUI healthBar;
    private Player enemy;

    //Attack component
    private boolean isAttack = false;
    private final Timeline attackTimeline;
    private final Timeline fireTimeline;

    public Mechan(int x, int y) {
        super(x, y, 100);
        controller = new PlayerController(this, SPEED, JUMP_FORCE);
        healthBar = new HealthBarUI(100);

        fireTimeline = new Timeline(new KeyFrame(Duration.seconds(0.05), e -> this.setFire(false)));
        attackTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> isAttack = false));
        attackTimeline.setCycleCount(Timeline.INDEFINITE);
        fireTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    public void update(Set<KeyCode> keysPressed){
        for (KeyCode keyCode : keysPressed) {
            switch (keyCode) {
                case LEFT -> {
                    controller.moveLeft();
                    setFacingLeft(true);
                }
                case RIGHT -> {
                    controller.moveRight();
                    setFacingLeft(false);
                }
                case SPACE -> fire();
            }
        }

        //Change character status image
        if(keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.RIGHT))
            setStatus(1);
        else
            setStatus(0);

        //update character position
        controller.update();
    }

    public void render(GraphicsContext gc) {
        setPlayerImage();

        if (!isFacingLeft()) {
            gc.save(); // Save the current canvas state
            gc.translate(getX() + WIDTH, getY()); // Shifts the drawing start point to the right by the width of the image.
            gc.scale(-1, 1); // Horizontal Flip
            gc.drawImage(playerCurrImage, 0, 0, WIDTH, HEIGHT); // Drawing from a new origin
            gc.restore(); // Restore the canvas state to the most recent save point
        } else {
            // If facing left, draw normally
            gc.drawImage(playerCurrImage, getX(), getY(), WIDTH, HEIGHT);
        }

        if (isFire()) {
            // Horizontal flip or not depending on the orientation of the player's character
            if (!isFacingLeft()) {
                gc.save(); // Save the current canvas state
                gc.translate(getX() + attackRange + WIDTH, getY()- attackRange * 0.1 + HEIGHT / 2.0); // Move the drawing start point to the attack position
                gc.scale(-1, 1); // Horizontal Flip
                gc.drawImage(gunFire, 0, 0, attackRange, attackRange * 0.25); // Drawing from a new origin
                gc.restore(); // Restore the canvas state to the most recent save point
            } else {
                gc.drawImage(gunFire, getX() - attackRange, getY() - attackRange * 0.1 + HEIGHT / 2.0, attackRange, attackRange * 0.25);
            }
        }

        healthBar.draw(gc, getHealth(), true);
    }

    public void Jump(){
        controller.Jump();
    }

    private void fire() {
        if (!isAttack) {
            this.setFire(true);
            isAttack = true;

            checkFireCollision();

            //Attack Aftershock
            attackTimeline.play();
            fireTimeline.play();
        }
    }

    private void checkFireCollision(){
        double gunFireX = getX(); // x-coordinate of gun fire

        double mageX = enemy.getX(); // Get the x-coordinate of the mage
        double mageY = enemy.getY(); // Get the y-coordinate of the mage


        if (!isFacingLeft()) {
            // If the character is facing right, the x-coordinate of the gun fire
            // is the character's x-coordinate plus the attack range.
            gunFireX += attackRange;
        } else {
            // If the character is facing left, the x-coordinate of gun fire
            // is the character's x-coordinate minus the range of the attack.
            gunFireX -= attackRange;
        }

        // Check that the x-coordinate of the gun fire does not overlap with
        // the x-coordinate of the mage, taking into account the width of both.
        boolean overlapX = Math.abs(gunFireX - mageX) < (attackRange + (getWIDTH() * 0.6)) / 2;

        // Check for overlap on the y-axis
        // (given that the board origin is in the upper left corner, the y-coordinate needs to be inverted)
        boolean overlapY = Math.abs(mageY - getY()) < (attackRange * 0.1 + HEIGHT) / 2;

        // Collisions occur if they overlap on both the x and y axes
        if (overlapX && overlapY) {
            // Calculate the damage done to the character
            enemy.damage(20);
            System.out.println("hit!");
        }
    }

    private void setPlayerImage() {
        switch(getStatus()){
            case 0 -> playerCurrImage = playerStandImage;
            case 1 -> playerCurrImage = playerMoveImage;
            case 2 -> playerCurrImage = playerJumpImage;
        }
    }

}
