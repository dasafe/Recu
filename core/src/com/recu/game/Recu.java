package com.recu.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import sun.java2d.ScreenUpdateManager;

public class Recu extends ApplicationAdapter {

	SpriteBatch batch;
	float stateTime;

	Animation<TextureRegion> espaldaAnimation;
	Animation<TextureRegion> caraAnimation;
	Animation<TextureRegion> derechaAnimation;
	Animation<TextureRegion> balaAnimation;

	private TextureAtlas pj;
	private TextureAtlas b;
	private TextureRegion animacion;
	private TextureRegion espalda0;
	private TextureRegion cara0;
	private TextureRegion derecha0;
	private TextureRegion izquierda0;
	private TextureRegion balaImg;
	public static Texture fondo;

	private int posicion;
	private OrthographicCamera camera;
	Rectangle jugador;
	Array<Rectangle> balas;
	long lastDropTime;
	private BitmapFont hud;
	float respawn = 1;
	boolean over = false;
	boolean inicio = true;

	public float finalTime;
	public float startTime;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 320);

		fondo = new Texture("textures/fondo.jpg");
		pj = new TextureAtlas(Gdx.files.internal("textures/pj.pack"));
		espalda0 = pj.findRegion("espalda0");
		cara0 = pj.findRegion("cara0");
		derecha0 = pj.findRegion("derecha0");
		izquierda0 = pj.findRegion("izquierda0");
		izquierda0.flip(true,false);

		b = new TextureAtlas(Gdx.files.internal("textures/bala.pack"));
		balaAnimation =  new Animation<TextureRegion>(20f, b.findRegions("bala"), Animation.PlayMode.LOOP);

		caraAnimation = new Animation<TextureRegion>(20f, pj.findRegions("cara"), Animation.PlayMode.LOOP);
		espaldaAnimation = new Animation<TextureRegion>(20f, pj.findRegions("espalda"), Animation.PlayMode.LOOP);
		derechaAnimation = new Animation<TextureRegion>(20f, pj.findRegions("derecha"), Animation.PlayMode.LOOP);

		jugador = new Rectangle();

		jugador.width = 30;
		jugador.height = 40;

		jugador.x = 480 / 2 - jugador.width  / 2;
		jugador.y = 320 / 2 - jugador.height / 2;

		hud = new BitmapFont();

		balas = new Array<Rectangle>();
		spawnBalas();
	}

	@Override
	public void render () {
		stateTime += 1 + Gdx.graphics.getDeltaTime();
		if (inicio){
			startTime = stateTime;
		}
		Gdx.gl.glClearColor(0, 0, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(fondo,0,0);
		if (!over)
		hud.draw(batch, "Tiempo sobrevivido: " + Math.round((stateTime - startTime)/100), 5, 310);

		if(Gdx.input.isKeyPressed(Input.Keys.UP)){
			animacion = new Sprite(espaldaAnimation.getKeyFrame(stateTime,true));
			posicion = 1;
		}else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
			animacion = new Sprite(caraAnimation.getKeyFrame(stateTime,true));
			posicion = -1;
		}else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			animacion = new Sprite(derechaAnimation.getKeyFrame(stateTime,true));
			posicion = 2;
		}else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			animacion = new Sprite(derechaAnimation.getKeyFrame(stateTime,true));
			animacion.flip(true,false);
			posicion = -2;
		}else if (Gdx.input.isTouched()) {
			if (Gdx.input.getX() < Gdx.graphics.getWidth() * 5 / 12){
				animacion = new Sprite(derechaAnimation.getKeyFrame(stateTime,true));
				animacion.flip(true,false);
				posicion = -2;
			} else if (Gdx.input.getX() > Gdx.graphics.getWidth() * 7 / 12) {
				animacion = new Sprite(derechaAnimation.getKeyFrame(stateTime,true));
				posicion = 2;
			}
			if (Gdx.input.getY() < Gdx.graphics.getHeight() * 5 / 12){
				animacion = new Sprite(espaldaAnimation.getKeyFrame(stateTime,true));
				posicion = 1;
			} else if (Gdx.input.getY() > Gdx.graphics.getHeight() * 7 / 12){
				animacion = new Sprite(caraAnimation.getKeyFrame(stateTime,true));
				posicion = -1;
			}
		}else {
			if (posicion == -1){
				animacion = cara0;
			}else if (posicion == 2){
				animacion = derecha0;
			}else if (posicion == -2){
				animacion = izquierda0;
			}else {
				animacion = espalda0;
			}
		}
		if (Gdx.input.isTouched()) {
			if (Gdx.input.getX() < Gdx.graphics.getWidth() * 5 / 12){
				jugador.x -= 200 * Gdx.graphics.getDeltaTime();
			} else if (Gdx.input.getX() > Gdx.graphics.getWidth() * 7 / 12) {
				jugador.x += 200 * Gdx.graphics.getDeltaTime();
			}
			if (Gdx.input.getY() < Gdx.graphics.getHeight() * 5 / 12){
				jugador.y += 200 * Gdx.graphics.getDeltaTime();
			} else if (Gdx.input.getY() > Gdx.graphics.getHeight() * 7 / 12){
				jugador.y -= 200 * Gdx.graphics.getDeltaTime();
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
			jugador.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			jugador.x += 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
			jugador.y -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.UP))
			jugador.y += 200 * Gdx.graphics.getDeltaTime();

		if (jugador.x < 0)
			jugador.x = 0;
		if (jugador.x > 480 - jugador.width)
			jugador.x = 480 - jugador.width;
		if (jugador.y < 0)
			jugador.y = 0;
		if (jugador.y > 320 - jugador.height)
			jugador.y = 320 - jugador.height;

		for (Rectangle bala : balas) {
			bala.x += bala.getWidth() * 5 * Gdx.graphics.getDeltaTime();
			bala.y += bala.getHeight() * 5 * Gdx.graphics.getDeltaTime();
			balaImg = new Sprite(balaAnimation.getKeyFrame(stateTime,true));
			batch.draw(balaImg, bala.x, bala.y, bala.width,bala.height);
		}

		batch.draw(animacion,jugador.x,jugador.y,jugador.width,jugador.height);

		if (!over) {
			if (TimeUtils.nanoTime() - lastDropTime > 1000000000 / respawn) {
				respawn += 0.02;
				spawnBalas();
			}

			Iterator<Rectangle> iter = balas.iterator();

			while (iter.hasNext()) {
				Rectangle bala = iter.next();

				if (bala.y < -10 || bala.y > 320) {
					iter.remove();
				}
				if (bala.x < -10 || bala.x > 480) {
					iter.remove();
				}

				if (bala.overlaps(jugador)) {
					finalTime = stateTime - startTime;
					iter.remove();
					over = true;
				}
			}
		}
		if (!over){
			inicio = false;
			batch.end();
		}
		if (over){
			hud.draw(batch, "GAME OVER", 480/2-45, 320/2+20);
			hud.draw(batch, "Tiempo sobrevivido: " + Math.round((finalTime) / 100) +" s", 480/2-72, 320/2);
			hud.draw(batch, "Enter o pulsa AQUI para reintentar", 480/2-100, 30);
			batch.end();
			if (Gdx.input.isTouched()) {
				if (Gdx.input.getY() > Gdx.graphics.getHeight() * 11 / 12){
					over = false;
					inicio = true;
					create();
					render();
				}
			}else if (Gdx.input.isKeyPressed(Input.Keys.ENTER)){
				over = false;
				inicio = true;
				create();
				render();
			}
		}
	}

	private void spawnBalas() {
		Rectangle bala = new Rectangle();
		bala.x = MathUtils.random(5, 470);
		bala.y = MathUtils.random(5, 310);
		bala.width=0;
		bala.height=0;
		while (bala.getWidth() > -20 && bala.getWidth() < 20){
			bala.setWidth(MathUtils.random(-30, 30));
		}
		while (bala.getHeight() > -20 && bala.getHeight() < 20){
			bala.setHeight(MathUtils.random(-30, 30));
		}
		balas.add(bala);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
