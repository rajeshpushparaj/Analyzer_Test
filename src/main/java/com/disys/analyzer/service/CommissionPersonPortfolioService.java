/**
 * 
 */
package com.disys.analyzer.service;

import java.util.List;
import com.disys.analyzer.model.CommissionPersonPortfolio;
import com.disys.analyzer.model.dto.UserDTO;

/**
 * @author Sajid
 * 
 */
public interface CommissionPersonPortfolioService
{
	public String addUpdateAnalyserCommissionPersonPortfolio(String commPerson, String commPersonOld, String userId, String portfolioCode, String portfolioDescription, int actionType);
	
	public List<CommissionPersonPortfolio> getCommissionUsersPortfolioList(String userId, String userList, String orderBy, String searchString, String companyCode);
	
	public CommissionPersonPortfolio getModifyCommissionPersonPortfolio(String userId, String commissionPersonId);
	
	public String changeCommissionPersonPortfolioStatus(String userId, String updatedBy, String personName, Integer status);
	
	public Boolean updateCommissionPersonPortfolio(String userId, String updatedBy, String personName, String portfolioCode, String portfolioDescription);
	
	public List<UserDTO> spGetActiveUsersPortfolio(String userId, String companyCode);
}
