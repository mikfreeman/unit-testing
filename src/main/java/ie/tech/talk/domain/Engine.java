package ie.tech.talk.domain;

import ie.tech.talk.exception.EngineException;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo class for unit testing.
 * 
 * Supposed to represent an Engine (Which from the below I know absolutely nothing about)
 * 
 * This class will be the target of the unit test in one test and the mocked object in another test
 * 
 * @author Michael Freeman
 * 
 */
public class Engine
{
	private boolean starting = false;
	private boolean running = false;

	private List<SparkPlug> sparkPlugs = new ArrayList<SparkPlug>();

	public void stopEngine()
	{
		this.running = false;
	}

	public void startEngine()
	{
		this.starting = true;
		this.running = true;

		// If there are no spark plugs don't start the engine
		if (sparkPlugs.isEmpty())
		{
			this.running = false;
		}
		else
		{
			// Make sure none of the spark plugs are faulty before starting
			for (SparkPlug sparkPlug : sparkPlugs)
			{
				if (!sparkPlug.isWorking())
				{
					this.running = false;
				}
			}
		}

		this.starting = false;

	}

	// Is the engine running (In case we need to make a fast get away)
	public boolean isRunning()
	{
		if (starting)
		{
			return false;
		}
		else
		{
			return running;
		}

	}

	// Take a look at the spark plugs
	public List<SparkPlug> inspectSparkPlugs()
	{
		if (isRunning())
		{
			throw new EngineException("Engine is still running");
		}

		return this.sparkPlugs;
	}

	// Fit new and hopefully working spark plugs
	public void fitSparkPlugs(List<SparkPlug> sparkPlugs)
	{
		if (isRunning())
		{
			throw new EngineException("Engine is still running");
		}
		this.sparkPlugs = sparkPlugs;
	}
}
