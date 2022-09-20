package essentialmonolith.model;

import java.math.BigDecimal;
import java.util.List;

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
	@MapsId("weekDimensionId")
	private WeekDimension weekDimension;
	@ManyToOne
	@MapsId("hoursRangeDimensionId")
	private HoursRangeDimension hoursRangeDimension;
	@ManyToOne
	@MapsId("rateRangeDimensionId")
	private RateRangeDimension rateRangeDimension;

	public void setDimension(Dimension d) {
		if (d instanceof Project) {
			project = (Project) d;
		} else if (d instanceof Employee) {
			employee = (Employee) d;
		} else if (d instanceof WeekDimension) {
			weekDimension = (WeekDimension) d;
		} else if (d instanceof HoursRangeDimension) {
			hoursRangeDimension = (HoursRangeDimension) d;
		} else if (d instanceof RateRangeDimension) {
			rateRangeDimension = (RateRangeDimension) d;
		}
	}
}
