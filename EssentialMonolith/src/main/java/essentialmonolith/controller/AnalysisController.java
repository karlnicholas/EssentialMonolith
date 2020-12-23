package essentialmonolith.controller;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import essentialmonolith.dto.AnalysisDimension;
import essentialmonolith.model.AnalysisRun;
import essentialmonolith.service.AnalysisService;

@RestController
@RequestMapping("analysis")
public class AnalysisController {
	@Autowired
	private AnalysisService analysisService;
	@GetMapping
	public ResponseEntity<AnalysisRun> getSaless() {
		return ResponseEntity.ok(analysisService.getAnalysisRun());
	}
	@GetMapping("count")
	public ResponseEntity<Long> getFactCount() {
		return ResponseEntity.ok(analysisService.getFactCount());
	}
	@GetMapping("billingdimensions")
	public ResponseEntity<List<AnalysisDimension>> getBillingDimensions() {
		return ResponseEntity.ok(analysisService.getBillingDimensions());
	}
	@GetMapping("billingresult")
	public ResponseEntity<SummaryStatistics> getPurchaseResult(
			@RequestParam(name = "Project", required = false) Long project, 
			@RequestParam(name = "Employee", required = false) Long employee, 
			@RequestParam(name = "Week", required = false) Long week, 
			@RequestParam(name = "HoursRange", required = false) Long hoursRange, 
			@RequestParam(name = "RateRange", required = false) Long rateRange
	) {
		return ResponseEntity.ok(analysisService.getBillingQueryResult(project, employee, week, hoursRange, rateRange));
	}
	@GetMapping("populate")
	public ResponseEntity<Void> getStartPopulate() {
		if ( analysisService.startPopulate() ) {
			analysisService.populate();
		}
		return ResponseEntity.ok().build();
	}
}
