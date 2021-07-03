package Agent.ML.ReplayMemory;

import java.util.List;

/**
 * <h1>Entry</h1>
 * <p>Entry class allows a easy solution for storing state, next state, reward, isalive and action entrys in the replay memory</p>
 * @author roeec
 */
public class Entry {
	private List<Double> state;
	private List<Double> next_State;
	private int action;
	private double reward;
	boolean isAlive;
	/**
	 * Constructor
	 * @param state - current state
	 * @param next_State - next state
	 * @param action - action take
	 * @param reward - reward givven
	 * @param isAlive - if bird is still alive after action taken
	 */
	public Entry(List<Double> state, List<Double> next_State, int action, double reward, boolean isAlive) {
		this.state = state;
		this.next_State = next_State;
		this.action = action;
		this.reward = reward;
		this.isAlive = isAlive;
	}
	
	public boolean isAlive() {
		return isAlive;
	}

	public void setisAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	public int getStateAmount() {
		return state.size();
	}
	public List<Double> getState() {
		return state;
	}
	public void setState(List<Double> state) {
		this.state = state;
	}
	public List<Double> getNext_State() {
		return next_State;
	}
	public void setNext_State(List<Double> next_State) {
		this.next_State = next_State;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public double getReward() {
		return reward;
	}
	public void setReward(double reward) {
		this.reward = reward;
	}
	
	
}
