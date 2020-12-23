package essentialmonolith.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import essentialmonolith.model.Employee;
import essentialmonolith.service.HumanResourcesService;

@RestController
@RequestMapping("api/hr")
public class HumanResourcesController {
	private final HumanResourcesService  humanResourcesService;
	public HumanResourcesController(HumanResourcesService humanResourcesService) {
		this.humanResourcesService = humanResourcesService;
	}
	@GetMapping
	public ResponseEntity<List<Employee>> getEmployees() {
		return ResponseEntity.ok(humanResourcesService.getEmployees());
	}
}
