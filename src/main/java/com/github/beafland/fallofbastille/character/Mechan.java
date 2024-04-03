package com.github.beafland.fallofbastille.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.Objects;
import java.util.Set;

public class Mechan extends Player{
    private static PlayerController controller;

    //初始化角色图片，不知道为什么一定要这么写要不然找不到图片
    private static final Image playerStandImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanician.png")));
    private static final Image playerMoveImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanicianMove.gif")));
    private static final Image playerJumpImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanicianJump.gif")));
    private Image playerCurrImage = playerStandImage;
    private static final Image gunFire = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/gunFire.png")));
    private static final int attackRange = 150;
    private static final int SPEED = 5;
    private static final int JUMP_FORCE = 20;

    public Mechan(int x, int y) {
        super(x, y, 100);
        controller = new PlayerController(this, SPEED, JUMP_FORCE);
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
                gc.translate(getX() + WIDTH / 2, getY() + HEIGHT / 2); // 将绘制起点移动到攻击位置
                gc.scale(-1, 1); // 水平翻转
                gc.drawImage(gunFire, 0, 0, attackRange, 75); // 从新的原点绘制
                gc.restore(); // 恢复画布状态到最近的保存点
            } else {
                gc.drawImage(gunFire, getX() - WIDTH / 2, getY() + HEIGHT / 3, attackRange, 75);
            }
        }
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
                case SPACE -> controller.fire();
            }
        }
        controller.update(keysPressed);
    }

    public void Jump(){
        controller.Jump();
    }

    public void setPlayerImage() {
        switch(getStatus()){
            case 0 -> playerCurrImage = playerStandImage;
            case 1 -> playerCurrImage = playerMoveImage;
            case 2 -> playerCurrImage = playerJumpImage;
        }
    }
}
