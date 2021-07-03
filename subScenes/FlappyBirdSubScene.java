package subScenes;


import javafx.animation.TranslateTransition;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

/**
 * <h1>FlappyBirdSubScene</h1>
 * <p>The score scene is the subscene in the menu that shows the top X scores</p> 
 * @author roeec
 *
 */
public class FlappyBirdSubScene extends SubScene{
	private final String FONT_PATH = "";
	private final String BACKGROUND_IMAGE = "";
	private double WIDTH;
	private double HEIGHT;
	private double endPointY;
	private double startPointY;
	private boolean isHidden;
	
	// Constructor
	public FlappyBirdSubScene(double f, double height2,double startPointY2, double endPointY2) {
		super(new BorderPane(),f, height2);
		this.WIDTH = f;
		this.HEIGHT = height2;
		this.endPointY = endPointY2;
		this.startPointY = startPointY2;
		isHidden = true;
		prefWidth(WIDTH);
		prefHeight(HEIGHT);
		Image backgroundImage;
		try {
			backgroundImage = new Image(BACKGROUND_IMAGE, WIDTH,HEIGHT,false,false);
		}
		catch (IllegalArgumentException e) {
			System.out.println("ERROR: Could not load background Image for scores SubScene!");
			backgroundImage = new Image("LoadingErrorContents\\whiteBox.png", WIDTH,HEIGHT,false,false);
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
	// Get pane
	public BorderPane getPane() {
		return (BorderPane) this.getRoot();
	}
}
