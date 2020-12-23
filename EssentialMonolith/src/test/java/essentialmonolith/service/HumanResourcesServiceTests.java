package essentialmonolith.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import essentialmonolith.model.Employee;
import essentialmonolith.repository.EmployeeRepository;

import static org.mockito.Mockito.*;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class HumanResaourcesServiceTests {
	@Mock EmployeeRepository employeeRepository;
	
	@InjectMocks HumanResourcesService humanResourcesService;
	
	@BeforeAll
	public void beforeAll() {
		doReturn(Collections.singletonList(Employee.builder().id(0L).name("E").build())).when(employeeRepository).findAll();
	}
	
	@Test
	public void getEmployeesTest() {
		List<Employee> employees = humanResourcesService.getEmployees();
		assertEquals(1, employees.size());
	}

}
