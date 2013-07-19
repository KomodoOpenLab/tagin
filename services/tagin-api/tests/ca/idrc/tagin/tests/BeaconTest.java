package ca.idrc.tagin.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.idrc.tagin.model.Beacon;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class BeaconTest {
	
	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
			.setApplyAllHighRepJobPolicy());

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	public void testBeaconId() {
		Beacon b = new Beacon("id1", 2400, -60);
		Assert.assertTrue(b.getId().equals("id1;2400"));
	}
	
	@Test
	public void testCompareTo() {
		Beacon b1 = new Beacon("id1", 2400, -40);
		Beacon b2 = new Beacon("id2", 2400, -50);
		Assert.assertTrue(b1.compareTo(b2) == -1);
	}
}
