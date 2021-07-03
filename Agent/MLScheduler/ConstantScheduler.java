package Agent.MLScheduler;
/**
 * <h1>Constant Scheduler</h1>
 * <p>A Constant Learning rate scheduler. it is used when wanting a constant learning rate without changing the training loop code</p>
 * @author roeec
 *
 */
public class ConstantScheduler extends Scheduler {

	/**
	 * Constructor
	 * @param alpha - learning rate
	 */
	public ConstantScheduler(double alpha) {
		super(alpha);
	}
	@Override
	public void step() {
	}

}
