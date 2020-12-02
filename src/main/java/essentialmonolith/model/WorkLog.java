package essentialmonolith.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkLog {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
	private LocalDate entryDate;
	private Integer hours;
	private BigDecimal rate;
	@ManyToOne
	private Project project;
	@ManyToOne
	private Employee employee;
}
