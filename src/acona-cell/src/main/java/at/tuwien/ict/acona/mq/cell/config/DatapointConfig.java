package at.tuwien.ict.acona.mq.cell.config;

import com.google.gson.JsonObject;

import at.tuwien.ict.acona.cell.cellfunction.SyncMode;
import at.tuwien.ict.acona.cell.datastructures.Datapoint;
import at.tuwien.ict.acona.cell.datastructures.DatapointBuilder;

public class DatapointConfig {
	public final static String LOCALAGENTNAME = "";
	// public final static SyncMode DEFAULTSYNCMODE = SyncMode.READONLY;

	public static final String ID = "id";
	public static final String ADDRESS = "address";
	public static final String AGENTID = "agentid";
	public static final String SYNCMODE = "syncmode"; // pull, push, "" oder
														// null

	// Keep the jsonobject in order to be able to add more settings. If only a
	// class is used, flexibility is lost for creating
	// new json

	private final JsonObject configObject;

	// public static DatapointConfig newConfig(String name, String address) {
	// return new DatapointConfig(name, address, LOCALAGENTNAME, DEFAULTSYNCMODE);
	// }

	public static DatapointConfig newConfig(String name, String address, SyncMode syncmode) {
		Datapoint dp = DatapointBuilder.newDatapoint(address);
		return new DatapointConfig(name, dp.getAddress(), dp.getAgent(LOCALAGENTNAME), syncmode);
	}

	public static synchronized DatapointConfig newConfig(String name, String address, String agentid, SyncMode syncmode) {
		return new DatapointConfig(name, address, agentid, syncmode);
	}

	public static DatapointConfig newConfig(JsonObject config) throws Exception {
		return new DatapointConfig(config);
	}

	private DatapointConfig(String id, String address, String agentid, SyncMode syncmode) {
		super();
		this.configObject = new JsonObject();
		this.setId(id);
		this.setAddress(address);
		this.setAgentId(agentid);
		this.setSyncMode(syncmode);
	}

	private DatapointConfig(JsonObject config) throws Exception {
		super();
		if (this.isSubscriptionConfig(config) == true) {
			this.configObject = config;

			if (this.configObject.get(AGENTID) == null) {
				this.setAgentId(LOCALAGENTNAME);
			}
		} else {
			throw new Exception("The json is no subscription config");
		}

	}

	private void setId(String id) {
		this.configObject.addProperty(ID, id);
	}

	private void setAddress(String id) {
		this.configObject.addProperty(ADDRESS, id);
	}

	private void setAgentId(String id) {
		this.configObject.addProperty(AGENTID, id);
	}

	private void setSyncMode(SyncMode mode) {
		this.configObject.addProperty(SYNCMODE, mode.toString());
	}

	public String getId() {
		return this.configObject.get(ID).getAsString();
	}

	public String getAddress() {
		return this.configObject.get(ADDRESS).getAsString();
	}

	/**
	 * Return the destination agentid. Compare the local agent id with the destination id. If the
	 * default value "" is used, it means that the destination should be the local agent
	 * 
	 * @param callerAgentName
	 * @return
	 */
	public String getAgentid(String callerAgentName) {
		String agentName = this.configObject.get(AGENTID).getAsString();

		if (agentName == null || agentName.isEmpty() || agentName.equals("")) {
			agentName = callerAgentName;
		}

		return agentName;
	}

	// private boolean hasAgentId() {
	// String agentName = this.configObject.get(AGENTID).getAsString();
	//
	// boolean result = true;
	//
	// if (agentName == null || agentName.isEmpty() || agentName.equals("")) {
	// result = false;
	// }
	//
	// return result;
	// }

	public SyncMode getSyncMode() {
		return SyncMode.valueOf(this.configObject.get(SYNCMODE).getAsString());
	}

	public JsonObject toJsonObject() {
		return this.configObject;
	}

	public boolean isSubscriptionConfig(JsonObject testObject) {
		boolean result = false;
		if (testObject.get(ADDRESS) != null && testObject.get(ADDRESS).isJsonPrimitive() == true
				&& testObject.get(ID) != null && testObject.get(ID).isJsonPrimitive() == true) {
			result = true;
		}

		return result;
	}

	public String getComposedAddress(String defaultAgentName) {
		String destinationAgent = this.getAgentid(defaultAgentName);
		String address = this.getAddress();

		// Generate key for the internal activator
		String key = destinationAgent + ":" + address;

		return key;
	}

	public Datapoint toDatapoint(String localAgentName) {
		return DatapointBuilder.newDatapoint(this.getAgentid(localAgentName) + ":" + this.getAddress());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubscriptionConfig [configObject=");
		builder.append(configObject);
		builder.append("]");
		return builder.toString();
	}

}
