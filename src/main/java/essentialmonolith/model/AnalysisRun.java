package essentialmonolith.model;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisRun {
	@Id private Long id;
	private LocalDateTime lastRunTime;
	private Boolean populating;
}
