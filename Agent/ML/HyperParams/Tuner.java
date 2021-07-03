package Agent.ML.HyperParams;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import Agent.ML.Pair;
import Agent.ML.Exceptions.InvalidParamsException;
import model.PersonScore;

/**
 * <h1>Tuner</h1>
 * <p>
 * The tuner tries different Hyper Params to find the best outputing network
 * </p>
 * 
 * @author roeec
 *
 */
public class Tuner {
	private List<HyperParmInfo> params;
	private List<Pair<Double, List<Double>>> results;
	private List<String> names;
	private Map<String, HyperParmInfo> map;
	private boolean done;
	private int currParamIndex;
	private String FILEPATH = "C:\\Users\\roeec\\eclipse-workspace\\Flappy Learn\\Flappy Learn\\src\\model\\tunerScores.txt";

	public Tuner() {
		params = new ArrayList<HyperParmInfo>();
		names = new ArrayList<String>();
		results = new ArrayList<Pair<Double, List<Double>>>();
		map = new HashMap<String, HyperParmInfo>();

		names.add("Score");
		done = false;

	}

	public void addParam(String name, List<Double> list) {
		if (list != null) {
			names.add(name);
			HyperParmInfo newParam = new HyperParmInfo(list);
			map.put(name, newParam);
			params.add(newParam);
		}
	}

	public double getParam(String name) throws InvalidParamsException {
		HyperParmInfo retrieve = map.get(name);
		if (retrieve == null)
			throw new InvalidParamsException("Invalid name for param !");
		return retrieve.getVal();
	}

	public void resetParamIndex() {
		currParamIndex = params.size() - 1;
	}

	public void step() {
		resetParamIndex();
		boolean stepBefore;

		do {
			stepBefore = false;
			HyperParmInfo currParam = params.get(currParamIndex);
			currParam.step();
			if (currParam.needReset()) {
				currParam.reset();
				stepBefore = true;
				if (currParamIndex == 0)
					done = true;
				else
					currParamIndex--;
			}
		} while (stepBefore);
		resetParamIndex();

	}

	public void addResult(double score) {
		List<Double> cpy = new ArrayList<Double>(params.size());
		for (HyperParmInfo currParam : params)
			cpy.add(currParam.getVal());

		results.add(new Pair<Double, List<Double>>(score, cpy));
	}

	public void saveScoreToFile() {
		System.out.println("Saving To File");
		Collections.sort((List<Pair<Double, List<Double>>>) results);
		try {
			File fileObj = new File(FILEPATH);
			if (fileObj.delete() && fileObj.createNewFile()) {
				FileWriter writer = new FileWriter(FILEPATH);
				for (Pair<Double, List<Double>> result : results) {
					String str = "";
					for (Double param : result.getVar2()) {
						str += param + ", ";
					}
					writer.write("|" + result.getVar1() + "| " + str + "\n");
				}
				writer.close();
			}

		} catch (Exception e) {
			System.out.println("ERROR: Could not update file! (ScoreBoardHandler.java)");
			e.printStackTrace();
		}
	}

	public boolean isRunning() {
		return !done;
	}
}
