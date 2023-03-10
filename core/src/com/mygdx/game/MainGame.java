package com.mygdx.game;

import com.badlogic.gdx.Game;

import com.mygdx.game.extra.AssetMan;
import com.mygdx.game.screens.GameOverScreen;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.GetReadyScreen;

public class MainGame extends Game {

	private GameScreen gameScreen;
	private GameOverScreen gameOverScreen;
	private GetReadyScreen getReadyScreen;
	public AssetMan assetManager;

	/**
	 * Método de ciclo de vida que se llama automáticamente cuando se inicia la aplicación, y configura
	 * la primera pantalla que se mostrará al usuario
	 */
	@Override
	public void create () {
		this.assetManager = new AssetMan();

		this.gameScreen = new GameScreen(this);
		this.gameOverScreen = new GameOverScreen(this);
		this.getReadyScreen = new GetReadyScreen(this);

		setScreen(this.getReadyScreen);
	}
}
