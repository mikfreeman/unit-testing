package ie.tech.talk.domain;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import ie.tech.talk.exception.TechTalkException;

import java.math.BigDecimal;

import org.junit.Test;

public class InterestRateTest
{
	@Test
	public void testInterestRate()
	{
		InterestRate interestRate = new InterestRate();
		assertEquals("00000", interestRate.getRateAsString());

		interestRate = new InterestRate("04500");
		assertEquals("04500", interestRate.getRateAsString());

		interestRate = new InterestRate("00001");
		assertEquals("00001", interestRate.getRateAsString());

		interestRate = new InterestRate("99999");
		assertEquals("99999", interestRate.getRateAsString());

		InterestRate base = new InterestRate("03000");
		InterestRate margin = new InterestRate("00500");
		InterestRate discount = new InterestRate();
		InterestRate adjustment = new InterestRate("00750");

		BigDecimal rate = base.getRate();
		rate = rate.add(margin.getRate());
		rate = rate.subtract(discount.getRate());
		rate = rate.subtract(adjustment.getRate());

		interestRate = new InterestRate(rate);

		assertEquals("02750", interestRate.getRateAsString());
	}

	@Test(expected = TechTalkException.class)
	public void testInvalidInterestRate()
	{
		try
		{
			new InterestRate("250000");
			fail();
		}
		catch (TechTalkException e)
		{

		}

		new InterestRate("25.00");
	}
}
