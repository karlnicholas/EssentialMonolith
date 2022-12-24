package essentialmonolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import essentialmonolith.model.Week;

public interface WeekDimensionRepository extends JpaRepository<Week, Long>{

}
