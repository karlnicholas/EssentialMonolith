package essentialmonolith.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import essentialmonolith.model.Employee;
import essentialmonolith.model.Project;
import essentialmonolith.model.WorkLog;
import essentialmonolith.service.PlanningService;

import static org.mockito.Mockito.*;

@WebMvcTest(PlanningController.class)
public class PlanningControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private PlanningService planningService;
	
	@Test
	public void getWorkLogsTest() throws Exception {
		doReturn(Collections.singletonList(				WorkLog.builder()
				.id(0L)
				.entryDate(LocalDate.parse("2020-01-01"))
				.hours(1)
				.rate(BigDecimal.TEN)
				.employee(Employee.builder().id(0L).name("E").build())
				.project(Project.builder().id(0L).name("P").build())
				.build()))
		.when(planningService).getWorkLogs();

		mockMvc.perform(get("/api/planning")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("E")));
	}
}
