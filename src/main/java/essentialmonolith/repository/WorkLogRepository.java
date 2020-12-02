package essentialmonolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import essentialmonolith.model.WorkLog;

public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {

}
