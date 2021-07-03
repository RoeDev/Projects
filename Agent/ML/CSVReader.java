	package Agent.ML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import Agent.ML.Exceptions.InvalidCSVException;

/**
 * <h1>CSV Reader</h1>
 * <p>CSV reader allows reading from external datasets for using the machine learning in other applications</p>
 * @author roeec
 *
 */
public class CSVReader {
	private File fileObj;
	private Scanner input;
	private FileWriter writer;
	
	/**
	 * Constructor
	 * @param file - dataset
	 * @throws IOException
	 */
	public CSVReader(String file) throws IOException {
		fileObj = new File(file);
		if (fileObj.exists()) {
			input = new Scanner(fileObj);
			writer = new FileWriter(fileObj, true);
		}
		else {
			fileObj.createNewFile();
			System.out.println("File created: " + fileObj.getName());
			input = new Scanner(fileObj);
			writer = new FileWriter(fileObj);
		}
	}
	/**
	 * Wipes dataset
	 * @throws IOException
	 */
	public void wipeFile() throws IOException {
		if (fileObj.delete()) {
			System.out.println("File deleted");
		}
		if (fileObj.createNewFile()) {
			System.out.println("File created: " + fileObj.getName());
			input = new Scanner(fileObj);
			writer = new FileWriter(fileObj);
		}
	}
	/**
	 * appends to dataSet
	 * @param ar - array of doubles
	 * @throws IOException
	 */
	public void append(Double... ar) throws IOException {
		String str = "";
		for(int i = 0; i < ar.length; i++) {
			str += ar[i];
			if(i < ar.length-1)
				str+= ",";
			else
				str+="\n";
			
		}
		append(str);
	}
	/**
	 * append to file
	 * @param str - string that will be appended
	 * @throws IOException
	 */
	public void append(String str) throws IOException {
		writer.write(str);
	}
	/**
	 * checks if file exists
	 * @return true if file exists. false else
	 */
	public boolean exists() {
		return fileObj.exists();
	}
	/**
	 * closes resource(dataset)
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (writer != null)
			writer.close();
		if (input != null)
			input.close();
		
	}
	/**
	 * Add row to dataset
	 * @param row - list of doubles to be appended
	 * @throws IOException
	 */
	public void addRowToCSV(List<Double> row) throws IOException{
		String str = "";
		for(Double param : row) {
			str += param + ",";
		}
		append(str);
	}
	/**
	 * @return true if their is another line in dataset, else false
	 */
	public boolean hasNext() {
		return input.hasNextLine();
	}
	/**
	 * @return next item in dataset
	 */
	public List<Double> getNext() {
		String row = input.nextLine();
		return splitRow(row);
	}
	/**
	 * get lines param next lines from csv, if the csv doesn't have that much next lines, it will return what it has
	 * @param lines - amount of next lines to return
	 * @return next lines
	 */
	public List<List<Double>> getNextLinesFromCSV(int lines) {
		List<List<Double>> arr = new ArrayList<List<Double>>();
		for(int i = 0; i < lines && hasNext(); i++)
			arr.add(getNext());
		return arr;
	}
	/**
	 * Loads full dataset into memory
	 * @return full dataset in a list of dataset entrys(list)
	 * @throws InvalidCSVException
	 */
	public List<List<Double>> getFullDataSet() throws InvalidCSVException {
		
		if (!fileObj.exists())
			throw new InvalidCSVException("Loading data set", "CSV File doesn't exist!");

		List<List<Double>> dataSet = new LinkedList<List<Double>>();

		try {
			Scanner input = new Scanner(fileObj);
			while (input.hasNextLine()) {
				String row = input.nextLine();
				dataSet.add(splitRow(row));
			}
			input.close();
			return dataSet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * receives row of csv file and cuts it into params
	 * @param row of string in csv to list of doubles
	 * @return list of doubles
	 */
	private List<Double> splitRow(String row) {
		List<Double> split = new ArrayList<Double>();
		int start = 0;
		for (int i = 0; i < row.length(); i++) {
			if (row.charAt(i) == ',') {
				split.add(Double.parseDouble(row.substring(start, i)));
				start = i + 1;
			}
		}
		split.add(Double.parseDouble(row.substring(start, row.length())));
		return split;
	}

}
