package essentialmonolith.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import essentialmonolith.dto.OlapResult;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;

import essentialmonolith.dto.AnalysisView;
import essentialmonolith.model.AnalysisRun;
import essentialmonolith.model.BillingFact;
import essentialmonolith.model.BillingFactId;
import essentialmonolith.model.Employee;
import essentialmonolith.model.HoursRangeDimension;
import essentialmonolith.model.Project;
import essentialmonolith.model.RateRangeDimension;
import essentialmonolith.model.WeekDimension;
import essentialmonolith.model.WorkLog;
import essentialmonolith.repository.AnalysisRunRepository;
import essentialmonolith.repository.BillingFactRepository;
import essentialmonolith.repository.EmployeeRepository;
import essentialmonolith.repository.HoursRangeDimensionRepository;
import essentialmonolith.repository.ProjectRepository;
import essentialmonolith.repository.RateRangeDimensionRepository;
import essentialmonolith.repository.WeekDimensionRepository;
import essentialmonolith.repository.WorkLogRepository;

@SpringBootTest
public class AnalysisServiceTests {
	
	@InjectMocks AnalysisService analysisService;

	@Mock private WorkLogRepository workLogRepository;
	@Mock private AnalysisRunRepository analysisRunRepository;
	@Mock private BillingFactRepository billingFactRepository;
	@Mock private ProjectRepository projectRepository;
	@Mock private EmployeeRepository employeeRepository;
	@Mock private WeekDimensionRepository weekDimensionRepository;
	@Mock private HoursRangeDimensionRepository hoursRangeDimensionRepository;
	@Mock private RateRangeDimensionRepository rateRangeDimensionRepository;
	
	private AnalysisRun analysisRun;

	private BillingFact billingFactAC;
	
	@BeforeEach
	public void before() {
		Project project = Project.builder().id(0L).name("P").build();
		Employee employee = Employee.builder().id(0L).name("E").build();
		WeekDimension weekDimension = WeekDimension.builder().id(0L).name("0").build();
		HoursRangeDimension hoursRangeDimension = HoursRangeDimension.builder().id(0L).name("40+").build();
		RateRangeDimension rateRangeDimension = RateRangeDimension.builder().id(0L).name("80+").build(); 
		BillingFactId billingFactId = BillingFactId.builder().projectId(0L).employeeId(0L).weekDimensionId(0L).hoursRangeDimensionId(0L).rateRangeDimensionId(0L).build();
		BillingFact billingFact = BillingFact.builder()
				.billingFactId(billingFactId)
				.amount(new BigDecimal("3200.00"))
				.project(project)
				.employee(employee)
				.weekDimension(weekDimension)
				.hoursRangeDimension(hoursRangeDimension)
				.rateRangeDimension(rateRangeDimension)
				.build();
		WorkLog workLog = WorkLog.builder()
				.id(0L)
				.project(project)
				.employee(employee)
				.entryDate(LocalDate.parse("2020-01-01"))
				.hours(40)
				.rate(new BigDecimal("80.00"))
				.build();

		List<WorkLog> workLogs = new ArrayList<>();
		workLogs.add(workLog);
		workLogs.add(workLog);

		doReturn(Collections.singletonList(project)).when(projectRepository).findAll();
		doReturn(Collections.singletonList(employee)).when(employeeRepository).findAll();
		doReturn(workLogs).when(workLogRepository).findAll();

		analysisRun = AnalysisRun.builder().id(0L).lastRunTime(LocalDateTime.parse("2020-01-01T00:00:00")).populating(Boolean.FALSE).build();

		doReturn(Optional.of(analysisRun)).when(analysisRunRepository).findById(Mockito.anyLong());
		doReturn(analysisRun).when(analysisRunRepository).save(Mockito.any());
		
		doReturn(weekDimension).when(weekDimensionRepository).save(Mockito.any());
		doReturn(hoursRangeDimension).when(hoursRangeDimensionRepository).save(Mockito.any());
		doReturn(rateRangeDimension).when(rateRangeDimensionRepository).save(Mockito.any());
		
		doReturn(1L).when(billingFactRepository).count();
		doReturn(Collections.singletonList(billingFact))
			.when(billingFactRepository)
			.findAll(Mockito.<Example<BillingFact>>any());
		doAnswer(i->{
				billingFactAC = i.getArgument(0); 
	        	return billingFactAC;
		}).when(billingFactRepository).save(Mockito.any());
	}

	@Test
	public void getAnalysisView() {
		AnalysisView analysisViewResponse = analysisService.getAnalysisView();
		assertEquals(analysisRun, analysisViewResponse.getAnalysisRun());
		assertEquals(5, analysisViewResponse.getAnalysisDimensions().size());
		Long count = analysisViewResponse.getFactCount();
		assertEquals(1, count);
	}

	/**
	 * Map, reduce for analysis tables
	 */
	@Test
	public void populate() {
		CompletableFuture<Void> await = analysisService.populate();
		try {
			// not actually needed because SpringBootTest doesn't have an executor pool? 
			await.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		verify(billingFactRepository).save(Mockito.any());
		assertEquals(new BigDecimal("6400.00"), billingFactAC.getAmount());
	}

	@Test
	public void startPopulate() {
		boolean started = analysisService.startPopulate();
		assertTrue(started);
	}

	@Test
	public void getPurchaseResult() {
		OlapResult olapResult = analysisService.getBillingQueryResult(null, null, null, null, null);
		assertEquals(3200.0, olapResult.getSummaryStatistics().getMean());
		assertEquals(1, olapResult.getSummaryStatistics().getN());
	}

}
