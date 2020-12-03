package essentialmonolith.dto;

import java.util.List;

import essentialmonolith.model.Dimension;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisDimension {
	private String name;
	private List<Dimension> dimensions;
}
