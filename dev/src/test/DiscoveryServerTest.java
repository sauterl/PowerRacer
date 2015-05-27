package test;

import static org.junit.Assert.*;

import org.junit.Test;

import server.discovery.DiscoveryServer;

public class DiscoveryServerTest {

	@Test
	public void test() {
		DiscoveryServer ds = new DiscoveryServer();
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ds.terminate();
		// assertEquals(ds.socket,null);
	}

}
