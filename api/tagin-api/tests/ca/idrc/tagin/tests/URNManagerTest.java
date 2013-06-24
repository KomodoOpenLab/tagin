package ca.idrc.tagin.tests;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.idrc.tagin.dao.EMFService;
import ca.idrc.tagin.model.Fingerprint;
import ca.idrc.tagin.model.Pattern;
import ca.idrc.tagin.spi.v1.URNManager;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class URNManagerTest {
	
	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
			.setApplyAllHighRepJobPolicy());
	
	private Fingerprint f1;
	private Fingerprint f2;
	
	@Before
	public void setUp() {
		helper.setUp();
		Pattern p1 = new Pattern();
		p1.put("id1", 2400, -75);
		p1.put("id2", 2600, -50);
		p1.updateRanks();
		f1 = new Fingerprint(p1);
		
		Pattern p2 = new Pattern();
		p2.put("id2", 2600, -65);
		p2.put("id3", 2400, -40);
		p2.updateRanks();
		f2 = new Fingerprint(p2);
		
		EntityManager em = EMFService.createEntityManager();
		em.getTransaction().begin();
		em.persist(f1);
		em.persist(f2);
		em.getTransaction().commit();
		em.close();
	}

	@After
	public void tearDown() {
		helper.tearDown();
		// TODO: clear datastore
	}
	

	@Test
	public void testGetNeighbours() {
		List<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
		Pattern p = new Pattern();
		p.put("id3", 2400, -70);
		p.put("id4", 2200, -50);
		p.updateRanks();
		Fingerprint fp = new Fingerprint(p);
		fingerprints = URNManager.getNeighbours(fp);
		Assert.assertTrue(fingerprints.get(0).getPattern().contains("id3", 2400));
	}

}
