package essentialmonolith.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import essentialmonolith.model.WorkLog;
import essentialmonolith.service.WorkLogService;

@RestController
@RequestMapping("worklog")
public class WorkLogController {
	private final WorkLogService  workLogService;
	public WorkLogController(WorkLogService workLogService) {
		this.workLogService = workLogService;
	}
	@GetMapping
	public ResponseEntity<List<WorkLog>> getWorkLogs() {
		return ResponseEntity.ok(workLogService.getWorkLogs());
	}
}
