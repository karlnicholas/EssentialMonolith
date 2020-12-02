package essentialmonolith.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import essentialmonolith.model.Department;
import essentialmonolith.model.Employee;
import essentialmonolith.service.HumanResourcesService;

import static org.mockito.Mockito.*;

@WebMvcTest(HumanResourcesController.class)
public class HumanResourcesControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private HumanResourcesService humanResourcesService;
	
	@Test
	public void getEmployeesTest() throws Exception {
		doReturn(Collections.singletonList(Employee.builder().id(0L).name("E")
				.department(Department.builder().id(0L).name("D")
						.build()).build()))
		.when(humanResourcesService).getEmployees();

		mockMvc.perform(get("/hr")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("E")));
	}
}
