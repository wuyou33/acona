package at.tuwien.ict.acona.cell.custombehaviors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.ict.acona.cell.core.Cell;
import at.tuwien.ict.acona.cell.core.CellFunctionBehavior;
import at.tuwien.ict.acona.cell.datastructures.Datapackage;

public class AdditionBehavior extends CellFunctionBehavior {
	
	protected static Logger log = LoggerFactory.getLogger(AdditionBehavior.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AdditionBehavior(String name, Cell caller) {
		super(name, caller);
	}

	@Override
	public void function(Datapackage data) {
		
		
		log.info("execute behavior addition!!!!");
		
	}

}
