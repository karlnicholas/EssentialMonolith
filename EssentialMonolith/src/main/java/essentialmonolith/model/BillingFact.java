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
	@MapsId("weekDimensionId")
	private WeekDimension weekDimension;
	@ManyToOne
	@MapsId("hoursRangeDimensionId")
	private HoursRangeDimension hoursRangeDimension;
	@ManyToOne
	@MapsId("rateRangeDimensionId")
	private RateRangeDimension rateRangeDimension;
}
