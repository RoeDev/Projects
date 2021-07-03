package defaultSettings;

public class MLSettings {
	//Neural network Settings
	public static int EPISODES = 1000000;
	public static boolean PRINT = false;
	public static final long seed = 1;
	
	//Replay Memory Settings
	public static final int REPLAY_MEMORY_CAPACITY = 30000;

	
	//Flappy Bird Settings
	public static final double LOSS_REWARD = -100;
	public static final double STAY_ALIVE_REWARD = 0;
	public static final double SCORE_REWARD = 100;
	
}
