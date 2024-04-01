package com.github.beafland.fallofbastille.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class bot {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 100;
    //初始化角色图片，不知道为什么一定要这么写要不然找不到图片
    private static final Image playerStandImage = new Image(Objects.requireNonNull(bot.class.getResourceAsStream("/images/mechanician.png")));
    private static final Image playerMoveImage = new Image(Objects.requireNonNull(bot.class.getResourceAsStream("/images/mechanicianMove.gif")));
    private static final Image gunFire = new Image(Objects.requireNonNull(bot.class.getResourceAsStream("/images/gunFire.png")));
    private static final int attackRange = 20;
    public boolean facingLeft = false;
    public boolean isFire = false;
    private Image playerCurrImage;
    private double x;
    private double y;

    public bot(int x, int y) {
        this.x = x;
        this.y = y - HEIGHT / 2;
        this.playerCurrImage = playerStandImage;
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    public void render(GraphicsContext gc) {
        if (!facingLeft) {
            gc.save(); // 保存当前画布状态
            gc.translate(x + WIDTH, y); // 将绘制起点向右移动图像宽度
            gc.scale(-1, 1); // 水平翻转
            gc.drawImage(playerCurrImage, 0, 0, WIDTH, HEIGHT); // 从新的原点绘制
            gc.restore(); // 恢复画布状态到最近的保存点
        } else {
            // 如果面朝左边，正常绘制
            gc.drawImage(playerCurrImage, x, y, WIDTH, HEIGHT);
        }

        if (isFire) {
            // 根据玩家角色的朝向决定是否进行水平翻转
            if (!facingLeft) {
                gc.save(); // 保存当前画布状态
                gc.translate(x + WIDTH / 2, y + HEIGHT / 2); // 将绘制起点移动到攻击位置
                gc.scale(-1, 1); // 水平翻转
                gc.drawImage(gunFire, -WIDTH * 1.2, -attackRange / 2, 50, attackRange); // 从新的原点绘制
                gc.restore(); // 恢复画布状态到最近的保存点
            } else {
                gc.drawImage(gunFire, x - WIDTH / 1.5, y + HEIGHT / 2.4, 50, attackRange);
            }
        }
    }

    public void setMovement(boolean isMove) {
        if (isMove) {
            this.playerCurrImage = playerMoveImage;
        } else {
            this.playerCurrImage = playerStandImage;
        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}