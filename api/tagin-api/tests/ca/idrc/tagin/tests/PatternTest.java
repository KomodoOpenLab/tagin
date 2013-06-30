package ca.idrc.tagin.tests;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.idrc.tagin.model.Beacon;
import ca.idrc.tagin.model.Pattern;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class PatternTest {

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
	public void testUpdateRanks() {
		Pattern p = new Pattern();
		p.put("id1", 2400, -75);
		p.put("id2", 2600, -50);
		Assert.assertEquals(Beacon.NULL_RSSI, p.getMaxRssi());
		p.updateRanks();
		Assert.assertTrue(p.getMaxRssi() != Beacon.NULL_RSSI);
	}

	@Test
	public void testCalculateChangeVector() {
		Pattern p1 = new Pattern();
		p1.put("id1", 2400, -30);
		p1.put("id2", 2600, -70);
		p1.updateRanks();

		Pattern p2 = new Pattern();
		p2.put("id2", 2600, -60);
		p2.put("id3", 2200, -50);
		p2.updateRanks();

		List<Beacon> changeVector = p1.calculateChangeVector(p2);
		Double delta = p2.getBeacons().get("id2;2600").getRank() - p1.getBeacons().get("id2;2600").getRank();
		Assert.assertEquals(delta, changeVector.get(0).getRank(), 0.0);
		Assert.assertEquals(1.0, changeVector.get(1).getRank(), 0.0);

		changeVector = p1.calculateChangeVector(p1);
		Assert.assertEquals(0.0, changeVector.get(0).getRank(), 0.0);
		Assert.assertEquals(0.0, changeVector.get(1).getRank(), 0.0);
	}

}
