package ie.tech.talk.service;

import static ie.tech.talk.test.TestUtils.getFaultySparkPlugs;
import static ie.tech.talk.test.TestUtils.getWorkingEngine;
import static ie.tech.talk.test.TestUtils.getWorkingSparkPlugs;
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
import ie.tech.talk.exception.CarServiceException;
import ie.tech.talk.exception.DiagnosticFailureException;
import ie.tech.talk.utils.GarageUtils;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit test that demonstrates the the usage of mocking, verifying and stubbing.
 * 
 * This implementation uses Mockito
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

	// Can use an annotation to create the mock
	@Mock
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
		garageServiceImpl.setCarServiceHistoryServiceImpl(carServiceHistoryServiceImpl);
		sparkPlugs = getWorkingSparkPlugs();

		car = new Car();
		engine = mock(Engine.class);
		car.setEngine(engine);
	}

	// Using Mockito to stub methods and verify behaviour
	@SuppressWarnings("unchecked")
	@Test
	public void testServiceEngineWithWorkingSparkPlugs()
	{
		when(engine.inspectSparkPlugs()).thenReturn(sparkPlugs);
		when(engine.isRunning()).thenReturn(true);

		garageServiceImpl.serviceEngine(car);

		verify(engine).stopEngine();
		// This should not be called. Stub the engine to return faulty spark plugs to see the error.
		verify(engine, never()).fitSparkPlugs(any(List.class));
		verify(engine).startEngine();
	}

	// This time we use faulty spark plugs
	@SuppressWarnings("unchecked")
	@Test
	public void testServiceEngineWithFaultySparkPlugs()
	{
		// Return faulty spark plugs
		when(engine.inspectSparkPlugs()).thenReturn(getFaultySparkPlugs());
		when(engine.isRunning()).thenReturn(true);

		garageServiceImpl.serviceEngine(car);

		verify(engine).stopEngine();
		// This should now be called.
		verify(engine).fitSparkPlugs(any(List.class));
		verify(engine).startEngine();
	}

	// This time the spark plugs we install turn out to be faulty.
	// The engine didn't properly start so we expect an error
	@Test(expected = CarServiceException.class)
	public void testServiceEngineWithFaultyReplacementSparkPlugs()
	{
		when(engine.inspectSparkPlugs()).thenReturn(getFaultySparkPlugs());

		// The Engine has failed to start after the service.
		when(engine.isRunning()).thenReturn(false);

		garageServiceImpl.serviceEngine(car);
	}

	// We have decided its OK to not mock the the Engine object and to use a real object.
	// But we still want to verify and stub some methods that the correct methods are called.
	// We use a technique called partial mocking to achieve that
	@SuppressWarnings("unchecked")
	@Test
	public void testServiceEngineWithWorkingSparkPlugsUsingSpy()
	{
		Engine realEngine = new Engine();
		realEngine.fitSparkPlugs(sparkPlugs);
		Engine engine = spy(realEngine);
		car.setEngine(engine);

		// We stub the inspectSparkPlugs of the non mock object
		when(engine.inspectSparkPlugs()).thenReturn(getFaultySparkPlugs());

		garageServiceImpl.serviceEngine(car);

		verify(engine).stopEngine();
		verify(engine).fitSparkPlugs(any(List.class));
		verify(engine).startEngine();
	}

	// Stubbing Static methods. Returning faulty spark plugs from the static call
	@Test(expected = CarServiceException.class)
	public void testServiceEngineWithStubbedStaticMethods()
	{
		List<SparkPlug> faultySparkPlugs = getFaultySparkPlugs();
		Engine engine = getWorkingEngine();
		car.setEngine(engine);

		PowerMockito.mockStatic(GarageUtils.class);
		when(GarageUtils.checkSparkPlugs(engine.inspectSparkPlugs())).thenReturn(false);
		when(GarageUtils.getNewSparkPlugs()).thenReturn(faultySparkPlugs);

		garageServiceImpl.serviceEngine(car);
	}

	// Mocking a private method. In this case we want the runEngineDiagnostics to fail
	// We use PowerMockito to achieve this.
	@Test(expected = DiagnosticFailureException.class)
	@PrepareForTest(GarageServiceImpl.class)
	public void testServiceEngineWithBrokenEngine() throws Exception
	{
		Engine engine = getWorkingEngine();
		car.setEngine(engine);

		GarageServiceImpl partialGarageService = PowerMockito.spy(garageServiceImpl);

		PowerMockito.doReturn(false).when(partialGarageService, "runEngineDiagnostics", engine);

		partialGarageService.serviceEngine(car);
	}

	// Capturing and verifying the argument to a method.
	// In this case we want to ensure that the correct service data is recorded.
	@Test
	public void testCorrectServiceDateProvided()
	{
		Engine engine = getWorkingEngine();
		car.setEngine(engine);

		garageServiceImpl.serviceEngine(car);

		ArgumentCaptor<ServiceRecord> argument = ArgumentCaptor.forClass(ServiceRecord.class);
		verify(carServiceHistoryServiceImpl).updateServiceHistory(argument.capture());
		DateTime serviceDate = argument.getValue().getServiceDate();

		assertEquals(new DateTime().getYear(), serviceDate.getYear());
		assertEquals(new DateTime().getMonthOfYear(), serviceDate.getMonthOfYear());
		assertEquals(new DateTime().getYear(), serviceDate.getYear());
	}
}
