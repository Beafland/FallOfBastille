package com.github.beafland.fallofbastille.character;

import com.github.beafland.fallofbastille.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Set;

public class PlayerController {
    private static final int SPEED = 4;
    private static final double LERP_SPEED = 0.1;
    private final Player player;
    private final Timeline attackTimeline;
    private final Timeline fireTimeline;
    private double targetX; // 目标X位置
    private double targetY; // 目标Y位置
    private boolean isAttack = false;

    public PlayerController(Player player) {
        this.player = player;
        this.targetX = player.getX();
        this.targetY = player.getY();

        fireTimeline = new Timeline(new KeyFrame(Duration.seconds(0.2), e -> player.isFire = false));
        attackTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> isAttack = false));
        attackTimeline.setCycleCount(Timeline.INDEFINITE);
        fireTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void update(Set<KeyCode> keysPressed) {
        for (KeyCode keyCode : keysPressed) {
            switch (keyCode) {
                case LEFT -> {
                    moveLeft();
                    player.facingLeft = true;
                }
                case RIGHT -> {
                    moveRight();
                    player.facingLeft = false;
                }
                case UP -> moveUp();
                case DOWN -> moveDown();

                case SPACE -> fire();
            }
        }

        player.setMovement(targetY - player.getY() + targetX - player.getX() != 0);
        player.setX(targetX + (targetX - player.getX()) * LERP_SPEED);
        player.setY(targetY + (targetY - player.getY()) * LERP_SPEED);
    }

    public void fire() {
        if (!isAttack) {
            player.isFire = true;
            isAttack = true;
            System.out.println("Fire!");
            attackTimeline.play();
            fireTimeline.play();
        }
    }

    public void moveLeft() {
        targetX -= SPEED;
        if (targetX < 0) {
            targetX = 0;
        }
    }

    public void moveRight() {
        targetX += SPEED;
        if (targetX + Player.getWIDTH() > Game.WIDTH) {
            targetX = Game.WIDTH - Player.getWIDTH();
        }
    }

    public void moveDown() {
        targetY += SPEED;
        if (targetY + Player.getHEIGHT() > Game.HEIGHT) {
            targetY = Game.HEIGHT - Player.getHEIGHT();
        }
    }

    public void moveUp() {
        targetY -= SPEED;
        if (targetY < 0) {
            targetY = 0;
        }
    }
}
