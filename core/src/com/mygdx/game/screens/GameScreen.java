package com.mygdx.game.screens;

import static com.mygdx.game.extra.Utils.SCREEN_HEIGHT;
import static com.mygdx.game.extra.Utils.SCREEN_WIDTH;
import static com.mygdx.game.extra.Utils.USER_BALL;
import static com.mygdx.game.extra.Utils.USER_LEFT_WALL;
import static com.mygdx.game.extra.Utils.USER_RIGHT_WALL;
import static com.mygdx.game.extra.Utils.USER_ROOF;
import static com.mygdx.game.extra.Utils.USER_FLOOR;
import static com.mygdx.game.extra.Utils.USER_SHOVEL_LEFT;
import static com.mygdx.game.extra.Utils.USER_SHOVEL_RIGHT;
import static com.mygdx.game.extra.Utils.WORLD_HEIGHT;
import static com.mygdx.game.extra.Utils.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.MainGame;

import com.mygdx.game.actors.Ball;
import com.mygdx.game.actors.PaddleLeft;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.game.actors.PaddleRight;

public class GameScreen extends BaseScreen implements ContactListener{
    private static final float WAIT_TIME = 2f;

    private Stage stage;
    private PaddleLeft paddleLeft;
    private PaddleRight paddleRight;

    private boolean leftHit = false;
    private boolean rightHit = false;
    private boolean gameOver = false;
    private boolean isScored = false;

    private int scoreNumberLeftPaddle;
    private int scoreNumberRightPaddle;

    private Image background;

    private World world;
    private Fixture roof;
    private Fixture floor;
    private Fixture leftWall;
    private Fixture rightWall;

    private Music musicbg;
    private Sound hitSound;
    private Sound scoreSound;

    private Ball ball;

    private OrthographicCamera worldCamera;
    private OrthographicCamera fontCameraLeft;
    private OrthographicCamera fontCameraRight;

    private BitmapFont scoreLeft;
    private BitmapFont scoreRight;

    /**
     * Constructor de la pantalla en el juego que se muestra cuando está jugando.
     * @param mainGame controla la lógica del juego y maneja la transición de una pantalla a otra.
     */
    public GameScreen(MainGame mainGame) {
        super(mainGame);
        //añadimos la gravedad
        this.world = new World(new Vector2(0, 0), true);
        /*Permite que el contenido del mundo del juego se ajuste a la pantalla de una manera que se vea bien,
        sin distorsionar el tamaño o la forma de los elementos del juego.*/
        FitViewport fitViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);

        this.stage = new Stage(fitViewport);

        //Asignamos un objeto que implemente la interfaz contactListener al mundo.
        this.world.setContactListener(this);

        this.worldCamera = (OrthographicCamera)this.stage.getCamera();

        prepareGameSound();
        prepareScore();

        //Temporizador de 2 segundos antes de que aparezca la bola
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                addBall();
            }
        }, WAIT_TIME);
    }

    /**
     * Método para añadir el fondo de la pantalla del juego
     */
    public void addBackground(){
        this.background = new Image(mainGame.assetManager.getGameBackground());
        this.background.setPosition(0,0);
        this.background.setSize(WORLD_WIDTH, WORLD_HEIGHT);
        this.stage.addActor(this.background);
    }

    /**
     * Método para añadir la pala izquierda y su ID
     */
    public void addShovelLeft(){
        Animation<TextureRegion> shovelSprite = mainGame.assetManager.getShovelAnimationLeft();
        //damos el mundo, los sprites que va a utilizar y la posición en el mundo con los vectores
        this.paddleLeft = new PaddleLeft(this.world, shovelSprite, new Vector2(0.5f, 2.3f), USER_SHOVEL_LEFT);
        this.stage.addActor(this.paddleLeft);
    }

    /**
     * Método para añadir la pala derecha y su ID
     */
    public void addShovelRight(){
        Animation<TextureRegion> shovelSprite = mainGame.assetManager.getShovelAnimationRight();
        //damos el mundo, los sprites que va a utilizar y la posición en el mundo con los vectores
        this.paddleRight = new PaddleRight(this.world, shovelSprite, new Vector2(7.6f, 2.3f), USER_SHOVEL_RIGHT);
        this.stage.addActor(this.paddleRight);
    }

    /**
     * Método para añadir el suelo donde indicamos su posición, la forma de su body y su ID
     */
    private void addFloor() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(WORLD_WIDTH / 2f, 0.2f);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        PolygonShape edge = new PolygonShape();
        edge.setAsBox(4f, 0.1f);
        floor = body.createFixture(edge, 3);
        floor.setUserData(USER_FLOOR);
        edge.dispose();
    }

    /**
     * Método para añadir el techo donde indicamos su posición, la forma de su body y su ID
     */
    public void addRoof(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(WORLD_WIDTH/ 2f, WORLD_HEIGHT);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        PolygonShape edge = new PolygonShape();
        edge.setAsBox(4f,0.1f);
        roof = body.createFixture(edge, 1);
        roof.setUserData(USER_ROOF);
        edge.dispose();
    }

    /**
     * Método para añadir la pared izquierda donde indicamos su posición, la forma de su body y su ID
     */
    private void addLeftWall(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        EdgeShape edge = new EdgeShape();
        edge.set(0, 0,0, WORLD_HEIGHT);
        leftWall = body.createFixture(edge,1);
        leftWall.setUserData(USER_LEFT_WALL);
        edge.dispose();
    }

    /**
     * Método para añadir la pared derecha donde indicamos su posición, la forma de su body y su ID
     */
    private void addRighttWall(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        EdgeShape edge = new EdgeShape();
        edge.set(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
        rightWall = body.createFixture(edge,1);
        rightWall.setUserData(USER_RIGHT_WALL);
        edge.dispose();
    }

    /**
     * Método para añadir la pelota donde indicamos su posición
     */
    private void addBall(){
        isScored = false;
        TextureRegion ball = mainGame.assetManager.getBall();
        //Posición donde se coloca la física de la pelota
        this.ball = new Ball(this.world, ball, new Vector2(4f,2.4f));
        this.stage.addActor(this.ball);
    }

    /**
     * Método para añadir la pelota donde indicamos su posición y con un parámetro que dependiendo de quien
     * le metan punto le aparecerá la pelota hacia él
     * @param x posición de la pelota en el eje X
     */
    private void addBall(int x){
        isScored = false;
        TextureRegion ball = mainGame.assetManager.getBall();
        //Posición donde se coloca la física de la pelota
        this.ball = new Ball(this.world, ball, new Vector2(4f,2.4f));
        this.stage.addActor(this.ball);

        this.ball.aplicarImpulso(x);
    }

    /**
     * Creamos una función para establecer la configuración relacionada con la Música y el Sonido
     */
    private void prepareGameSound(){
        this.musicbg = this.mainGame.assetManager.getMusicBG();
        this.hitSound = this.mainGame.assetManager.getHitSound();
        this.scoreSound = this.mainGame.assetManager.getScoreSound();
    }

    /**
     * Creamos una función para establecer la configuración relacionada con el texto de la puntuación.
     */
    private void prepareScore(){
        //Cargamos la fuente del contador de la izquierda
        this.scoreLeft = this.mainGame.assetManager.getFont();
        this.scoreLeft.getData().scale(1f);

        //Se crea la cámara y se establece su tamaño en píxeles para que coincida con el de la pantalla, después, se actualiza.
        this.fontCameraLeft = new OrthographicCamera();
        this.fontCameraLeft.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.fontCameraLeft.update();

        //Cargamos la fuente del contador de la derecha
        this.scoreRight = this.mainGame.assetManager.getFont();
        this.scoreRight.getData().scale(1f);

        //Se crea la cámara y se establece su tamaño en píxeles para que coincida con el de la pantalla, después, se actualiza.
        this.fontCameraRight = new OrthographicCamera();
        this.fontCameraRight.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.fontCameraRight.update();
    }

    /**
     * Método se llama continuamente en un bucle mientras la pantalla está activa y se encarga de dibujar los elementos de la pantalla en cada cuadro
     * @param delta tiempo transcurrido, en segundos, desde la última llamada al método
     */
    @Override
    public void render(float delta){
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.stage.act();

        //Si el contador de algun jugador es 5
        if(scoreNumberLeftPaddle  > 4 || scoreNumberRightPaddle > 4){
            gameOver = true;
        }

        //Si el juego ha terminado
        if (gameOver){
            try {
                //"Dormimos" el hilo durante 1'5 segundos para no cambie de pantalla y que no haya un cambio de pantalla brusco
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Ponemos la pantalla de fin del juego
            mainGame.setScreen(new GameOverScreen(this.mainGame));
        }

        this.world.step(delta, 6, 2);
        this.stage.draw();

        /*Antes de representar el mundo gráficamente, actualizamos el batch con los datos de la cámara
        * del mundo, de manera que se puedan renderizar los elementos en función de su tamaño.*/
        this.stage.getBatch().setProjectionMatrix(worldCamera.combined);
        this.stage.act();
        this.world.step(delta, 6, 2);
        this.stage.draw();

        /*Establecemos los parámetros de la cámara fuente en la matriz de proyección para que el texto
        sea proyectado con sus dimensiones en píxeles.*/
        this.stage.getBatch().setProjectionMatrix(this.fontCameraLeft.combined);
        this.stage.getBatch().setProjectionMatrix(this.fontCameraRight.combined);

        this.stage.getBatch().begin();
        this.scoreLeft.draw(this.stage.getBatch(), "" + this.scoreNumberLeftPaddle, SCREEN_WIDTH / 10, SCREEN_HEIGHT / 1.1f);
        this.scoreRight.draw(this.stage.getBatch(),"" + this.scoreNumberRightPaddle, SCREEN_WIDTH / 1.2f , SCREEN_HEIGHT / 1.1f);

        this.stage.getBatch().end();

        //Si se ha marcado un punto
        if(isScored){
            Gdx.app.postRunnable(new Runnable() {
                @Override
                //Eliminamos la pelota y aparece de nuevo hacia quien ha perdido ese punto
                public void run() {
                    isScored = false;
                    ball.detach();
                    ball.remove();
                    //Si la bola toca la pared cuando ha golpeado la pala derecha
                    if (leftHit){
                        addBall(-2);
                    }else{              //Si ha sido la derecha
                        addBall(-1);
                    }
                }
            });
        }


    }
    //Agregamos los elementos a la pantalla

    /**
     * Método que se llama automáticamente cuando se muestra la pantalla.
     */
    @Override
    public void show(){
        addBackground();
        addFloor();
        addRoof();
        addLeftWall();
        addRighttWall();
        addShovelLeft();
        addShovelRight();
        //Ponemos la música en bucle y la reproducimos
        this.musicbg.setLooping(true);
        this.musicbg.play();
    }

    /**
     * Liberar recursos y eliminar instancias
     */
    @Override
    public void hide(){
        this.paddleLeft.detach();
        this.paddleLeft.remove();

        this.paddleRight.detach();
        this.paddleRight.remove();

        if(this.ball != null){
            this.ball.detach();
            this.ball.remove();
        }

        this.musicbg.stop();
        this.stage.dispose();
    }

    /**
     * Libera la memoria
     */
    @Override
    public void dispose(){
        this.stage.dispose();
        this.world.dispose();
    }

    /**COLISIONES*/
    /**
     * Función que nos ayuda a determinar qué objetos han colisionado.
     * @param contact representa la colsión entre dos fixtures
     * @param objA objeto que ha colisionado
     * @param objB objeto que ha colisionado
     * @return
     */
    private boolean areColider(Contact contact, Object objA, Object objB) {
        return (contact.getFixtureA().getUserData().equals(objA) && contact.getFixtureB().getUserData().equals(objB)) ||
                (contact.getFixtureA().getUserData().equals(objB) && contact.getFixtureB().getUserData().equals(objA));
    }

    /**
     * Se crea un método que se llamará cada vez que se produzca algún contacto entre objetos.
     * @param contact representa la colsión entre dos fixtures
     */
    @Override
    public void beginContact(Contact contact) {
        //comprueba si la pelota ha colisionado con la pala de la izquierda
        if(areColider(contact, USER_BALL, USER_SHOVEL_LEFT)){
            ball.aplicarImpulso(0);
            leftHit = true;
            rightHit = false;
            this.hitSound.play();
        }
        //comprueba si la pelota ha colisionado con la pala de la derecha
        if(areColider(contact, USER_BALL, USER_SHOVEL_RIGHT)){
            ball.aplicarImpulso(1);
            leftHit = false;
            rightHit = true;
            this.hitSound.play();
        }
        //comprueba si la pelota ha colisionado con el techo
        if (areColider(contact, USER_BALL, USER_ROOF)){
            if (leftHit){
                ball.aplicarImpulso(2);
            }

            if (rightHit){
                ball.aplicarImpulso(3);
            }
        }
        //comprueba si la pelota ha colisionado con el suelo
        if (areColider(contact, USER_BALL, USER_FLOOR)){
            if (leftHit){
                ball.aplicarImpulso(4);
            }

            if (rightHit){
                ball.aplicarImpulso(5);
            }
        }
        //comprueba si la pelota ha colisionado con la pared de la izquierda
        if (areColider(contact, USER_LEFT_WALL, USER_BALL)){
            this.ball.stopBall();
            this.scoreSound.play();
            this.scoreNumberRightPaddle++;
            isScored = true;
        }
        //comprueba si la pelota ha colisionado con la pared de la derecha
        if (areColider(contact, USER_RIGHT_WALL, USER_BALL)){
            this.ball.stopBall();
            this.scoreSound.play();
            this.scoreNumberLeftPaddle++;
            isScored = true;
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}