package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Score board handler for storing the scoreboard into a file
 * @author roeec
 *
 */
public class ScoreBoardHandler {
	private List<PersonScore> list;
	private int maxScores;
	private int numScores;
	private String FILEPATH = "C:\\Users\\roeec\\eclipse-workspace\\Flappy Learn\\Flappy Learn\\src\\model\\scores.txt";
	// Constructor
	public ScoreBoardHandler(int maxScores) {
		this.maxScores = maxScores;
		//I chose the LinkedList collection because of the ability to add/remove anywhere without
		//needing to move the whole list
		list = new LinkedList<PersonScore>();
		
		
		//The file's format is 'score' 'name'
		//score is of type double
		//name is of type string
		
		try {
			File myObj = new File(FILEPATH);
			//if file is not created already, create it
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("Opening file " + myObj.getName());
				//scanner for going over the file
				Scanner input = new Scanner(myObj);
				while (input.hasNext()) {
					String info = input.nextLine();

					//finding where the space is between the score and name to insert to list
					int i;
					for (i = 0; i < info.length() && info.charAt(i) != ' '; i++);
					list.add(new PersonScore(Double.parseDouble(info.substring(0,i)),
							info.substring(i + 1, info.length())));
					if(numScores < maxScores)
						numScores++;
				}
				input.close();
			}
			
		} catch (Exception  e) {
			System.out.println("ERROR: Could not read from file! (ScoreBoardHandler.java)");
			e.printStackTrace();
		}
	}
	
	//the function checks if a personScore is eliable to get added the the scoreboard
	//if the personScore is eligable, it is added and updated in the scoreboard file
	//Output:	int		<-- returns the index at which the score was inserted into (place)
	//						if it wasn't inserted the function returns -1
	public int add(PersonScore newScore) {
		//checking if new score is high enough to join scoreboard
		int i;
		for(i = 0; i < list.size() && list.get(i).getScore() >= newScore.getScore(); i++);
		//if new score is eligable for joining score board, rewrite the file with updated scores
		//and update list
		System.out.println("i=" + i);
		//if(i < maxScores) {
			/*
			 * if(numScores == maxScores) list.remove(i);
			 */
			list.add(i, newScore);
			System.out.println(list);
			if(numScores < maxScores) {
				numScores++;
				updateFile(newScore);
				return numScores-1;
			}
			updateFile(newScore);
			return i;
			/*
			 * } else {
			 * 
			 * }
			 */
	}
	// The function updates the file stored with a new score
	public void updateFile(PersonScore newScore) {
		//rewriting the score file with updated scores
		System.out.println("Updating File! (ScoreBoardHandler.java)");
		try {
			//The file's format is 'score' 'name'
			//score is of type double
			//name is of type string
			FileWriter writer = new FileWriter(FILEPATH);
			System.out.println(numScores);
			for(int i = 0; i < list.size();i++) {
				writer.write(list.get(i).getScore() + " " + list.get(i).getName() + "\n");
			}
			writer.close();
			
		} catch (Exception e) {
			System.out.println("ERROR: Could not update file! (ScoreBoardHandler.java)");
			e.printStackTrace();
		}
	}
	// the function returns the list of scores stored
	public List<PersonScore> getList() {
		return list;
	}
	// the function returns the amount of scores stored
	public int getNumScores() {
		return numScores;
	}
	// the function clears the scores stored
	public void clearList() {
		list.clear();
		numScores = 0;
	}
}
