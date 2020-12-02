package essentialmonolith.model;

import javax.persistence.*;

import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Department {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
	private String name;
}
