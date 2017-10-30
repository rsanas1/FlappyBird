package com.rishi;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;

	Texture gameOver;

	Texture background;   //In game Texture is images

	Texture [] birds;
	Texture toptube;
	Texture bottomtube;

	int flapstate = 0;  // to decide which bird image to display
	float bird_y = 0;
	float velocity = 0;
	float gravity = (float) 1.5;
	int gameState = 0; // game has began or not
	Random random;
	float maxTubeOffset;
	float tubeVelocity = 4;
	int gap = 500;
	int numberOfTubes = 4;
	float [] tubeX = new float[numberOfTubes];
	float [] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	Rectangle[] topTubeRectangle;
	Rectangle[] bottomTubeRectangle;
	Circle birdCircle;
	ShapeRenderer shapeRenderer;

	int score;
	int scoringTube;
	BitmapFont font;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		score =0;
		scoringTube =0;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(6);


		gameOver =new Texture("game_over.png");
		toptube = new Texture("toptube.png");
		bottomtube = new Texture("bottomtube.png");

		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		random= new Random();

		topTubeRectangle =new Rectangle[numberOfTubes];
		bottomTubeRectangle =new Rectangle[numberOfTubes];
		birdCircle = new Circle();
		shapeRenderer = new ShapeRenderer();

		distanceBetweenTubes =Gdx.graphics.getWidth() * 3/4;

		setUpGame();

	}

	public int getHighScore(){

		Preferences prefs = Gdx.app.getPreferences("MyPref");
			 return prefs.getInteger("max");

	}

	public void setHighScore(int score){
		Preferences prefs = Gdx.app.getPreferences("MyPref");
		prefs.putInteger("max", score);
		prefs.flush();
	}

	public void setUpGame(){


		bird_y = Gdx.graphics.getHeight()/2 - (birds[0].getHeight()/2);

		for(int i=0;i<numberOfTubes;i++){

			tubeOffset[i] = (random.nextFloat() -0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			tubeX[i] = Gdx.graphics.getWidth() / 2 - (bottomtube.getWidth() / 2) +i*distanceBetweenTubes +Gdx.graphics.getWidth();

			topTubeRectangle[i] = new Rectangle();
			bottomTubeRectangle[i] = new Rectangle();
		}
	}


	@Override
	public void render () {



		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState ==1) {

			if(tubeX[scoringTube]< Gdx.graphics.getWidth()/2)
			{
				score++;
				Gdx.app.log("Score" ,score+"");

				scoringTube = (scoringTube+1) % numberOfTubes;
			}

			if(Gdx.input.justTouched()){

				velocity = -30;

			}

			for(int i=0;i<numberOfTubes;i++) {

				if( tubeX[i]< - toptube.getWidth()){

					tubeX[i] +=numberOfTubes * distanceBetweenTubes ;
					tubeOffset[i] = (random.nextFloat() -0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				}
				else {
					tubeX[i] -= tubeVelocity;
				}

				batch.draw(bottomtube, tubeX[i]
						, Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOffset[i]);
				batch.draw(toptube, tubeX[i]
						,Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i]);

				topTubeRectangle[i] =new Rectangle(tubeX[i]
						,Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i],toptube.getWidth(),toptube.getHeight());

				bottomTubeRectangle[i] =new Rectangle(tubeX[i]
						, Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOffset[i],
						bottomtube.getWidth(), bottomtube.getHeight());


			}

			if(bird_y > 0 ){

				velocity +=gravity;
				bird_y -= velocity;
			}
			else{

				gameState = 2;
			}

		}
		else if(gameState == 0) {
			if(Gdx.input.justTouched()){

				gameState = 1;
			}
		}
		else{
			batch.draw(gameOver,Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2
					, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);

			if(score > getHighScore()){
				setHighScore(score);
			}

			font.draw(batch,"Score: "+String.valueOf(score),Gdx.graphics.getWidth()/2 - 150
					,Gdx.graphics.getHeight()/2 - 3 * gameOver.getHeight());
			font.draw(batch,"High Score: "+ getHighScore(),Gdx.graphics.getWidth()/2 - 250
					,Gdx.graphics.getHeight()/2 - 3*gameOver.getHeight() - 150);

			if(Gdx.input.justTouched()){

				gameState = 1;
				setUpGame();
				score =0;
				scoringTube =0;
				velocity=0;
			}
		}

		if (flapstate == 0) {
			flapstate = 1;
		} else {
			flapstate = 0;
		}


		batch.draw(birds[flapstate], Gdx.graphics.getWidth() / 2 - (birds[flapstate].getWidth() / 2)
				, bird_y);

		font.draw(batch,"Score :"+String.valueOf(score),50,Gdx.graphics.getHeight() - 50);
		batch.end();

		birdCircle.set(Gdx.graphics.getWidth()/2, bird_y + birds[flapstate].getHeight()/2, birds[flapstate].getWidth()/2);
		// x co-ordinate y co-ordinate radius

		//	shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//	shapeRenderer.setColor(Color.BLUE);
		//	shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

		for(int i=0 ; i<numberOfTubes ;i++){
			//		shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOffset[i],
			//			bottomtube.getWidth(),bottomtube.getHeight());

			//		shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i],
			//				toptube.getWidth(),toptube.getHeight());

			if(Intersector.overlaps(birdCircle , topTubeRectangle[i]) || Intersector.overlaps(birdCircle ,bottomTubeRectangle[i])){
				Gdx.app.log("Collision", "Yes");
				gameState = 2;
			}

		}
		shapeRenderer.end();
	}


}
