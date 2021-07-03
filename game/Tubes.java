package game;

import java.util.ArrayList;
import java.util.Random;

import defaultSettings.GameSettings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import view.gameViewManager;
/**
 * <h1>Tubes</h1>
 * <p>Tubes is in charge of moving visual tubes around in the world. In addition it is responsible for updating the tubePos list that contains the upcoming tube info</p>
 * @author roeec
 */
public class Tubes {
	
	private Pane gamePane;
	private Random rand;
	private ImageView[][] tubes;
	
	// Tube Mechanics
	private int currTubeFix;
	private int upcomingTube;
	private int tubesAmount;
	private final String TUBE_IMAGE = "model\\recources\\pipe.png";
	private final String UPSIDED_DOWN_TUBE_IMAGE = "model\\recources\\upside_down_pipe.png";
	public static final double TUBE_WIDTH = 200;
	private final double TUBE_HEIGHT = 960;//800;
	private final double UPSIDE_DOWN_TUBE_HEIGHT = 800;
	public static final double diffXBetweenTubes = GameSettings.WIDTH / 3 + 3 * TUBE_WIDTH;
	private final double diffYBetweenTubes = 7 * GameSettings.HEIGHT / 12;//4 * HEIGHT / 12 + UPSIDE_DOWN_TUBE_HEIGHT;
	private ArrayList<Double> tubesPos;
	public static double startVal;
	/**
	 * Constructor
	 * @param tubesAmount - amount of tube pairs
	 * @param tubesPos	  - list containing upcoming tubes info
	 * @param gamePane	  - game pane
	 */
	public Tubes(int tubesAmount,ArrayList<Double> tubesPos, Pane gamePane) {
		this.tubesAmount = tubesAmount;
		this.gamePane = gamePane;
		this.tubesPos = tubesPos;
		rand = new Random(System.currentTimeMillis());
		createTubes();
		
	}
	/**
	 * The function creates the tube images and initializes the tubePos list
	 */
	private void createTubes() {
		tubes = new ImageView[tubesAmount][2];
		boolean hasLoaded = true;
		setStartValDefault();
		for (int i = 0; i < tubes.length; i++) {
			try {
				tubes[i][0] = new ImageView(new Image(TUBE_IMAGE, TUBE_WIDTH, TUBE_HEIGHT, false, false));
				tubes[i][1] = new ImageView(new Image(UPSIDED_DOWN_TUBE_IMAGE, TUBE_WIDTH, UPSIDE_DOWN_TUBE_HEIGHT, false, false));
			} catch (IllegalArgumentException e) {
				hasLoaded = false;
				tubes[i][0] = new ImageView(new Image(GameSettings.DefaultErrorImage, TUBE_WIDTH, TUBE_HEIGHT, false, false));
				tubes[i][1] = new ImageView(new Image(GameSettings.DefaultErrorImage, TUBE_WIDTH, UPSIDE_DOWN_TUBE_HEIGHT, false, false));
			}
			setTubesLayoutDefault(i);
			gamePane.getChildren().add(tubes[i][0]);
			gamePane.getChildren().add(tubes[i][1]);
			
			
		}
		tubesPos.add(0.0);
		tubesPos.add(0.0);
		tubesPos.add(0.0);
		tubesPos.add(0.0);

		setTubesPosDefault();
		System.out.println("Above= " + tubesPos.get(0) + " bellow = " + tubesPos.get(1));
		if (hasLoaded)
			System.out.println("ERROR: Could not load Tubes Image! (Tubes.java)");
	}
	// Reset startVal variable
	private void setStartValDefault() {
		startVal = diffXBetweenTubes;//0.9 * WIDTH;
	}
	/**
	 * Sets tubes layout to default layout
	 * @param i - the relevant tube pair
	 */
	public void setTubesLayoutDefault(int i) {
		tubes[i][0].setLayoutX(startVal);
		tubes[i][1].setLayoutX(startVal);
		tubes[i][1].setLayoutY(randomHight());
		tubes[i][0].setLayoutY(tubes[i][1].getLayoutY() + diffYBetweenTubes + UPSIDE_DOWN_TUBE_HEIGHT);
		startVal += diffXBetweenTubes;
	}
	
	/**
	 * Sets the tube's position to the default position
	 */
	public void setTubesPosDefault() {
		tubesPos.set(0, (double) (tubes[currTubeFix % 3][1].getLayoutY() + UPSIDE_DOWN_TUBE_HEIGHT));	//tube is above Y
		tubesPos.set(1, (double) tubes[currTubeFix % 3][0].getLayoutY());	//tube is bellow Y
		tubesPos.set(2,(double) tubes[currTubeFix % 3][0].getLayoutX());	//tube starts at (X)
		tubesPos.set(3, (double) (tubes[currTubeFix % 3][0].getLayoutX() + TUBE_WIDTH));	//tubes ends at (X)
	}
	/**
	 * Resets the tube's position, and tubePos's info
	 */
	public void resetTubes() {
		currTubeFix = 0;
		upcomingTube = 0;
		setStartValDefault(); 
		for (int i = 0; i < tubes.length; i++)
			setTubesLayoutDefault(i);
		setTubesPosDefault();
	}
	/**
	 * The function calculates a random height that the space between the tubes will start at
	 * @return - random height calculated
	 */
	public double randomHight() {
		return (double) (-0.9 * UPSIDE_DOWN_TUBE_HEIGHT + rand.nextDouble() * (GameSettings.HEIGHT * 9 / 10 - diffYBetweenTubes));
	}
	
	/**
	 * Moves the tubes visually and updates the tubesPos list with correct info
	 */
	public void moveTubes() {
		for (int i = 0; i < tubes.length; i++) {
			tubes[i][0].setLayoutX(tubes[i][0].getLayoutX() - GameSettings.speedYTimer * GameSettings.WIDTH / 240);
			tubes[i][1].setLayoutX(tubes[i][1].getLayoutX() - GameSettings.speedYTimer * GameSettings.WIDTH / 240);
		}
		if(tubes[upcomingTube % 3][0].getLayoutX() + TUBE_WIDTH * 1.2 < FlappyBird.birdX) {
			upcomingTube++;
			tubesPos.set(0, (double) (tubes[upcomingTube % 3][1].getLayoutY() + UPSIDE_DOWN_TUBE_HEIGHT));	//tube is above Y
			tubesPos.set(1, (double) tubes[upcomingTube % 3][0].getLayoutY());
		}
		
		
		tubesPos.set(2, (double) (tubes[upcomingTube % 3][0].getLayoutX() - TUBE_WIDTH / 2));	//tube starts at X
		tubesPos.set(3, (double) (tubesPos.get(2) + TUBE_WIDTH));	//tube ends at X
	}
	/**
	 * Fixs a pair of tube's position once it has left the screen completely(left side of screen)
	 * it puts the pair on the right side of the screen outside visual appereance
	 */
	public void fixposTubes() {
		
		if (tubes[currTubeFix % 3][0].getLayoutX() <= -1.1 * TUBE_WIDTH) {
			tubes[currTubeFix % 3][0].setLayoutX(tubes[(currTubeFix + 2) % 3][0].getLayoutX() + diffXBetweenTubes);
			tubes[currTubeFix % 3][1].setLayoutX(tubes[(currTubeFix + 2) % 3][0].getLayoutX() + diffXBetweenTubes);
			tubes[currTubeFix % 3][1].setLayoutY(randomHight());
			tubes[currTubeFix % 3][0].setLayoutY(tubes[currTubeFix % 3][1].getLayoutY() + diffYBetweenTubes + UPSIDE_DOWN_TUBE_HEIGHT);
			
			currTubeFix++;
		}
	}
}
