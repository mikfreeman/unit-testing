package ie.tech.talk.service;

import static ie.tech.talk.test.TestUtils.getFaultySparkPlugs;
import static ie.tech.talk.test.TestUtils.getWorkingEngine;
import static ie.tech.talk.test.TestUtils.getWorkingSparkPlugs;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import ie.tech.talk.domain.Car;
import ie.tech.talk.domain.Engine;
import ie.tech.talk.domain.ServiceRecord;
import ie.tech.talk.domain.SparkPlug;
import ie.tech.talk.exception.CarServiceException;
import ie.tech.talk.exception.DiagnosticFailureException;
import ie.tech.talk.utils.GarageUtils;

import java.util.List;

import org.easymock.Capture;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit test that demonstrates the the usage of mocking, verifying and stubbing.
 * 
 * This implementation uses Easy Mock
 * 
 * @author Michael Freeman
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(GarageUtils.class)
public class GarageServiceImplEasyMockTest
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
		carServiceHistoryServiceImpl = createMock(CarServiceHistoryServiceImpl.class);
		garageServiceImpl.setCarServiceHistoryServiceImpl(carServiceHistoryServiceImpl);
		sparkPlugs = getWorkingSparkPlugs();

		car = new Car();
		engine = createMock(Engine.class);
		car.setEngine(engine);
	}

	// Using Easy Mock to stub methods and verify behaviour
	@Test
	public void testServiceEngineWithWorkingSparkPlugs()
	{
		engine.stopEngine();

		expect(engine.inspectSparkPlugs()).andReturn(sparkPlugs);
		// Note in mockito we verify that a method call did not take place. In easymock an exception is thrown if it is
		// called.
		expect(engine.isRunning()).andReturn(true);
		engine.startEngine();
		replay(engine);
		garageServiceImpl.serviceEngine(car);
		verify(engine);
	}

	// This time we use faulty spark plugs
	@SuppressWarnings("unchecked")
	@Test
	public void testServiceEngineWithFaultySparkPlugs()
	{
		engine.stopEngine();
		expect(engine.inspectSparkPlugs()).andReturn(getFaultySparkPlugs());
		engine.fitSparkPlugs(anyObject(List.class));
		expect(engine.isRunning()).andReturn(true);
		engine.startEngine();
		replay(engine);
		garageServiceImpl.serviceEngine(car);
		verify(engine);
	}

	@SuppressWarnings("unchecked")
	// This time the spark plugs we install turn out to be faulty.
	// The engine didn't properly start so we expect an error
	@Test(expected = CarServiceException.class)
	public void testServiceEngineWithFaultyReplacementSparkPlugs()
	{
		engine.stopEngine();
		expect(engine.inspectSparkPlugs()).andReturn(getFaultySparkPlugs());
		engine.fitSparkPlugs(anyObject(List.class));
		engine.startEngine();
		expect(engine.isRunning()).andReturn(false);
		replay(engine);
		garageServiceImpl.serviceEngine(car);
	}

	// We have decided its OK to not mock the the Engine object and to use a real object.
	// But we still want to verify and stub some methods that the correct methods are called.
	// We use a technique called partial mocking to achieve that
	// NOTE : Could not find a way to verify the method calls with Easy Mock
	@Test
	public void testServiceEngineWithWorkingSparkPlugsUsingSpy()
	{
		Engine engine = createMockBuilder(Engine.class).addMockedMethod("inspectSparkPlugs").createMock();
		engine.fitSparkPlugs(sparkPlugs);
		car.setEngine(engine);

		expect(engine.inspectSparkPlugs()).andReturn(getFaultySparkPlugs());
		replay(engine);

		garageServiceImpl.serviceEngine(car);

		// No way to verify the methods called on the spy
		verify(engine);

	}

	// Stubbing Static methods. Returning faulty spark plugs from the static call
	@Test(expected = CarServiceException.class)
	public void testServiceEngineWithStubbedStaticMethods()
	{
		List<SparkPlug> faultySparkPlugs = getFaultySparkPlugs();
		Engine engine = getWorkingEngine();
		car.setEngine(engine);

		PowerMock.mockStatic(GarageUtils.class);
		expect(GarageUtils.checkSparkPlugs(engine.inspectSparkPlugs())).andReturn(false);
		expect(GarageUtils.getNewSparkPlugs()).andReturn(faultySparkPlugs);

		// Note we have to use the PowerMock version
		PowerMock.replay(GarageUtils.class);

		garageServiceImpl.serviceEngine(car);
	}

	// Mocking a private method. In this case we want the runEngineDiagnostics to fail
	// We use PowerMock to achieve this.
	@Test(expected = DiagnosticFailureException.class)
	@PrepareForTest(GarageServiceImpl.class)
	public void testServiceEngineWithBrokenEngine() throws Exception
	{
		Engine engine = getWorkingEngine();
		car.setEngine(engine);

		GarageServiceImpl partialGarageService = PowerMock.createPartialMock(GarageServiceImpl.class,
				"runEngineDiagnostics");

		PowerMock.expectPrivate(partialGarageService, "runEngineDiagnostics", engine).andReturn(true);

		partialGarageService.serviceEngine(car);
	}

	// Capturing and verifying the argument to a method.
	// In this case we want to ensure that the correct service data is recorded.
	@Test
	public void testCorrectServiceDateProvided()
	{
		Engine engine = getWorkingEngine();
		car.setEngine(engine);

		Capture<ServiceRecord> serviceRecord = new Capture<ServiceRecord>();
		carServiceHistoryServiceImpl.updateServiceHistory(capture(serviceRecord));

		replay(carServiceHistoryServiceImpl);

		garageServiceImpl.serviceEngine(car);
		DateTime serviceDate = serviceRecord.getValue().getServiceDate();

		assertEquals(new DateTime().getYear(), serviceDate.getYear());
		assertEquals(new DateTime().getMonthOfYear(), serviceDate.getMonthOfYear());
		assertEquals(new DateTime().getYear(), serviceDate.getYear());
	}
}
