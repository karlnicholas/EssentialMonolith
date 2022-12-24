package essentialmonolith.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import essentialmonolith.dto.*;
import essentialmonolith.model.*;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import essentialmonolith.model.BillingFact.BillingFactBuilder;
import essentialmonolith.repository.AnalysisRunRepository;
import essentialmonolith.repository.BillingFactRepository;
import essentialmonolith.repository.EmployeeRepository;
import essentialmonolith.repository.HoursRangeRepository;
import essentialmonolith.repository.ProjectRepository;
import essentialmonolith.repository.RateRangeRepository;
import essentialmonolith.repository.WeekRepository;
import essentialmonolith.repository.WorkLogRepository;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;

@Service
@Slf4j
public class AnalysisService {
	private final WorkLogRepository workLogRepository;
	private final ProjectRepository projectRepository;
	private final EmployeeRepository employeeRepository;
	private final AnalysisRunRepository analysisRunRepository;
	private final BillingFactRepository billingFactRepository;
	private final WeekRepository weekRepository;
	private final HoursRangeRepository hoursRangeRepository;
	private final RateRangeRepository rateRangeRepository;
	
	public AnalysisService(
			WorkLogRepository workLogRepository, 
			ProjectRepository projectRepository,
			EmployeeRepository employeeRepository, 
			AnalysisRunRepository analysisRunRepository, 
			BillingFactRepository billingFactRepository, 
			WeekRepository weekRepository,
			HoursRangeRepository hoursRangeRepository,
			RateRangeRepository rateRangeRepository
	) {
		this.workLogRepository = workLogRepository;
		this.projectRepository = projectRepository;
		this.employeeRepository = employeeRepository;
		this.analysisRunRepository = analysisRunRepository;
		this.billingFactRepository = billingFactRepository;
		this.weekRepository = weekRepository;
		this.hoursRangeRepository = hoursRangeRepository;
		this.rateRangeRepository = rateRangeRepository;
	}

	private String getWeek(LocalDate entryDate) {
		return Integer.toString(entryDate.getDayOfYear() / 7);
	}

	private String getHoursRange(Integer hours) {
		return hours >= 40 ? "40+" : "<40";
	}

	private String getRateRange(BigDecimal rate) {
		return rate.compareTo(new BigDecimal("80")) >= 0 ? "80+" : "<80";
	}

	/**
	 * Map, reduce for analysis tables
	 */
	@Async
	public CompletableFuture<Void> populate() {
		try {
			AnalysisRun analysisRun = analysisRunRepository.findById(1L).get();
			analysisRun.setLastRunTime(LocalDateTime.now());
			analysisRun.setPopulating(Boolean.TRUE);
			analysisRunRepository.save(analysisRun);

			billingFactRepository.deleteAll();
			weekRepository.deleteAll();
			hoursRangeRepository.deleteAll();
			rateRangeRepository.deleteAll();

			Map<String, Week> weeksMap = workLogRepository.findAll().stream().map(WorkLog::getEntryDate)
					.map(this::getWeek)
					.distinct()
					.map(week -> Week.builder().name(week).build())
					.map(weekRepository::save)
					.collect(Collectors.toMap(Week::getName, Function.identity()));

			Map<String, HoursRange> hoursRangeMap = workLogRepository.findAll().stream().map(WorkLog::getHours)
					.map(this::getHoursRange)
					.distinct()
					.map(hoursRange -> HoursRange.builder().name(hoursRange).build())
					.map(hoursRangeRepository::save)
					.collect(Collectors.toMap(HoursRange::getName, Function.identity()));

			Map<String, RateRange> rateRangeMap = workLogRepository.findAll().stream().map(WorkLog::getRate)
					.map(rate -> getRateRange(rate))
					.distinct()
					.map(rateRange -> RateRange.builder().name(rateRange).build())
					.map(rateRangeRepository::save)
					.collect(Collectors.toMap(RateRange::getName, Function.identity()));

			workLogRepository
					.findAll().stream().map(
							workLog -> BillingFact.builder().billingFactId(new BillingFactId())
									.amount(workLog.getRate().multiply(BigDecimal.valueOf(workLog.getHours())))
									.project(workLog.getProject()).employee(workLog.getEmployee())
									.week(weeksMap.get(getWeek(workLog.getEntryDate())))
									.hoursRange(hoursRangeMap.get(getHoursRange(workLog.getHours())))
									.rateRange(rateRangeMap.get(getRateRange(workLog.getRate()))).build())
					.collect(Collectors.groupingBy(
							billingFact -> BillingFactId.builder().projectId(billingFact.getProject().getId())
									.employeeId(billingFact.getEmployee().getId())
									.weekId(billingFact.getWeek().getId())
									.hoursRangeId(billingFact.getWeek().getId())
									.rateRangeId(billingFact.getWeek().getId()).build(),
							Collectors.reducing((bf1, bf2) -> {
								bf1.setAmount(bf1.getAmount().add(bf2.getAmount()));
								return bf1;
							})))
					.values().stream().map(Optional::get).forEach(billingFactRepository::save);
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			AnalysisRun analysisRun = analysisRunRepository.findById(1L).get();
			analysisRun.setLastRunTime(LocalDateTime.now());
			analysisRun.setPopulating(Boolean.FALSE);
			analysisRunRepository.save(analysisRun);
		}
		return CompletableFuture.completedFuture(null);
	}

	public OlapResult getBillingQueryResult(Long project, Long employee, Long week, Long hoursRange, Long rateRange) {
		OlapResult olapResult = OlapResult.builder().summaryStatistics(new SummaryStatistics()).facts(new ArrayList<>()).build();
		BillingFactBuilder b = BillingFact.builder();
		if (project != null)
			b.project(Project.builder().id(project).build());
		if (employee != null)
			b.employee(Employee.builder().id(employee).build());
		if (week != null)
			b.week(Week.builder().id(week).build());
		if (hoursRange != null)
			b.hoursRange(HoursRange.builder().id(hoursRange).build());
		if (rateRange != null)
			b.rateRange(RateRange.builder().id(rateRange).build());
		billingFactRepository.findAll(Example.of(b.build())).forEach(bf->{
			olapResult.getFacts().add(bf);
			olapResult.getSummaryStatistics().addValue(bf.getAmount().doubleValue());
		});
		return olapResult;
	}

	public AnalysisRun getAnalysisRunState() {
		AnalysisRun analysisRun = analysisRunRepository.findById(1L).get();
		if ( analysisRun.getPopulating() ) return analysisRun;
		analysisRun.setLastRunTime(LocalDateTime.now());
		analysisRun.setPopulating(Boolean.FALSE);
		analysisRun = analysisRunRepository.save(analysisRun);
		return analysisRun;
	}
	
	public AnalysisView getAnalysisView() {
		return AnalysisView.builder()
			.analysisRun(analysisRunRepository.findById(1L).get())
			.analysisDimensions(getBillingDimensions())
			.factCount(billingFactRepository.count())
			.build();
	}
	
	private List<AnalysisDimension> getBillingDimensions() {
		List<AnalysisDimension> dimensions = new ArrayList<>();
		dimensions.add(AnalysisDimension.builder().name("Project")
				.dimensions(new ArrayList<>(projectRepository.findAll())).build());
		dimensions.add(AnalysisDimension.builder().name("Employee")
				.dimensions(new ArrayList<>(employeeRepository.findAll())).build());
		dimensions.add(AnalysisDimension.builder().name("Week")
				.dimensions(new ArrayList<>(weekRepository.findAll())).build());
		dimensions.add(AnalysisDimension.builder().name("HoursRange")
				.dimensions(new ArrayList<>(hoursRangeRepository.findAll())).build());
		dimensions.add(AnalysisDimension.builder().name("RateRange")
				.dimensions(new ArrayList<>(rateRangeRepository.findAll())).build());
		return dimensions;
	}

	@PersistenceContext
	private EntityManager entityManager;

	public OlapResult getBillingQueryResultPlay(OlapQuery olapQuery) {
		OlapResult olapResult = OlapResult.builder().summaryStatistics(new SummaryStatistics()).facts(new ArrayList<>()).build();

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<Tuple> q = cb.createQuery(Tuple.class);
		Root<BillingFact> bf = q.from(BillingFact.class);
		Path<Number> bfAmount = bf.get("amount");
		Expression<Number> sum = cb.sum(bfAmount);

		int selectSize;
		if ( olapQuery.getGroupByList() != null ) {
			selectSize = olapQuery.getGroupByList().size();
			Expression[] exs = new Expression[olapQuery.getGroupByList().size()];
			Selection[] sels = new Selection[exs.length+1];
			sels[0] = sum;
			int index = 0;
			for ( String groupBy: olapQuery.getGroupByList()) {
				Path<Dimension> d = bf.get(groupBy);
				exs[index++] = d;
				sels[index] = d;
			}
			q.select(cb.construct(Tuple.class, sels));
			q.groupBy(exs);
		} else {
			List<String> ps = List.of("amount", "project", "employee", "week", "hoursRange", "rateRange");
			selectSize = ps.size() - 1;
			q.select(cb.construct(Tuple.class, ps.stream().map(bf::get).collect(Collectors.toList()).toArray(new Path[ps.size()])));
		}

		if ( olapQuery.getWhereList() != null ) {
			Path<Long>[] whereProperties = new Path[olapQuery.getWhereList().size()];
			int index = 0;
			for (IdPair idPair: olapQuery.getWhereList()) {
				whereProperties[index++] = bf.get(idPair.getProperty()).get("id");
			}
			Predicate[] predicates = new Predicate[olapQuery.getWhereList().size()];
			for ( int i = 0; i < whereProperties.length; ++i) {
				predicates[i] = cb.equal(whereProperties[i], olapQuery.getWhereList().get(i).getId());
			}
			q.where(cb.or(predicates));
		}
		entityManager.createQuery(q).getResultList().forEach(qr->{
			BillingFact bfr = new BillingFact();
			bfr.setAmount(qr.get(0, BigDecimal.class));
			for ( int i=0; i < selectSize; ++i) {
				bfr.setDimension(qr.get(i+1, Dimension.class));
			}
			olapResult.getFacts().add(bfr);
			olapResult.getSummaryStatistics().addValue(bfr.getAmount().doubleValue());
		});
		return olapResult;
	}
}