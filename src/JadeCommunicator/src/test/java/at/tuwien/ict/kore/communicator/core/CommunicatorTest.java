package at.tuwien.ict.kore.communicator.core;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import at.tuwien.ict.kore.communicator.core.Communicator;
import at.tuwien.ict.kore.communicator.core.CommunicatorImpl;
import at.tuwien.ict.kore.communicator.demoagents.InitiatorAgent;
import at.tuwien.ict.kore.communicator.demoagents.PongAgent;
import at.tuwien.ict.kore.communicator.util.JadeContainerUtil;
import jade.core.Runtime;
import jade.wrapper.ContainerController;

public class CommunicatorTest {

	private static Logger log = LoggerFactory.getLogger("main");
	private final JadeContainerUtil util = new JadeContainerUtil();
	private Communicator comm;
	
	private ContainerController agentContainer;
	ContainerController mainContainerController;
	
	@Before
	public void setup() {
		//Message syntax:
		//receiver, type, message as JSonObject that can be transformed into a Cell data structure
		
		try {
			//Create container
			log.debug("Create or get main container");
			mainContainerController = this.util.createMainJADEContainer("localhost", 1099, "MainContainer");
					
			log.debug("Create subcontainer");
			agentContainer = this.util.createAgentContainer("localhost", 1099, "Subcontainer"); 
			
			//log.debug("Create gui");
			//this.util.createRMAInContainer(agentContainer);
			
			//Create gateway
			log.debug("Create gateway");
			comm = new CommunicatorImpl();
			comm.init();
			
		} catch (Exception e) {
			log.error("Cannot initialize test environment", e);
		}
	}
	
	@After
	public void takeDown() {
		synchronized (this) {
			try {
				this.wait(200);
			} catch (InterruptedException e) {
				
			}
		}
		
		Runtime runtime = Runtime.instance();
		runtime.shutDown();
		synchronized (this) {
			try {
				this.wait(200);
			} catch (InterruptedException e) {
				
			}
		}
		this.comm.shutDown();
	}
	
	@Test 
	public void agentInitiatorFunctionTest() {
		try {
			String returnmessage = "pong";
			String expectedAnswer = "pingpong";
			String sendContent = "ping";
			String realanswer = "";
			
			//Create JsonMessage with String message, String receiver, String type
//			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String receiver = "InitiatorAgent";
//			String type = "read";
//			String sourceString = "{\"receiver\": \""+ receiver + "\", "
//								+  "\"type\": \"" + type + "\", "
//								+  "\"body\" : {\"content\" : \"" + sendContent + "\"}}"; 
//			log.debug("source string={}", sourceString);
//			JsonObject message = gson.fromJson(sourceString, JsonObject.class);
			
			//Create agent in the system
			Object[] args = new String[2];
			args[0] = "ControlGateway";
			args[1] = expectedAnswer;
			
			//Send anything to init the gateway
			//OBSOLETE this.comm.sendAsynchronousMessageToAgent("init", "DF", "");
			
			this.util.createAgent(receiver, InitiatorAgent.class, args, agentContainer);
			
			//createmessage
			//String message = "ping";
			
			
			//Send Message
			//this.comm.sendAsynchronousMessageToAgent(message, "PongAgent");
			
			log.debug("wait 2000ms for agent to answer");
			synchronized (this) {
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					
				}
			}
			
			log.debug("Wait ended. Take message from agent gateway");
			
			realanswer = this.comm.getMessageFromAgent(1000).get(JsonMessage.BODY).getAsJsonObject().get(JsonMessage.CONTENT).getAsString();
			
			log.info("Test finished. Expected message={}, received message={}", expectedAnswer, realanswer);
			assertEquals(expectedAnswer, realanswer);
			log.info("Test passed");
		} catch (Exception e) {
			log.error("Cannot test system", e);
			fail("Error");
		}
	}

	@Test
	public void asynchronResponderTest() {
		try {
			//create message
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String receiver = "PongAgent";
			String type = "read";
			String sendContent = "ping";
			String sourceString = "{\"RECEIVER\": [\""+ receiver + "\"], "
								+  "\"TYPE\": \"" + type + "\", "
								+  "\"BODY\" : {\"CONTENT\" : \"" + sendContent + "\"}}"; 
			log.debug("source string={}", sourceString);
			JsonObject message = gson.fromJson(sourceString, JsonObject.class);
			//String message = "ping";
			String expectedAnswer  = "pingpong";
			String answer = "";
			
			//Create agent in the system
			String[] args = {"1", "pong"};
			this.util.createAgent("PongAgent", PongAgent.class, args, agentContainer);
						
			//Send Message
			this.comm.sendAsynchronousMessageToAgent(message);
			
			log.debug("wait for agent to answer");
			synchronized (this) {
				try {
					this.wait(5000);
				} catch (InterruptedException e) {
					
				}
			}
			
			log.debug("Waiting 5000ms finished");
			
			//JsonObject obj = this.comm.getMessageFromAgent();
			
			answer = this.comm.getMessageFromAgent(1000).get(JsonMessage.BODY).getAsJsonObject().get(JsonMessage.CONTENT).getAsJsonPrimitive().getAsString();
			
			assertEquals(expectedAnswer, answer);
			log.info("Test passed");
		} catch (Exception e) {
			log.error("Cannot init system", e);
			fail("Error");
		}
	}
	
	@Test
	public void synchronResponderTest() {
		try {
			//createmessage
			String message = "ping";
			String expectedAnswer  = "pingpong";
			String answer = "";
			
			//Create agent in the system
			String[] args = {"1", "pong"};
			this.util.createAgent("PongAgent", PongAgent.class, args, agentContainer);
			
			synchronized (this) {
				try {
					this.wait(500);
				} catch (InterruptedException e) {
					
				}
			}
			
			//Send Message
			JsonObject reply = this.comm.sendSynchronousMessageToAgent(JsonMessage.toContentString(message), "PongAgent", "", 100000);
			
			log.debug("got reply={}", reply);
//			synchronized (this) {
//				try {
//					this.wait(5000);
//				} catch (InterruptedException e) {
//					
//				}
//			}
			
			answer = reply.get(JsonMessage.BODY).getAsJsonObject().get(JsonMessage.CONTENT).getAsJsonPrimitive().getAsString();
			
			assertEquals(expectedAnswer, answer);
			log.info("Test passed");
		} catch (Exception e) {
			log.error("Cannot init system", e);
			fail("Error");
		}
	}

}
