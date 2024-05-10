package com.github.beafland.fallofbastille.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.Objects;
import java.util.Set;

public class Mage extends Player {
    private static PlayerController controller;

    //player images
    private final Image playerStandImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/Mage/Mage.png")));
    private final Image attackImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/Mage/mageAttack.png")));
    private final Image useFireImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/Mage/mageFire.gif")));
    private final int attackRange = 150;
    private final int SPEED = 4;
    private final int JUMP_FORCE = 18;
    private final HealthBarUI healthBar;
    private Image playerCurrImage = playerStandImage;
    private FireBall fireball;
    private boolean useFireBall = false;

    private Player enemy;

    public Mage(int x, int y) {
        super(x, y, 100);
        controller = new PlayerController(this, SPEED, JUMP_FORCE);
        healthBar = new HealthBarUI(100);
    }

    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    public void render(GraphicsContext gc) {
        setPlayerImage();

        if (!isFacingLeft()) {
            gc.save(); // 保存当前画布状态
            gc.translate(getX() + WIDTH, getY()); // 将绘制起点向右移动图像宽度
            gc.scale(-1, 1); // 水平翻转
            gc.drawImage(playerCurrImage, 0, 0, WIDTH, HEIGHT); // 从新的原点绘制
            gc.restore(); // 恢复画布状态到最近的保存点
        } else {
            // 如果面朝左边，正常绘制
            gc.drawImage(playerCurrImage, getX(), getY(), WIDTH, HEIGHT);
        }

        healthBar.draw(gc, getHealth(), false);

        if (useFireBall && fireball != null) {
            //根据玩家角色的朝向决定是否进行水平翻转
            fireball.render(gc);
            if (fireball.getX() > 2000 || fireball.getX() < 0) {
                useFireBall = false;
                fireball = null;
            }
        }

    }

    public void update(Set<KeyCode> keysPressed) {
        for (KeyCode keyCode : keysPressed) {
            switch (keyCode) {
                case A -> {
                    controller.moveLeft();
                    setFacingLeft(false);
                }
                case D -> {
                    controller.moveRight();
                    setFacingLeft(true);
                }
                case G -> {
                    useFireBall();
                }
            }
        }
        //Change character status image
        if (keysPressed.contains(KeyCode.G)) setStatus(12);
        else setStatus(0);

        //update character position
        controller.update();

        //check fireball collision if used
        if (useFireBall) checkCollison();
    }

    private void checkCollison() {
        double mechanX = enemy.getX(); // 获取 mechan 的 x 坐标
        double mechanY = enemy.getY(); // 获取 mechan 的 y 坐标

        double fireBallX = fireball.getX(); // 获取 fireball 的 x 坐标
        double fireBallY = fireball.getY(); // 获取 fireball 的 y 坐标

        // 检查是否在 x 轴上重叠
        boolean overlapX = Math.abs(fireBallX - mechanX) < (fireball.getFireBallSize() + WIDTH / 2.0);

        // 检查是否在 y 轴上重叠（考虑到画板原点在左上角，需要反转 Y 坐标）
        boolean overlapY = Math.abs(fireBallY - mechanY) < (fireball.getFireBallHeight() * 0.8 + HEIGHT) / 2;

        // 如果在 x 和 y 轴上都重叠，则发生碰撞
        if (overlapX && overlapY) {
            // 计算对角色造成的伤害
            enemy.damage((int) (fireball.getFireBallSize() * 0.1));
            useFireBall = false;
            fireball = null;
        }
    }

    public void Jump() {
        controller.Jump();
    }

    public void useFireBall() {
        //create a new fireball if null
        //increase the fireball instead
        if (fireball == null) {
            fireball = new FireBall();
        } else {
            fireball.fireBallIncrease();
        }

    }

    public void FireBallRelease() {
        useFireBall = true;
        if (fireball != null)
            fireball.release(getX(), getY(), isFacingLeft());
    }

    //updating character image
    private void setPlayerImage() {
        switch (getStatus()) {
            case 0 -> playerCurrImage = playerStandImage;
            case 11 -> playerCurrImage = attackImage;
            case 12 -> playerCurrImage = useFireImage;
        }
    }
}
