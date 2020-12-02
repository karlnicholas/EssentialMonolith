package essentialmonolith.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import essentialmonolith.model.Employee;
import essentialmonolith.model.Project;
import essentialmonolith.model.WorkLog;
import essentialmonolith.repository.WorkLogRepository;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class PlanningServiceTests {
	@Mock WorkLogRepository workLogRepository;
	
	@InjectMocks PlanningService planningService;
	
	@BeforeAll
	public void beforeAll() {
		Mockito.doReturn(Collections.singletonList(
				WorkLog.builder()
				.id(0L)
				.entryDate(LocalDate.parse("2020-01-01"))
				.hours(1)
				.rate(BigDecimal.TEN)
				.employee(Employee.builder().id(0L).name("E").build())
				.project(Project.builder().id(0L).name("P").build())
				.build())).when(workLogRepository).findAll();
	}
	
	@Test
	public void getWorkLogsTest() {
		List<WorkLog> workLogs = planningService.getWorkLogs();
		assertEquals(1, workLogs.size());
	}

}
