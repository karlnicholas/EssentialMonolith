package essentialmonolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import essentialmonolith.model.BillingFact;

public interface BillingFactRepository extends JpaRepository<BillingFact, Long>{

}
