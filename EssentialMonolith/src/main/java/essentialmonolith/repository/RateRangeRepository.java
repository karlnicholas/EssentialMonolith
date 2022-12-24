package essentialmonolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import essentialmonolith.model.RateRange;

public interface RateRangeDimensionRepository extends JpaRepository<RateRange, Long>{

}
