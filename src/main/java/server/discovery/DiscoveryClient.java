package server.discovery;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

// import java.util.Enumeration;
/**
 * Thread used to detect {@link DiscoveryServer}s on the same Network.
 * 
 * @author Florian
 *
 */
public class DiscoveryClient extends Thread {
	int port;
	DatagramSocket socket;
	ArrayList<InetAddress> serverAddresses = new ArrayList<InetAddress>();
	boolean terminateFlag = false;

	/**
	 * Default constructor.
	 * <p>
	 * Sets the client port to 4032 and runs the thread constructor.
	 */
	private DiscoveryClient() {
		super();
		port = 4032;
	}

	/**
	 * Secondary constructor used to specify client port.
	 * <p>
	 * Sets the client port to the specified integer and runs the thread
	 * constructor.
	 * 
	 * @param port
	 *            the port to which the broadcast will be sent and at which a
	 *            reply will be expected
	 */
	private DiscoveryClient(int port) {
		super();
		this.port = port;
	}

	/**
	 * Finds and returns the InetAddresses of all {@link DiscoveryServer}s
	 * currently active on the default port (4032).
	 * 
	 * @return an array of the InetAddresses of currently active
	 *         {@link DiscoveryServer}s
	 */
	public static InetAddress[] getServerAddresses() {
		DiscoveryClient client = new DiscoveryClient();

		client.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Discovery Client ERROR while sleeping!");
			e.printStackTrace();
		}
		InetAddress[] serverAddresses = client.getAddressArray();
		client.terminate();

		return serverAddresses;
	}

	/**
	 * Finds and returns the InetAddresses of all {@link DiscoveryServer}s
	 * currently active on the specified port.
	 * 
	 * @return an array of the InetAddresses of currently active
	 *         {@link DiscoveryServer}s
	 * @param port
	 *            the port at which the Discovery Client will be constructed
	 */
	public static InetAddress[] getServerAddresses(int port) {
		DiscoveryClient client = new DiscoveryClient(port);

		client.start();
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			System.out.println("Discovery Client ERROR while sleeping!");
			e.printStackTrace();
		}
		InetAddress[] serverAddresses = client.getAddressArray();
		client.terminate();

		return serverAddresses;
	}

	/**
	 * Converts the current InetAddress ArrayList serveraddresses into an array
	 * and returns it.
	 * 
	 * @return the DiscoveryClient's current ArrayList of
	 *         {@link DiscoveryServer} addresses as an array
	 */
	private InetAddress[] getAddressArray() {
		return serverAddresses.toArray(new InetAddress[serverAddresses.size()]);
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		try {
			// Opens broadcasting socket
			socket = new DatagramSocket();
			socket.setBroadcast(true);

			// Prepare and send request to 255.255.255.255
			byte[] request = "DSCVR".getBytes();
			/*
			 * try{ DatagramPacket requestPacket = new
			 * DatagramPacket(request,request
			 * .length,InetAddress.getByName("255.255.255.255"),port);
			 * socket.send(requestPacket); } catch(Exception e){
			 * System.out.println
			 * ("DiscoveryClient ERROR while sending request!");
			 * e.printStackTrace(); }
			 */

			// Send request to all network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue; // Don't broadcast to the Loopback interface
				}

				for (InterfaceAddress interfaceAddress : networkInterface
						.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null) {
						continue;
					}

					// Send the broadcast package
					try {
						DatagramPacket sendPacket = new DatagramPacket(request,
								request.length, broadcast, port);
						socket.send(sendPacket);
					} catch (Exception e) {
						System.out
								.println("DiscoveryClient ERROR while sending request!");
						e.printStackTrace();
					}

				}
			}

			// Wait for all replies
			while (true) {
				DatagramPacket responsePacket = new DatagramPacket(
						new byte[100], 100);
				socket.receive(responsePacket);

				// Check packet for the right contents
				if (new String(responsePacket.getData()).trim().equals("DSCRS")
						&& !serverAddresses.contains(responsePacket
								.getAddress())) {
					serverAddresses.add(responsePacket.getAddress());
				}
			}

		} catch (IOException e) {
			if (terminateFlag) {

			} else {
				System.out.println("DiscoveryClient ERROR!");
			}
		}
	}

	/**
	 * Terminates the DiscoveryClient by interrupting the thread and closing the
	 * port. Allows freeing of socket resources by setting the socket to null.
	 */
	public void terminate() {
		terminateFlag = true;
		interrupt();
		socket.close();
		socket = null;
	}
}
