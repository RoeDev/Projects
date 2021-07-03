package Agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Agent.ML.CSVReader;
import Agent.ML.Sequential;
import Agent.ML.serializeModels;
import Agent.ML.Exceptions.HudException;
import Agent.ML.Exceptions.InvalidMatrixException;
import Agent.ML.HyperParams.Tuner;
import Agent.ML.ReplayMemory.ReplayMemory;
import defaultSettings.MLSettings;
import game.FlappyBird;
import game.HUD;
import game.ScoreCount;
import javafx.scene.layout.Pane;

/**
 * <h1>Brain</h1>
 * <p>
 * Brain is the AI for the game. It's responsible for the flow the reinforcement
 * learning and teaching the neural network.\n it picks the actions that the AI
 * does, trains the agent's network and makes sure everything is in place(Brain
 * is run on a different thread)
 * </p>
 * 
 * @author roeec
 *
 */
public class Brain extends Agent {

	private ArrayList<Double> tubesPos;
	// Tube Info:
	// tubePos keeps the coordinates about the 2 upcoming tubes
	// Element Axis Info
	// 0 (Y) Tube is above
	// 1 (Y) Tube is bellow
	// 2 (X) Tube starts at
	// 3 (X) Tube ends at

	private NeuralNetwork nn;
	// Neural network Object

	// CSV reader for importing external dataSets
	private CSVReader cs;

	// Replay memory
	private ReplayMemory memory;

	// Score display counter
	private ScoreCount scoreUpdater;

	// HUD - Heads Up Display (Shows stats about the game onscreen)
	private HUD hud;

	// Formatter for saving networks
	private SimpleDateFormat formatter;

	// Serialization of Neural network
	private serializeModels sModels;
	private boolean isAlive;
	private boolean closeThread = false;
	private boolean readyForNextStep = true;

	/**
	 * Constructor
	 * 
	 * @param pane         - game pane
	 * @param scoreUpdater - score HUD
	 * @param tubesPos     - upcoming tube positions
	 * @param hud          - HUD (heads up display)
	 * @param sModels      - object that can serialize a network(sequential)
	 * @param model        - a sequential model if loading a network is necessary
	 */
	public Brain(Pane pane, ScoreCount scoreUpdater, ArrayList<Double> tubesPos, HUD hud, serializeModels sModels,
			Sequential model, Tuner tuner) {
		this.hud = hud;
		this.scoreUpdater = scoreUpdater;
		this.pane = pane;
		this.tubesPos = tubesPos;
		this.sModels = sModels;
		isAlive = true;
		// Initialize Formatter
		formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		// Creating bird
		bird = new FlappyBird(pane, scoreUpdater, tubesPos);

		// Creating ReplayMemory
		memory = new ReplayMemory(MLSettings.REPLAY_MEMORY_CAPACITY);

		// Creating Neural Network object
		nn = new NeuralNetwork(sModels, model, scoreUpdater, hud, tuner);
	}

	/**
	 * The function resets the bird's settings
	 */
	@Override
	public void resetBirdSettings() {
		if (!closeThread) {

			if (MLSettings.PRINT)
				System.out.println("Start learning!");

			readyForNextStep = false;
			isAlive = true;

			// Resets Bird completely
			bird.removeBirdFromPane();
			bird = new FlappyBird(pane, scoreUpdater, tubesPos);

			// if bird has scored more than X points, it's neural network will be saved
			if (scoreUpdater.getScore() >= 8000)
				sModels.addModel(formatter.format((new Date())) + " <>\t\t " + scoreUpdater.getScore(), nn.getNetwork());

			// Train Neural Network
			nn.trainingLoop(memory);

			readyForNextStep = true;

			if (MLSettings.PRINT)
				System.out.println("ready For Next Step");
		}
	}

	/**
	 * his function stops a bird's object and returns to the main menu
	 */
	@Override
	public void stopBird() {
		System.out.println("Stopped Bird!");
		// Next loop run() function does, the thread will exit
		closeThread = true;
	}

	/**
	 * This function makes sure the thread doesn't close, once the while loop
	 * finishes, the Thread will close
	 */
	@Override
	public void run() {
		while (!closeThread) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The function represents a gameStep A gameStep updates the enviorment(bird),
	 * and depending the object may do more Things
	 */
	@Override
	public boolean gameStep() {
		// to prevent gameStep being called before it finishes we make readyForNextStep
		// false
		if (!closeThread) {
			readyForNextStep = false;
			if (isAlive) {
				try {
					// Getting bird State
					List<Double> state = bird.getBirdState();

					// Selecting an action
					// action = 0 -> bird jumps
					// action = 1 -> bird does nothing
					int action = nn.selectAction(new Matrix(state, 0));

					// If action is 0, bird should jump
					if (action == 0) {
						bird.onKeyPressed();
						Thread.sleep(2);
						bird.onKeyReleased();
					}

					// Updating bird State
					bird.updateState();
					scoreUpdater.updateMaxScoreCount();
					// Getting next state
					List<Double> next_State = bird.getBirdState();

					// Getting reward caused by the action we did
					double reward = bird.getReward();

					// Resetting the reward so it doesn't get passed onto the next state
					bird.resetReward();

					// Checking if bird is alive
					// returns true if bird is alive, else false
					isAlive = bird.isAlive();

					// Updating HUD
					hud.updateHudParam("State", new Matrix(state, 1));
					hud.updateHudParam("Reward", reward);
					hud.updateHUD();

					// push Entry into ReplayMemory
					// Entry is made of: State, Next_State, action, reward, isAlive
					memory.push(state, next_State, action, reward, isAlive);

				} catch (InvalidMatrixException | CloneNotSupportedException | HudException | InterruptedException e) {
					e.printStackTrace();
				}
			}
			readyForNextStep = true;
		}
		return isAlive;
	}

	/**
	 * Returns true if the object is ready for next call, else returns false
	 */
	@Override
	public boolean getReadyForNextStep() {
		return readyForNextStep;
	}

}
