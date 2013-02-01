package ie.tech.talk.test;

import ie.tech.talk.domain.EngineTest;
import ie.tech.talk.service.GarageServiceImplTest;
import ie.tech.talk.utils.GarageUtilsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
{EngineTest.class, GarageServiceImplTest.class, GarageUtilsTest.class})
public class AllTests
{

}
