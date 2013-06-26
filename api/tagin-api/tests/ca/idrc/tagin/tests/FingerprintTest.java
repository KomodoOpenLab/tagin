package ca.idrc.tagin.tests;

import java.util.ArrayList;
import java.util.List;

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
	
	private Pattern p1, p2, p3;
	
	@Before
	public void setUp() {
		helper.setUp();
		p1 = new Pattern();
		p1.put("id1", 2400, -75);
		p1.put("id2", 2600, -50);
		p1.updateRanks();
		
		p2 = new Pattern();
		p2.put("id2", 2600, -65);
		p2.put("id3", 2400, -40);
		p2.updateRanks();
		
		p3 = new Pattern();
		p3.put("id8", 2400, -45);
		p3.put("id9", 2400, -60);
		p3.updateRanks();
		
		TaginDao dao = new TaginEntityManager();
		dao.persistPattern(p1);
		dao.persistPattern(p2);
		dao.persistPattern(p3);
		dao.close();
	}

	@After
	public void tearDown() {
		TaginDao dao = new TaginEntityManager();
		dao.removePattern(p1.getId());
		dao.removePattern(p2.getId());
		dao.removePattern(p3.getId());
		dao.close();
		helper.tearDown();
	}

	@Test
	public void testGetNeighbours() {
		List<Neighbour> neighbours = new ArrayList<Neighbour>();
		TaginDao dao = new TaginEntityManager();
		Fingerprint f1 = dao.getFingerprint(p1.getKey().getParent().getId());
		neighbours = f1.getNeighbours();
		
		Fingerprint neighbour = neighbours.get(0).getFingerprint();
		Assert.assertTrue(neighbour.getPattern().contains("id2", 2600));
		Assert.assertTrue(neighbour.getPattern().contains("id3", 2400));
	}
	
	@Test
	public void testRankDistanceToNotNeighbour() {
		TaginDao dao = new TaginEntityManager();
		Fingerprint f2 = dao.getFingerprint(p2.getKey().getParent().getId());
		Fingerprint f3 = dao.getFingerprint(p3.getKey().getParent().getId());
		Assert.assertEquals(1.0, f2.rankDistanceTo(f3).doubleValue(), 0.0);
	}

}
