package com.github.beafland.fallofbastille.character;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Set;

public class Mechan extends Player{
    //player images
    private final Image playerStandImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanician.png")));
    private final Image playerMoveImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanicianMove.gif")));
    private final Image playerJumpImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanicianJump.gif")));
    private Image playerCurrImage = playerStandImage;
    private final Image gunFire = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/gunFire.png")));

    //initialize player
    private static PlayerController controller;
    private final int attackRange = 150;
    private final int SPEED = 5;
    private final int JUMP_FORCE = 20;
    private final HealthBarUI healthBar;
    private Player enemy;

    //Attack component
    private boolean isAttack = false;
    private final Timeline attackTimeline;
    private final Timeline fireTimeline;

    public Mechan(int x, int y) {
        super(x, y, 100);
        controller = new PlayerController(this, SPEED, JUMP_FORCE);
        healthBar = new HealthBarUI(100);

        fireTimeline = new Timeline(new KeyFrame(Duration.seconds(0.05), e -> this.setFire(false)));
        attackTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> isAttack = false));
        attackTimeline.setCycleCount(Timeline.INDEFINITE);
        fireTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    public void update(Set<KeyCode> keysPressed){
        for (KeyCode keyCode : keysPressed) {
            switch (keyCode) {
                case LEFT -> {
                    controller.moveLeft();
                    setFacingLeft(true);
                }
                case RIGHT -> {
                    controller.moveRight();
                    setFacingLeft(false);
                }
                case SPACE -> fire();
            }
        }

        //Change character status image
        if(keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.RIGHT))
            setStatus(1);
        else
            setStatus(0);

        //update character position
        controller.update();
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

        if (isFire()) {
            // 根据玩家角色的朝向决定是否进行水平翻转
            if (!isFacingLeft()) {
                gc.save(); // 保存当前画布状态
                gc.translate(getX() + attackRange + WIDTH, getY()- attackRange * 0.1 + HEIGHT / 2.0); // 将绘制起点移动到攻击位置
                gc.scale(-1, 1); // 水平翻转
                gc.drawImage(gunFire, 0, 0, attackRange, attackRange * 0.25); // 从新的原点绘制
                gc.restore(); // 恢复画布状态到最近的保存点
            } else {
                gc.drawImage(gunFire, getX() - attackRange, getY() - attackRange * 0.1 + HEIGHT / 2.0, attackRange, attackRange * 0.25);
            }
        }

        healthBar.draw(gc, getHealth(), true);
    }

    public void Jump(){
        controller.Jump();
    }

    private void fire() {
        if (!isAttack) {
            this.setFire(true);
            isAttack = true;

            checkFireCollision();

            //攻击后摇
            attackTimeline.play();
            fireTimeline.play();
        }
    }

    private void checkFireCollision(){
        double gunFireX = getX(); // gun fire 的 x 坐标

        double mageX = enemy.getX(); // 获取 mage 的 x 坐标
        double mageY = enemy.getY(); // 获取 mage 的 y 坐标


        if (!isFacingLeft()) {
            // 如果人物朝向右边，则 gun fire 的 x 坐标为人物的 x 坐标加上攻击范围
            gunFireX += attackRange;
        } else {
            // 如果人物朝向左边，则 gun fire 的 x 坐标为人物的 x 坐标减去攻击范围
            gunFireX -= attackRange;
        }

        // 检查 gun fire 的 x 坐标是否与 mage 的 x 坐标重叠，并考虑两者的宽度
        boolean overlapX = Math.abs(gunFireX - mageX) < (attackRange + (getWIDTH() * 0.6)) / 2;

        // 检查是否在 y 轴上重叠（考虑到画板原点在左上角，需要反转 Y 坐标）
        boolean overlapY = Math.abs(mageY - getY()) < (attackRange * 0.1 + HEIGHT) / 2;

        // 如果在 x 和 y 轴上都重叠，则发生碰撞
        if (overlapX && overlapY) {
            // 计算对角色造成的伤害
            enemy.damage((20));
            System.out.println("hit!");
        }
    }

    private void setPlayerImage() {
        switch(getStatus()){
            case 0 -> playerCurrImage = playerStandImage;
            case 1 -> playerCurrImage = playerMoveImage;
            case 2 -> playerCurrImage = playerJumpImage;
        }
    }

}
