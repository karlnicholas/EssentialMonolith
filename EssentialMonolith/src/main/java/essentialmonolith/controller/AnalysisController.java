package essentialmonolith.controller;

import essentialmonolith.dto.OlapResult;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import essentialmonolith.dto.AnalysisView;
import essentialmonolith.service.AnalysisService;

@RestController
@RequestMapping("api/analysis")
public class AnalysisController {
	@Autowired
	private AnalysisService analysisService;
	@GetMapping
	public ResponseEntity<AnalysisView> getAnalysisView() {
		return ResponseEntity.ok(analysisService.getAnalysisView());
	}
//	@GetMapping("count")
//	public ResponseEntity<Long> getFactCount() {
//		return ResponseEntity.ok(analysisService.getFactCount());
//	}
//	@GetMapping("billingdimensions")
//	public ResponseEntity<List<AnalysisDimension>> getBillingDimensions() {
//		return ResponseEntity.ok(analysisService.getBillingDimensions());
//	}
	@GetMapping("billingresult")
	public ResponseEntity<OlapResult> getBillingResult(
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
