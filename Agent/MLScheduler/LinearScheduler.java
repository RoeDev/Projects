package Agent.MLScheduler;
/**
 * <h1>LinearScheduler</h1>
 * <p>The linear scheduler decreases the learning rate each time a step is called</p>
 * @author roeec
 *
 */
public class LinearScheduler extends Scheduler{
	private double decrement;
	/**
	 * Constructor
	 * @param alpha - starting learning rate
	 * @param steps - the times the step() function will be called till learning rate will be 0.0000001
	 */
	public LinearScheduler(double alpha, int steps) {
		super(alpha);
		this.decrement = alpha / (double) steps;
		System.out.println("decrement=" + decrement);
	}
	/**
	 * Step function decreases the learning rate by the decrement
	 */
	@Override
	public void step() {
		if(alpha > 0.0000001)
			alpha -= decrement;
	}
}
