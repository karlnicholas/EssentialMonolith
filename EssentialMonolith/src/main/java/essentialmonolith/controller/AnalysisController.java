package essentialmonolith.controller;

import essentialmonolith.dto.OlapQuery;
import essentialmonolith.dto.OlapResult;
import essentialmonolith.model.AnalysisRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<AnalysisRun> getStartPopulate() {
		AnalysisRun analysisRun = analysisService.getAnalysisRunState();
		if ( !analysisRun.getPopulating() ) {
			analysisService.populate();
			analysisRun.setPopulating(true);
		}
		return ResponseEntity.ok().body(analysisRun);
	}
	@GetMapping("play")
	public ResponseEntity<OlapResult> getPlay() {
//		return ResponseEntity.ok(analysisService.getBillingQueryResultPlay(List.of("project", "employee" ), List.of(new IdPair("employee", 1L), new IdPair("employee", 2L))));
//		return ResponseEntity.ok(analysisService.getBillingQueryResultPlay(null, List.of(new IdPair("employee", 1L))));
		return ResponseEntity.ok(analysisService.getBillingQueryResultPlay(null));
	}
	@PostMapping("olap")
	public ResponseEntity<OlapResult> getPlay(@RequestBody OlapQuery olapQuery) {
		System.out.println("olapQuery: " + olapQuery);
//		return ResponseEntity.ok(analysisService.getBillingQueryResultPlay(List.of("project", "employee" ), List.of(new IdPair("employee", 1L), new IdPair("employee", 2L))));
		return ResponseEntity.ok(analysisService.getBillingQueryResultPlay(olapQuery));
//		return ResponseEntity.ok(analysisService.getBillingQueryResultPlay(null, null));
	}
}
