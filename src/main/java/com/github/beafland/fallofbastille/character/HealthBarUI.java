package com.github.beafland.fallofbastille.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HealthBarUI {
    private final int fullHealth;
    private double currHealth;
    private double fakeHealth;

    public HealthBarUI(int fullHealth) {
        this.fullHealth = fullHealth;
        this.currHealth = fullHealth;
        this.fakeHealth = fullHealth;
    }

    public void draw(GraphicsContext gc, double health, boolean rightCorner) {
        double width = 500;
        double height = 50;
        int x = 50;
        int y = 10;

        // Gradually decrease the current health to simulate damage effect
        if (currHealth > health) {
            currHealth -= 0.5;
        }
        // Gradually decrease the fake health to give a delayed health bar effect
        if (fakeHealth > currHealth) {
            fakeHealth -= 0.2;
        }

        // Calculate the width of the health bar based on current health
        double barWidth = width * (currHealth / fullHealth);
        // Calculate the width of the delayed health effect
        double fakeBarWidth = width * (fakeHealth / fullHealth);

        if (rightCorner) {
            gc.save(); // Save the current state of the canvas
            gc.translate(1350 + width, y); // Move the drawing origin to the right by the width of the bar
            gc.scale(-1, 1); // Flip horizontally
        }

        // Draw the background bar
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x, y, width, height);

        // Draw the delayed health bar effect
        gc.setFill(Color.DARKRED);
        gc.fillRect(x, y, fakeBarWidth, height);

        // Draw the actual health bar
        gc.setFill(Color.RED);
        gc.fillRect(x, y, barWidth, height);

        // Draw the border around the health bar
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);

        if (rightCorner) {
            gc.restore(); // Restore the canvas state to the last saved point
        }
    }
}
