package com.github.beafland.fallofbastille.character;

import com.github.beafland.fallofbastille.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Set;

public class BotController {
    private static final int SPEED = 4;
    private static final double LERP_SPEED = 0.1;
    private final Bot bot;
    private final Timeline attackTimeline;
    private double targetX; // 目标X位置
    private double targetY; // 目标Y位置
    private boolean isAttack = false;

    public BotController(Bot bot) {
        this.bot = bot;
        this.targetX = bot.getX();
        this.targetY = bot.getY();

        attackTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> isAttack = false));
        attackTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void update() {
        bot.setX(targetX + (targetX - bot.getX()) * LERP_SPEED);
        bot.setY(targetY + (targetY - bot.getY()) * LERP_SPEED);
    }

    public void fire() {
        if (!isAttack) {
            isAttack = true;
            System.out.println("Fire!");
            attackTimeline.play();
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
