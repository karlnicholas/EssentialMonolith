package essentialmonolith.model;

import java.math.BigDecimal;

import javax.persistence.*;

import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingFact {
	@EmbeddedId private BillingFactId billingFactId;
	private BigDecimal amount;
	@ManyToOne
	@MapsId("projectId")
	private Project project;
	@ManyToOne
	@MapsId("employeeId")
	private Employee employee;
	@ManyToOne
	@MapsId("weekId")
	private Week week;
	@ManyToOne
	@MapsId("hoursRangeId")
	private HoursRange hoursRange;
	@ManyToOne
	@MapsId("rateRangeId")
	private RateRange rateRange;

	public void setDimension(Dimension d) {
		if (d instanceof Project) {
			project = (Project) d;
		} else if (d instanceof Employee) {
			employee = (Employee) d;
		} else if (d instanceof Week) {
			week = (Week) d;
		} else if (d instanceof HoursRange) {
			hoursRange = (HoursRange) d;
		} else if (d instanceof RateRange) {
			rateRange = (RateRange) d;
		}
	}
}
