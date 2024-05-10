package com.github.beafland.fallofbastille;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.Objects;
import java.util.Set;

import static com.github.beafland.fallofbastille.Game.mechan;
import static com.github.beafland.fallofbastille.Game.mage;

public class AnimationLoop extends AnimationTimer {
    private final GraphicsContext gc;
    private final Set<KeyCode> keysPressedMechan;
    private final Set<KeyCode> keysPressedMage;
    private final Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Environment/background.png")));
    private final Image leftHouseImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Environment/leftHouse.png")));
    private final Image rightHouseImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Environment/rightHouse.png")));
    private boolean gameEnded = false;

    public AnimationLoop(GraphicsContext gc, Set<KeyCode> keysPressedMechan, Set<KeyCode> keysPressedMage) {
        this.gc = gc;
        this.keysPressedMechan = keysPressedMechan;
        this.keysPressedMage = keysPressedMage;
    }

    @Override
    public void handle(long now) {
        // 清除画布
        gc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
        gc.drawImage(backgroundImage, 0, 0, Game.WIDTH, Game.HEIGHT);
        gc.drawImage(leftHouseImage, 0, 580, 550, 300);
        gc.drawImage(rightHouseImage, 1100, 100);

        updateGame();

        // 绘制游戏场景
        mechan.render(gc);
        mage.render(gc);

        if(gameEnded){
            drawEndGameOverlay();
        }
    }

    private void updateGame(){
        // 如果游戏结束，停止接收玩家的按键输入
        if (mechan.getHealth() <= 0 || mage.getHealth() <= 0) {
            gameEnded = true;
            keysPressedMechan.clear();
            keysPressedMage.clear();
        }

        // 根据按键状态更新游戏状态
        if (!gameEnded) {
            mechan.update(keysPressedMechan);
            mage.update(keysPressedMage);
        }
    }

    private void drawEndGameOverlay() {
        gc.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置半透明的黑色
        gc.fillRect(0, 0, Game.WIDTH, Game.HEIGHT); // 绘制矩形

        String winner = mechan.getHealth() <= 0 ? "Mage" : "Mechan";
        gc.setFill(Color.WHITE); // 设置文字颜色
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 60)); // 设置字体和大小
        gc.setTextAlign(TextAlignment.CENTER); // 设置文字居中对齐
        gc.fillText("Winner: " + winner, Game.WIDTH / 2, Game.HEIGHT / 2); // 显示胜利者信息
    }
}

