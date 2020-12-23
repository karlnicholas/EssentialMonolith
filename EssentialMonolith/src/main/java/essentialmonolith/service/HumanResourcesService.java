package essentialmonolith.service;

import java.util.List;

import org.springframework.stereotype.Service;

import essentialmonolith.model.Employee;
import essentialmonolith.repository.EmployeeRepository;

@Service
public class HumanResourcesService {
	private final EmployeeRepository employeeRepository;
	public HumanResourcesService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}
	public List<Employee> getEmployees() {
		return employeeRepository.findAll();
	}
}
