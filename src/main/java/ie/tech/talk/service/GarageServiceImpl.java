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

public class GarageServiceImpl
{
	private CarServiceHistoryServiceImpl carServiceHistoryServiceImpl;

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
