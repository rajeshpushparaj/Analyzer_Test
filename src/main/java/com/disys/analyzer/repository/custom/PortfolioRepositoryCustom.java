package com.disys.analyzer.repository.custom;

import java.util.List;

import com.disys.analyzer.model.dto.PortfolioDTO;
import com.disys.analyzer.model.UpdatePortfolio;

public interface PortfolioRepositoryCustom {

	public List<PortfolioDTO> getAllPortfolios(String userId, String companyCode);

	List<UpdatePortfolio> getAllPortfolios(String userId);
	
	public String spAddUpdatePortFolioData (String userId, UpdatePortfolio updatePortfolio,  Integer actionType);
}
