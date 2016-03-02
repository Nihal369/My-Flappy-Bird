package me.nihalismail.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class FlappyBird extends ApplicationAdapter {
	//Basic properties
	SpriteBatch batch;
	Texture background;
	Texture topTube;
	Texture bottomTube;
	Circle birdCircle;
	ShapeRenderer shapeRenderer;
	Texture stopGame;
	BitmapFont font;

	//Bird properties
	Texture[] birds;
	int flapState=0;
	int birdY;

	//Game properites
	float velocity=0;
	boolean gameStatus=false;
	float gap=400;
	int count;
	float maxTubeOffSet;
	int numberOfTubes=4;
	float[] tubex=new float[numberOfTubes];//X positions of 4tubes
	float[] tubeOffset=new float[numberOfTubes];//Offsets of 4 tubes
	float distanceBetweenTubes;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	boolean gameOver;

	//Java properties
	Random rg=new Random();

	//Scoring logic
	int score=0;
	int scoringTube=0;
	int highScore;


	@Override
	//Called only once when each time game is created
	public void create () {
		batch = new SpriteBatch();
		background=new Texture("bg.png");
		topTube=new Texture("toptube.png");
		bottomTube=new Texture("bottomtube.png");
		birds=new Texture[2];
		birds[0]=new Texture("bird.png");
		birds[1]=new Texture("bird2.png");
		birdY=Gdx.graphics.getHeight()/2-birds[0].getHeight()/2;
		distanceBetweenTubes= (float) (Gdx.graphics.getWidth()* 0.75);
		topTubeRectangles=new Rectangle[numberOfTubes];
		bottomTubeRectangles=new Rectangle[numberOfTubes];
		for(int i=0;i<numberOfTubes;i++) {
			tubex[i] = Gdx.graphics.getWidth() + 600+ i*distanceBetweenTubes;
			tubeOffset[i]=(rg.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
			topTubeRectangles[i]=new Rectangle();
			bottomTubeRectangles[i]=new Rectangle();
		}
		count=0;
		maxTubeOffSet=Gdx.graphics.getHeight()/2 -gap/2 -100;
		birdCircle=new Circle();
		shapeRenderer=new ShapeRenderer();
		score=0;
		font=new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().scale(10);
		stopGame=new Texture("gameover.png");
		gameOver=false;
		highScore=0;

	}

	@Override
	//Refreshes 60fps
	public void render () {
		//Start game
		if(Gdx.input.isTouched() && gameOver==false)
		{
			Gdx.app.log("Touched","true");
			gameStatus=true;
		}
		if (gameStatus)
		{
			//Flap wings of the bird
			if (flapState==1)
				flapState=0;
			else flapState=1;

			if(birdY>25  || velocity<0) {
				velocity += 1;
				birdY -= velocity;
			}
			//Upward movement
			if (Gdx.input.justTouched())
			{
				if(velocity<=0)
					velocity-=18;
				else
					velocity-=28;
			}
			//Gravity
			if(birdY>=Gdx.graphics.getHeight()-150) {
				birdY = Gdx.graphics.getHeight() - 150;
				velocity += 0.95;
				birdY -= velocity;
			}
		}

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		font.draw(batch,String.valueOf(score),Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()-200);
		//Score increases when pipe crosses half screen
		if(gameStatus) {
			if (tubex[scoringTube]<Gdx.graphics.getWidth()/2)
			{
				score++;
				Gdx.app.log("Score",String.valueOf(score));
				if(scoringTube<numberOfTubes-1) {
					scoringTube++;
				}
				else
				{
					scoringTube=0;
				}
			}
			//Tube logic
			for (int i = 0; i < numberOfTubes; i++) {
				if(tubex[i]< -topTube.getWidth()) {
					tubex[i]+=numberOfTubes*distanceBetweenTubes;
					tubeOffset[i]=(rg.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
				}
				else {
					tubex[i] -= 7;
					//Position defined for bottom left of each sprite
					batch.draw(topTube, tubex[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
					batch.draw(bottomTube, tubex[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
					topTubeRectangles[i]=new Rectangle(tubex[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
					bottomTubeRectangles[i]=new Rectangle(tubex[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());
					font.draw(batch,String.valueOf(score),Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()-200);
				};
			}
		}

		//Draw bird
		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
		batch.end();


		//Casting sprites as images for programming,Uncomment shaperenderer for shapes
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[0].getHeight() / 2, birds[0].getWidth() / 2);

		/*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);*/

		//Game over check
		for (int i=0;i<numberOfTubes;i++) {
			/*shapeRenderer.rect(tubex[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
			shapeRenderer.rect(tubex[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());*/
			if (Intersector.overlaps(birdCircle,bottomTubeRectangles[i]) || Intersector.overlaps(birdCircle,topTubeRectangles[i]))
			{
				Gdx.app.log("Collision","Yes");
				gameStatus=false;
				gameOver=true;
			}
		}
		if(gameOver)
		{
			batch.begin();
			batch.draw(stopGame, Gdx.graphics.getWidth() / 2 - stopGame.getWidth() / 2, Gdx.graphics.getHeight() / 2 - stopGame.getHeight() / 2);
			batch.end();

			//Update gigh score
			if(score>highScore) {
				highScore = score;
				batch.begin();
				font.draw(batch,"High score:"+String.valueOf(highScore),100,Gdx.graphics.getHeight()-400);
				batch.end();
			}

			//Batches need to begin and end to draw textures
			batch.begin();
			font.draw(batch,"High score:"+String.valueOf(highScore),100,Gdx.graphics.getHeight()-400);
			batch.end();

			//Restart game
			if (Gdx.input.isTouched())
			{
				gameOver=false;
				birdY=Gdx.graphics.getHeight()/2-birds[0].getHeight()/2;
				for(int i=0;i<numberOfTubes;i++) {
					tubex[i] = Gdx.graphics.getWidth() + 600+ i*distanceBetweenTubes;
					tubeOffset[i]=(rg.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
					topTubeRectangles[i]=new Rectangle();
					bottomTubeRectangles[i]=new Rectangle();
				}
				score=0;
				scoringTube=0;
				velocity=0;
			}
		}
/*		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
		shapeRenderer.end();*/
	}
}
