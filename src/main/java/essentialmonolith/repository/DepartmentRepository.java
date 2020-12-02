package essentialmonolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import essentialmonolith.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
