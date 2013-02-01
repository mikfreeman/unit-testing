package ie.tech.talk.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import ie.tech.talk.domain.Car;
import ie.tech.talk.domain.Engine;
import ie.tech.talk.domain.ServiceRecord;
import ie.tech.talk.domain.SparkPlug;
import ie.tech.talk.exception.TechTalkException;
import ie.tech.talk.utils.GarageUtils;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GarageUtils.class)
public class GarageServiceImplTest
{
	private GarageServiceImpl garageServiceImpl;
	private List<SparkPlug> sparkPlugs;
	private CarServiceHistoryServiceImpl carServiceHistoryServiceImpl;

	@Before
	public void setUp()
	{
		garageServiceImpl = new GarageServiceImpl();
		carServiceHistoryServiceImpl = mock(CarServiceHistoryServiceImpl.class);
		garageServiceImpl.setCarServiceHistoryServiceImpl(carServiceHistoryServiceImpl);

		sparkPlugs = new ArrayList<SparkPlug>();
		sparkPlugs.add(new SparkPlug());
	}

	// Using Mockito to Verify and mock behaviour
	@SuppressWarnings("unchecked")
	@Test
	public void testServiceEngineWithWorkingSparkPlugs()
	{
		Car car = new Car();
		Engine engine = mock(Engine.class);
		car.setEngine(engine);

		when(engine.inspectSparkPlugs()).thenReturn(sparkPlugs);
		when(engine.isRunning()).thenReturn(true);

		garageServiceImpl.serviceEngine(car);

		verify(engine).stopEngine();
		// This should not be called. Change the sparkplug to return false to see error.
		verify(engine, never()).fitSparkPlugs(any(List.class));
		verify(engine).startEngine();

	}

	// Using Mockito to Verify and mock behaviour
	@SuppressWarnings("unchecked")
	@Test
	public void testServiceEngineWithFaultySparkPlugs()
	{
		SparkPlug faultySparkPlug = new SparkPlug();
		faultySparkPlug.setWorking(false);
		sparkPlugs.add(faultySparkPlug);

		Car car = new Car();
		Engine engine = mock(Engine.class);
		car.setEngine(engine);

		when(engine.inspectSparkPlugs()).thenReturn(sparkPlugs);
		when(engine.isRunning()).thenReturn(true);

		garageServiceImpl.serviceEngine(car);

		verify(engine).stopEngine();
		// This should not be called. Change the sparkplug to return false to see error.
		verify(engine).fitSparkPlugs(any(List.class));
		verify(engine).startEngine();
	}

	// Using Mockito to Verify and mock behaviour
	@Test(expected = TechTalkException.class)
	public void testServiceEngineWithFaultyReplacementSparkPlugs()
	{
		SparkPlug faultySparkPlug = new SparkPlug();
		faultySparkPlug.setWorking(false);
		sparkPlugs.add(faultySparkPlug);

		Car car = new Car();
		Engine engine = mock(Engine.class);
		car.setEngine(engine);

		when(engine.inspectSparkPlugs()).thenReturn(sparkPlugs);
		when(engine.isRunning()).thenReturn(false);

		garageServiceImpl.serviceEngine(car);
	}

	// Using Mockito to Verify with real object
	@SuppressWarnings("unchecked")
	@Test
	public void testServiceEngineWithWorkingSparkPlugsUsingSpy()
	{
		Car car = new Car();
		Engine realEngine = new Engine();
		realEngine.fitSparkPlugs(sparkPlugs);
		Engine engine = spy(realEngine);

		car.setEngine(engine);
		garageServiceImpl.serviceEngine(car);

		verify(engine).stopEngine();
		// This should not be called. Change the sparkplug to return false to see error.
		verify(engine, never()).fitSparkPlugs(any(List.class));
		verify(engine).startEngine();
	}

	// Using Mockito to Verify with real object and mock static call
	@Test(expected = TechTalkException.class)
	public void testServiceEngineWithFaultyReplacementSparkPlugsUsingSpy()
	{
		SparkPlug faultySparkPlug = new SparkPlug();
		faultySparkPlug.setWorking(false);
		sparkPlugs.add(faultySparkPlug);

		Engine realEngine = new Engine();
		realEngine.fitSparkPlugs(sparkPlugs);
		Engine engine = spy(realEngine);

		Car car = new Car();
		car.setEngine(engine);

		PowerMockito.mockStatic(GarageUtils.class);
		when(GarageUtils.checkSparkPlugs(sparkPlugs)).thenReturn(false);
		when(GarageUtils.getNewSparkPlugs()).thenReturn(sparkPlugs);

		garageServiceImpl.serviceEngine(car);
	}

	// Mocking a private method
	@Test(expected = TechTalkException.class)
	@PrepareForTest(GarageServiceImpl.class)
	public void testServiceEngineWithBrokenEngine() throws Exception
	{

		Engine engine = new Engine();
		engine.fitSparkPlugs(sparkPlugs);
		Car car = new Car();
		car.setEngine(engine);

		GarageServiceImpl partialGarageService = PowerMockito.spy(garageServiceImpl);

		PowerMockito.doReturn(false).when(partialGarageService, "runEngineDiagnostics", engine);

		partialGarageService.serviceEngine(car);
	}

	// Using Mockito to Verify and mock behaviour
	@SuppressWarnings("unchecked")
	@Test
	public void testCorrectServiceDateProvided()
	{
		Car car = new Car();
		Engine engine = mock(Engine.class);
		car.setEngine(engine);

		when(engine.inspectSparkPlugs()).thenReturn(sparkPlugs);
		when(engine.isRunning()).thenReturn(true);

		garageServiceImpl.serviceEngine(car);

		verify(engine).stopEngine();
		// This should not be called. Change the sparkplug to return false to see error.
		verify(engine, never()).fitSparkPlugs(any(List.class));
		verify(engine).startEngine();

		ArgumentCaptor<ServiceRecord> argument = ArgumentCaptor.forClass(ServiceRecord.class);
		verify(carServiceHistoryServiceImpl).updateServiceHistory(argument.capture());
		DateTime serviceDate = argument.getValue().getServiceDate();

		assertEquals(new DateTime().getYear(), serviceDate.getYear());
		assertEquals(new DateTime().getMonthOfYear(), serviceDate.getMonthOfYear());
		assertEquals(new DateTime().getYear(), serviceDate.getYear());

	}

}
