package com.github.beafland.fallofbastille.character;

import com.github.beafland.fallofbastille.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Set;

public class PlayerController {
    private static final int SPEED = 5;
    private static final double LERP_SPEED = 0.1;
    private static final double GRAVITY = 0.5;
    private static final int JUMP_FORCE = 20;
    private final Player player;
    private final Timeline attackTimeline;
    private final Timeline fireTimeline;
    private double yVelocity;
    private int JumpingChange = 2;
    private boolean isAttack = false;

    public PlayerController(Player player) {
        this.player = player;
        this.yVelocity = 0;

        fireTimeline = new Timeline(new KeyFrame(Duration.seconds(0.05), e -> player.isFire = false));
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
                case SPACE -> fire();
            }
        }

        player.setMovement(keysPressed);

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
        if(yVelocity > 0) {
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
        player.setX(player.getX()-SPEED);
        if (player.getX() < 0) {
            player.setX(0);
        }
    }

    public void moveRight() {
        player.setX(player.getX()+SPEED);
        if (player.getX() + Player.getWIDTH() > Game.WIDTH) {
            player.setX(Game.WIDTH - Player.getWIDTH());
        }
    }

    public void Jump() {
        if(JumpingChange-- > 0)
            yVelocity = -1 * JUMP_FORCE;
    }
}
