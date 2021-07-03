package Agent.MLScheduler;

/**
 * <h1>Sigmoid</h1>
 * <p> Sigmoid is used as a scheduler in this instance. it is responsible of calculating the learning rate and have it change like a minus sigmoid function</p>
 * @author roeec
 */
public class Sigmoid extends Scheduler{
	private double decrement;
	private double change;//higher = bigger change is short time
	private double maxVal;
	private double y = 0.0002;
	/**
	 * Constructor
	 * @param steps - steps Learning rate is 0
	 * @param maxVal - maxVal is the starting learning rate
	 */
	public Sigmoid(int steps, double maxVal) {
		super(0);
		change = 1;
		this.maxVal = maxVal;
		setParams(findSigmoidXFromY(maxVal * 38 / 40));
		this.decrement = Math.abs(alpha) / (double) steps;
	}
	/**
	 * Constructor
	 * @param steps - steps Learning rate is 0
	 * @param maxVal - maxVal is the starting learning rate
	 * @param change - controls the curve of sigmoid
	 */
	public Sigmoid(int steps, double maxVal, double change) {
		super(0);
		this.change = change;
		this.maxVal = maxVal;
		setParams(findSigmoidXFromY(maxVal * 38 / 40));
		this.decrement = Math.abs(alpha) / (double) steps;
		System.out.println("alpha, dec: " + alpha + ", " + decrement);

	}
	/**
	 * the function increments alpha which will decrease the learning rate next time it is called
	 */
	@Override
	public void step() {
		alpha += decrement;
	}
	/**
	 *  The function returns the learning rate
	 *  @return - Learning rate
	 */
	@Override
	public double getLearningRate() {
		double p = calcSigmoid(alpha);
		return p;
	}
	/**
	 * the function calculates sigmoid's Y val for a X val
	 * @param x - current X val
	 * @return - y val for corresponding x
	 */
	private double calcSigmoid(double x) {
		return -maxVal * Math.pow(Math.E,change * x) / (Math.pow(Math.E,change * x) + 1) + maxVal;
	}
	/**
	 * calculates the x value given a y value of the sigmoid
	 * @param y - for calculated x
	 * @return x value for corresponding Y val
	 */
	private double findSigmoidXFromY(double y) {
		return Math.log(maxVal/y - 1)/change;
	}
}
