package Agent.ML.ReplayMemory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import Agent.Matrix;
import defaultSettings.MLSettings;

/**
 * <h1>Replay Memory</h1>
 * <p>The replay memory stores entrys in a queue that has a maximum size. once it reaches it's max size, it overwrites the oldest entry with the new one.</p>
 * <p>The Replay memory's purpose is to store and supply randomly selected mini batches from the bird's experience</p>
 * @author roeec
 *
 */
public class ReplayMemory {
	
	private int maxCapacity;
	private int capacityFillTimes;
	private int currIndex;
	private List<Entry> memory;
	/**
	 * Constructor
	 * @param maxCapacity - maximum queue size
	 */
	public ReplayMemory(int maxCapacity) {
		this.maxCapacity = maxCapacity;
		memory = new ArrayList<Entry>(maxCapacity);
		for(int i = 0; i < maxCapacity; i++)
			memory.add(null);
	}
	/**
	 * adds a entry to the queue
	 * @param state - current bird state
	 * @param next_State - next bird state after action
	 * @param action - action taken for current state
	 * @param reward - reward givven from the action taken
	 * @param isAlive - if the bird is alive after the action was taken
	 */
	public void push(List<Double> state, List<Double> next_State, int action, double reward, boolean isAlive) {
		Entry entry = new Entry(state, next_State, action, reward, isAlive);
		if(currIndex == maxCapacity) {
			currIndex = 0;
			capacityFillTimes++;
		}
		memory.set(currIndex, entry);
		currIndex++;
		if(currIndex % 100 == 0 && MLSettings.PRINT)
			System.out.println("\t\t\t\t\t\t\t\t\t\t\t\tReplayMemory Size: " + (capacityFillTimes * maxCapacity + currIndex));
	}
	/**
	 * Samples a randomly picked minibatch from the queue
	 * @param batch_Size - mini batch size
	 * @return minibatch
	 */
	public List<Entry> sample(int batch_Size) {
		Random rand = new Random(System.currentTimeMillis());
		if(batch_Size <= (capacityFillTimes * maxCapacity + currIndex)) {
			double maxIndex = (capacityFillTimes == 0) ? currIndex : maxCapacity;
			List<Entry> ret = new ArrayList<Entry>();
			for(int i = 0; i < batch_Size; i++)
				ret.add(memory.get((int) (rand.nextDouble() * maxIndex)));
			return ret;
		}
		return null;
	}
	/**
	 * get actions from List<Entry> aka minibatch
	 * @param entrys - mini batch
	 * @return list of actions taken from mini batch
	 */
	public List<Double> getActions(List<Entry> entrys) {
		List<Double> actions = new ArrayList<Double>();
		Iterator it = entrys.iterator();
		while(it.hasNext()) {
			Entry next = (Entry) it.next();
			actions.add((double) next.getAction());
		}
		return actions;
	}
	/**
	 * get is alive from List<Entry> aka minibatch
	 * @param entrys - mini batch
	 * @return list of isAlives givven from mini batch
	 */
	public List<Boolean> getIsAlive(List<Entry> entrys) {
		List<Boolean> isAlive = new ArrayList<Boolean>();
		Iterator it = entrys.iterator();
		while(it.hasNext()) {
			Entry next = (Entry) it.next();
			isAlive.add(next.isAlive());
		}
		return isAlive;
	}
	/**
	 * get staets from List<Entry> aka minibatch
	 * @param entrys - mini batch
	 * @return Matrix of states from mini batch (each row is a state)
	 */
	public Matrix getStates(List<Entry> entrys) {
		double[][] states = new double[entrys.size()][entrys.get(0).getStateAmount()];
		
		Iterator it = entrys.iterator();
		for(int i = 0; i < states.length && it.hasNext(); i++) {
			Entry next = (Entry) it.next();
			Iterator stateParams = next.getState().iterator();
			for(int j = 0; j < states[0].length && stateParams.hasNext(); j++)
				states[i][j] = (double) stateParams.next();
		}
		return new Matrix(states);
	}
	/**
	 * get next states from List<Entry> aka minibatch
	 * @param entrys - mini batch
	 * @return Matrix of next states from mini batch (each row is a state)
	 */
	public Matrix getNextStates(List<Entry> entrys) {
		double[][] states = new double[entrys.size()][entrys.get(0).getStateAmount()];
		
		Iterator it = entrys.iterator();
		for(int i = 0; i < states.length && it.hasNext(); i++) {
			Entry next = (Entry) it.next();
			Iterator stateParams = next.getNext_State().iterator();
			for(int j = 0; j < states[0].length && stateParams.hasNext(); j++)
				states[i][j] = (double) stateParams.next();
		}
		return new Matrix(states);
	}
	/**
	 * get rewards from List<Entry> aka minibatch
	 * @param entrys - mini batch
	 * @return list of rewards from mini batch
	 */
	public List<Double> getRewards(List<Entry> entrys) {
		List<Double> rewards = new ArrayList<Double>(entrys.size());
		
		Iterator it = entrys.iterator();
		while(it.hasNext()) {
			rewards.add(((Entry) it.next()).getReward());
		}
		
		return rewards;
	}
	/**
	 * @return amount of pushed entrys
	 */
	public int getLength() {
		return currIndex + maxCapacity * capacityFillTimes;
	}
}
