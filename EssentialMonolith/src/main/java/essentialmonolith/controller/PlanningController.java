package essentialmonolith.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import essentialmonolith.model.WorkLog;
import essentialmonolith.service.PlanningService;

@RestController
@RequestMapping("api/planning")
public class PlanningController {
	private final PlanningService  workLogService;
	public PlanningController(PlanningService workLogService) {
		this.workLogService = workLogService;
	}
	@GetMapping
	public ResponseEntity<List<WorkLog>> getWorkLogs() {
		return ResponseEntity.ok(workLogService.getWorkLogs());
	}
}
