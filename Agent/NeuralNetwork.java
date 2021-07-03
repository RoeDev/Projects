package Agent;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import Agent.ML.CSVReader;
import Agent.ML.Pair;
import Agent.ML.Sequential;
import Agent.ML.serializeModels;
import Agent.ML.Exceptions.HudException;
import Agent.ML.Exceptions.InvalidCSVException;
import Agent.ML.Exceptions.InvalidMatrixException;
import Agent.ML.Exceptions.InvalidModuleException;
import Agent.ML.HyperParams.Tuner;
import Agent.ML.Layers.ActionSelect;
import Agent.ML.Layers.Linear;
import Agent.ML.Layers.Relu;
import Agent.ML.Layers.SoftMax;
import Agent.ML.Loss.HuberLoss;
import Agent.ML.Loss.Loss;
import Agent.ML.Loss.Mse;
import Agent.ML.Optimizer.Adam;
import Agent.ML.Optimizer.Optimizer;
import Agent.ML.Optimizer.SGD;
import Agent.ML.ReplayMemory.Entry;
import Agent.ML.ReplayMemory.ReplayMemory;
import Agent.MLScheduler.ConstantScheduler;
import Agent.MLScheduler.LinearScheduler;
import Agent.MLScheduler.Scheduler;
import Agent.MLScheduler.Sigmoid;
import Agent.MLScheduler.SigmoidV2Scheduler;
import defaultSettings.MLSettings;
import game.HUD;
import game.ScoreCount;

import java.text.SimpleDateFormat;  

/**
 * <h1>NeuralNetwork</h1>
 * 
 * <p>NeuralNetwork is a class that does most of the Reinforcenemt learning tasks.\n
 * the class stores a network, maintains it, teaches it, and allows easy access to action select given a specific state</p>
 * 
 * <p>In addition, the class can serialize it's network and store it, updates multiple HUDS (heads up displays) ands allows for simple hyperParamater changes</p>
 * 
 * @author roeec
 */


public class NeuralNetwork {
	// private Params
	private serializeModels sModels;
	private Random rand;
	private ScoreCount scoreUpdater;
	private HUD hud;
	private SimpleDateFormat formatter;
	private Tuner tuner;
	// Hyper params
	private int batch = 64;
	private int epochs = 15;
	private int updateNEpochs = 100;
	private double startLR = 0.001;		//was 0.001
	private int lRTimes = 1000;
	private double gamma = 0.9;

	// Exploration VS Exploitation Params
	private double epsilon = 1;
	private double epsilon_End = 0.001;
	private double epsilon_Decay = 0.997;

	// Modules
	private Sequential policy_net;
	private Sequential target_net;
	private Loss loss;
	private Optimizer opt;
	private Scheduler scheduler;

	private int countTrainingTimes;

	// CSV Reader for importing external datasets(added feature)
	private CSVReader CSV;
	
	/**
	 * Constructor that initializes all the params needed
	 * 
	 * @param sModels 		- is an object that can serialize a Sequential object
	 * @param model	  		- A sequential object that needs to be loaded
	 * @param scoreUpdater	- HUD that allows updates on Learning amounts
	 * @param hud			- HUD that allows updating state, reward and loss
	 */
	public NeuralNetwork(serializeModels sModels,Sequential model,ScoreCount scoreUpdater, HUD hud, Tuner tuner) {
		this.hud = hud;
		this.scoreUpdater = scoreUpdater;
		this.sModels = sModels;
		this.tuner = tuner;
		rand = new Random();
	    formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//	    loadTunerHyperParams();
	    InitializeParams(model);
	}
	/**
	 * Constructor that initializes all the params needed using CSV reader
	 * 
	 * @param sModels 		- is an object that can serialize a Sequential object
	 * @param model	  		- A sequential object that needs to be loaded
	 * @param scoreUpdater	- HUD that allows updates on Learning amounts
	 * @param hud			- HUD that allows updating state, reward and loss
	 */
	public NeuralNetwork(serializeModels sModels,Sequential model,CSVReader CSV, ScoreCount scoreUpdater, HUD hud, Tuner tuner) {
		this.hud = hud;
		this.scoreUpdater = scoreUpdater;
		this.CSV = CSV;
		this.sModels = sModels;
		this.tuner = tuner;
		rand = new Random();
	    formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
	    InitializeParams(model);
	}
	
	public void loadTunerHyperParams() {
		try {
			if(tuner != null) {
				this.batch = (int) tuner.getParam("batch");
				this.epochs = (int) tuner.getParam("epochs");
				this.updateNEpochs = (int) tuner.getParam("updateNEpochs");
				this.startLR = tuner.getParam("startLR");		//was 0.001
				this.lRTimes = (int) tuner.getParam("lRTimes");
				this.gamma = tuner.getParam("gamma");
			} 
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The function initializes the network related modules
	 * @param model	- will be not null in case that a network is loaded in
	 */
	private void InitializeParams(Sequential model) {
		try {
			// building our policy network
			// if model is null, no model was loaded and we need to Initialize the model
			// else we will use the model loaded
			if(model == null) {
				System.out.println("New Model Loaded");
				policy_net = buildpolicy_net();
				if(policy_net == null)
					throw new InvalidModuleException("Sequential", "Error Creating Policy net!");
			}
			else {
				System.out.println("Model Loaded");
				policy_net = model.clone();
				startLR = 0.0000001;
				
				// For Show!
				epsilon = 0.01;
			}
			
			// Initializing target network with the same weights and biases as policy_net
			target_net = policy_net.clone();
			
			// Initializing Loss
//			loss = new HuberLoss(1);
			loss = new Mse(3);

			// Initializing Optimizer
//			opt = new Adam(policy_net.getModules());
			opt = new SGD(policy_net.getModules());
			
			// Initializing Scheduler
//			scheduler = new ConstantScheduler(0.0001);
//			scheduler = new LinearScheduler(startLR, lRTimes);
			scheduler = new SigmoidV2Scheduler(startLR, lRTimes);
//			scheduler = new Sigmoid(5000,0.001);
//			scheduler = new Sigmoid(lRTimes,startLR);
			
			try {
				hud.updateHudParam("batch", batch);
				hud.updateHudParam("epochs", epochs);
				hud.updateHudParam("updateNEpochs", updateNEpochs);
				hud.updateHudParam("startLR", startLR);
				hud.updateHudParam("lRTimes", lRTimes);
				hud.updateHudParam("gamma", gamma);
			}catch(HudException he) {
				he.printStackTrace();
			}
			
		} catch (InvalidModuleException | CloneNotSupportedException ime) {
			ime.printStackTrace();
		}
	}
	/**
	 *  The function builds a Network with modules
	 * @return the network built
	 */
	private Sequential buildpolicy_net() {
		
		// Creates a Sequential network and adds models to it
		try {
			Sequential network = new Sequential();

			network.addModule(new Linear(6, 64));

			network.addModule(new Relu());
			
			network.addModule(new Linear(64, 128));
			
			network.addModule(new Relu());
			
			network.addModule(new Linear(128, 256));
			
			network.addModule(new Relu());
			
			network.addModule(new Linear(256, 2));
			

			return network;
		} catch (InvalidModuleException e) {
			e.printStackTrace();
		}
		System.exit(0);
		return null;
	}
	
	
	/**
	 * The function trains the policy net epochs times using the according policy net, loss, optimizer and scheduler stored.
	 * @param memory used for getting random mini batches from
	 */
	public void trainingLoop(ReplayMemory memory) {
		policy_net.setPrintFlag(false);
		loss.setPrintMode(false);
		
		// Check if the ReplayMemory has stored enough Entrys for a miniBatch
		if(memory.getLength() < batch) {
			System.out.println("Not enough entrys for training for batch of " + batch);
			return;
		}
		
		try {
			// Total loss calculation
			Matrix totalLoss = new Matrix(1, 1, -5);
			totalLoss.setCells(0);
			
			// Creating ActionSelect objects
			ActionSelect policyNetActionSelect = new ActionSelect();
			ActionSelect targetNetActionSelect = new ActionSelect();
			
			// Learning from a minibatch happens epochs times
			for (int j = 0; j < epochs; j++) {
				
				// Creating a random miniBatch from the ReplayMemory
				List<Entry> miniBatch = memory.sample(batch);
				if(miniBatch == null)
					break;
				
				// target network prediction
				Matrix next_state_values = target_net.forward(memory.getNextStates(miniBatch));
				
				// taking relevant qvalues for next state
				next_state_values = targetNetActionSelect.forward(next_state_values);
				
				// masking so all samples that are at state 'Done' get a value of 0
				// state 'Done' means the last state of the episode(the state the bird dies)
				List<Boolean> isAliveSamples = memory.getIsAlive(miniBatch);
				Iterator it = isAliveSamples.iterator();
				for(int i = 0; i < batch && it.hasNext(); i++)
					if(!(boolean) it.next())
						next_state_values.multiplyRow(i, 0);
				
				// receiving rewards from corresponding mini batch
				List<Double> rewards = memory.getRewards(miniBatch);

				// multiplying all cells of next state actions by gamma and adding their corresponding rewards
				next_state_values.multiplyCells(gamma);
				Matrix expected_state_action_values = next_state_values.addMatrix(new Matrix(rewards,1));
				
				// setting desired output
				loss.setDesiredOutputMatrix(expected_state_action_values);
				
				// zeroGrad - zeros all gradient values
				policy_net.zeroGrad();
				
				// getting states from mini batch
				Matrix sampleBatchStates = memory.getStates(miniBatch);
				
				// passing state through policy net forward
				Matrix policy_net_Prediction = policy_net.forward(sampleBatchStates);
				
				// getting mini batch actions
				List<Double> actions = memory.getActions(miniBatch);
				
				// setting those actions to setAction object
				policyNetActionSelect.setActions(new Matrix(actions,1));
				
				// returns a batch size x 1 matrix, keeping only the actions chosen
				policy_net_Prediction = policyNetActionSelect.forward(policy_net_Prediction);
				
				// calculating loss
				Matrix lossValue = loss.forward(policy_net_Prediction);
				// summing total loss(for print)
				totalLoss = totalLoss.addMatrix(lossValue);

				// calculating gradients of loss function
				Matrix lossGrad = loss.backward(lossValue);
	
				// calculating backward for policy select action
				Matrix policyNet_outGrad = policyNetActionSelect.backward(lossGrad);
				policyNetActionSelect.reset();
				
				// BackPropogation
				// calculating gradients for policy_net
				policy_net.backward(policyNet_outGrad);

				// next step for scheduler (should decrease learning rate)
				scheduler.step();

				// setting optimizer learningRate
				double learningRate = scheduler.getLearningRate();
//				System.out.println("LR: " + learningRate);
				opt.setLearningRate(learningRate);
				
				// clamping network gradients
				policy_net.clamp(-1, 1);
				// optimizer step
				opt.step();

				// increasing counting training times
				countTrainingTimes++;
				
				// updating target network to equal policy network
				if(countTrainingTimes % updateNEpochs == 0) {
					if(MLSettings.PRINT)
						System.out.println("\t\t\t\t\t\tPolicy Net Cloned! (" + countTrainingTimes + ")");
					target_net = policy_net.clone();
				}
				
				// update target net every X training learns to equal policy network
//				if(countTrainingTimes % 1000 == 0)
//					sModels.addModel(formatter.format((new Date())) + " <> " + countTrainingTimes, policy_net);
				
				// updating HUD
//				if(countTrainingTimes % 100 == 0)
					scoreUpdater.setLearningAmount(countTrainingTimes);
					hud.updateHudParam("Learning Rate", learningRate);
			}
			hud.updateHudParam("Loss", totalLoss);
			if(MLSettings.PRINT) {
				totalLoss.multiplyCells(1.0 / (epochs));
				System.out.println("LossMean: " + totalLoss);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

//	public void loadDataSet(CSVReader cs) throws InvalidCSVException {
//		// TODO: store full dataset in memory
//		// then split it,
//		// make sure NN runs on the correct dataset
//		if (!cs.exists())
//			throw new InvalidCSVException("CSV", "CSV doesn't exist!");
//
//		this.CSV = cs;
//		dataSet = cs.getFullDataSet();
//
//	}

	public void addRowToDataSet(double reward, List<Double> state) throws IOException {
		state.add(0, reward);
		CSV.addRowToCSV(state);
	}

	/**
	 * The function selects the bird's action according to it's current move
	 * 
	 * @param state
	 * @return
	 * @throws InvalidMatrixException
	 * @throws CloneNotSupportedException
	 */
	public int selectAction(Matrix state) throws InvalidMatrixException, CloneNotSupportedException {
		return selectAction(state, 0);
	}
	/**
	 *  The function selects the bird's action according to it's current move
	 *  
	 * @param state - matrix representing current state
	 * @param row 	- row in matrix to check
	 * @return		0 meaning jump and 1 meaning do nothing
	 * @throws InvalidMatrixException
	 * @throws CloneNotSupportedException
	 */
	private int selectAction(Matrix state, int row) throws InvalidMatrixException, CloneNotSupportedException {
		try {
			// updating exploration vs exploitation rate
			if(epsilon > epsilon_End)
				epsilon *= epsilon_Decay;
			
			// randomly do random actions
			if (rand.nextDouble() <= epsilon) {
				
					hud.updateHudParam("Action", "Random");
				
				if(MLSettings.PRINT)
					System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tepsilon: " + epsilon);
				if (rand.nextDouble() > 0.5)
					return 0;
				return 1;
			}
			hud.updateHudParam("Action", "Network");
		} catch (HudException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Matrix forward = policy_net.forward(state);
		return forward.indexOfMaxElementInRow(row);
	}
	
	/**
	 * inferenceLoop is a function that can check new values on a network, it is was usually used when testing the network on a simpler problem
	 * 
	 * @param policy_net	- network being tested
	 * @param loss			- Loss object
	 * @param dataSet		- dataset thats being tested(preferably this be data the network has never seen
	 * @param batch			- batch size
	 */
	public void inferenceLoop(Sequential policy_net, Loss loss, List<Pair<Double, Double>> dataSet, int batch) {
		System.out.println("--------------------------------------------------------");
		policy_net.setPrintFlag(false);
		loss.setPrintMode(false);
		try {
			for (int i = 0; i + batch < dataSet.size(); i += batch) {
				double data[][] = new double[batch][1];
				double desiredOutpt[][] = new double[batch][1];
				for (int k = 0; k < batch; k++) {
					data[k][0] = dataSet.get(i + k).getVar1();
					desiredOutpt[k][0] = dataSet.get(i + k).getVar2();

				}
				System.out.println("i=" + i);

				Matrix y_hat = policy_net.forward(new Matrix(data));

				y_hat.printSideBySide(new Matrix(desiredOutpt));
				loss.setDesiredOutputMatrix(new Matrix(desiredOutpt));

				Matrix lossValue = loss.forward(y_hat);

				System.out.println("\t\t\tLoss for current batch:" + lossValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// returns the current policy network
	public Sequential getNetwork() {
		return policy_net;
	}
}

