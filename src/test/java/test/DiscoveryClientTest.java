package test;

// import static org.junit.Assert.*;

import java.net.InetAddress;

import org.junit.Test;

import server.discovery.DiscoveryClient;
import server.discovery.DiscoveryServer;

public class DiscoveryClientTest {

	@Test
	public void test() {
		DiscoveryServer server = new DiscoveryServer();

		for (InetAddress ia : DiscoveryClient.getServerAddresses()) {
			System.out.print(ia);
			System.out.println(" " + ia.getHostName());
		}
		server.terminate();
	}

}
