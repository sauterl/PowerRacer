/**
 * 
 */
package server.discovery;

import java.io.IOException;
import java.net.*;

/**
 * Thread, which allows devices running it to be discovered by devices running
 * the {@link DiscoveryClient}.
 * <p>
 * Furthermore, this allows the discovering device to determine the exact
 * address of the device running the DiscoveryServer, allowing the establishment
 * of a direct connection.
 * 
 * @author Florian
 * @see server.discovery.DiscoveryClient
 */
public class DiscoveryServer extends Thread {
	int port;
	boolean terminateFlag = false;
	DatagramSocket socket;

	/**
	 * Default constructor.
	 * <p>
	 * Sets the server port to 4032, runs the thread constructor and starts the
	 * thread.
	 * 
	 */
	public DiscoveryServer() {
		super();
		port = 4032;
		this.start();
	}

	/**
	 * Secondary constructor used to specify server port.
	 * <p>
	 * Sets the server port to the specified integer, runs the thread
	 * constructor and starts the thread.
	 * 
	 * @param port
	 *            the port at which the discovery server will listen and from
	 *            which the server will send a reply
	 */
	public DiscoveryServer(int port) {
		super();
		this.port = port;
		this.start();
	}

	/**
	 * Opens server socket and listens for incoming requests. Sends a reply
	 * packet to any valid requests.
	 */
	@Override
	public void run() {
		try {
			// Set up socket to accept all incoming connections on the specified
			// port
			socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while (true) {

				// Prepare to receive a packet
				DatagramPacket requestPacket = new DatagramPacket(
						new byte[100], 100);
				socket.receive(requestPacket);

				// Check message for the right command
				if (new String(requestPacket.getData()).trim().equals("DSCVR")) {

					// Send a response
					byte[] response = "DSCRS".getBytes();
					DatagramPacket responsePacket = new DatagramPacket(
							response, response.length,
							requestPacket.getAddress(), requestPacket.getPort());
					socket.send(responsePacket);
				}
			}

		} catch (IOException e) {
			if (terminateFlag) {

			} else {
				System.out.println("DiscoveryServer ERROR!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Terminates the DiscoveryServer by interrupting the thread and closing the
	 * server port. Allows freeing of socket resources by setting the socket to
	 * null.
	 */
	public void terminate() {
		terminateFlag = true;
		interrupt();
		socket.close();
		socket = null;
	}
}
