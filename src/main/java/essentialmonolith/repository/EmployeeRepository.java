package essentialmonolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import essentialmonolith.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
