package com.mygdx.game.extra;

import static com.mygdx.game.extra.Utils.ATLAS_MAP;
import static com.mygdx.game.extra.Utils.BACKGROUND_IMAGE;
import static com.mygdx.game.extra.Utils.FONT_FNT;
import static com.mygdx.game.extra.Utils.FONT_PNG;
import static com.mygdx.game.extra.Utils.GAME_READY_OVER_BACKGROUND;
import static com.mygdx.game.extra.Utils.SHOVEL_GREEN;
import static com.mygdx.game.extra.Utils.SHOVEL_PURPLE;
import static com.mygdx.game.extra.Utils.SOUND_HIT;
import static com.mygdx.game.extra.Utils.SOUND_SCORE;
import static com.mygdx.game.extra.Utils.USER_BALL;
import static com.mygdx.game.extra.Utils.MUSIC_BG;
import static com.mygdx.game.extra.Utils.SHOVEL_BLUE;
import static com.mygdx.game.extra.Utils.SHOVEL_RED;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetMan {

    private AssetManager assetManager;
    private TextureAtlas textureAtlas;

    /**
     * Constructor que se encarga de cargar diferentes tipos de recursos utilizando la clase AssetManager
     */
    public AssetMan(){
        this.assetManager = new AssetManager();

        assetManager.load(ATLAS_MAP, TextureAtlas.class);
        assetManager.load(MUSIC_BG,Music.class);
        assetManager.load(SOUND_HIT, Sound.class);
        assetManager.load(SOUND_SCORE, Sound.class);
        assetManager.finishLoading();

        this.textureAtlas = assetManager.get(ATLAS_MAP);
    }

    /**
     * Método que devuelve la Imagen de fondo
     * @return devuelve la región de textura de la pantalla de juego
     */
    public TextureRegion getGameBackground(){return this.textureAtlas.findRegion(BACKGROUND_IMAGE);}

    /**
     * Método que devuelve la Imagen de fondo
     * @return devuelve la región de textura de la pantalla para la pantalla de inicio o fin
     */
    public TextureRegion getScreenBackground(){return this.textureAtlas.findRegion(GAME_READY_OVER_BACKGROUND);}

    /**
     * Devuelve la Animación de la pala
     * @return animación de la pala izquierda que dura 2 segundos
     */
    public Animation<TextureRegion> getShovelAnimationLeft(){
        //tiempo que dura un frame(cada uno 1 segundo en este caso)
        return new Animation<TextureRegion>(1f,
                textureAtlas.findRegion(SHOVEL_BLUE),
                textureAtlas.findRegion(SHOVEL_RED));
    }

    /**
     * Devuelve la Animación de la pala
     * @return animación de la pala izquierda que dura 2 segundos
     */
    public Animation<TextureRegion> getShovelAnimationRight(){
        return new Animation<TextureRegion>(1f,
                textureAtlas.findRegion(SHOVEL_GREEN),
                textureAtlas.findRegion(SHOVEL_PURPLE));
    }

    /**
     * Textura de la pelota
     * @return devuelve la región de textura de la bola
     */
    public TextureRegion getBall(){
        return this.textureAtlas.findRegion(USER_BALL);
    }

    //Sonidos

    /**
     * Devuelve un objeto de la clase Music
     * @return nombre de la pista de música que se desea cargar por la constante "MUSIC_BG".
     */
    public Music getMusicBG(){
        return this.assetManager.get(MUSIC_BG);
    }

    /**
     * Devuelve un objeto de la clase Sound
     * @return nombre de la pista de sonido que se desea cargar por la constante "SOUND_HIT".
     */
    public Sound getHitSound(){
        return this.assetManager.get(SOUND_HIT);
    }

    /**
     * Devuelve un objeto de la clase Sound
     * @return nombre de la pista de sonido que se desea cargar por la constante "SOUND_SCORE".
     */
    public Sound getScoreSound(){
        return this.assetManager.get(SOUND_SCORE);
    }

    /**
     * Devuelve un objeto de la clase BitmapFont
     * @return fuente de texto y la imagen correspondiente.
     */
    public BitmapFont getFont(){
        return new BitmapFont(Gdx.files.internal(FONT_FNT),Gdx.files.internal(FONT_PNG), false);
    }
}