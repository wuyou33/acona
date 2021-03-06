package at.tuwien.ict.acona.demowebservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import at.tuwien.ict.acona.cell.cellfunction.ControlCommand;
import at.tuwien.ict.acona.cell.cellfunction.SyncMode;
import at.tuwien.ict.acona.cell.cellfunction.specialfunctions.CFStateGenerator;
import at.tuwien.ict.acona.cell.config.CellConfig;
import at.tuwien.ict.acona.cell.config.CellFunctionConfig;
import at.tuwien.ict.acona.cell.core.CellGatewayImpl;
import at.tuwien.ict.acona.cell.datastructures.DatapointBuilder;
import at.tuwien.ict.acona.demowebservice.cellfunctions.ComparisonAlgorithm;
import at.tuwien.ict.acona.demowebservice.cellfunctions.UserInterfaceCollector;
import at.tuwien.ict.acona.demowebservice.cellfunctions.WeatherService;
import at.tuwien.ict.acona.demowebservice.helpers.WeatherServiceClientMock;
import at.tuwien.ict.acona.launcher.SystemControllerImpl;
import jade.core.Runtime;

public class DemoWebServiceTester {

	private static final Logger log = LoggerFactory.getLogger(DemoWebServiceTester.class);
	private SystemControllerImpl controller = SystemControllerImpl.getLauncher();

	@Before
	public void setUp() throws Exception {
		try {
			// Create container
			log.debug("Create or get main container");
			this.controller.createMainContainer("localhost", 1099, "MainContainer");

			log.debug("Create subcontainer");
			this.controller.createSubContainer("localhost", 1099, "Subcontainer");

			// log.debug("Create gui");
			// this.commUtil.createDebugUserInterface();

			// Create gateway
			// commUtil.initJadeGateway();

		} catch (Exception e) {
			log.error("Cannot initialize test environment", e);
		}
	}

	@After
	public void tearDown() throws Exception {
		synchronized (this) {
			try {
				this.wait(2000);
			} catch (InterruptedException e) {

			}
		}

		Runtime runtime = Runtime.instance();
		runtime.shutDown();
		synchronized (this) {
			try {
				this.wait(2000);
			} catch (InterruptedException e) {

			}
		}
	}

	/**
	 * Create a broker agent. Create a depot. Add money to depot, read state of depot, buy stock, sell stock, unregister depot
	 * 
	 */
	@Test
	public void functionStateTest() {
		try {
			String weatherAgent1Name = "WeatherAgent1";
			// String weatherAgent2Name = "WeatherAgent2";
			String weatherservice = "Weather";
			String publishAddress = "helloworld.currentweather";

			CellConfig cf = CellConfig.newConfig(weatherAgent1Name)
					.addCellfunction(CellFunctionConfig.newConfig(weatherservice, WeatherServiceClientMock.class)
							.addManagedDatapoint(WeatherServiceClientMock.WEATHERADDRESSID, publishAddress, weatherAgent1Name, SyncMode.WRITEONLY))
					// .addCellfunction(CellFunctionConfig.newConfig(weatherservice + "d", WeatherService.class))
					.addCellfunction(CellFunctionConfig.newConfig(CFStateGenerator.class))
					.addCellfunction(CellFunctionConfig.newConfig("LamprosUI", UserInterfaceCollector.class)
							.addManagedDatapoint(UserInterfaceCollector.SYSTEMSTATEADDRESSID, "systemstate", weatherAgent1Name, SyncMode.SUBSCRIBEONLY)
							.addManagedDatapoint("ui1", publishAddress, weatherAgent1Name, SyncMode.SUBSCRIBEONLY));
			CellGatewayImpl weatherAgent = this.controller.createAgent(cf);

			// === Init finished ===//

			synchronized (this) {
				try {
					this.wait(2000);
				} catch (InterruptedException e) {

				}
			}
			log.info("=== All agents initialized ===");

			weatherAgent.getCommunicator().write(DatapointBuilder.newDatapoint(weatherservice + ".command").setValue(ControlCommand.START));

			// Wait while the system runs
			synchronized (this) {
				try {
					this.wait(20000);
				} catch (InterruptedException e) {

				}
			}

			// Read the state of the system
			JsonObject systemState = weatherAgent.getCommunicator().read(CFStateGenerator.SYSTEMSTATEADDRESS).getValue().getAsJsonObject();

			String currentResult = systemState.get("hasFunction").getAsJsonArray().get(0).getAsJsonObject().get("hasState").getAsString();
			String expectedResult = "RUNNING"; // As the system is still running, when the request is sent

			weatherAgent.getCommunicator().write(DatapointBuilder.newDatapoint(weatherservice + ".command").setValue(ControlCommand.STOP));

			log.info("current result={}, expected result={}", currentResult, expectedResult);
			assertEquals(currentResult, expectedResult);

			log.info("Tests passed");
		} catch (Exception e) {
			log.error("Error testing system", e);
			fail("Error");
		}

	}

	@Test
	public void algorithmTest() {
		try {

			// === General variables ===//
			String weatherAgent1Name = "WeatherAgent1";
			String weatherAgent2Name = "WeatherAgent2";
			String weatherAgent3Name = "WeatherAgent3";
			String weatherAgent4Name = "WeatherAgent4";
			String algorithmAgentName = "AlgorithmAgent";
			String algorithmService = "algorithm";
			String weatherservice = "Weather";
			String publishAddress = "helloworld.currentweather";

			CellGatewayImpl weatherAgent1 = this.controller.createAgent(CellConfig.newConfig(weatherAgent1Name)
					.addCellfunction(CellFunctionConfig.newConfig(weatherservice, WeatherServiceClientMock.class)
							.addManagedDatapoint(WeatherServiceClientMock.WEATHERADDRESSID, publishAddress, weatherAgent1Name, SyncMode.WRITEONLY)
							.setProperty(WeatherServiceClientMock.CITYNAME, "abudhabi")
							.setProperty(WeatherServiceClientMock.USERID, "5bac1f7f2b67f3fb3452350c23401903"))
					.addCellfunction(CellFunctionConfig.newConfig(CFStateGenerator.class)));

			CellGatewayImpl weatherAgent2 = this.controller.createAgent(CellConfig.newConfig(weatherAgent2Name)
					.addCellfunction(CellFunctionConfig.newConfig(weatherservice, WeatherServiceClientMock.class)
							.setProperty(WeatherService.CITYNAME, "vienna")
							.setProperty(WeatherService.USERID, "5bac1f7f2b67f3fb3452350c23401903")
							.addManagedDatapoint(WeatherServiceClientMock.WEATHERADDRESSID, publishAddress, weatherAgent2Name, SyncMode.WRITEONLY))
					.addCellfunction(CellFunctionConfig.newConfig(CFStateGenerator.class)));

			CellGatewayImpl weatherAgent3 = this.controller.createAgent(CellConfig.newConfig(weatherAgent3Name)
					.addCellfunction(CellFunctionConfig.newConfig(weatherservice, WeatherServiceClientMock.class)
							.setProperty(WeatherService.CITYNAME, "stockholm")
							.setProperty(WeatherService.USERID, "5bac1f7f2b67f3fb3452350c23401903")
							.addManagedDatapoint(WeatherServiceClientMock.WEATHERADDRESSID, publishAddress, weatherAgent3Name, SyncMode.WRITEONLY))
					.addCellfunction(CellFunctionConfig.newConfig(CFStateGenerator.class)));

			synchronized (this) {
				try {
					this.wait(200);
				} catch (InterruptedException e) {

				}
			}

			CellGatewayImpl calculator = this.controller.createAgent(CellConfig.newConfig(algorithmAgentName)
					// .addCellfunction(CellFunctionConfig.newConfig(algorithmService, ComparisonAlgorithmAlternative.class)
					.addCellfunction(CellFunctionConfig.newConfig(algorithmService, ComparisonAlgorithm.class)
							.addManagedDatapoint("Vienna", publishAddress, weatherAgent2Name, SyncMode.SUBSCRIBEONLY)
							.addManagedDatapoint("Stockholm", publishAddress, weatherAgent3Name, SyncMode.SUBSCRIBEONLY)
							.addManagedDatapoint("Mocktown", publishAddress, weatherAgent1Name, SyncMode.SUBSCRIBEONLY))
					.addCellfunction(CellFunctionConfig.newConfig("LamprosUI", UserInterfaceCollector.class)
							.addManagedDatapoint(UserInterfaceCollector.SYSTEMSTATEADDRESSID, CFStateGenerator.SYSTEMSTATEADDRESS, algorithmAgentName, SyncMode.SUBSCRIBEONLY)
							.addManagedDatapoint("RESULT", algorithmService + ".result", algorithmAgentName, SyncMode.SUBSCRIBEONLY)
							.addManagedDatapoint("ui1", publishAddress, weatherAgent1Name, SyncMode.SUBSCRIBEONLY))
					.addCellfunction(CellFunctionConfig.newConfig(CFStateGenerator.class)));

			synchronized (this) {
				try {
					this.wait(2000);
				} catch (InterruptedException e) {

				}
			}

			log.info("=== All agents initialized ===");

			weatherAgent1.getCommunicator().write(DatapointBuilder.newDatapoint(weatherservice + ".command").setValue(ControlCommand.START));
			weatherAgent2.getCommunicator().write(DatapointBuilder.newDatapoint(weatherservice + ".command").setValue(ControlCommand.START));
			weatherAgent3.getCommunicator().write(DatapointBuilder.newDatapoint(weatherservice + ".command").setValue(ControlCommand.START));

			synchronized (this) {
				try {
					this.wait(200000);
				} catch (InterruptedException e) {

				}
			}

			assert (false);
			log.info("Tests passed");
		} catch (Exception e) {
			log.error("Error testing system", e);
			fail("Error");
		}
	}

	/**
	 * Create a broker agent. Create a depot. Add money to depot, read state of depot, buy stock, sell stock, unregister depot
	 * 
	 */
	@Test
	public void functionWeatherServiceTest() {
		try {
			String weatherAgent1Name = "WeatherAgent1";
			// String weatherAgent2Name = "WeatherAgent2";
			String weatherservice = "Weather";
			String publishAddress = "helloworld.currentweather";

			CellConfig cf = CellConfig.newConfig(weatherAgent1Name)
					.addCellfunction(CellFunctionConfig.newConfig(weatherservice, WeatherService.class)
							.setProperty(WeatherService.CITYNAME, "vienna")
							.setProperty(WeatherService.USERID, "5bac1f7f2b67f3fb3452350c23401903")
							.addManagedDatapoint(WeatherServiceClientMock.WEATHERADDRESSID, publishAddress, weatherAgent1Name, SyncMode.WRITEONLY));
			CellGatewayImpl weatherAgent = this.controller.createAgent(cf);

			// === Init finished ===//

			synchronized (this) {
				try {
					this.wait(2000);
				} catch (InterruptedException e) {

				}
			}
			log.info("=== All agents initialized ===");

			weatherAgent.getCommunicator().write(DatapointBuilder.newDatapoint(weatherservice + ".command").setValue(ControlCommand.START));

			// Wait while the system runs
			synchronized (this) {
				try {
					this.wait(20000);
				} catch (InterruptedException e) {

				}
			}

			// Read the state of the system
			// JsonObject systemState = weatherAgent.readLocalDatapoint(CFStateGenerator.SYSTEMSTATEADDRESS).getValue().getAsJsonObject();

			// String currentResult = systemState.get("hasFunction").getAsJsonArray().get(0).getAsJsonObject().get("hasState").getAsString();
			// String expectedResult = "RUNNING"; //As the system is still running, when the request is sent

			// weatherAgent.getCommunicator().write(Datapoints.newDatapoint(weatherservice + ".command").setValue(ControlCommand.STOP));

			// log.info("current result={}, expected result={}", currentResult, expectedResult);
			// assertEquals(currentResult, expectedResult);
			assert (false);
			log.info("Tests passed");
		} catch (Exception e) {
			log.error("Error testing system", e);
			fail("Error");
		}

	}
}
