package ie.tech.talk.domain;

import ie.tech.talk.exception.TechTalkException;

import java.util.ArrayList;
import java.util.List;

public class Engine
{
	private boolean running = false;

	private List<SparkPlug> sparkPlugs = new ArrayList<SparkPlug>();

	public void stopEngine()
	{
		this.running = false;
	}

	public void startEngine()
	{
		this.running = true;
	}

	public boolean isRunning()
	{
		return running;
	}

	public List<SparkPlug> inspectSparkPlugs()
	{
		if (isRunning())
		{
			throw new TechTalkException("Engine is still running");
		}

		return this.sparkPlugs;
	}

	public void replaceSparkPlus(List<SparkPlug> sparkPlugs)
	{
		if (isRunning())
		{
			throw new TechTalkException("Engine is still running");
		}
		this.sparkPlugs = sparkPlugs;
	}

}
