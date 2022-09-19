package essentialmonolith.dto;

import essentialmonolith.model.BillingFact;
import essentialmonolith.model.Dimension;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.List;

@Data
@Builder
public class OlapResult {
    private List<BillingFact> facts;
    private SummaryStatistics summaryStatistics;
}
