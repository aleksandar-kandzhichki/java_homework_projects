import java.util.Map;

public class DataEntry {

	String time;
	String context;
	String component;
	String name;
	String description;
	String origin;
	String ip;
	
	DataEntry() {
		time = "";
		context = "";
		component = "";
		name = "";
		description = "";
		origin = "";
		ip = "";
	}
	
	DataEntry(DataEntry old){
		time = old.time;
		context = old.context;
		component = old.component;
		name = old.name;
		description = old.description;
		origin = old.origin;
		ip = old.ip;
	}
	
	
	String toSequenceRow(Map<String, Integer> mapper) {
		return Integer.toString(mapper.get(context)) + " " + Integer.toString(mapper.get(component)) + " " + 
				Integer.toString(mapper.get(name)) + " " + Integer.toString(mapper.get(description));
	}
	
	DataEntry fromRow(String row) throws Exception {
		String separator = ",";
		return fromRow(row, separator);
	}
	DataEntry fromRow(String row, String separator) throws Exception {
		String[] split = row.split(separator);
		
		if(split.length < 7) throw new Exception("Invalid row data - not enough columns for row: " + row);
		time = split[0]+split[1];
		context = split[2];
		component = split[3];
		name = split[4];
		description = split[5];
		origin = split[6];
		if(split.length > 7) ip = split[7];
		
		return this;
	}
}
