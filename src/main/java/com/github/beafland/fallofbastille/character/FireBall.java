package com.github.beafland.fallofbastille.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class FireBall{
    private final Image fireBallImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/Mage/fire.gif")));

    private double fireBallSize;
    private double fireBallHeight;
    private boolean goingLeft;
    public final int WIDTH = 120;
    public final int HEIGHT = WIDTH / 4 * 5;

    private double x;
    private double y;

    public FireBall(){
        fireBallSize = 100;
        fireBallHeight = fireBallSize * 1.25;
    }

    public void release(double x, double y, boolean goingRight){
        this.x = x;
        this.y = y;
        this.goingLeft = goingRight;
    }

    public void fireBallIncrease(){
        double maxFireBallSize = 500;
        if(fireBallSize < maxFireBallSize){
            fireBallSize += 1;
            fireBallHeight = fireBallSize * 1.25;
        }
        System.out.println(fireBallSize);
    }

    public void render(GraphicsContext gc){
        if (!goingLeft) {
            gc.save(); // 保存当前画布状态
            gc.translate(x, y - fireBallSize / 2 + HEIGHT / 3.0); // 将绘制起点移动到攻击位置d
            gc.scale(-1, 1); // 水平翻转
            gc.drawImage(fireBallImage, 0, 0, fireBallHeight, fireBallSize);
            gc.restore(); // 恢复画布状态到最近的保存点
            x-=10;
        } else {
            gc.drawImage(fireBallImage, x + WIDTH / 2.0, y - fireBallSize / 2 + HEIGHT / 3.0,
                    fireBallHeight, fireBallSize);

            x+=10;
        }
    }

    public double getX(){return x;}

    public double getY() {
        return y;
    }

    public double getFireBallSize() {
        return fireBallSize;
    }

    public double getFireBallHeight() {
        return fireBallHeight;
    }
}
