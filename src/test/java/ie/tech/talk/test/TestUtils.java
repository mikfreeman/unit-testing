package ie.tech.talk.test;

import ie.tech.talk.domain.SparkPlug;

import java.util.ArrayList;
import java.util.List;

public class TestUtils
{
	public static List<SparkPlug> getWorkingSparkPlugs()
	{
		List<SparkPlug> sparkPlugs = new ArrayList<SparkPlug>();
		sparkPlugs.add(new SparkPlug());

		return sparkPlugs;
	}

	public static List<SparkPlug> getFaultySparkPlugs()
	{
		List<SparkPlug> sparkPlugs = new ArrayList<SparkPlug>();
		SparkPlug faultySparkPlug = new SparkPlug();
		faultySparkPlug.setWorking(false);
		sparkPlugs.add(faultySparkPlug);

		return sparkPlugs;
	}

}
