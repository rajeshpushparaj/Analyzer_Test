package com.disys.analyzer.service;

import java.util.List;

import com.disys.analyzer.model.dto.PortfolioDTO;
import com.disys.analyzer.model.UpdatePortfolio;

public interface PortfolioService {

	public List<PortfolioDTO> getAllPortfolios(String userId, String companyCode);
	public List<UpdatePortfolio> getAllPortfolios(String userId);
	public String spAddUpdatePortFolioData (String userId, UpdatePortfolio updatePortfolio, Integer actionType);
}
