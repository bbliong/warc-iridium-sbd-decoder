package gov.usgs.warc.iridium.sbd.decoder.parser.elements;

import static org.assertj.core.api.Assertions.assertThat;

import gov.usgs.warc.iridium.sbd.decoder.Tests;
import gov.usgs.warc.iridium.sbd.decoder.Tests.SkipMethod;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link LocationInformation} element
 *
 * @author darceyj
 * @since Jan 8, 2018
 *
 */
public class LocationInformationTest
{
	/**
	 * Assert that the test has all the required methods
	 *
	 * @throws java.lang.Exception
	 * @since Jan 8, 2018
	 */
	@BeforeAll
	public static void setUpBeforeAll() throws Exception
	{
		final Class<?> classToTest = LocationInformation.class;
		final Class<?> testingClass = LocationInformationTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.BUILDER, SkipMethod.EQUALS_AND_HASHCODE,
				SkipMethod.TO_STRING, SkipMethod.CAN_EQUAL);

	}

	/**
	 * Testing {@link LocationInformation} object
	 *
	 * @since Jan 8, 2018
	 */
	private LocationInformation m_Testable;

	/**
	 * Set up the location information object.
	 *
	 * @since Jan 8, 2018
	 */
	@BeforeEach
	public void setUp()
	{
		m_Testable = LocationInformation.builder().cepRadius(1).id((byte) 0x03)
				.latitude(100.0).longitude(200.0).length((short) 10).build();
	}

	/**
	 * Test method for {@link LocationInformation#getCepRadius()}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetCepRadius()
	{
		assertThat(m_Testable.getCepRadius()).isEqualTo(1);
	}

	/**
	 * Test method for {@link LocationInformation#getId}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetId()
	{

		assertThat(m_Testable.getId()).isEqualTo((byte) 0x03);
	}

	/**
	 * Test method for {@link LocationInformation#getLatitude()}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetLatitude()
	{
		assertThat(m_Testable.getLatitude()).isEqualTo(100.0);
	}

	/**
	 * Test method for {@link LocationInformation#getLength()}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetLength()
	{
		final short expected = 10;
		assertThat(m_Testable.getLength()).isEqualTo(expected);
	}

	/**
	 * Test method for {@link LocationInformation#getLongitude()}
	 *
	 * @since Jan 8, 2018
	 */
	@Test
	public void testGetLongitude()
	{
		assertThat(m_Testable.getLongitude()).isEqualTo(200.0);
	}

}
