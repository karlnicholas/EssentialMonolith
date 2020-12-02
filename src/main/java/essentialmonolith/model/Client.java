package essentialmonolith.model;

import javax.persistence.*;

import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
	private String name;
}
