package at.tuwien.ict.acona.cell.communicator;

import java.util.List;

import at.tuwien.ict.acona.cell.cellfunction.CellFunction;
import at.tuwien.ict.acona.cell.datastructures.Datapoint;

/**
 * The basic class for communication between functions, services in or within
 * other agents
 * 
 * @author wendt
 *
 */
public interface AgentCommunicator {

	/**
	 * Sets default timeout for all communication functions
	 * 
	 * @param timeout
	 */
	public void setDefaultTimeout(int timeout);

	/**
	 * Get the default timeout
	 * 
	 * @return
	 */
	public int getDefaultTimeout();

	/**
	 * Execute a service
	 * 
	 * @param agentName:
	 *            Destination agent
	 * @param serviceName:
	 *            Desination service name
	 * @param methodParameters:
	 *            List of input for the service.
	 * @param timeout:
	 *            Timeout to wait
	 * @return
	 * @throws Exception
	 */
	public List<Datapoint> execute(String agentName, String serviceName, List<Datapoint> methodParameters, int timeout) throws Exception;

	/**
	 * Execute a service
	 * 
	 * @param agentName:
	 *            Destination agent
	 * @param serviceName:
	 *            Desination service name
	 * @param methodParameters:
	 *            List of input for the service.
	 * @param timeout:
	 *            Timeout to wait
	 * @param useSubscribeProtocol:
	 *            Use subscribe protocol, a special case for FIPA
	 * @return
	 * @throws Exception
	 */
	public List<Datapoint> execute(String agentName, String serviceName, List<Datapoint> methodParameters, int timeout, boolean useSubscribeProtocol) throws Exception;

	/**
	 * Execute the service as a non-blocking function
	 * 
	 * @param agentName
	 * @param serviceName
	 * @param methodParameters
	 * @throws Exception
	 */
	public void executeAsynchronous(String agentName, String serviceName, List<Datapoint> methodParameters) throws Exception;

	// === Init Cellfunctions as services, which shall have exteral access ===//
	/**
	 * Any cell function can create a responder to handle its incoming requests
	 * 
	 * @param function
	 */
	public void createResponderForFunction(CellFunction function);

	/**
	 * Remove a created responder function
	 * 
	 * @param function
	 */
	public void removeResponderForFunction(CellFunction function);
}