package com.github.beafland.fallofbastille.character;

abstract class Player {
    public static final int WIDTH = 120;
    public static final int HEIGHT = WIDTH / 4 * 5;
    private boolean facingLeft = true;
    private boolean isFire = false;
    private int status = 0;

    private double x;
    private double y;

    private int health;

    public Player(int x, int y, int health) {
        this.x = x;
        this.y = y - HEIGHT / 2;
        this.health = health;
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isFire() {
        return isFire;
    }

    public void setFire(boolean fire) {
        isFire = fire;
    }

    public boolean isFacingLeft() {
        return facingLeft;
    }

    public void setFacingLeft(boolean facingLeft) {
        this.facingLeft = facingLeft;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    protected void damage(int damage) {
        health -= damage;
    }
}