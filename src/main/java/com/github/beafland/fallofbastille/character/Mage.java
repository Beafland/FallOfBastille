package com.github.beafland.fallofbastille.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.Objects;
import java.util.Set;

public class Mage extends Player{
    private static PlayerController controller;

    private static final Image gunFire = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/Mage/fire.gif")));
    private static final Image playerCurrImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/Mage/Mage.png")));

    private static final int attackRange = 150;
    private static final int SPEED = 4;
    private static final int JUMP_FORCE = 18;
    private static HealthBarUI healthBar;

    public Mage(int x, int y) {
        super(x, y, 100);
        controller = new PlayerController(this, SPEED, JUMP_FORCE);
        healthBar = new HealthBarUI(100);
    }

    public void render(GraphicsContext gc) {
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

//        if (isFire()) {
//            // 根据玩家角色的朝向决定是否进行水平翻转
//            if (!isFacingLeft()) {
//                gc.save(); // 保存当前画布状态
//                gc.translate(getX() + WIDTH / 2, getY() + HEIGHT / 2); // 将绘制起点移动到攻击位置
//                gc.scale(-1, 1); // 水平翻转
//                gc.drawImage(gunFire, 0, 0, attackRange, 75); // 从新的原点绘制
//                gc.restore(); // 恢复画布状态到最近的保存点
//            } else {
//                gc.drawImage(gunFire, getX() - WIDTH / 2, getY() + HEIGHT / 3, attackRange, 75);
//            }
//        }
    }

    public void update(Set<KeyCode> keysPressed){
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
                case G ->{
                    controller.fire();
                }
            }
        }
        controller.update(keysPressed);
    }

    public void Jump(){
        controller.Jump();
    }

//    public void setPlayerImage() {
//        switch(getStatus()){
//            case 0 -> playerCurrImage = playerStandImage;
//            case 1 -> playerCurrImage = playerMoveImage;
//            case 2 -> playerCurrImage = playerJumpImage;
//        }
//    }
}
