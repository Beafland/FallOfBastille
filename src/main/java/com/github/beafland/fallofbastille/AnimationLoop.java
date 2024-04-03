package com.github.beafland.fallofbastille;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.Objects;
import java.util.Set;

import static com.github.beafland.fallofbastille.Game.mechan;
import static com.github.beafland.fallofbastille.Game.mage;

public class AnimationLoop extends AnimationTimer {
    private final GraphicsContext gc;
    private final Set<KeyCode> keysPressed;
    private final Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Environment/background.png")));
    private final Image leftHouseImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Environment/leftHouse.png")));
    private final Image rightHouseImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Environment/rightHouse.png")));

    public AnimationLoop(GraphicsContext gc, Set<KeyCode> keysPressed) {
        this.gc = gc;
        this.keysPressed = keysPressed;
    }

    @Override
    public void handle(long now) {
        // 清除画布
        gc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
        gc.drawImage(backgroundImage, 0, 0, Game.WIDTH, Game.HEIGHT);
        gc.drawImage(leftHouseImage, 0, 580, 550, 300);
        gc.drawImage(rightHouseImage, 1100, 100);

        // 根据按键状态更新游戏状态
        mechan.update(keysPressed);
        mage.update(keysPressed);

        // 绘制游戏场景
        mechan.render(gc);
        mage.render(gc);
    }
}

