package ie.tech.talk.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import ie.tech.talk.exception.TechTalkException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Demonstrates a standard unit test with no mocking or method verification
 * 
 * @author Michael Freeman
 * 
 */
public class EngineTest
{
	private Engine engine;

	@Before
	public void setUp()
	{
		engine = new Engine();
		List<SparkPlug> sparkPlugs = engine.inspectSparkPlugs();
		sparkPlugs.add(new SparkPlug());
	}

	@Test
	public void testStartStopEngine()
	{
		engine.startEngine();
		assertTrue(engine.isRunning());
		engine.stopEngine();
		assertFalse(engine.isRunning());
	}

	@Test
	public void testStartEngineNoSparkPlugs()
	{
		engine = new Engine();
		engine.startEngine();
		assertFalse(engine.isRunning());
	}

	@Test
	public void testStartEngineWithDefectiveSparkPlugs()
	{
		List<SparkPlug> sparkPlugs = engine.inspectSparkPlugs();
		SparkPlug defectiveSparkPlug = new SparkPlug();
		defectiveSparkPlug.setWorking(false);
		sparkPlugs.add(defectiveSparkPlug);

		// Enable the below and test will still pass but coverage would drop
		// engine = new Engine();
		engine.startEngine();
		assertFalse(engine.isRunning());
	}

	@Test(expected = TechTalkException.class)
	public void testInspectSparkPlugsWithEngineRunning()
	{
		engine.startEngine();
		engine.inspectSparkPlugs();
	}

	@Test
	public void testFitSparkPlugs()
	{
		List<SparkPlug> newSparkPlugs = new ArrayList<SparkPlug>();
		engine.fitSparkPlugs(newSparkPlugs);
		assertSame(newSparkPlugs, engine.inspectSparkPlugs());
	}

	@Test(expected = TechTalkException.class)
	public void testFitSparkPlugsWithRunningEngine()
	{
		engine.startEngine();
		List<SparkPlug> newSparkPlugs = new ArrayList<SparkPlug>();
		engine.fitSparkPlugs(newSparkPlugs);
	}

}
