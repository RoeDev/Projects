package Agent.ML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class serializeModels {
	private Map<String, Sequential> map;
	private String path = "src\\models";
	private File fileObj;
	
	public serializeModels() {
		load();
	}
	
	public synchronized void addModel(String name, Sequential m) {	
		load();
		map.put(name, m);
		save();
	}
	public void removeModel(String name) {
		load();
		map.remove(name);
		save();
	}
	
	public void load() {
		fileObj = new File(path);
		try {
			
			if (fileObj.exists() && fileObj.length() > 0) {
				System.out.println("Size: " + fileObj.length());
				FileInputStream fis = new FileInputStream(path);
	            ObjectInputStream ois = new ObjectInputStream(fis);
	            Object obj = ois.readObject();
	            if(obj instanceof Map)
	            	map = (HashMap<String, Sequential>) obj;
	            else {
	            	System.out.println("Error Reading from map!");
					map = new HashMap<String, Sequential>();
	            }
				ois.close();
	            fis.close();
			}
			else {
				fileObj.createNewFile();
				map = new HashMap<String, Sequential>();
				System.out.println("File created: " + fileObj.getName());
				
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			map = new HashMap<String, Sequential>();
		}
	}
	private void save() {
		if(map == null)
			System.out.println("MAP IS NULL!");
		fileObj = new File(path);
		try {
			if(fileObj.delete() && fileObj.createNewFile()) {
				FileOutputStream fos = new FileOutputStream(path);
	            ObjectOutputStream oos = new ObjectOutputStream(fos);
	            oos.writeObject((HashMap<String, Sequential>)map);
	            oos.close();
	            fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Map<String, Sequential> getMap() {
		load();
		return map;
	}
}
