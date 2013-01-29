package ie.tech.talk.domain;

import ie.tech.talk.exception.TechTalkException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class InterestRate
{
	private static final int RATE_SCALE = 3;

	private BigDecimal rate;

	public InterestRate()
	{
		this.rate = new BigDecimal("00.000").setScale(RATE_SCALE, BigDecimal.ROUND_HALF_UP);
	}

	public InterestRate(BigDecimal rate)
	{
		this.rate = rate.setScale(RATE_SCALE, BigDecimal.ROUND_HALF_UP);
	}

	public InterestRate(String rate)
	{
		if (rate.length() != 5 || rate.indexOf('.') != -1)
		{
			throw new TechTalkException("Invalid Interest Rate");
		}

		this.rate = new BigDecimal(rate).movePointLeft(RATE_SCALE);
	}

	public BigDecimal getRate()
	{
		return rate;
	}

	public String getRateAsString()
	{
		return getFormatedInterestRate();
	}

	@Override
	public String toString()
	{
		return getFormatedInterestRate();
	}

	private String getFormatedInterestRate()
	{
		NumberFormat format = new DecimalFormat("00.000");

		String parsed = format.format(rate);

		String formattedRate = parsed.replaceAll("\\.", "");

		return formattedRate;
	}
}
