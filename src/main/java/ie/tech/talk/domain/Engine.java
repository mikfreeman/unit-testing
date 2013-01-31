package ie.tech.talk.domain;

import ie.tech.talk.exception.TechTalkException;

import java.util.ArrayList;
import java.util.List;

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

		if (sparkPlugs.isEmpty())
		{
			this.running = false;
		}
		else
		{
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

	public List<SparkPlug> inspectSparkPlugs()
	{
		if (isRunning())
		{
			throw new TechTalkException("Engine is still running");
		}

		return this.sparkPlugs;
	}

	public void fitSparkPlugs(List<SparkPlug> sparkPlugs)
	{
		if (isRunning())
		{
			throw new TechTalkException("Engine is still running");
		}
		this.sparkPlugs = sparkPlugs;
	}

}
