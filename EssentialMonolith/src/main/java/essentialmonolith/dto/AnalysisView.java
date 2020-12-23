package essentialmonolith.dto;

import java.util.List;

import essentialmonolith.model.AnalysisRun;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisView {
	private List<AnalysisDimension> analysisDimensions;
	private AnalysisRun analysisRun;
	private Long factCount;
}
