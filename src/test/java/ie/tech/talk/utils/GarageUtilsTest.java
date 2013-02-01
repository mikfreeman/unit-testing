package ie.tech.talk.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import ie.tech.talk.domain.SparkPlug;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(InventoryUtils.class)
public class GarageUtilsTest
{
	@Test
	public void testNewSparkPlugs()
	{
		List<SparkPlug> sparkPlugs = new ArrayList<SparkPlug>();
		assertFalse(GarageUtils.checkSparkPlugs(sparkPlugs));

		sparkPlugs.add(new SparkPlug());
		assertTrue(GarageUtils.checkSparkPlugs(sparkPlugs));

		SparkPlug defectiveSparkPlug = new SparkPlug();
		defectiveSparkPlug.setWorking(false);
		sparkPlugs.add(defectiveSparkPlug);

		assertFalse(GarageUtils.checkSparkPlugs(sparkPlugs));
	}

	// Verifying Static Invocation
	@Test
	public void testGetNewSparkPlugs()
	{
		PowerMockito.mockStatic(InventoryUtils.class);

		List<SparkPlug> sparkPlugs = GarageUtils.getNewSparkPlugs();
		assertEquals(8, sparkPlugs.size());
		PowerMockito.verifyStatic(Mockito.times(8));
		InventoryUtils.getNewSparkPlugFromInventory();

	}
}
