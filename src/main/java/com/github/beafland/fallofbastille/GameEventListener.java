package com.github.beafland.fallofbastille;

import javafx.scene.input.KeyCode;

public interface GameEventListener {
	void onKeyPressed(KeyCode key);
    void onKeyReleased(KeyCode key);
	void onJump(String playerType);
}
