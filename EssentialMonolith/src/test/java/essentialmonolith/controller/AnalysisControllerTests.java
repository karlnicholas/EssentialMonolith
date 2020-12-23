package essentialmonolith.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import essentialmonolith.dto.AnalysisDimension;
import essentialmonolith.model.Dimension;
import essentialmonolith.service.AnalysisService;

import static org.mockito.Mockito.*;

@WebMvcTest(AnalysisController.class)
public class AnalysisControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private AnalysisService analysisService;
	
	@Test
	public void getCountTest() throws Exception {
		doReturn(1L).when(analysisService).getFactCount();

		mockMvc.perform(get("/analysis/count")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("1")));
	}
	@Test
	public void getBillingDimensions() throws Exception {
		AnalysisDimension analysisDimension = AnalysisDimension.builder().name("AD").dimensions(Collections.singletonList(Dimension.builder().id(0L).name("D").build())).build();
		doReturn(Collections.singletonList(analysisDimension)).when(analysisService).getBillingDimensions();

		mockMvc.perform(get("/analysis/billingdimensions")).andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("AD")));
	}
	@Test
	public void getPurchaseResult() throws Exception {
		SummaryStatistics summaryStatistics = new SummaryStatistics();
		summaryStatistics.addValue(10.0);
		doReturn(summaryStatistics).when(analysisService).getBillingQueryResult(null, null, null, null, null);

		mockMvc.perform(get("/analysis/billingresult")).andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("10.0")));
	}
	@Test
	public void getStartPopulate() throws Exception {
		doReturn(Boolean.TRUE).when(analysisService).startPopulate();

		mockMvc.perform(get("/analysis/populate")).andDo(print())
		.andExpect(status().isOk());
	}
}
