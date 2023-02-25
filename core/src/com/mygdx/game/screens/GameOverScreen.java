package com.mygdx.game.screens;

import static com.mygdx.game.extra.Utils.SCREEN_HEIGHT;
import static com.mygdx.game.extra.Utils.SCREEN_WIDTH;
import static com.mygdx.game.extra.Utils.WORLD_HEIGHT;
import static com.mygdx.game.extra.Utils.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.MainGame;

public class GameOverScreen extends BaseScreen{

    private Image background;
    private Stage stage;

    private World world;
    private OrthographicCamera worldCamera;

    private OrthographicCamera fontCamera;
    private BitmapFont texto;

    public GameOverScreen(MainGame mainGame) {
        super(mainGame);

        this.world = new World(new Vector2(0,-10), true);
        FitViewport fitViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.stage = new Stage(fitViewport);

        this.worldCamera = (OrthographicCamera)this.stage.getCamera();

        textgameOver();
    }
    //Método para añadir el fondo a la pantalla
    public void addBackground(){
        this.background = new Image(mainGame.assetManager.getScreenBackground());
        this.background.setPosition(0,0);
        this.background.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        this.stage.addActor(this.background);
    }
    //Creamos una función para establecer la configuración relacionada con el texto de la pantalla de inicio.
    private void textgameOver(){
        //Cargamos la fuente del texto
        this.texto = this.mainGame.assetManager.getFont();
        this.texto.getData().scale(1f);

        //Se crea la cámara y se establece su tamaño en píxeles para que coincida con el de la pantalla, después, se actualiza.
        this.fontCamera = new OrthographicCamera();
        this.fontCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.fontCamera.update();
    }

    @Override
    public void show(){
        addBackground();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        /*Justo antes de dibujar el mundo, le volvemos a pasar al batch, los datos de
        la cámara del mundo, para que vuelva a representar tod0 en función del tamaño de este*/
        this.stage.getBatch().setProjectionMatrix(worldCamera.combined);
        this.stage.act();
        this.world.step(delta,6,2);
        this.stage.draw();

        /*Cargamos la matriz de proyección con los datos de la cámara de la fuente, para que proyecte
         el texto con las dimensiones en píxeles*/
        this.stage.getBatch().setProjectionMatrix(this.fontCamera.combined);
        this.stage.getBatch().begin();
        this.texto.draw(this.stage.getBatch(), "Volver a jugar",SCREEN_WIDTH / 6.3f, SCREEN_HEIGHT / 1.7f);
        this.stage.getBatch().end();

        if(Gdx.input.justTouched()){
            mainGame.setScreen(new GameScreen(mainGame));
        }
    }

    @Override
    public void dispose() {
        this.stage.dispose();
        this.world.dispose();
    }
}