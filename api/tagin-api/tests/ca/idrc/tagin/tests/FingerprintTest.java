package ca.idrc.tagin.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.idrc.tagin.dao.TaginDao;
import ca.idrc.tagin.dao.TaginEntityManager;
import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Neighbour;
import ca.idrc.tagin.model.Pattern;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class FingerprintTest {
	
	private final LocalServiceTestHelper helper =
		new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
		.setApplyAllHighRepJobPolicy());
	
	// Pattern p2 is merged into p1, as they are close neighbours.
	// Pattern p3 is neighbour to p1, but is not close enough to merge.
	// Pattern p4 does not share any beacon with the previous patterns.
	private Pattern p1, p2, p3, p4;
	
	@Before
	public void setUp() {
		helper.setUp();
		p1 = new Pattern();
		p1.put("id1", 2400, -75);
		p1.put("id2", 2600, -50);
		p1.updateRanks();
		
		p2 = new Pattern();
		p2.put("id2", 2600, -52);
		p2.put("id3", 2400, -78);
		p2.updateRanks();
		
		p3 = new Pattern();
		p3.put("id2", 2600, -48);
		p3.put("id4", 2400, -45);
		p3.updateRanks();
		
		p4 = new Pattern();
		p4.put("id8", 2400, -45);
		p4.put("id9", 2400, -60);
		p4.updateRanks();
		
		TaginDao dao = new TaginEntityManager();
		dao.persistPattern(p1);
		dao.persistPattern(p2);
		dao.persistPattern(p3);
		dao.persistPattern(p4);
		dao.close();
	}

	@After
	public void tearDown() {
		TaginDao dao = new TaginEntityManager();
		dao.removePattern(p1.getId());
		dao.removePattern(p2.getId());
		dao.removePattern(p3.getId());
		dao.removePattern(p4.getId());
		dao.close();
		helper.tearDown();
	}
	
	@Test
	public void testMergedNeighbours() {
		// p2 must be merged into p1 as they are close neighbours.
		// This means that no new fingerprint will be generated for p2.
		Assert.assertNull(p2.getKey());
	}

	@Test
	public void testGetNeighbours() {
		TaginDao dao = new TaginEntityManager();
		Fingerprint f1 = dao.getFingerprint(p1.getKey().getParent().getId());
		Neighbour neighbour = f1.findNeighbours().get(0);
		Fingerprint fp = neighbour.getFingerprint();
		Assert.assertTrue(fp.getPattern().contains("id2", 2600));
		Assert.assertTrue(fp.getPattern().contains("id4", 2400));
		dao.close();
	}
	
	@Test
	public void testRankDistanceToNeighbour() {
		TaginDao dao = new TaginEntityManager();
		Fingerprint f1 = dao.getFingerprint(p1.getKey().getParent().getId());
		Fingerprint f3 = dao.getFingerprint(p3.getKey().getParent().getId());
		Assert.assertTrue(f1.rankDistanceTo(f3) > Fingerprint.THRESHOLD);
		dao.close();
	}
	
	@Test
	public void testRankDistanceToNotNeighbour() {
		TaginDao dao = new TaginEntityManager();
		Fingerprint f1 = dao.getFingerprint(p1.getKey().getParent().getId());
		Fingerprint f4 = dao.getFingerprint(p4.getKey().getParent().getId());
		Assert.assertEquals(1.0, f1.rankDistanceTo(f4).doubleValue(), 0.0);
		dao.close();
	}

}
