package game;

import defaultSettings.GameSettings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
/**
 * <h1>Moving Background</h1>
 * <p> The background is an image moved every time moveBackground() is called</p>
 * @author roeec
 */
public class MovingBackground {
	
	private Pane pane;
	private ImageView backgroundImage;
	/**
	 * Constructor
	 * @param pane - game pane
	 */
	public MovingBackground(Pane pane) {
		this.pane = pane;
		setBackground();
	}
	/**
	 * sets the backgroundImage
	 */
	private void setBackground() {
		backgroundImage = new ImageView(new Image("view\\resources\\background.png", GameSettings.WIDTH * 3, GameSettings.HEIGHT, false, false));
		backgroundImage.setLayoutX(0);
		pane.getChildren().add(backgroundImage);
	}
	
	/**
	 * Moves the background left and resets it's position if neccesary
	 */
	public void moveBackground() {
		backgroundImage.setLayoutX(backgroundImage.getLayoutX() - 2 * GameSettings.speedYTimer);
		if(backgroundImage.getLayoutX() == -2 * GameSettings.WIDTH)
			backgroundImage.setLayoutX(0);
	}
	/**
	 * resets background position
	 */
	public void resetLayout() {
		backgroundImage.setLayoutX(0);
	}
}
