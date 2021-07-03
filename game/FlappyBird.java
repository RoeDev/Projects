package game;

import java.util.ArrayList;
import java.util.List;

import defaultSettings.GameSettings;
import defaultSettings.MLSettings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
/**
 * <h1>Flappy Bird</h1>
 * <p>Flappy bird stores the bird in the game and moves it accourding to actions made by the player, gravity and current vertical speed.\n
 * For a Bot/AI this is also the environment which they get states, send actions to, get rewards and isAlive</p>
 * @author roeec
 *
 */
public class FlappyBird {
	
	// Game Pane
	private Pane pane;
	
	// Score Count object
	private ScoreCount scoreUpdater;

	// Tube Coordinates
	ArrayList<Double> tubesPos;
	
	// Game Mechanics
	private boolean isAlive = true;
	private boolean jump;
	private boolean scoreflg = false;
	private double reward = 0;
	
	// Movement Mechanics
	private double angle;
	private double defaultSpeedY = 45;//50
	private double speedY;
	private double speedX = defaultSpeedY;
	private double time = 0.5 * GameSettings.speedYTimer;
	private double gravity = 5;
		
	// Bird Settings
	private ImageView bird;
	private boolean isFLapping = false;
	public static final double birdX = GameSettings.WIDTH / 4;
	
	// Lag Reduction Settings
	// Default checkPerXFrames = 1
	// Turn checkPerXFrames to a number bigger than if laggy, this means the bird will check death ever second frame
	private int frameCounter;
	private int checkPerXFrames = 1;
	
	/**
	 *  Constructor
	 * @param pane	- game pane
	 * @param scoreUpdater	- Score HUD
	 * @param tubesPos	- relevant tube positions (only upcoming tubes)
	 */
	public FlappyBird(Pane pane, ScoreCount scoreUpdater, ArrayList<Double> tubesPos) {
		this.scoreUpdater = scoreUpdater;
		this.pane = pane;
		this.tubesPos = tubesPos;
		jump = false;
		
		try {
			bird = new ImageView(new Image(GameSettings.BIRD_IMAGE, GameSettings.BIRD_WIDTH, GameSettings.BIRD_LENGTH, false, false));
		} catch (IllegalArgumentException e) {
			System.out.println("ERROR: Could not load Bird Image! (gameViewManager)");
			bird = new ImageView(new Image("LoadingErrorContents\\blackBox.png", GameSettings.BIRD_WIDTH, GameSettings.BIRD_LENGTH, false, false));
		}
		setBirdLayoutDefault();
		pane.getChildren().add(bird);
		
	}
	
	/**
	 *  Removes bird ImageView from Pane
	 */
	public void removeBirdFromPane() {
		pane.getChildren().remove(bird);
	}
	
	/**
	 *  Sets Bird default Position
	 */
	private void setBirdLayoutDefault() {
		bird.setLayoutX(birdX);
		bird.setLayoutY(GameSettings.HEIGHT / 2);
	}
	
	/**
	 * Jump Activation
	 */
	public void onKeyPressed() {
		if (!jump) {
			speedY = defaultSpeedY;
			jump = true;
		}
	}
	
	/**
	 *  Jump De-Activation
	 */
	public void onKeyReleased() {
		if (jump)
			jump = false;
	}
	
	/**
	 * Updates the bird's position and checks if bird is still alive in new State
	 */
	public void updateState() {
		// Calculate bird angle
		angle = -1 * Math.atan(speedY / speedX) * 180 / Math.PI;
		if (angle < 0)
			angle = -15;
		
		if (speedY > 0)
			bird.setRotate(angle);
		else
			bird.setRotate(angle);
		
		// Simulates gravity 
		gravity();
		
		// Simulates Flapping
		flapping();
		
		
		frameCounter++;
		//check if frameCounter is equal to checkPerXFrames to prevent lag
		if(frameCounter == checkPerXFrames) {
			// checking if the bird hit the borders or pillars
			if (checkHeightDetection() || checkPillarDetectionAndScoreDetection())
				isAlive = false;
			else
				isAlive = true;
			frameCounter = 0;
		}
	}
	
	
	/*
	 * START Hit Detection
	 */
	/**
	 * Checks if the bird hit a pillar(Tube) if so the function will return true, else false
	 * @return - true if the bird hit the pillar, else false
	 */
	private boolean checkPillarDetectionAndScoreDetection() {
		//if the bird is past the start point (X) of the upcoming tube
		if(tubesPos.get(2) <= birdX) {
			//if the bird is before the end point (X) of the upcoming tube
			if(tubesPos.get(3) + GameSettings.BIRD_WIDTH >= birdX) {    
				double birdY = (double) bird.getLayoutY();
				if(birdY <= tubesPos.get(0) || birdY + GameSettings.BIRD_LENGTH >= tubesPos.get(1)) {
					if(MLSettings.PRINT) {
						System.out.println("PillarX = " + tubesPos.get(2) + " PillarEndX= " + tubesPos.get(3) + " BirdX = " + birdX + "Above= " + tubesPos.get(0) + " bellow = " + tubesPos.get(1) + " BirdY = " + birdY);
						System.out.println("The bird has touched the Pilar! y=" + bird.getLayoutY() + " x= " + bird.getLayoutX());
					}
					reward = MLSettings.LOSS_REWARD;
					return true;
				}
				scoreflg = true;
			}
			else if(scoreflg){
				if(MLSettings.PRINT)
					System.out.println("Score++");
				scoreflg = false;
				reward = MLSettings.SCORE_REWARD;
				
				//Updates the scoreCount
				scoreUpdater.updateScore();
			}
		}
		return false;
	}

	/**
	 * Check if bird hit the roof or floor, will return true if so, else return false
	 * @return true if the bird hit the roof or floor, else false
	 */
	private boolean checkHeightDetection() {
		if (bird.getLayoutY() <= 0 || bird.getLayoutY() + GameSettings.BIRD_LENGTH>= GameSettings.HEIGHT) {
			if(MLSettings.PRINT)
				System.out.println("The bird has touched the border! y=" + bird.getLayoutY());
			reward = MLSettings.LOSS_REWARD;
			return true;
		}
		return false;
	}
	/*
	 * END Hit Detection
	 */

	/**
	 *  This function simulates gravity for the bird with real physics calculations
	 */
	private void gravity() {
		// y = y0 + v0*t + 0/5 * gravity * time^2
		// y  = new vertical height
		// y0 = current vertical height
		// t  = The time passed between the when y0 was calculated and atm, this is equal to a frame in my case, and i represented it's value at the begining of the file
		// gravity = t acceleration the bird gets towards the floor
		bird.setLayoutY(bird.getLayoutY() - speedY * time + 0.5 * gravity * time * time);
		
		// V = v0 + at
		// v = new vertical speed
		// gravity = t acceleration the bird gets towards the floor
		// t  = The time passed between the when y0 was calculated and atm, this is equal to a frame in my case, and i represented it's value at the begining of the file
		speedY = speedY - gravity * time;
	}
	
	/**
	 * The function simulates the flapping animation
	 */
	private void flapping() {
		if(speedY <= defaultSpeedY && speedY >= 8*defaultSpeedY / 12) {
			if(!isFLapping) {
				isFLapping = true;
				setFlapOn();
			}
		}
		else {
			isFLapping = false;
			setFlapOff();
		}
			
	}
	
	/**
	 * Sets the bird's image to flapping on
	 */
	private void setFlapOn() {
		try {
			bird.setImage(new Image(GameSettings.FLAP_BIRD_IMAGE, GameSettings.BIRD_WIDTH, GameSettings.BIRD_LENGTH, false, false));
		} catch (IllegalArgumentException e) {
			System.out.println("ERROR: Could not load Bird Image! (gameViewManager)");
			bird.setImage(new Image("LoadingErrorContents\\blackBox.png", GameSettings.BIRD_WIDTH, GameSettings.BIRD_LENGTH, false, false));
		}
	}
	
	/**
	 *  Sets the bird's image to flapping off
	 */
	private void setFlapOff() {
		try {
			bird.setImage(new Image(GameSettings.BIRD_IMAGE, GameSettings.BIRD_WIDTH, GameSettings.BIRD_LENGTH, false, false));
		} catch (IllegalArgumentException e) {
			System.out.println("ERROR: Could not load Bird Image! (gameViewManager)");
			bird.setImage(new Image("LoadingErrorContents\\blackBox.png", GameSettings.BIRD_WIDTH, GameSettings.BIRD_LENGTH, false, false));
		}
	}
	
	/**
	 *  Get State of bird
	 * @return - a list containing information about the bird in the world
	 */
	public List<Double> getBirdState() {
		List<Double> arr = new ArrayList<Double>();
		arr.add((tubesPos.get(0) - bird.getLayoutY()) / GameSettings.HEIGHT);							//the distance between the bird and the top part of upcoming tube (tube Height - bird height) (Y)
		arr.add((tubesPos.get(1) - bird.getLayoutY()) / GameSettings.HEIGHT);							//the distance between the bird and the bottom part of upcoming tube
		arr.add((tubesPos.get(2) - birdX) / Tubes.diffXBetweenTubes);								//the distance between the bird and the beginning of the upcoming (X)
		arr.add((tubesPos.get(3) + GameSettings.BIRD_WIDTH - birdX) / (Tubes.diffXBetweenTubes + Tubes.TUBE_WIDTH + GameSettings.BIRD_WIDTH));			//the distance between the bird and the end of the upcoming tube	(X)
		arr.add(bird.getLayoutY() / GameSettings.HEIGHT);												//bird height
		arr.add((speedY + defaultSpeedY) /  ((defaultSpeedY + Math.sqrt(2 * GameSettings.HEIGHT * gravity)) / 2) - 1);	    //bird current vertical speed
		return arr;
	}
	/**
	 *  Sets reward to Stay Alive reward
	 */
	public void resetReward() {
		this.reward = MLSettings.STAY_ALIVE_REWARD;
	}
	public double getReward() {
		return reward;
	}
	public boolean isAlive() {
		return isAlive;
	}
}