package view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Agent.ML.Sequential;
import Agent.ML.serializeModels;
import Agent.ML.HyperParams.Tuner;
import defaultSettings.GameSettings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.shape.Arc;
import javafx.stage.Stage;
import model.MenuButtons;
import model.ScoreBoard;
import subScenes.FlappyBirdSubScene;
import subScenes.LoadModelScene;
/**
 * <h1>MainMenuManager</h1>
 * <p>The MainMenuManager is responsible for all menu related objects.\n
 * It launches </p>
 * @author roeec
 *
 */
public class MainMenuManager extends StageManager{
	private AnchorPane mainPane;
	public static final int BUTTON_WIDTH = 750;
	public static final int BUTTON_HEIGHT = 150;
	private FlappyBirdSubScene scoresScene;
	private ScoreBoard scoreBoard;
	private gameViewManager gameViewer;
	private LoadModelScene loadModelScene;
	private serializeModels sModels;
	private Sequential model;
	private Tuner tuner;
	// Constructor
	public MainMenuManager() {
		mainPane = new AnchorPane();
		scene = new Scene(mainPane,GameSettings.WIDTH,GameSettings.HEIGHT);
		stage = new Stage();
		sModels = new serializeModels();
		tuner = new Tuner();
		addTunerVariables();
		showMenuView();
		createButtons();
		createBackground();
		createSubScenes();
		
	}
	public void addTunerVariables() {
		List<Double> batch= new ArrayList<Double>();
		for(int i = 16; i < 512; i*=2)
			batch.add((double) i);
		List<Double> epochs= new ArrayList<Double>();
		for(int i = 5; i < 30; i+=5)
			epochs.add((double)i);
		List<Double> updateNEpochs= new ArrayList<Double>();
		updateNEpochs.add((double) 50);
		updateNEpochs.add((double) 100);
		updateNEpochs.add((double) 200);
		updateNEpochs.add((double) 400);
		List<Double> startLR= new ArrayList<Double>();
		for(double i = 0.01; i > 0.000000001; i /=10)
			startLR.add(i);
		List<Double> lRTimes= new ArrayList<Double>();
		lRTimes.add((double) 600);
		lRTimes.add((double) 1000);
		lRTimes.add((double) 1500);
		lRTimes.add((double) 2500);
		lRTimes.add((double) 5000);
		
		
		tuner.addParam("batch", batch);
		tuner.addParam("epochs", epochs);
		tuner.addParam("updateNEpochs", updateNEpochs);
		tuner.addParam("startLR", startLR);
		tuner.addParam("lRTimes", lRTimes);
	}
	// Shows the main menu after being called from gameView
	public void showMenu(Stage gameStage) {
		gameStage.hide();
		gameStage.close();
		stage = new Stage();
		gameViewer = null;
		showMenuView();
	}
	// Shows the main menu
	public void showMenuView() {
		model = null;
		stage.setScene(scene);
		stage.show();
		System.out.println(sModels);
		System.out.println("MAIN MENU");
	}
	// shows main menu windows without changing the model stored
	public void showMenuViewWithModel() {
		stage.setScene(scene);
		stage.show();
		System.out.println("MAIN MENU");
	}
	/*
	 * START Concentrated Create calls
	 */
	// the function creates the menu buttons
	private void createButtons() {
		HumanPlayerButton(); 
		MachineLearningButton();
		scoresButton();
		loadModelButton();
	}
	// the function creates the background
	private void createBackground() {
		Image backgroundImage = new Image("view\\resources\\Flappy_learn_pic.png", GameSettings.WIDTH,GameSettings.HEIGHT,false,false);
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,BackgroundPosition.DEFAULT,null);
		mainPane.setBackground(new Background(background));
	}
	// the function adds the top scores sub scene
	private void createSubScenes() {
		ScoresSubScene();
	}
	/*
	 * END Concentrated Create calls 
	 */
	
	/*
	 * START Buttons
	 */
	private void addMenuButton(MenuButtons button, double width, int posY) {
		button.setLayoutX(width / 2 - BUTTON_WIDTH/2);
		button.setLayoutY(posY* GameSettings.HEIGHT / 32);
		mainPane.getChildren().add(button);
	}
	private void HumanPlayerButton() {
		MenuButtons HumanPlayerB = new MenuButtons("Human Player",BUTTON_WIDTH,BUTTON_HEIGHT,false,0);
		addMenuButton(HumanPlayerB,GameSettings.WIDTH, 18);
		
		HumanPlayerB.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				startGame(0);
			};
		});
	}
	private void MachineLearningButton() {
		MenuButtons MachineLearningB = new MenuButtons("Machine Learning",BUTTON_WIDTH,BUTTON_HEIGHT,false,0);
		addMenuButton(MachineLearningB,GameSettings.WIDTH, 24);
		MachineLearningB.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				startGame(1);
//				System.out.println("Update Score!");
//				scoreBoard.addScore("Roee Ifergan!", 150);
			};
		});
	}
	private void scoresClearButton() {
		MenuButtons scoresClearB = new MenuButtons("Clear",BUTTON_WIDTH/3,BUTTON_HEIGHT/2,false,0);
		scoresScene.getPane().setRight(scoresClearB);
		scoresClearB.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("clear me?");
				File file = new File("C:\\Users\\roeec\\eclipse-workspace\\Flappy Learn\\Flappy Learn\\src\\model\\scores.txt");
				file.delete();
				scoreBoard.clearGrid();
			};
		});
		
	}
	private void loadModelButton() {
		loadModelScene  = new LoadModelScene(this, sModels);
		MenuButtons loadModels = new MenuButtons("Load",BUTTON_WIDTH/3,BUTTON_HEIGHT/2,true,35);				//CHANGE THIS TO LOAD SCREEN FOR LOADING MODELS!
		addMenuButton(loadModels,GameSettings.WIDTH * 2 + BUTTON_WIDTH/4, 26);
		loadModels.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Button");
				loadModelScene.showScene(stage);
			};
		});
	}
	
	private void scoresButton() {
		MenuButtons scoresB = new MenuButtons("Scores",BUTTON_WIDTH/3,BUTTON_HEIGHT/2,true,35);
		addMenuButton(scoresB,GameSettings.WIDTH * 2 + BUTTON_WIDTH/4, 29);
		scoresB.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				scoresScene.movSubScene();
			};
		});
	}
	/*
	 * END Buttons
	 */
	
	/*
	 * START Subscene
	 */
	private void ScoresSubScene() {
		double height =  5 * GameSettings.HEIGHT / 12;
		double startPointY = (double) (-1 * height);
		double endPointY = (double) (height + GameSettings.HEIGHT / 12);
		int scoreAmount = 5;
		scoresScene = new FlappyBirdSubScene(GameSettings.WIDTH/2, height,startPointY,endPointY);
		scoresScene.setLayoutX(GameSettings.WIDTH/4);
		scoresScene.setLayoutY(startPointY);
		mainPane.getChildren().add(scoresScene);
		scoreBoard = new ScoreBoard(scoresScene.getPane(), scoreAmount);
		scoresClearButton();
	}
	/*
	 * END Subscene
	 */
	
	/*
	 * START Game Actions
	 */
	/*
	 * gameType = 0 is Player playing
	 * gameType = 1 is ML playing
	 */
	// The function starts the gameview gametimer, and initializes it's start playing sequence
	public void startGame(int gameType) {
		int speedTimer = 1;
		tuner.step();
		gameViewer = new gameViewManager(this,sModels,model,gameType,scoreBoard, tuner);
		gameViewer.openGame(stage);		
	}
	/*
	 * END Game Actions
	 */
	// the functions sets the stored model for loading in neural networks
	public void setModel(Sequential model) {
		this.model = model;
	}
}
