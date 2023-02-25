package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.extra.Utils;

public class Ball extends Actor {

    private static final float BALL_WIDTH = 0.25f;
    private static final float BALL_HEIGHT = 0.25f;
    public static final float BALL_RAIDUS = 0.10f;
    public static final float BALL_SPEED = -2f;

    private TextureRegion ballTexture;
    private World world;
    private Body body;
    private Fixture fixture;

    //Constructor con mundo, textura y posicion
    public Ball(World world, TextureRegion ballTexture, Vector2 position) {
        this.world = world;
        this.ballTexture = ballTexture;

        createBody(position);
        createFixture();
    }
    //Método para crear la pelota donde indicamos la forma de su body, su ID, y su velocidad inicial
    private void createBody(Vector2 position){
        BodyDef def = new BodyDef();
        def.position.set(position);
        def.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(def);

        body.setUserData(Utils.USER_BALL);
        body.setLinearVelocity(BALL_SPEED,0);
    }

    //Creamos método para la fixture
    private void createFixture(){
        CircleShape circular = new CircleShape();
        circular.setRadius(BALL_RAIDUS);

        this.fixture = this.body.createFixture(circular,2);
        circular.dispose();

        fixture.setUserData(Utils.USER_BALL);
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        //damos la posición de la bola
        setPosition(this.body.getPosition().x - (BALL_WIDTH / 2), this.body.getPosition().y - (BALL_HEIGHT / 2));
        batch.draw(this.ballTexture, getX(), getY(), BALL_WIDTH, BALL_HEIGHT);
    }
    //Liberamos recursos
    public void detach(){
        if(!world.isLocked()){
            this.body.destroyFixture(fixture);
            this.world.destroyBody(body);
        }
    }
    //Función que sirve para aplicar la dirección a la pelota dependiendo en donde haya colisionado
    public void aplicarImpulso(int n){
        Vector2 vc = null;
        switch(n){
            //Si ha puntuado la pala izquierda
            case -2:
                vc = new Vector2(2, 0);
                break;
            //Si ha puntuado la pala derecha
            case -1:
                vc = new Vector2(-2, 0);
                break;
            //Si la pelota ha rebotado con la pala izquierda
            case 0:
                vc = new Vector2(3, MathUtils.random(-5, 5));
                break;
            //Si la pelota ha rebotado con la pala derecha
            case 1:
                vc = new Vector2(-3, MathUtils.random(-5, 5));
                break;
            //Si la pelota ha rebotado en el techo yendo para la derecha(rebotada por la pala izquierda)
            case 2:
                vc = new Vector2(3, MathUtils.random(-5, -1));
                break;
            //Si la pelota ha rebotado en el techo yendo para la izquierda(rebotada por la pala derecha)
            case 3:
                vc = new Vector2(-3, MathUtils.random(-5, -1));
                break;
            //Si la pelota ha rebotado en el suelo yendo para la derecha(rebotada por la pala izquierda)
            case 4:
                vc = new Vector2(3, MathUtils.random(1, 5));
                break;
            //Si la pelota ha rebotado en el suelo yendo para la izquierda(rebotada por la pala derecha)
            case 5:
                vc = new Vector2(-3, MathUtils.random(1, 5));
                break;
        }
        this.body.setLinearVelocity(vc);
    }
    //Para la bola
    public void stopBall(){
        this.body.setLinearVelocity(0,0);
        this.body.setAngularVelocity(0);
    }
}