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
import ie.tech.talk.test.TestUtils;
import ie.tech.talk.utils.GarageUtils;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit test that demonstrates the the usage of mocking and verifying.
 * 
 * @author Michael Freeman
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(GarageUtils.class)
public class GarageServiceImplTest
{
	private GarageServiceImpl garageServiceImpl;
	private List<SparkPlug> sparkPlugs;
	private CarServiceHistoryServiceImpl carServiceHistoryServiceImpl;
	private Car car;
	private Engine engine;

	/**
	 * SetUp the classes we need to test. This is executed before every test is executed. Fields can be overridden in
	 * the test if needed.
	 */
	@Before
	public void setUp()
	{
		garageServiceImpl = new GarageServiceImpl();
		carServiceHistoryServiceImpl = mock(CarServiceHistoryServiceImpl.class);
		garageServiceImpl.setCarServiceHistoryServiceImpl(carServiceHistoryServiceImpl);
		sparkPlugs = TestUtils.getWorkingSparkPlugs();

		car = new Car();
		engine = mock(Engine.class);
		car.setEngine(engine);
	}

	// Using Mockito to verify and mock behaviour
	@SuppressWarnings("unchecked")
	@Test
	public void testServiceEngineWithWorkingSparkPlugs()
	{

		when(engine.inspectSparkPlugs()).thenReturn(sparkPlugs);
		when(engine.isRunning()).thenReturn(true);

		garageServiceImpl.serviceEngine(car);

		verify(engine).stopEngine();
		// This should not be called. Change the sparkplug to return false to see error.
		verify(engine, never()).fitSparkPlugs(any(List.class));
		verify(engine).startEngine();

	}

	// Using Mockito to verify and mock behaviour. This time we use faulty spark plugs
	@SuppressWarnings("unchecked")
	@Test
	public void testServiceEngineWithFaultySparkPlugs()
	{
		// Return faulty spark plugs
		when(engine.inspectSparkPlugs()).thenReturn(TestUtils.getFaultySparkPlugs());
		when(engine.isRunning()).thenReturn(true);

		garageServiceImpl.serviceEngine(car);

		verify(engine).stopEngine();
		// This should now be called.
		verify(engine).fitSparkPlugs(any(List.class));
		verify(engine).startEngine();
	}

	// Using Mockito to verify and mock behaviour. This time the spark plugs we install turn out to be faulty.
	// We expect the class to throw an error
	@Test(expected = TechTalkException.class)
	public void testServiceEngineWithFaultyReplacementSparkPlugs()
	{
		when(engine.inspectSparkPlugs()).thenReturn(TestUtils.getFaultySparkPlugs());

		// The Engine has failed to start after the service.
		when(engine.isRunning()).thenReturn(false);

		garageServiceImpl.serviceEngine(car);
	}

	// We have decided its ok to not mock the the Engine object and to use a real object.
	// But we still want to verify that the correct methods are called.
	@SuppressWarnings("unchecked")
	@Test
	public void testServiceEngineWithWorkingSparkPlugsUsingSpy()
	{

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

	// Mocking Static methods
	@Test(expected = TechTalkException.class)
	public void testServiceEngineWithFaultyReplacementSparkPlugsUsingSpy()
	{
		SparkPlug faultySparkPlug = new SparkPlug();
		faultySparkPlug.setWorking(false);
		sparkPlugs.add(faultySparkPlug);

		Engine realEngine = new Engine();
		realEngine.fitSparkPlugs(sparkPlugs);
		Engine engine = spy(realEngine);

		car.setEngine(engine);

		PowerMockito.mockStatic(GarageUtils.class);
		when(GarageUtils.checkSparkPlugs(sparkPlugs)).thenReturn(false);
		when(GarageUtils.getNewSparkPlugs()).thenReturn(sparkPlugs);

		garageServiceImpl.serviceEngine(car);
	}

	// Mocking a private method. In this case we want the runEngineDiagnostics to fail
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

	// Capturing and verifying the argument to a method.
	// In this case we want to ensure that the correct service data is recorded.
	@SuppressWarnings("unchecked")
	@Test
	public void testCorrectServiceDateProvided()
	{
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
