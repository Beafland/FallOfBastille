package com.github.beafland.fallofbastille;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import java.util.Set;

import static com.github.beafland.fallofbastille.Game.player;

public class AnimationLoop extends AnimationTimer {
    private final GraphicsContext gc;
    private final Set<KeyCode> keysPressed;

    public AnimationLoop(GraphicsContext gc, Set<KeyCode> keysPressed) {
        this.gc = gc;
        this.keysPressed = keysPressed;
    }

    @Override
    public void handle(long now) {
        // 清除画布
        gc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);

        // 根据按键状态更新游戏状态
        player.update(keysPressed);

        // 绘制游戏场景
        player.render(gc);
    }
}

