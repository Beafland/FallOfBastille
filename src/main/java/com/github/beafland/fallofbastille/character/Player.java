package com.github.beafland.fallofbastille.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Player {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 100;
    private static final int SPEED = 5;
    private static final double LERP_SPEED = 0.1;
    //初始化角色图片，不知道为什么一定要这么写要不然找不到图片
    private static final Image playerStandImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechanician.png")));
    private static final Image playerMoveImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechanicianMove.gif")));
    private static final Image gunFire = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/gunFire.png")));
    private Image playerCurrImage;
    public boolean facingLeft = false;
    public boolean isFire = false;

    private double x;
    private double y;

    public Player(int x, int y) {
        this.x = x;
        this.y = y - HEIGHT / 2;
        this.playerCurrImage = playerStandImage;
    }

//    private void fire() {
//        try {
//            // 休眠3秒钟
//            isFire = true;
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            // 在捕获InterruptedException异常时处理中断
//            e.printStackTrace();
//        }
//        isFire = false;
//    }

    public void render(GraphicsContext gc) {
        if (!facingLeft) {
            gc.save(); // 保存当前画布状态
            gc.translate(x + WIDTH, y); // 将绘制起点向右移动图像宽度
            gc.scale(-1, 1); // 水平翻转
            gc.drawImage(playerCurrImage, 0, 0, WIDTH, HEIGHT); // 从新的原点绘制
            gc.restore(); // 恢复画布状态到最近的保存点
        } else {
            // 如果面朝右边，正常绘制
            gc.drawImage(playerCurrImage, x, y, WIDTH, HEIGHT);
        }
    }

    public void setMovement(boolean isMove) {
        if (isMove) {
            this.playerCurrImage = playerMoveImage;
        } else {
            this.playerCurrImage = playerStandImage;
        }
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }
}