package subScenes;

import javafx.animation.TranslateTransition;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
/**
 * <h1>ScoreScene</h1>
 * <p>The score scene is the subscene in the menu that shows the top X scores</p> 
 * @author roeec
 *
 */
public class ScoreScene extends SubScene{
	
	
	private final String FONT_PATH = "";
	private final String BACKGROUND_IMAGE = "";
	private double WIDTH;
	private double HEIGHT;
	private double endPointY;
	private double startPointY;
	private boolean isHidden;
	
	// Constructor
	public ScoreScene(double width, double height,double startPointY, double endPointY) {
		super(new BorderPane(),width, height);
		this.WIDTH = width;
		this.HEIGHT = height;
		this.endPointY = endPointY;
		this.startPointY = startPointY;
		isHidden = true;
		prefWidth(WIDTH);
		prefHeight(HEIGHT);
		Image backgroundImage;
		try {
			backgroundImage = new Image(BACKGROUND_IMAGE, WIDTH,HEIGHT,false,false);
		}
		catch (IllegalArgumentException e) {
			System.out.println("ERROR: Could not load background Image for scores SubScene! (ScoreScene.java)");
			backgroundImage = new Image("LoadingErrorContents\\blackBox.png", WIDTH,HEIGHT,false,false);
		}
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,BackgroundPosition.DEFAULT,null);
		BorderPane  root = (BorderPane) this.getRoot();
		root.setBackground(new Background(background));
	
		
	}
	// Move animation for subScene
	public void movSubScene() {
		TranslateTransition transition = new TranslateTransition();
		transition.setDuration(Duration.seconds(0.3));
		transition.setNode(this);
		isHidden = !isHidden;
		if(isHidden) {
			transition.setToY(startPointY);
			System.out.println("move up");
		}
		else {
			System.out.println("Move down to" + endPointY);
			transition.setToY(endPointY);
		}
		transition.play();
	}
}
