package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.sun.prism.paint.Color;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
/**
 * <h1>ScoreBoard</h1>
 * <p>Score board makes and javaFX table needed for displaying names and corresponding scores</p>
 * @author roeec
 *
 */
public class ScoreBoard {
	private BorderPane pane;
	private final String FONT_PATH = "";
	private int MAIN_TITLE_FONT_SIZE = 65;
	private int TITLE_FONT_SIZE = 35;
	private int FONT_SIZE = 25;
	private Text scores[];
	private int numScores;
	private ScoreBoardHandler scoreBoard;
	private GridPane table;
	// Constructor
	public ScoreBoard(BorderPane pane, int scoreAmount) {
		this.pane = pane;		
		
		
		//Launching ScoreBoardHandler
		scoreBoard = new ScoreBoardHandler(scoreAmount);
		
		
		//Creating and initializing Table Presets
		//Table will be of type GridPane
	    table = new GridPane();
		table.setPadding(new Insets(10,10,10,10));
		table.setVgap(10);
		table.setHgap(100);
		table.setAlignment(Pos.BASELINE_LEFT);
		
		//Scores Title
		setTitles();
		
		//we need triple the slots because every score is made of a number, score,name
		scores = new Text[scoreAmount * 3 + 3];//+3 is for titles
		numScores = scoreBoard.getNumScores();
		
		//adding numbering, scores and names to table
		updateTable();
		
				
		//Layout in pane
		pane.setPadding(new Insets(50));
		pane.setCenter(table);
	}
	
	// the function adds a name and score entry to the scoreboard
	public void addScore(String name, double score) {
		int index = scoreBoard.add(new PersonScore(score, name));
		//if score was added to board
		System.out.println("Index = " + index);
		if(index > -1) {
			updateTable();
		}
	}
	
	//Updates table cells starting from second row
	public void updateTable() {
		numScores = scoreBoard.getNumScores();
		System.out.println("numScores= " + numScores);
		List<PersonScore> sList = scoreBoard.getList();
		for(int i = 0;i < numScores; i++) {
			table.getChildren().remove(scores[(i+1)*3]);
			table.getChildren().remove(scores[(i+1)*3+1]);
			table.getChildren().remove(scores[(i+1)*3+2]);
			
			scores[(i+1)*3] = new Text((i+1) + ".");
			scores[(i+1)*3+1] = new Text(sList.get(i).getScore() + "");
			scores[(i+1)*3+2] = new Text(sList.get(i).getName());
			try {
				scores[(i+1)*3].setFont(Font.loadFont(new FileInputStream(FONT_PATH), TITLE_FONT_SIZE));
				scores[(i+1)*3+1].setFont(Font.loadFont(new FileInputStream(FONT_PATH), FONT_SIZE));
				scores[(i+1)*3+2].setFont(Font.loadFont(new FileInputStream(FONT_PATH), FONT_SIZE));
			} catch (IOException e) {
				//setting default Font if error loading in custom font
				System.out.println("Setting Default Font! (ScoreBoard.java)");
				scores[(i+1)*3].setFont(Font.font("Verdana",TITLE_FONT_SIZE));
				scores[(i+1)*3+1].setFont(Font.font("Verdana",FONT_SIZE));
				scores[(i+1)*3+2].setFont(Font.font("Verdana",FONT_SIZE));
			}
			
			table.add(scores[(i+1) * 3], 0, i+1);
			table.add(scores[(i+1) * 3+1], 1, i+1);
			table.add(scores[(i+1) * 3+2], 2, i+1);
		}
	}
	
	//sets fonts for a Text Object
	private void setFonts(Text text, String Path, int size) {
		try {
			text.setFont(Font.loadFont(new FileInputStream(Path), size));
			
		} catch (IOException e) {
			//setting default Font if error loading custom font
			System.out.println("Setting Default Font! (ScoreBoard.java)");
			text.setFont(Font.font("Verdana",size));
		}
	}
	
	// The function sets the titles for the table
	private void setTitles() {
		Text title = new Text("You ScoreBoard!");
		//adding Score & Name titles to table
		Text scoresTitle = new Text("Score");
		Text nameTitle = new Text("Name");
		Text keepAlignment = new Text("   ");
		//Setting Fonts
		setFonts(title, FONT_PATH, MAIN_TITLE_FONT_SIZE);
		setFonts(scoresTitle, FONT_PATH, TITLE_FONT_SIZE);
		setFonts(nameTitle, FONT_PATH, TITLE_FONT_SIZE);
		setFonts(keepAlignment, FONT_PATH, TITLE_FONT_SIZE);
		//Layout in pane
		pane.setTop(title);
		
		//Scores Title
		table.add(keepAlignment, 0, 0);
		table.add(scoresTitle, 1, 0);
		table.add(nameTitle, 2, 0);
		
		
	}
	// The function clears(resets) the table
	public void clearGrid() {
		table.getChildren().clear();
		scoreBoard.clearList();
		setTitles();
		pane.setCenter(table);
	}
}
