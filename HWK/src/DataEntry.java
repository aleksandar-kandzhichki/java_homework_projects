import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.database.Item;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.database.Sequence;

public class DataEntry {

	String date;
	String time;
	String context;
	String component;
	String name;
	String description;
	String origin;
	String ip;
	
	DataEntry() {
		date = "";
		time = "";
		context = "";
		component = "";
		name = "";
		description = "";
		origin = "";
		ip = "";
	}
	
	DataEntry(DataEntry old){
		date = old.date;
		time = old.time;
		context = old.context;
		component = old.component;
		name = old.name;
		description = old.description;
		origin = old.origin;
		ip = old.ip;
	}
	
	
	DataEntry fromRow(String row) throws Exception {
		String separator = ",";
		return fromRow(row, separator);
	}
	DataEntry fromRow(String row, String separator) throws Exception {
		String[] split = row.split(separator);
		
		if(split.length < 7) throw new Exception("Invalid row data - not enough columns for row: " + row);
		date = split[0].replaceAll("\"", "");
		time = split[1].replaceAll("\"", "");
		context = split[2];
		component = split[3];
		name = split[4];
		description = split[5];
		origin = split[6];
		if(split.length > 7) ip = split[7];
		
		return this;
	}
	

	
	public static String toSequence(Map<String, Integer> mapper, List<DataEntry> activities) {
		String row = "";
		Iterator<DataEntry> activitiesIter = activities.iterator();
		while(activitiesIter.hasNext()) {
			row += mapper.get(activitiesIter.next().context) + " -1 ";
		}
		
		// return example: "1 -1 2 -1 3 -1 4 -1 7 -1 8 -1 9" (-1 is separator for the + numebers)
 		return row;
	}
	
	public static Set<String> getUserIps(List<DataEntry> data) {
		return data.stream().map(e -> e.ip).collect(Collectors.toSet());
	}
	
	public static List<Sequence> getSequenceList(Map<String, Integer> keyMapper, List<DataEntry> data) {

		List<DataEntry> currentItemset = new ArrayList<DataEntry>();
		List<Sequence> rows = new ArrayList<Sequence>();
		
		Iterator<String> userIpsIter = DataEntry.getUserIps(data).iterator();
		Integer i = 0;
		
		
		// iterate through all users
		do {
			String temp = "";
			while(temp.equals("") && userIpsIter.hasNext()) {
				temp = userIpsIter.next();
			}
			
			final String currentUserIp = temp;
			currentItemset.clear();
			
			// prepare all records for single user. From them we make multiple rows, each row represents 1 user session activity (1 day)
			List<DataEntry> userRecords = data.stream().filter(entry -> (entry.ip.equals(currentUserIp))).collect(Collectors.toList());
			Iterator<DataEntry> userRecordsIter = userRecords.iterator();

			String currentTime = userRecords.get(0).date;
			
			// iterate all records in file for this user
			do {
				DataEntry curDataEntry = userRecordsIter.next();
				if(!curDataEntry.date.equals(currentTime)) {
					// then we should start writing new row, save current
					// String currentRow = DataEntry.toSequence(keyMapper, currentItemset);
					// currentRow += "-2";
					// rows.add(currentRow);
					
					String[] itemStrs = DataEntry.toSequence(keyMapper, currentItemset).split(" -1 ");
					Sequence seq = new Sequence(++i);
					for(int j=0;j<itemStrs.length; j++) {
						seq.addItem(new Item(Integer.valueOf(itemStrs[j])));
					}
					rows.add(seq);
					
					currentTime = curDataEntry.date;
					currentItemset.add(curDataEntry);
				}
				else {
					currentItemset.add(curDataEntry);
				}
			} while(userRecordsIter.hasNext());
			
			if(!currentItemset.isEmpty()) {
				String[] itemStrs = DataEntry.toSequence(keyMapper, currentItemset).split(" -1 ");
				Sequence seq = new Sequence(++i);
				for(int j=0;j<itemStrs.length; j++) {
					seq.addItem(new Item(Integer.valueOf(itemStrs[j])));
				}
				rows.add(seq);
			}
		} while(userIpsIter.hasNext()); 
		
		return rows;
	}


	public static List<String> getSequenceRows(Map<String, Integer> keyMapper, List<DataEntry> data) {
		// USELESS. COULD be used for CMRULES
		
		List<DataEntry> currentItemset = new ArrayList<DataEntry>();
		List<String> rows = new ArrayList<String>();
		
		Iterator<String> userIpsIter = DataEntry.getUserIps(data).iterator();

		
		// iterate through all users
		while(userIpsIter.hasNext()) {
			final String currentUserIp = userIpsIter.next();
			currentItemset.clear();
			String currentTime = "";
			
			// prepare all records for single user. From them we make multiple rows, each row represents 1 user session activity (1 day)
			List<DataEntry> userRecords = data.stream().filter(entry -> (entry.ip.equals(currentUserIp))).collect(Collectors.toList());
			Iterator<DataEntry> userRecordsIter = userRecords.iterator();
			
			// iterate all records in file for this user
			while(userRecordsIter.hasNext()) {
				DataEntry curDataEntry = userRecordsIter.next();
				if(!curDataEntry.date.equals(currentTime)) {
					// then we should start writing new row, save current
					String currentRow = DataEntry.toSequence(keyMapper, currentItemset);
					currentRow += "-2";
					rows.add(currentRow);
					
					currentTime = curDataEntry.date;
					currentItemset.add(curDataEntry);
				}
				else {
					currentItemset.add(curDataEntry);
				}
			}
		}
		
		return rows;
	}
	
}
