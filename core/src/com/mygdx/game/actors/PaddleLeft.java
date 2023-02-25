package com.mygdx.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.extra.Utils;


public class PaddleLeft extends Actor{
    private static final float SHOVEL_WIDTH = 0.2f;
    private static final float SHOVEL_HEIGHT = 0.7f;

    private Animation<TextureRegion> shovelAnimation;
    private Vector2 position;

    private float stateTime;

    private World world;
    private Body body;
    private Fixture fixture;

    //Constructor con mundo, textura, posicion y ID
    public PaddleLeft(World world, Animation<TextureRegion> shovelAnimation, Vector2 position, String userData) {
        this.shovelAnimation = shovelAnimation;
        this.position = position;
        this.world = world;

        this.stateTime = 0f;

        createBody();
        createFixture();

        this.fixture.setUserData(userData);
    }
    //Método para crear la pala donde indicamos la forma de su body
    public void createBody(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(this.position);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        this.body = this.world.createBody(bodyDef);
    }

    //Creamos método para la fixture
    public void createFixture(){
        PolygonShape shape = new PolygonShape();

        //Tamaño de la física de la pala
        shape.setAsBox(SHOVEL_WIDTH / 2, SHOVEL_HEIGHT);
        this.fixture = this.body.createFixture(shape, 2);
        this.fixture.setUserData(Utils.USER_SHOVEL_LEFT);

        shape.dispose();
    }

    @Override
    public void act(float delta) {
        //Evitamos que la física de la pala se mueva en el eje angular
        body.setAngularVelocity(0);

        /*Es posible que la física de la pala siga rotando un poco al chocar en las partes superiores e inferiores
         *se deba a que la pala está colisionando con otros cuerpos en el mundo
         *así que stablecemos la rotación de la pala en cero en frame*/
        body.setTransform(body.getPosition(), 0);

        //Verificamos si hay un toque en la pantalla
        if (Gdx.input.isTouched()) {
            //Convertimos las coordenadas del toque al mundo de Box2D
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            getStage().getCamera().unproject(touchPos);

            //Calculamos la distancia en el eje Y entre la pala y la posición del toque
            float targetY = touchPos.y;
            float paddleY = body.getPosition().y;
            float yDistance = targetY - paddleY;

            //Calculamos la velocidad necesaria para mover la pala hacia la posición del toque
            float maxSpeed = 5f; // Ajusta la velocidad máxima como desees
            float ySpeed = yDistance / delta; // Velocidad en unidades de Box2D por segundo
            ySpeed = MathUtils.clamp(ySpeed, -maxSpeed, maxSpeed); // Limitamos la velocidad máxima
            body.setLinearVelocity(0f, ySpeed);
        } else {
            //Si no hay toque, detenemos la pala
            body.setLinearVelocity(0f, 0f);
        }
    }

    public void draw(Batch batch, float parentAlpha){
        //Colocamos el sprite
        setPosition(body.getPosition().x - 0.4f, body.getPosition().y - 1f);
        //Ancho y alto del sprite y posición en la que se dibuja
        batch.draw(this.shovelAnimation.getKeyFrame(stateTime,true), getX()  + 0.25f, getY() + 0.25f, 0.25f, 1.5f );

        stateTime += Gdx.graphics.getDeltaTime();
    }

    //Liberamos recursos
    public void detach(){
        this.body.destroyFixture(this.fixture);
        this.world.destroyBody(this.body);
    }

}