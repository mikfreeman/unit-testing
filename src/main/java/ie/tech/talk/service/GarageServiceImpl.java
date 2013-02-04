package ie.tech.talk.service;

import ie.tech.talk.domain.Car;
import ie.tech.talk.domain.Engine;
import ie.tech.talk.domain.ServiceRecord;
import ie.tech.talk.domain.SparkPlug;
import ie.tech.talk.exception.CarServiceException;
import ie.tech.talk.exception.DiagnosticFailureException;
import ie.tech.talk.utils.GarageUtils;

import java.util.List;

import org.joda.time.DateTime;

/**
 * Demo class for unit testing.
 * 
 * Represents the servicing of a car (My best guess at how the magical Pixies that power the engine are refreshed)
 * 
 * @author Michael Freeman
 * 
 */
public class GarageServiceImpl
{
	private CarServiceHistoryServiceImpl carServiceHistoryServiceImpl;

	// The complex method that we are going to test.
	// 1. Range of possible outcomes.
	// 2. Can throw multiple exceptions.
	// 3. Calls static methods.
	// 4. Rely's on complex private methods.
	// 5. Calls other services (We need to verify the arguments to these calls).
	// 6. No return type to test.
	public void serviceEngine(Car car)
	{
		Engine engine = car.getEngine();
		engine.stopEngine();
		List<SparkPlug> sparkPlugs = engine.inspectSparkPlugs();

		if (!GarageUtils.checkSparkPlugs(sparkPlugs))
		{
			List<SparkPlug> newSparkPlugs = GarageUtils.getNewSparkPlugs();
			engine.fitSparkPlugs(newSparkPlugs);
		}

		if (!runEngineDiagnostics(engine))
		{
			throw new DiagnosticFailureException(
					"Engine problem. New part needed before engine service can be completed");
		}

		engine.startEngine();

		ServiceRecord serviceRecord = new ServiceRecord();
		serviceRecord.setServiceDate(new DateTime());

		carServiceHistoryServiceImpl.updateServiceHistory(serviceRecord);

		if (!engine.isRunning())
		{
			throw new CarServiceException("Problem servicing Engine");
		}
	}

	// Used to demonstrate how partial mocking can be used to stub private methods
	private boolean runEngineDiagnostics(Engine engine)
	{
		// Do some long running process that requires an integration test

		return true;
	}

	public CarServiceHistoryServiceImpl getCarServiceHistoryServiceImpl()
	{
		return carServiceHistoryServiceImpl;
	}

	public void setCarServiceHistoryServiceImpl(CarServiceHistoryServiceImpl carServiceHistoryServiceImpl)
	{
		this.carServiceHistoryServiceImpl = carServiceHistoryServiceImpl;
	}

}
