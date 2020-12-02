package essentialmonolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import essentialmonolith.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

}
