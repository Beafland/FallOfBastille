package com.github.beafland.fallofbastille.character;

import com.github.beafland.fallofbastille.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Set;

public class PlayerController {
    private static final double GRAVITY = 0.4;
    private final Player player;
    private final Timeline attackTimeline;
    private final Timeline fireTimeline;

    private final int SPEED;
    private final int JUMP_FORCE;
    private double yVelocity = 0;
    private int JumpingChange = 2;
    private boolean isAttack = false;

    public PlayerController(Player player, int SPEED, int JUMP_FORCE) {
        this.player = player;
        this.SPEED = SPEED;
        this.JUMP_FORCE = JUMP_FORCE;

        fireTimeline = new Timeline(new KeyFrame(Duration.seconds(0.05), e -> player.setFire(false)));
        attackTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> isAttack = false));
        attackTimeline.setCycleCount(Timeline.INDEFINITE);
        fireTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void update(Set<KeyCode> keysPressed) {

        moveStatus(keysPressed);

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

    private void moveStatus(Set<KeyCode> keysPressed) {
        if (yVelocity != 0) {
            player.setStatus(2);
        } else if (keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.RIGHT)){
            player.setStatus(1);
        } else {
            player.setStatus(0);
        }
    }

    public void fire() {
        if (!isAttack) {
            player.setFire(true);
            isAttack = true;
            System.out.println("Fire!");
            player.setHealth(player.getHealth() - 20);
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
