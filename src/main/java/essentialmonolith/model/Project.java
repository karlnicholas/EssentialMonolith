package essentialmonolith.model;

import javax.persistence.*;

import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Project {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
	private String name;
	@ManyToOne
	private Client client;
}
