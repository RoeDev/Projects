package Agent.MLScheduler;
/**
 * <h1>Scheduler</h1>
 * <p>The scheduler calculates the learning rate</p>
 * @author roeec
 */
public abstract class Scheduler {
	protected double alpha;
	private double protectedAlhpa;
	
	public abstract void step();
	/**
	 * Constructor
	 * @param alpha - starting learning rate
	 */
	public Scheduler(double alpha) {
		setParams(alpha);
	}
	/**
	 * set learning rate
	 * @param alpha - Learning rate
	 */
	protected void setParams(double alpha) {
		System.out.println("alpha: " + alpha);
		this.alpha = alpha;
		this.protectedAlhpa = alpha;
	}
	/**
	 * get learning rate
	 * @return learning rate
	 */
	public double getLearningRate() {
		return alpha;
	}
	/*
	 * resets the learning rate to the initial learning rate
	 */
	public void resetLR() {
		alpha = protectedAlhpa;
	}
}
