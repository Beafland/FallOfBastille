package com.github.beafland.fallofbastille.character;

import com.github.beafland.fallofbastille.Game;
import javafx.scene.input.KeyCode;

import java.util.Set;

public class PlayerController {
    private final Player player;
    private static final int SPEED = 4;
    private static final double LERP_SPEED = 0.1;
    private double targetX; // 目标X位置
    private double targetY; // 目标Y位置

    public PlayerController(Player player) {
        this.player = player;
        this.targetX = player.getX();
        this.targetY = player.getY();
    }

    public void update(Set<KeyCode> keysPressed) {
        player.setMovement(!keysPressed.isEmpty());

        for(KeyCode keyCode : keysPressed) {
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
//                case SPACE -> fire();
            }
        }


        player.setX(targetX + (targetX - player.getX()) * LERP_SPEED);
        player.setY(targetY + (targetY - player.getY()) * LERP_SPEED);
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
