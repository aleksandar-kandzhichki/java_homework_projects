import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import ca.pfv.spmf.algorithms.sequential_rules.cmrules.AlgoCMRules;

public class MiningHelper {

	Vector<String> mapper;
	Map<String, Integer> keys;
	int uniqueKeys;
	static String dataMiningInput = "dataInput.txt";
	static String dataMiningOutput = "dataOutput.txt";
	
	MiningHelper() {
		mapper = new Vector<String>();
		keys = new HashMap<String, Integer>();
		uniqueKeys = 0;
	}

	
	private void extendMapper(DataEntry entry) {
		if(!keys.containsKey(entry.component)) {mapper.add(entry.component);keys.put(entry.component, ++uniqueKeys);} 
		if(!keys.containsKey(entry.context)) {mapper.add(entry.context); keys.put(entry.context, ++uniqueKeys);} 
		if(!keys.containsKey(entry.description)) {mapper.add(entry.description); keys.put(entry.description, ++uniqueKeys);} 
		if(!keys.containsKey(entry.name)) {mapper.add(entry.name); keys.put(entry.name, ++uniqueKeys);} 
	}
	
	public void prepareFilesForDataMining(List<DataEntry> data) throws IOException {
		BufferedWriter fileToPrepare = new BufferedWriter(new FileWriter(new File(dataMiningInput)));
		
		java.util.Iterator<DataEntry> iter = data.iterator();
		DataEntry cur;
		while(iter.hasNext()) {
			cur= iter.next();
			extendMapper(cur);
			String row = cur.toSequenceRow(keys);
			fileToPrepare.write(row);
			fileToPrepare.newLine();
		}
		
		fileToPrepare.close();
	}
	
	public void runDataMining() {
		AlgoCMRules lib = new AlgoCMRules();
		String input = dataMiningInput;
		String output = dataMiningOutput;
		
		try {
			lib.runAlgorithm(input, output, 0.01, 0.01);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
