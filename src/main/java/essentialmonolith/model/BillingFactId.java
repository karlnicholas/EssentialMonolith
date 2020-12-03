package essentialmonolith.model;

import java.io.Serializable;

import javax.persistence.*;

import lombok.*;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingFactId implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long projectId;
	private Long employeeId;
	private Long weekDimensionId;
	private Long hoursRangeDimensionId;
	private Long rateRangeDimensionId;
}
