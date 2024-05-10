package com.github.beafland.fallofbastille.character;

import com.github.beafland.fallofbastille.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;


public class PlayerController {
    private static final double GRAVITY = 0.4;
    private final Player player;

    private final int SPEED;
    private final int JUMP_FORCE;
    private double yVelocity = 0;
    private int JumpingChange = 2;

    public PlayerController(Player player, int SPEED, int JUMP_FORCE) {
        this.player = player;
        this.SPEED = SPEED;
        this.JUMP_FORCE = JUMP_FORCE;
    }

    public void update() {

        // 应用重力
        yVelocity += GRAVITY;
        player.setY(player.getY() + yVelocity);

        // 地面碰撞检测
        if (player.getY() + Player.HEIGHT > Game.HEIGHT) {
            player.setY(Game.HEIGHT - Player.HEIGHT);
            yVelocity = 0;
            JumpingChange = 2;
        }

        // 平台碰撞检测
        int leftPlatformTopY = 650;
        int leftPlatformStartX = 0;
        int leftPlatformEndX = 500;
        int rightPlatformTopY = 380;
        int rightPlatformStartX = 1200;
        int rightPlatformEndX = 2000;

        // 检测玩家是否在平台的横向范围内
        if (yVelocity > 0) {
            if (player.getX() >= leftPlatformStartX && player.getX() <= leftPlatformEndX) {
                // 检测玩家是否触碰到平台的顶部
                if (player.getY() + Player.HEIGHT >= leftPlatformTopY && player.getY() + Player.HEIGHT - yVelocity <= leftPlatformTopY) { // 10为假定的厚度
                    player.setY(leftPlatformTopY - Player.HEIGHT);
                    yVelocity = 0;
                    JumpingChange = 2;
                }
            } else if (player.getX() >= rightPlatformStartX && player.getX() <= rightPlatformEndX) {
                // 检测玩家是否触碰到平台的顶部
                if (player.getY() + Player.HEIGHT >= rightPlatformTopY && player.getY() + Player.HEIGHT - yVelocity <= rightPlatformTopY) { // 10为假定的厚度
                    player.setY(rightPlatformTopY - Player.HEIGHT);
                    yVelocity = 0;
                    JumpingChange = 2;
                }
            }
        }

        //重置站姿
        if (yVelocity != 0) {
            player.setStatus(2);
        }

    }

    public void moveLeft() {
        player.setX(player.getX() - SPEED);
        if (player.getX() < 0) {
            player.setX(0);
        }
    }

    public void moveRight() {
        player.setX(player.getX() + SPEED);
        if (player.getX() + Player.getWIDTH() > Game.WIDTH) {
            player.setX(Game.WIDTH - Player.getWIDTH());
        }
    }

    public void Jump() {
        if (JumpingChange-- > 0) yVelocity = -1 * JUMP_FORCE;
    }
}
