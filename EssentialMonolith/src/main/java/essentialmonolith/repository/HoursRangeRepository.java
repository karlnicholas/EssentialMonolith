package essentialmonolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import essentialmonolith.model.HoursRange;

public interface HoursRangeDimensionRepository extends JpaRepository<HoursRange, Long>{

}
