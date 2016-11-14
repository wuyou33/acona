package at.tuwien.ict.kore.behaviours;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import _OLD.at.tuwien.ict.acona.cell.activator.jadebehaviour.CellFunctionBehaviourImpl;
import at.tuwien.ict.acona.cell.core.Cell;
import at.tuwien.ict.acona.cell.datastructures.Datapoint;

public class SubtractionBehaviour extends CellFunctionBehaviourImpl {
	
	protected static Logger log = LoggerFactory.getLogger(SubtractionBehaviour.class);
	
	private static final String OPERAND1ADDRESS = "operand1";
	private static final String OPERAND2ADDRESS = "operand2";
	private static final String RESULTADDRESS = "result";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SubtractionBehaviour() {
		super();
	}

	@Override
	public void function(Map<String, Datapoint> data) {
		
		//Get operand1
		double operand1 = data.get(conf.get(OPERAND1ADDRESS).getAsString()).getValue().getAsDouble();
		//Get operand2
		double operand2 = data.get(conf.get(OPERAND2ADDRESS).getAsString()).getValue().getAsDouble();
		
		//Perform operation
		double result = operand1 - operand2;
		
		//Write result in memory
		this.caller.getDataStorage().write(Datapoint.newDatapoint(conf.get(RESULTADDRESS).getAsString()).setValue(String.valueOf(result)), caller.getName());
		log.info("{}> Add {} + {} = {}", this.name, operand1, operand2, result);
		
		
	}

}