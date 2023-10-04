package essentialmonolith.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import essentialmonolith.dto.OlapResult;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import essentialmonolith.dto.AnalysisDimension;
import essentialmonolith.dto.AnalysisView;
import essentialmonolith.model.AnalysisRun;
import essentialmonolith.model.Dimension;
import essentialmonolith.service.AnalysisService;

import static org.mockito.Mockito.*;

@WebMvcTest(AnalysisController.class)
public class AnalysisControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private AnalysisService analysisService;
	
	private final String analysisResponse = "{\"analysisDimensions\":[{\"name\":\"AD\",\"dimensions\":[{\"id\":0,\"name\":\"D\"}]}],\"analysisRun\":{\"id\":1,\"lastRunTime\":\"2020-01-01T00:00:00\",\"populating\":false},\"factCount\":1}\r\n";
	
	@Test
	public void getBillingDimensions() throws Exception {
		AnalysisView analysisView = AnalysisView.builder()
				.factCount(1L)
				.analysisDimensions(
						Collections.singletonList(AnalysisDimension.builder().name("AD").dimensions(Collections.singletonList(Dimension.builder().id(0L).name("D").build())).build()))
				.analysisRun(AnalysisRun.builder().id(1L).lastRunTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0)).populating(false).build())
				.build();
		doReturn(analysisView).when(analysisService).getAnalysisView();

		mockMvc.perform(get("/api/analysis")).andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().json(analysisResponse));
	}
	@Test
	public void getQueryResult() throws Exception {
		SummaryStatistics summaryStatistics = new SummaryStatistics();
		summaryStatistics.addValue(10.0);
		OlapResult olapResult = OlapResult.builder().summaryStatistics(summaryStatistics).build();
		doReturn(olapResult).when(analysisService).getBillingQueryResult(null, null, null, null, null);

		mockMvc.perform(get("/api/analysis/billingresult")).andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("10.0")));
	}
	@Test
	public void getStartPopulate() throws Exception {
		doReturn(CompletableFuture.completedFuture(null)).when(analysisService).populate();

		AnalysisRun analysisRun = new AnalysisRun();
		analysisRun.setLastRunTime(LocalDateTime.now());
		analysisRun.setPopulating(Boolean.TRUE);
		doReturn(analysisRun).when(analysisService).getAnalysisRunState();

		mockMvc.perform(get("/api/analysis/populate")).andDo(print())
		.andExpect(status().isOk());
	}
}
