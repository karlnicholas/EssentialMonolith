package essentialmonolith.service;

import java.util.List;

import org.springframework.stereotype.Service;

import essentialmonolith.model.WorkLog;
import essentialmonolith.repository.WorkLogRepository;

@Service
public class PlanningService {
	private final WorkLogRepository workLogRepository;
	public PlanningService(WorkLogRepository workLogRepository) {
		this.workLogRepository = workLogRepository;
	}

	public List<WorkLog> getWorkLogs() {
		return workLogRepository.findAll();
	}
}
