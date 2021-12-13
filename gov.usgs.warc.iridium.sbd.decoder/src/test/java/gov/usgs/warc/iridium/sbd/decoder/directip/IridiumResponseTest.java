package gov.usgs.warc.iridium.sbd.decoder.directip;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import gov.usgs.warc.iridium.sbd.decoder.ParsingTestsHelper;
import gov.usgs.warc.iridium.sbd.decoder.Tests;
import gov.usgs.warc.iridium.sbd.decoder.Tests.SkipMethod;
import gov.usgs.warc.iridium.sbd.decoder.parser.Message;
import gov.usgs.warc.iridium.sbd.decoder.parser.SbdParser;
import gov.usgs.warc.iridium.sbd.domain.SbdDataType;
import gov.usgs.warc.iridium.sbd.domain.SbdDataTypeProvider;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrder;
import gov.usgs.warc.iridium.sbd.domain.SbdDecodeOrderProvider;
import gov.usgs.warc.iridium.sbd.domain.SbdStationId;
import gov.usgs.warc.iridium.sbd.domain.SbdStationIdProvider;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * The {@link IridiumResponse} test
 *
 * @author darceyj
 * @since Feb 12, 2018
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
public class IridiumResponseTest
{

	/**
	 * @author darceyj
	 * @since Feb 12, 2018
	 *
	 */
	@Configuration
	static class ContextConfiguration
	{

		/**
		 * @param p_Context
		 *            {@link ApplicationContext}
		 * @param p_IridiumStationIdRepository
		 *            {@link SbdStationIdProvider}
		 * @param p_IridiumDataTypeRepository
		 *            {@link SbdDataTypeProvider}
		 * @param p_IridiumDecodeOrderRepository
		 *            {@link SbdDecodeOrderProvider}
		 * @return {@link SbdProcessorImpl} instance
		 * @author darceyj
		 * @since Nov 8, 2017
		 */
		@Bean
		@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
		public SbdProcessor processor(final ApplicationContext p_Context,
				final SbdStationIdProvider<SbdStationId> p_IridiumStationIdRepository,
				final SbdDataTypeProvider<SbdDataType> p_IridiumDataTypeRepository,
				final SbdDecodeOrderProvider<SbdDecodeOrder> p_IridiumDecodeOrderRepository)
		{
			return new SbdProcessorImpl(p_Context, p_IridiumStationIdRepository,
					p_IridiumDataTypeRepository,
					p_IridiumDecodeOrderRepository);
		}

	}

	/**
	 * Assert that the testing class has all the required methods.
	 *
	 * @throws java.lang.Exception
	 * @since Feb 12, 2018
	 */
	@BeforeAll
	public static void setUpBeforeAll() throws Exception
	{
		final Class<?> classToTest = IridiumResponse.class;
		final Class<?> testingClass = IridiumResponseTest.class;
		Tests.assertHasRequiredMethods(classToTest, testingClass,
				SkipMethod.BUILDER, SkipMethod.CAN_EQUAL,
				SkipMethod.EQUALS_AND_HASHCODE, SkipMethod.TO_STRING);

	}

	/**
	 * @author mckelvym
	 * @since Mar 22, 2019
	 */
	@MockBean
	private SbdDataTypeProvider<SbdDataType>		m_DataTypeRepository;

	/**
	 * The decode order repository bean
	 *
	 * @since Feb 12, 2018
	 */
	@MockBean
	private SbdDecodeOrderProvider<SbdDecodeOrder>	m_DecodeOrderRepository;

	/**
	 * The {@link SbdStationIdProvider}
	 *
	 * @since Feb 12, 2018
	 */
	@MockBean
	private SbdStationIdProvider<SbdStationId>		m_IridiumStationRepository;

	/**
	 * The station ID to test with
	 *
	 * @since May 9, 2018
	 */
	private Long									m_StationIdTest;

	/**
	 * The {@link IridiumResponse} to test with
	 *
	 * @since Feb 12, 2018
	 */
	private IridiumResponse							m_Testable;

	/**
	 * @throws java.lang.Exception
	 * @since Feb 12, 2018
	 */
	@BeforeEach
	public void setUp() throws Exception
	{
		m_StationIdTest = 1L;
		final String imei = "300234010124740";
		final SbdStationId iridiumStationId = new SbdStationId()
		{

			@Override
			public Long getId()
			{
				return 221L;
			}

			@Override
			public String getImei()
			{
				return imei;
			}

			@Override
			public Long getStationId()
			{
				return m_StationIdTest;
			}
		};

		when(m_IridiumStationRepository.findByImei(imei))
				.thenReturn(Lists.newArrayList(iridiumStationId));
		when(m_DataTypeRepository.findByStationId(m_StationIdTest))
				.thenReturn(ParsingTestsHelper.getDataTypeSet());
		when(m_DecodeOrderRepository.findByStationId(m_StationIdTest))
				.thenReturn(ParsingTestsHelper.getDecodeOrderSet());
		final SbdParser parser = new SbdParser(
				ParsingTestsHelper.setupMessageBytes("00"));
		final Collection<SbdStationId> stationsList = m_IridiumStationRepository
				.findByImei(String
						.valueOf(parser.getMessage().getHeader().getImei()));
		final Optional<SbdStationId> opt = stationsList.stream().findFirst();
		assertThat(opt).isNotEqualTo(Optional.empty());
		final Long stationId = opt.get().getStationId();

		parser.setDecodeConfiguration(
				m_DataTypeRepository.findByStationId(stationId),
				m_DecodeOrderRepository.findByStationId(stationId));
		final Map<SbdDataType, Double> dataMap = parser.getValuesFromMessage();
		final Table<SbdStationId, SbdDataType, Double> valueTable = HashBasedTable
				.create();
		dataMap.forEach((datatype, value) -> valueTable.put(iridiumStationId,
				datatype, value));
		m_Testable = IridiumResponse.builder().message(parser.getMessage())
				.stations(stationsList).values(valueTable).build();
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.directip.IridiumResponse#getMessage()}.
	 */
	@Test
	public void testGetMessage()
	{
		assertThat(m_Testable.getMessage()).isNotNull();
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.directip.IridiumResponse#getStations()}.
	 */
	@Test
	public void testGetStations()
	{
		assertThat(m_Testable.getStations()).isNotNull();
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.directip.IridiumResponse#getValues()}.
	 */
	@Test
	public void testGetValues()
	{
		assertThat(m_Testable.getValues()).isNotNull();

	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.directip.IridiumResponse#setMessage(Message)}.
	 */
	@Test
	public void testSetMessage()
	{
		assertThat(m_Testable.getMessage()).isNotNull();
		m_Testable.setMessage(null);
		assertThat(m_Testable.getMessage()).isNull();
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.directip.IridiumResponse#setStations(Collection)}.
	 */
	@Test
	public void testSetStations()
	{
		assertThat(m_Testable.getStations()).isNotNull();
		m_Testable.setStations(null);
		assertThat(m_Testable.getStations()).isNull();
	}

	/**
	 * Test method for
	 * {@link gov.usgs.warc.iridium.sbd.decoder.directip.IridiumResponse#setValues(Table)}.
	 */
	@Test
	public void testSetValues()
	{
		assertThat(m_Testable.getValues()).isNotNull();
		m_Testable.setValues(null);
		assertThat(m_Testable.getValues()).isNull();
	}

}
