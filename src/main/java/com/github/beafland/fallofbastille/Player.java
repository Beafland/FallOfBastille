package com.github.beafland.fallofbastille;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.Objects;
import java.util.Set;

public class Player {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 100;
    private static final int SPEED = 5;
    private static final double LERP_SPEED = 0.1;

    private Image playerCurrImage;
    private static final Image playerStandImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechanician.png")));
    private static final Image playerMoveImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechanicianMove.gif")));
    private boolean facingLeft = false;

    private double targetX; // 目标X位置
    private double targetY; // 目标Y位置

    private int x;
    private int y;

    public Player(int x, int y) {
        this.targetX = this.x = x;
        this.targetY = this.y = y - HEIGHT/2;
        this.playerCurrImage = playerStandImage;
    }

    public void update(Set<KeyCode> keysPressed) {
        if(keysPressed.isEmpty())
            this.playerCurrImage = playerStandImage;
        else
            this.playerCurrImage = playerMoveImage;


        if (keysPressed.contains(KeyCode.LEFT)) {
            moveLeft();
            facingLeft = true;
        }
        if (keysPressed.contains(KeyCode.RIGHT)) {
            moveRight();
            facingLeft = false;
        }
        if (keysPressed.contains(KeyCode.UP)) {
            moveUp();
        }
        if (keysPressed.contains(KeyCode.DOWN)) {
            moveDown();
        }

        x += (targetX - x) * LERP_SPEED;
        y += (targetY - y) * LERP_SPEED;
    }

    public void render(GraphicsContext gc) {
        if (!facingLeft) {
            gc.save(); // 保存当前画布状态
            gc.translate(x + WIDTH, y); // 将绘制起点向右移动图像宽度
            gc.scale(-1, 1); // 水平翻转
            gc.drawImage(playerCurrImage, 0, 0,WIDTH, HEIGHT); // 从新的原点绘制
            gc.restore(); // 恢复画布状态到最近的保存点
        } else {
            // 如果面朝右边，正常绘制
            gc.drawImage(playerCurrImage, x, y,WIDTH, HEIGHT);
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
        if (targetX + WIDTH > Game.WIDTH) {
            targetX = Game.WIDTH - WIDTH;
        }
    }

    public void moveDown() {
        targetY += SPEED;
        if (targetY + HEIGHT > Game.HEIGHT) {
            targetY = Game.HEIGHT - HEIGHT;
        }
    }

    public void moveUp() {
        targetY -= SPEED;
        if (targetY < 0) {
            targetY = 0;
        }
    }
}