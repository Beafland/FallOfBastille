package com.github.beafland.fallofbastille.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HealthBarUI {
    private final int fullHealth;
    private double currHealth;
    private double fakeHealth;

    public HealthBarUI(int fullHealth) {
        fakeHealth = currHealth = this.fullHealth = fullHealth;
    }

    public void draw(GraphicsContext gc, double health, boolean rightCorner) {
        double width = 500;
        double height = 50;
        int x = 50;
        int y = 10;

        if (currHealth > health) {
            currHealth -= 0.5;
        }
        if (fakeHealth > currHealth) {
            fakeHealth -= 0.2;
        }

        double barWidth = width * (currHealth / fullHealth);
        double fakeBarWidth = width * (fakeHealth / fullHealth);

        if (rightCorner) {
            gc.save(); // 保存当前画布状态
            gc.translate(1450 + width, y); // 将绘制起点向右移动图像宽度
            gc.scale(-1, 1); // 水平翻转
        }

        // 绘制底部背景
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x, y, width, height);

        // 绘制底部健康条
        gc.setFill(Color.DARKRED);
        gc.fillRect(x, y, fakeBarWidth, height);
        // 绘制健康条
        gc.setFill(Color.RED);
        gc.fillRect(x, y, barWidth, height);

        // 绘制边框
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);

        if (rightCorner) {
            gc.restore(); // 恢复画布状态到最近的保存点
        }

    }
}
