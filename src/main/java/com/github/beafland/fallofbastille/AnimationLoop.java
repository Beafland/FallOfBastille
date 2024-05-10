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
        // Clearing the canvas
        gc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
        gc.drawImage(backgroundImage, 0, 0, Game.WIDTH, Game.HEIGHT);
        gc.drawImage(leftHouseImage, 0, 580, 550, 300);
        gc.drawImage(rightHouseImage, 1100, 100);

        updateGame();

        // Drawing game scenes
        mechan.render(gc);
        mage.render(gc);

        if(gameEnded){
            drawEndGameOverlay();
        }
    }

    private void updateGame(){
        // If the game ends, stop receiving keystroke input from the player
        if (mechan.getHealth() <= 0 || mage.getHealth() <= 0) {
            gameEnded = true;
            keysPressedMechan.clear();
            keysPressedMage.clear();
        }

        // Update game status based on keystroke status
        if (!gameEnded) {
            mechan.update(keysPressedMechan);
            mage.update(keysPressedMage);
        }
    }

    private void drawEndGameOverlay() {
        gc.setFill(Color.rgb(0, 0, 0, 0.5)); // Set translucent black
        gc.fillRect(0, 0, Game.WIDTH, Game.HEIGHT); // Drawing Rectangles

        String winner = mechan.getHealth() <= 0 ? "Mage" : "Mechan";
        gc.setFill(Color.WHITE); // Setting the text colour
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 60)); // Setting fonts and sizes
        gc.setTextAlign(TextAlignment.CENTER); // Setting text centre alignment
        gc.fillText("Winner: " + winner, Game.WIDTH / 2, Game.HEIGHT / 2); // 显示胜利者信息
    }
}

