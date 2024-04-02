package com.github.beafland.fallofbastille.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Bot {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;
    //初始化角色图片，不知道为什么一定要这么写要不然找不到图片
    private static final Image botImage = new Image(Objects.requireNonNull(Bot.class.getResourceAsStream("/images/mechan/terrorDrone.png")));
    private static final int attackRange = 20;
    private double x;
    private double y;

    public Bot(int x, int y) {
        this.x = x;
        this.y = y - HEIGHT / 2;
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    public void render(GraphicsContext gc) {
            gc.drawImage(botImage, x, y, WIDTH, HEIGHT);
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