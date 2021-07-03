package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
/**
 * Menu Button
 * @author roeec
 *
 */
public class MenuButtons extends Button {
	private static final String FONT_PATH = "";
	private static final String BUTTON_PRESSED_STYLE = "";//= "view\\resources\\ButtonBackground.png";
	private static final String BUTTON_FREE_STYLE_BIG = "-fx-background-color: transparent; -fx-background-image: url('/model/recources/ButtonBackground.png');";
	private static final String BUTTON_FREE_STYLE_SMALL = "-fx-background-color: transparent; -fx-background-image: url('/model/recources/SmallButtonBackground.png');";
	private static int WIDTH;
	private static int HEIGHT;
	private static int FONT_SIZE = 65;
	private boolean buttonBeingPressed = false;
	
	// Constructor
	public MenuButtons(String text,int butttonWidth,int buttonHeight, boolean customFontSize, int fontSize) {
		
		WIDTH = butttonWidth;
		HEIGHT = buttonHeight;
		if(customFontSize)
			FONT_SIZE = fontSize;
		setText(text);
		setButtonFont();
		
		//setting prefered width and height for button (equal to image width and height)
		setPrefHeight(HEIGHT);
		setPrefWidth(WIDTH);
		setWidth(WIDTH);
		setHeight(HEIGHT);
		//setStyle("-fx-background-color: transparent; -fx-background-image: url('/model/recources/ButtonBackground.png'); background-repeat: no-repeat;background-size: cover;");
		if(butttonWidth > 500)
			setStyle(BUTTON_FREE_STYLE_BIG);
		else
			setStyle(BUTTON_FREE_STYLE_SMALL);
//	    ImageView image = new ImageView(new Image("/model/recources/ButtonBackground.png"));
//	    image.setX(WIDTH);
//	    image.setY(HEIGHT);
//	    image.setPreserveRatio(false);
//	    setGraphic(image);
		initializeButtonListeners();
	}
	// Setting button fonts
	private void setButtonFont() {
		try {
			setFont(Font.loadFont(new FileInputStream(FONT_PATH), FONT_SIZE));
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: loading in Font!");
			setFont(Font.font("Verdana",FONT_SIZE));
		}
	}
	
	// Setting button pressed Style
	private void setButtonPressedStyle() {
		if(!buttonBeingPressed) {
			buttonBeingPressed = true;
//			setStyle(BUTTON_PRESSED_STYLE);
//			setPrefHeight(HEIGHT);
			setLayoutY(getLayoutY() + 4);
		}
	}
	// Setting button released Style
	private void setButtonReleasedStyle() {
		if(buttonBeingPressed) {
			buttonBeingPressed = false;
//			setStyle(BUTTON_FREE_STYLE);
//			setPrefHeight(HEIGHT);
			setLayoutY(getLayoutY() - 4);
		}
	}
	
	// Initialize button listeners
	private void initializeButtonListeners() {
		
		
		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY)) {
					setButtonPressedStyle();
				}
			}
		});
		
		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY)) {
					setButtonReleasedStyle();
				}
			}
		});
		
		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setEffect(new DropShadow());
			}
		});
		
		setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setEffect(null);
			}
		});
	}
}
