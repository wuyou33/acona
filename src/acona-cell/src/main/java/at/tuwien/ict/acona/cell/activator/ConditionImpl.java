package at.tuwien.ict.acona.cell.activator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import at.tuwien.ict.acona.cell.datastructures.Datapoint;

public abstract class ConditionImpl implements Condition {

	protected static Logger log = LoggerFactory.getLogger(ConditionImpl.class);
	
	protected String name = "";
	
	@Override
	public void init(String name, JsonObject settings) {
		this.name = name;
		
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public abstract boolean testCondition(Datapoint data);

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("name=");
		builder.append(name);
		return builder.toString();
	}

}
