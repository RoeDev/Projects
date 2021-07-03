package Agent.MLScheduler;

public class SigmoidV2Scheduler extends Scheduler{
	private double a;
	private double desiredSlope = 0.1;
	private double decrement;
	public SigmoidV2Scheduler(double maxVal, int steps) {
		super(0);
		a = 1/maxVal;
		setParams(findXValForSlope(desiredSlope));
		this.decrement = Math.abs(maxVal) / (double) steps;
		System.out.println("decrement: " + decrement);
		
	}

	@Override
	public void step() {
		alpha -= decrement;
	}

	public double findXValForSlope(double slope) {
		return Math.log( ( 1 - 2 * slope + Math.sqrt( 1 - 4 * slope ) ) / ( 2 * slope ) ) / a;
	}
	
	@Override
	public double getLearningRate() {
		return (Math.pow(Math.E, a*alpha) / (Math.pow(Math.E, a*alpha) + 1))/a;
	}
}
