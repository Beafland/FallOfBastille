package com.github.beafland.fallofbastille.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HealthBarUI {
    private final int fullHealth;
    private double currHealth;
    private double fakeHealth;

    public HealthBarUI(int fullHealth) {
        fakeHealth = currHealth = this.fullHealth = fullHealth;
    }

    public void draw(GraphicsContext gc, double health, boolean rightCorner) {
        double width = 500;
        double height = 50;
        int x = 50;
        int y = 10;

        if (currHealth > health) {
            currHealth -= 0.5;
        }
        if (fakeHealth > currHealth) {
            fakeHealth -= 0.2;
        }

        double barWidth = width * (currHealth / fullHealth);
        double fakeBarWidth = width * (fakeHealth / fullHealth);

        if (rightCorner) {
            gc.save(); // Save the current canvas state
            gc.translate(1450 + width, y); // Shifts the drawing start point to the right by the width of the image.
            gc.scale(-1, 1); // Horizontal Flip
        }

        // Drawing the bottom background
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x, y, width, height);

        // Drawing the bottom health bar
        gc.setFill(Color.DARKRED);
        gc.fillRect(x, y, fakeBarWidth, height);
        // Mapping Health Strips
        gc.setFill(Color.RED);
        gc.fillRect(x, y, barWidth, height);

        // Drawing Borders
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);

        if (rightCorner) {
            gc.restore(); // Restore the canvas state to the most recent save point
        }

    }
}
