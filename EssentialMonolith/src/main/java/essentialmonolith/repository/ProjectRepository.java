package essentialmonolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import essentialmonolith.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
