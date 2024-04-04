package com.github.beafland.fallofbastille.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.Objects;
import java.util.Set;

public class Mechan extends Player{
    private static PlayerController controller;

    //初始化角色图片，不知道为什么一定要这么写要不然找不到图片
    private final Image playerStandImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanician.png")));
    private final Image playerMoveImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanicianMove.gif")));
    private final Image playerJumpImage = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/mechanicianJump.gif")));
    private Image playerCurrImage = playerStandImage;
    private final Image gunFire = new Image(Objects.requireNonNull(Player.class.getResourceAsStream("/images/mechan/gunFire.png")));

    private final int attackRange = 150;
    private final int SPEED = 5;
    private final int JUMP_FORCE = 20;
    private final HealthBarUI healthBar;

    public Mechan(int x, int y) {
        super(x, y, 100);
        controller = new PlayerController(this, SPEED, JUMP_FORCE);
        healthBar = new HealthBarUI(100);
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

        if(keysPressed.contains(KeyCode.LEFT) || keysPressed.contains(KeyCode.RIGHT))
            setStatus(1);
        else
            setStatus(0);

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

//        if (isFire()) {
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
//        }

        healthBar.draw(gc, getHealth(), true);
    }

    public void Jump(){
        controller.Jump();
    }

    private void setPlayerImage() {
        switch(getStatus()){
            case 0 -> playerCurrImage = playerStandImage;
            case 1 -> playerCurrImage = playerMoveImage;
            case 2 -> playerCurrImage = playerJumpImage;
        }
    }

}
