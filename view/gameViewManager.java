package view;

import java.util.ArrayList;

import Agent.Agent;
import Agent.Brain;
import Agent.Player;
import Agent.ML.Sequential;
import Agent.ML.serializeModels;
import Agent.ML.Exceptions.HudException;
import Agent.ML.HyperParams.Tuner;
import defaultSettings.GameSettings;
import defaultSettings.MLSettings;
import game.HUD;
import game.MovingBackground;
import game.ScoreCount;
import game.Tubes;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.MenuButtons;
import model.ScoreBoard;
import subScenes.ScoreScene;
/**
 * <h1>Game Manager</h1>
 * 
 * <p>This is the the game manager, it is responsible for everything thats on the game scene.
 * Things that the game manager is responsible for:</p>
 * 
 * -Tubes
 * -agent
 * -background
 * -HUDs
 * -game steps
 * -reseting Entitys
 * - Initializations of buttons, gametimers and more
 * @author roeec
 *
 */
public class gameViewManager extends StageManager {
	private AnchorPane gamePane;
	private MainMenuManager mainMenu;
	private Sequential model;
	private serializeModels sModels;
	private ScoreBoard scoreBoard;
	// Game Settings
	private int gameType;
	private AnimationTimer gameTimer;
	private long lastUpdateTime;
	private MovingBackground background;
	private ScoreCount scoreCnt;
	private HUD hud;
	private Tuner tuner;
	// Flappy Bird
	private Agent[] agents;
	private Thread[] agentThreads;
	private boolean isML;
	private int amountOfAgents = 1;

	// Tubes
	private Tubes tubes;
	private int tubesAmount = 3;
	private ArrayList<Double> tubesPos;
	//Tube Info:
	//tubePos keeps the coordinates about the 2 upcoming tubes
	//Element	Axis	Info
	//	0		(Y)	    Tube is above
	//	1		(Y)	    Tube is bellow
	//	2		(X) 	Tube starts at
	//	3		(X)	    Tube ends at

	public static boolean stopGame = false;

	// ScoreBoard
	private final double SCORE_BOARD_WIDTH;
	private final double SCORE_BOARD_HEIGHT;
	/*
	 * gameType = 0 is Player playing
	 * gameType = 1 is ML playing
	 */
	// Constructor
	public gameViewManager(MainMenuManager mainMenu,serializeModels sModels,Sequential model,int gameType, ScoreBoard scoreBoard, Tuner tuner) {
		this.scoreBoard = scoreBoard;
		this.sModels = sModels;
		this.mainMenu = mainMenu;
		this.model = model;
		this.gameType = gameType;
		this.SCORE_BOARD_WIDTH = GameSettings.WIDTH / 3;
		this.SCORE_BOARD_HEIGHT = GameSettings.HEIGHT / 2;
		this.tuner = tuner;
		initializeStage();

	}
	
	// Initializes stage,scene and gamepane
	public void initializeStage() {
		gamePane = new AnchorPane();
		scene = new Scene(gamePane, GameSettings.WIDTH, GameSettings.HEIGHT);
		stage = new Stage();
		stage.setScene(scene);
//		stage.setIconified(true);
	}

	// Starting game play
	public void openGame(Stage menuStage) {
		setBackground();
		createTubes();
		createScoreBox();
		
		createAgents();
		
		
		stage.show();
		menuStage.hide();
		menuStage.close();
		createGameLoop();
		
	}
	
	// Creating HUD
	private void createHUD() {
		try {
			hud = new HUD(gamePane);
			hud.addHudParam("Loss", "Invalid");
			hud.addHudParam("State", "Invalid");
			hud.addHudParam("Reward", "Invalid");
			hud.addHudParam("Learning Rate", "Invalid");
			hud.addHudParam("Action", "Invalid");
			
			hud.addHudParam("batch", "Invalid");
			hud.addHudParam("epochs", "Invalid");
			hud.addHudParam("updateNEpochs", "Invalid");
			hud.addHudParam("startLR", "Invalid");
			hud.addHudParam("lRTimes", "Invalid");
			hud.addHudParam("gamma", "Invalid");
		}catch(HudException he) {
			he.printStackTrace();
		}
	}
	
	// Creating Stop Button 
	private void createStopButton() {
		int button_Width = (int) (MainMenuManager.BUTTON_WIDTH / 3);
		int button_Height = (int) (MainMenuManager.BUTTON_HEIGHT / 2);
		
		MenuButtons StopB = new MenuButtons("Stop",button_Width,button_Height,false,0);
		
		StopB.setLayoutX(GameSettings.WIDTH / 2 - button_Width/2);
		StopB.setLayoutY(GameSettings.HEIGHT * 29 / 32 );
		gamePane.getChildren().add(StopB);
		StopB.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stopAgents();
			};
		});
	}
	
	// Stops all agents
	private void stopAgents() {
		for (int i = 0; i < amountOfAgents; i++)
			agents[i].stopBird();
	}
	
	// Creates agents
	private void createAgents() {
		if(gameType == 0)
			createPlayers();
		else {
			createHUD();
			createML();
			createStopButton();
		}
	}
	
	// Creates human player agents
	private void createPlayers() {
		agentThreads = new Thread[amountOfAgents];
		agents = new Player[amountOfAgents];
		isML = false;
		for (int i = 0; i < amountOfAgents; i++) {
			agents[i] = new Player(gamePane, scoreCnt, tubesPos, scoreBoard);
			agentThreads[i] = new Thread(agents[i]);
			agentThreads[i].start();
			System.out.println("i=" + i);
		}
	}
	// Creates Machine learning agents
	private void createML() {
		scoreCnt.addMaxScoreCounter();
		agentThreads = new Thread[amountOfAgents];
		agents = new Brain[amountOfAgents];
		isML = true;
		for (int i = 0; i < amountOfAgents; i++) {
			agents[i] = new Brain(gamePane, scoreCnt, tubesPos, hud,sModels,model,tuner);
			agentThreads[i] = new Thread(agents[i]);
			agentThreads[i].start();
			System.out.println("i=" + i);
		}
	}
	// Checks if their are still threads that their agent is alive
	private boolean checkIfThreadsAreAlive() {
		for (int i = 0; i < amountOfAgents; i++)
			if (agentThreads[i].isAlive())
				return true;
		return false;
	}
	// activates the gameStep function for all agents
	private boolean agentsStep() {
		boolean resetEnviorment = true;
		for (int i = 0; i < amountOfAgents; i++)
			if(!agentThreads[i].isAlive() || !agents[i].getReadyForNextStep() || agents[i].gameStep())
				resetEnviorment = false;
//		resetEnviorment = resetEnviorment && agentThreads[i].isAlive() && agents[i].gameStep();

		return resetEnviorment;
	}
	// Checks if all agents are ready for the next gamestep
	private boolean agentsReadyForNextStep() {
		for (int i = 0; i < amountOfAgents; i++)
			if(!agents[i].getReadyForNextStep())
				return false;
		return true;
	}
	
	// Creates tubes
	private void createTubes() {
		tubesPos = new ArrayList<Double>(4);
		tubes = new Tubes(tubesAmount, tubesPos, gamePane);
	}
	// Creates scoreCount HUD
	private void createScoreBox() {
		scoreCnt = new ScoreCount(gamePane);
	}
	// updating Score HUD with +1 score
	public void updateScoreBox() {
		scoreCnt.updateScore();
	}

	// closes the windows and returns to the main menu
	public void backToMainMenu() {
		mainMenu.showMenu(stage);
	}
	
	// Resets environment settings
	private void resetEnvironmentSettings() {
		if(MLSettings.PRINT)
			System.out.println("Reseting environment settings!");
		tubes.resetTubes();
		for(Agent agent : agents)
			agent.resetBirdSettings();
		background.resetLayout();
		scoreCnt.resetScoreCount();
	}
	
	// The function calls all the functions needed for moving components, agent game steps, reseting the environment  
	private void createGameLoop() {
		gameTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if(isML || now - lastUpdateTime >= 15_000_000) {
					if(agentsReadyForNextStep()) {
						tubes.fixposTubes();
						tubes.moveTubes();
						background.moveBackground();
						boolean resetEnviorment = agentsStep();
						if(resetEnviorment) {
							MLSettings.EPISODES--;
							if(MLSettings.EPISODES <= 0) {
								gameTimer.stop();
								stopAgents();
								mainMenu.showMenu(stage);
							}
							resetEnvironmentSettings();
						}
						else if (!checkIfThreadsAreAlive()) {
							System.out.println("STOPPED GAME!");
							gameTimer.stop();
							mainMenu.showMenu(stage);
						}
					}
					lastUpdateTime = now;
				}
			}
		};
		gameTimer.start();
	}

//	private void openScoreBoard() {
//		double height = 5 * GameSettings.HEIGHT / 12;
//		double startPointY = -SCORE_BOARD_HEIGHT * 1.1;// -1 * height;
//		double endPointY = SCORE_BOARD_HEIGHT * 1.2;// height + HEIGHT / 12;
//		ScoreScene scoreScene = new ScoreScene(SCORE_BOARD_WIDTH, SCORE_BOARD_HEIGHT, startPointY, endPointY);
//		scoreScene.setLayoutX(GameSettings.WIDTH / 4);
//		scoreScene.setLayoutY(startPointY);
//		gamePane.getChildren().add(scoreScene);
//		System.out.println(scoreScene.getLayoutX() + " " + scoreScene.getLayoutY());
//		scoreBoard = new ScoreBoard(scoreScene.getPane(), 5);
//		scoreScene.movSubScene();
//	}
	//sets the background of the game
	private void setBackground() {
		background = new MovingBackground(gamePane);
	}

}
