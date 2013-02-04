package ie.tech.talk.utils;

import ie.tech.talk.domain.SparkPlug;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to demonstrate how to stub static methods
 * 
 * @author Michael Freeman
 * 
 */
public class GarageUtils
{
	public static boolean checkSparkPlugs(List<SparkPlug> sparkPlugs)
	{
		if (sparkPlugs.isEmpty())
		{
			return false;
		}
		else
		{
			for (SparkPlug sparkPlug : sparkPlugs)
			{
				if (!sparkPlug.isWorking())
				{
					return false;
				}
			}
		}

		return true;
	}

	public static List<SparkPlug> getNewSparkPlugs()
	{
		List<SparkPlug> sparkPlugs = new ArrayList<SparkPlug>();

		for (int i = 0; i < 8; i++)
		{
			sparkPlugs.add(InventoryUtils.getNewSparkPlugFromInventory());
		}

		return sparkPlugs;
	}

}
