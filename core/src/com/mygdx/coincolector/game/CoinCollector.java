package com.mygdx.coincolector.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class CoinCollector extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background1;
	Texture background2;
	Texture[] bird;
	Texture gameOver;
	Random random;
	Rectangle birdRectangle;
	Rectangle groundRectngle;
	
	int birdState;
	int pause ;
	float gravity ;
	float velocity ;
	float birdY ;
	
	float GROUND_HEIGHT ;
	final int COIN_WAIT_TIME = 100;
	final int BOMB_WAIT_TIME = 300;
	int SCREEN_MAX_HEIGHT;
	final int SPEED = 500;
	final int BACKGROUND_CHANGE_COUNTER = 4;

	BitmapFont font;
	int score ;
	int gameState = 0;
	int travelSpeed = 4;
	int counterTravelSpeed = 0;
	int copyTravelSpeed = 4;
	int checkTravelSpeed = 0;

	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<>();
	Texture bomb;
	int bombCount;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background1 = new Texture("background_1.png");
		background2 = new Texture("background_2.jpg");

		bird = new Texture[3];
		bird[0] = new Texture("bird_fly_1.png");
		bird[1] = new Texture("bird_fly_2.png");
		bird[2] = new Texture("bird_hit.png");

		init();
		GROUND_HEIGHT = Gdx.graphics.getHeight() / 8;
		SCREEN_MAX_HEIGHT = Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 12;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();

		font = new BitmapFont();
		font.setColor(Color.CYAN);
		font.getData().setScale(10);

		gameOver = new Texture("game_over_2.png");

	}
	public void init(){
		birdState = 0;
		pause = 0;
		gravity = 0.2f;
		velocity = 0;
		birdY = Gdx.graphics.getHeight() / 2;
		score = 0;
		coinCount = 0;
		bombCount = 0;
		travelSpeed = 4;
		counterTravelSpeed = 0;

	}
	public float screenMaxHeight(float val){
		if(val >= SCREEN_MAX_HEIGHT){
			return SCREEN_MAX_HEIGHT;
		}else if(val <= GROUND_HEIGHT) {
			return GROUND_HEIGHT;
		}else{
			return val;
		}

	}
	public void make(ArrayList<Integer> ys,ArrayList<Integer> xs){
		float height = random.nextFloat()* Gdx.graphics.getHeight() + GROUND_HEIGHT;
		height = screenMaxHeight(height);
		ys.add((int)height);
		xs.add(Gdx.graphics.getWidth());
	}
	public void makeCoin(){
		make(coinYs,coinXs);
	}
	public void makeBomb(){
		make(bombYs,bombXs);
	}

	@Override
	public void render () {
		batch.begin();
		if(checkTravelSpeed % 2 == 0 ){
			batch.draw(background1,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}else{
			batch.draw(background2,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}


		/////////////////////////////// Game LIVE : State = 1//////////////////////////////
		if(gameState == 1){
			//Coins : take pause for COIN_WAIT_TIME units and then make a coin
			if(coinCount < COIN_WAIT_TIME){
				coinCount++;
			}else{
				coinCount = 0;
				makeCoin();
			}
			coinRectangle.clear();
			for(int i = 0; i < coinXs.size(); i++){
				batch.draw(coin, coinXs.get(i),coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - travelSpeed);
				coinRectangle.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}

			//bombs : take pause for BOMB_WAIT_TIME units and then make a bomb
			if(bombCount < BOMB_WAIT_TIME){
				bombCount++;
			}else{
				bombCount = 0;
				makeBomb();
			}
			bombRectangle.clear();
			for(int i = 0; i < bombXs.size(); i++){
				batch.draw(bomb, bombXs.get(i),bombYs.get(i));
				bombXs.set(i, bombXs.get(i) - (travelSpeed + 4));
				bombRectangle.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}
			//when touched on screen ,fly the bird
			if(Gdx.input.justTouched()){
				velocity = -10;
			}

			//take 6 units pause and then toggle bird state, which we will use to toggle bird image
			if(pause < 6){
				pause++;
			}else {

				pause = 0;
				if (birdState < 1) {
					birdState++;

				} else {
					birdState = 0;
				}
			}

			velocity += gravity;     //velocity: How much bird will fall
			birdY -= velocity;

			birdY = screenMaxHeight(birdY);
			
			//increasing speed of coins and bombs
			counterTravelSpeed++;
			if(counterTravelSpeed > travelSpeed + SPEED){
				travelSpeed++;
				counterTravelSpeed = 0;
			}
			//for background change
			if(travelSpeed == copyTravelSpeed + BACKGROUND_CHANGE_COUNTER){
				checkTravelSpeed++;
				copyTravelSpeed = travelSpeed;
			}

		}
		/////////////////////////////// Game Start : State = 0//////////////////////////////
		else if(gameState == 0){
			//Waiting to start
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		}else if(gameState == 2){
			//Game Over

			if(Gdx.input.justTouched()){
				init();
				gameState = 1;

				coinYs.clear();
				coinXs.clear();
				coinRectangle.clear();

				bombYs.clear();
				bombXs.clear();
				bombRectangle.clear();
			}


		}
		/////////////////////////////// Game Over State : State = 2//////////////////////////////
		if(gameState == 2) {
			batch.draw(bird[2], Gdx.graphics.getWidth() / 2 - bird[birdState].getWidth() / 2, birdY);
			batch.draw(gameOver,Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2,Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
		}else{
			batch.draw(bird[birdState], Gdx.graphics.getWidth() / 2 - bird[birdState].getWidth() / 2, birdY);
		}

		birdRectangle = new Rectangle( Gdx.graphics.getWidth() / 2 - bird[birdState].getWidth() / 2 , birdY,bird[birdState].getWidth(),bird[birdState].getHeight());
		groundRectngle = new Rectangle( 0, 0 ,Gdx.graphics.getWidth(),GROUND_HEIGHT + 1);


		//Collisions
		//1.With ground
		if (Intersector.overlaps(birdRectangle, groundRectngle)) {
//			Gdx.app.log("ground", "collisionGROUND");
			gameState = 2;

		}
		//2.With coin
		for(int i = 0; i < coinRectangle.size(); i++){
			if(Intersector.overlaps(birdRectangle, coinRectangle.get(i))){
//				Gdx.app.log("coin","collisionCOIN");
				score++;

				//To prevent overlapping with same coin ,we remove coin once collected
				coinRectangle.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);

				break;
			}
		}
		//3.With bomb
		for(int i = 0; i < bombRectangle.size(); i++) {
			if (Intersector.overlaps(birdRectangle, bombRectangle.get(i))) {
//				Gdx.app.log("bomb", "collisionBOMB");
				gameState = 2;

			}
		}
		System.out.println(Gdx.graphics.getSafeInsetBottom());
		font.draw(batch, String.valueOf(score), 100, 150);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
