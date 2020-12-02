package essentialmonolith.service;

import java.util.List;

import org.springframework.stereotype.Service;

import essentialmonolith.model.WorkLog;
import essentialmonolith.repository.WorkLogRepository;

@Service
public class WorkLogService {
	private final WorkLogRepository workLogRepository;
	public WorkLogService(WorkLogRepository workLogRepository) {
		super();
		this.workLogRepository = workLogRepository;
	}

	public List<WorkLog> getWorkLogs() {
		return workLogRepository.findAll();
	}
}
