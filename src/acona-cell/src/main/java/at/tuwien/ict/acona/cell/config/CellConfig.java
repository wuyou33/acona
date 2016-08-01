package at.tuwien.ict.acona.cell.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CellConfig {
	private static final String CELLNAME = "cellname";
	private static final String CELLCLASS = "cellclass";
	private static final String CELLCONDITIONS = "conditions";
	private static final String CELLBEHAVIOURS = "cellbehaviours";
	private static final String CELLACTIVATORS = "activators";
	
	//private Class<?> clzz;
	
	private final JsonObject configObject = new JsonObject();
	
	public static CellConfig newConfig(String name, String className) {
		return new CellConfig(name, className);
	}
	
	private CellConfig(String name, String className) {
		this.setName(name).setClassName(className);
		this.configObject.add(CELLCONDITIONS, new JsonArray());
		this.configObject.add(CELLBEHAVIOURS, new JsonArray());
		this.configObject.add(CELLACTIVATORS, new JsonArray());
	}
	
	private CellConfig setName(String name) {
		this.configObject.addProperty(CELLNAME, name);
		return this;
	}
	
	private CellConfig setClassName(String className) {
		this.configObject.addProperty(CELLCLASS, className);
		return this;
	}
	
	public CellConfig setClass(Class<?> clzz) {
		this.setClassName(clzz.getName());
		return this;
	}
	
	public Class<?> getClassToInvoke() throws Exception {
		return Class.forName(this.getClassName());
	}
	
	public CellConfig addProperty(String name, String value) {
		this.configObject.addProperty(name, value);
		return this;
	}
	
	public CellConfig addCondition(ConditionConfig config) {
		this.configObject.getAsJsonArray(CELLCONDITIONS).add(config.toJsonObject());
		return this;
	}
	
	public CellConfig addBehaviour(BehaviourConfig config) {
		this.configObject.getAsJsonArray(CELLBEHAVIOURS).add(config.toJsonObject());
		return this;
	}
	
	public CellConfig addActivator(ActivatorConfig config) {
		this.configObject.getAsJsonArray(CELLACTIVATORS).add(config.toJsonObject());
		return this;
	}
	
	public JsonArray getConditions() {
		return this.configObject.getAsJsonArray(CELLCONDITIONS);
	}
	
	public JsonArray getCellFunctionBehaviours() {
		return this.configObject.getAsJsonArray(CELLBEHAVIOURS);
	}
	
	public JsonArray getActivators() {
		return this.configObject.getAsJsonArray(CELLACTIVATORS);
	}
	
	public String getName() {
		return this.configObject.getAsJsonPrimitive(CELLNAME).getAsString();
	}
	
	public String getClassName() {
		return this.configObject.getAsJsonPrimitive(CELLCLASS).getAsString();
	}
	
	public String getProperty(String key) {
		return this.configObject.getAsJsonPrimitive(key).getAsString();
	}
	
	public JsonObject toJsonObject() {
		return this.configObject;
	}
	
}
