/**
 * 
 */
package com.disys.analyzer.service.implementation;

import java.io.Serializable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.disys.analyzer.model.CommissionPersonPortfolio;
import com.disys.analyzer.model.dto.UserDTO;
import com.disys.analyzer.repository.CommissionPersonPortfolioRepository;
import com.disys.analyzer.service.CommissionPersonPortfolioService;

/**
 * @author Sajid
 * 
 */
// @Service(value = "commisionPersonService")
@Service
@Transactional
public class CommissionPersonPortfolioServiceImpl implements CommissionPersonPortfolioService, Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired private CommissionPersonPortfolioRepository commissionPersonPortfolioRepository;
	
	@Override
	public String addUpdateAnalyserCommissionPersonPortfolio(String commPerson, String commPersonOld, String userId, String portfolioCode, String portfolioDescription, int actionType)
	{
		return commissionPersonPortfolioRepository.addUpdateAnalyserCommissionPersonPortfolio(commPerson, commPersonOld, userId, portfolioCode, portfolioDescription, actionType);
	}
	
	@Override
	public List<CommissionPersonPortfolio> getCommissionUsersPortfolioList(String userId, String userList, String orderBy, String searchString, String companyCode)
	{
		return commissionPersonPortfolioRepository.getCommissionUsersPortfolioList(userId, userList, orderBy, searchString, companyCode);
	}
	
	@Override
	public CommissionPersonPortfolio getModifyCommissionPersonPortfolio(String userId, String commissionPersonId)
	{
		return commissionPersonPortfolioRepository.getModifyCommissionPersonPortfolio(userId, commissionPersonId);
	}
	
	@Override
	public String changeCommissionPersonPortfolioStatus(String userId, String updatedBy, String personName, Integer status)
	{
		return commissionPersonPortfolioRepository.changeCommissionPersonPortfolioStatus(userId, updatedBy, personName, status);
	}
	
	@Override
	public Boolean updateCommissionPersonPortfolio(String userId, String updatedBy, String personName, String portfolioCode, String portfolioDescription)
	{
		return commissionPersonPortfolioRepository.updateCommissionPersonPortfolio(userId, updatedBy, personName, portfolioCode, portfolioDescription);
	}
	
	@Override
	public List<UserDTO> spGetActiveUsersPortfolio(String userId, String companyCode)
	{
		return commissionPersonPortfolioRepository.spGetActiveUsersPortfolio(userId, companyCode);
	}
}
