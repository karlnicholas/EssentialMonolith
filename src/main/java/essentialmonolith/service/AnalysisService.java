package essentialmonolith.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import essentialmonolith.dto.AnalysisDimension;
import essentialmonolith.model.AnalysisRun;
import essentialmonolith.model.BillingFact;
import essentialmonolith.model.BillingFact.BillingFactBuilder;
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
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AnalysisService {
	private final WorkLogRepository workLogRepository;
	private final ProjectRepository projectRepository;
	private final EmployeeRepository employeeRepository;
	private final AnalysisRunRepository analysisRunRepository;
	private final BillingFactRepository billingFactRepository;
	private final WeekDimensionRepository weekDimensionRepository;
	private final HoursRangeDimensionRepository hoursRangeDimensionRepository;
	private final RateRangeDimensionRepository rateRangeDimensionRepository;
	
	public AnalysisService(
			WorkLogRepository workLogRepository, 
			ProjectRepository projectRepository,
			EmployeeRepository employeeRepository, 
			AnalysisRunRepository analysisRunRepository, 
			BillingFactRepository billingFactRepository, 
			WeekDimensionRepository weekDimensionRepository, 
			HoursRangeDimensionRepository hoursRangeDimensionRepository, 
			RateRangeDimensionRepository rateRangeDimensionRepository 
	) {
		this.workLogRepository = workLogRepository;
		this.projectRepository = projectRepository;
		this.employeeRepository = employeeRepository;
		this.analysisRunRepository = analysisRunRepository;
		this.billingFactRepository = billingFactRepository;
		this.weekDimensionRepository = weekDimensionRepository; 
		this.hoursRangeDimensionRepository = hoursRangeDimensionRepository; 
		this.rateRangeDimensionRepository = rateRangeDimensionRepository; 
	}

	public AnalysisRun getAnalysisRun() {
		AnalysisRun analysisRun = analysisRunRepository.findById(1L).get();
		return analysisRun;
	}
	
	public List<AnalysisDimension> getBillingDimensions() {
		List<AnalysisDimension> dimensions = new ArrayList<>();
		dimensions.add(AnalysisDimension.builder()
				.name("Project")
				.dimensions(projectRepository.findAll().stream().collect(Collectors.toList())).build());
		dimensions.add(AnalysisDimension.builder()
				.name("Employee")
				.dimensions(employeeRepository.findAll().stream().collect(Collectors.toList())).build());
		dimensions.add(AnalysisDimension.builder()
				.name("Week")
				.dimensions(weekDimensionRepository.findAll().stream().collect(Collectors.toList())).build());
		dimensions.add(AnalysisDimension.builder()
				.name("HoursRange")
				.dimensions(hoursRangeDimensionRepository.findAll().stream().collect(Collectors.toList())).build());
		dimensions.add(AnalysisDimension.builder()
				.name("RateRange")
				.dimensions(rateRangeDimensionRepository.findAll().stream().collect(Collectors.toList())).build());
		return dimensions;
	}

	private String getWeek(LocalDate entryDate) {
		return Integer.toString(entryDate.getDayOfYear() / 7);
	}
	private String getHoursRange(Integer hours) {
		return hours >= 40 ? "40+":"<40";
	}
	private String getRateRange(BigDecimal rate) {
		return rate.compareTo(new BigDecimal("80")) >= 0 ? "80+":"<80";
	}
	/**
	 * Map, reduce for analysis tables
	 */
	@Async
	public CompletableFuture<Void> populate() {
		try {
			billingFactRepository.deleteAll();
			weekDimensionRepository.deleteAll();
			hoursRangeDimensionRepository.deleteAll();
			rateRangeDimensionRepository.deleteAll();
			
			Map<String, WeekDimension> weeksMap = workLogRepository.findAll().stream().map(WorkLog::getEntryDate)
			.map(date->getWeek(date))
			.distinct()
			.map(week->WeekDimension.builder().name(week).build())
			.map(weekDimensionRepository::save)
			.collect(Collectors.toMap(WeekDimension::getName, Function.identity()));

			Map<String, HoursRangeDimension> hoursRangeMap = workLogRepository.findAll().stream().map(WorkLog::getHours)
			.map(hours->getHoursRange(hours))
			.distinct()
			.map(hoursRange->HoursRangeDimension.builder().name(hoursRange).build())
			.map(hoursRangeDimensionRepository::save)
			.collect(Collectors.toMap(HoursRangeDimension::getName, Function.identity()));
			
			Map<String, RateRangeDimension> rateRangeMap = workLogRepository.findAll().stream().map(WorkLog::getRate)
			.map(rate->getRateRange(rate))
			.distinct()
			.map(rateRange->RateRangeDimension.builder().name(rateRange).build())
			.map(rateRangeDimensionRepository::save)
			.collect(Collectors.toMap(RateRangeDimension::getName, Function.identity()));

			workLogRepository.findAll().stream()
			.map(workLog->BillingFact.builder()
						.billingFactId(new BillingFactId())
						.amount(workLog.getRate().multiply(BigDecimal.valueOf(workLog.getHours())))
						.project(workLog.getProject())
						.employee(workLog.getEmployee())
						.weekDimension(weeksMap.get(getWeek(workLog.getEntryDate())))
						.hoursRangeDimension(hoursRangeMap.get(getHoursRange(workLog.getHours())))
						.rateRangeDimension(rateRangeMap.get(getRateRange(workLog.getRate())))
						.build()
			)
			.collect(Collectors.groupingBy(billingFact->
					BillingFactId.builder()
					.projectId(billingFact.getProject().getId())
					.employeeId(billingFact.getEmployee().getId())
					.weekDimensionId(billingFact.getWeekDimension().getId())
					.hoursRangeDimensionId(billingFact.getWeekDimension().getId())
					.rateRangeDimensionId(billingFact.getWeekDimension().getId())
					.build()
					, Collectors.reducing((bf1, bf2)->{
						bf1.setAmount(bf1.getAmount().add(bf2.getAmount()));
						return bf1;
					})
				)
			).values().stream().map(Optional::get)
			.forEach(billingFactRepository::save);
		} catch ( Exception e) {
			log.error(e.getMessage());
		} finally {
			AnalysisRun analysisRun = analysisRunRepository.findById(1L).get();
			analysisRun.setLastRunTime(LocalDateTime.now());
			analysisRun.setPopulating(Boolean.FALSE);
			analysisRunRepository.save(analysisRun);
		}
		return CompletableFuture.completedFuture(null);
	}

	public SummaryStatistics getBillingQueryResult(
		Long project, 
		Long employee, 
		Long week, 
		Long hoursRange, 
		Long rateRange
	) {
		SummaryStatistics summaryStatistics = new SummaryStatistics();
		BillingFactBuilder b = BillingFact.builder();
		if ( project != null ) b.project(Project.builder().id(project).build());
		if ( employee != null ) b.employee(Employee.builder().id(employee).build());
		if ( week != null ) b.weekDimension(WeekDimension.builder().id(week).build());
		if ( hoursRange != null ) b.hoursRangeDimension(HoursRangeDimension.builder().id(hoursRange).build());
		if ( rateRange != null ) b.rateRangeDimension(RateRangeDimension.builder().id(rateRange).build());
		billingFactRepository.findAll(Example.of(b.build())).stream().mapToDouble(billingFact->billingFact.getAmount().doubleValue()).forEach(summaryStatistics::addValue);
		return summaryStatistics;
	}

	public boolean startPopulate() {
		AnalysisRun analysisRun = analysisRunRepository.findById(1L).get();
		if ( analysisRun.getPopulating() ) return false;
		analysisRun.setLastRunTime(LocalDateTime.now());
		analysisRun.setPopulating(Boolean.TRUE);
		analysisRunRepository.save(analysisRun);
		return true;
	}
	
	public Long getFactCount() {
		return billingFactRepository.count();
	}


}