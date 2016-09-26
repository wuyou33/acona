package at.tuwien.ict.acona.cell.cellfunction;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import at.tuwien.ict.acona.cell.communicator.Communicator;
import at.tuwien.ict.acona.cell.config.CellFunctionConfig;
import at.tuwien.ict.acona.cell.config.SubscriptionConfig;
import at.tuwien.ict.acona.cell.core.Cell;
import at.tuwien.ict.acona.cell.datastructures.Datapoint;

public abstract class CellFunctionImpl implements CellFunction {
	
	protected static Logger log = LoggerFactory.getLogger(CellFunctionImpl.class);
	/**
	 * Cell, which executes this function
	 */
	private Cell cell;
	private CellFunctionConfig config;
	
	private int executeRate = 1000;
	private boolean executeOnce = true;
	
	/**
	 * Name of the activator
	 */
	private String name;
	
	/**
	 * List of datapoints that shall be subscribed
	 */
	private final Map<String, SubscriptionConfig> subscriptions = new HashMap<String, SubscriptionConfig>();	//Variable, datapoint
	
	protected ControlCommand currentCommand = ControlCommand.STOP;
	protected boolean runAllowed = false;

	@Override
	public CellFunction init(CellFunctionConfig config, Cell caller) throws Exception {
		try {
			//Extract settings
			this.config = config;
			this.cell = caller;
			
			//Get name
			this.name = config.getName();
			//Get execute once as optional
			if (config.isExecuteOnce()!=null) {
				this.setExecuteOnce(config.isExecuteOnce().getAsBoolean());
			}
			
			//Get executerate as optional
			if (config.getExecuteRate()!=null) {
				this.setExecuteRate(config.getExecuteRate().getAsInt());
			}
			
			//Possibility to add more subscriptions
			cellFunctionInit();
			
			//Get subscriptions from config and add to subscription list
			this.config.getSubscriptionConfig().forEach(s->{
				this.subscriptions.put(s.getId(), s);
			});
			
			//Get custom configs
			
		} catch (Exception e) {
			log.error("Cannot init function with config={}", config);
			throw new Exception(e.getMessage());
		}
		
		return this;
	}
	
	protected abstract void cellFunctionInit() throws Exception;
	
	//protected abstract void updateDatapoint(Datapoint subscribedData) throws Exception;
	@Override
	public void updateData(Map<String, Datapoint> data) {
		//Create datapointmapping ID to datapoint with new value
		Map<String, Datapoint> subscriptions = new HashMap<String, Datapoint>();
		this.getSubscribedDatapoints().forEach((k, v)->{
			if (data.containsKey(v.getAddress())) {
				subscriptions.put(k, data.get(v.getAddress()));
			}
		});
		
		this.updateDatapointsById(subscriptions);
	}
	
	protected abstract void updateDatapointsById(Map<String, Datapoint> data);
	
	protected abstract void executeFunction() throws Exception;
	
	protected abstract void executePostProcessing();
	
	protected abstract void executePreProcessing();

	public abstract void setCommand(ControlCommand command);

	@Override
	public String getFunctionName() {
		return this.name;
	}
	
	@Override
	public void setStart() {
		this.setCommand(ControlCommand.START);
	}

	@Override
	public void setStop() {
		this.setCommand(ControlCommand.STOP);
	}

	@Override
	public void setPause() {
		this.setCommand(ControlCommand.PAUSE);
		
	}

	@Override
	public void setExit() {
		//Unsubscribe all datapoints
		//this.getCell().getFunctionHandler().deregisterActivatorInstance(this);
		
		//Execute specific functions
		this.setCommand(ControlCommand.EXIT);
	}
	
	@Override
	public Map<String, SubscriptionConfig> getSubscribedDatapoints() {	//ID config
		return subscriptions;
	}
	
	@Override
	public CellFunctionConfig getFunctionConfig() {
		return this.config;
	}
	
	public int getExecuteRate() {
		return executeRate;
	}

	public void setExecuteRate(int blockingTime) {
		this.executeRate = blockingTime;
	}
	
	protected boolean isExecuteOnce() {
		return executeOnce;
	}

	protected void setExecuteOnce(boolean executeOnce) {
		this.executeOnce = executeOnce;
	}
	
	
	
	//=== read and write shortcuts ===//
	
	protected Communicator getCommunicator() {
		return this.getCell().getCommunicator();
	}
	
	protected void writeLocal(Datapoint datapoint) throws Exception {
		this.getCommunicator().write(datapoint);
	}
	
	protected <DATATYPE> void writeLocal(String address, DATATYPE datapoint) throws Exception {
		Gson gson = new Gson();
		String value = gson.toJson(datapoint);
		this.getCommunicator().write(Datapoint.newDatapoint(address).setValue(value));
	}
	
	protected Datapoint readLocal(String address) throws Exception {
		return this.getCommunicator().read(address);
	}
	
	protected JsonElement readLocalAsJson(String address) throws Exception {
		return this.getCommunicator().read(address).getValue();
	}
	
	protected String getCustomSetting(String key) {
		return this.getCell().getConfiguration().get(key).getAsString();
	}

	protected Cell getCell() {
		return cell;
	}

	protected ControlCommand getCurrentCommand() {
		return currentCommand;
	}

	protected void setCurrentCommand(ControlCommand currentCommand) {
		this.currentCommand = currentCommand;
	}

	protected boolean isAllowedToRun() {
		return runAllowed;
	}

	protected void setAllowedToRun(boolean isAllowedToRun) {
		this.runAllowed = isAllowedToRun;
	}

	protected CellFunctionConfig getConfig() {
		return config;
	}


}