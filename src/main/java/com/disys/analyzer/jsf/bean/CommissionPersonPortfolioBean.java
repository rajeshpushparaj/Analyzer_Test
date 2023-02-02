/**
 * 
 */
package com.disys.analyzer.jsf.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.jsf.FacesContextUtils;

import com.disys.analyzer.config.util.Constants;
import com.disys.analyzer.config.util.FacesMessageSeverity;
import com.disys.analyzer.config.util.FacesUtils;
import com.disys.analyzer.model.AnalyserCommisionPerson;
import com.disys.analyzer.model.CommissionPersonPortfolio;
import com.disys.analyzer.model.dto.PortfolioDTO;
import com.disys.analyzer.model.dto.UserDTO;
import com.disys.analyzer.service.CommissionPersonPortfolioService;
import com.disys.analyzer.service.PortfolioService;

/**
 * @author Sajid
 * @since Dec 2, 2018
 */
@ManagedBean
@ViewScoped
public class CommissionPersonPortfolioBean extends SpringBeanAutowiringSupport implements Serializable
{
	private static final long serialVersionUID = -2369732243308919916L;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private List<CommissionPersonPortfolio> list;
	private List<CommissionPersonPortfolio> filteredList;
	private List<UserDTO> activeUserListPortfolio;
	private AnalyserCommisionPerson selectedPerson;
	private String searchString;
	private String personName;
	private Integer status;
	private String updatedBy;
	
	private String userId;
	private String action;
	private String portfolioCode;
	private String portfolioDescription;
	private List<SelectItem> commisionPersonList;
	private List<SelectItem> portfolioList;
	private CommissionPersonPortfolio commissionPersonPortfolio;
	
	@Autowired 
	private PortfolioService portfolioService;
	
	@Autowired
	private CommissionPersonPortfolioService commissionPersonPortfolioService; 
	
	private String companyCode;
	
	@PostConstruct
	public void init()
	{
		try
		{
			FacesContextUtils.getRequiredWebApplicationContext(FacesContext.getCurrentInstance()).getAutowireCapableBeanFactory().autowireBean(this);
		}
		catch (Exception ex)
		{
			logger.debug(ex.getMessage());
		}
	}
	
	public CommissionPersonPortfolioBean()
	{
		logger.debug("In CommissionPersonPortfolioBean()");
		this.companyCode = Constants.DEFAULT_COMPANY_CODE;
		
		FacesContext fc = FacesUtils.getFacesContext();
		ExternalContext externalContext = fc.getExternalContext();
		Map<String, String> requestMap = externalContext.getRequestParameterMap();
		
		requestMap.entrySet().forEach(entry ->
		{
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			if (entry.getKey().equals("userId"))
			{
				userId = entry.getValue();
			}
			else if (entry.getKey().equals("action"))
			{
				action = entry.getValue();
			}
		});
		
		if(userId != null){
			//get selected person from DB
			commissionPersonPortfolio = commissionPersonPortfolioService.getModifyCommissionPersonPortfolio(FacesUtils.getCurrentUserId(), userId);
			System.out.println("commissionPersonPortfolio.getPersonName() "+commissionPersonPortfolio.getPersonName());
			System.out.println("commissionPersonPortfolio.getPortfolioCode() "+commissionPersonPortfolio.getPortfolioCode());
			if(commissionPersonPortfolio != null){
				System.out.println("Selected person name is : "+commissionPersonPortfolio.getPersonName());
			}
		} else {			
			commissionPersonPortfolio = new CommissionPersonPortfolio();
			list = new ArrayList<CommissionPersonPortfolio>();	
			eraseFilter();
		}
		filter();
	}
	
	public List<UserDTO> initActiveUsersPortfolioList()	//To remove extra procedure call from Analyzer page
	{
		if(companyCode == null || companyCode.isEmpty())
		{
			companyCode = Constants.DEFAULT_COMPANY_CODE;
		}
		activeUserListPortfolio = commissionPersonPortfolioService.spGetActiveUsersPortfolio(FacesUtils.getCurrentUserId(), companyCode);
		System.out.println("CommissionPersonPortfolioBean Active users list Portfolio size is : "+activeUserListPortfolio.size());
		return activeUserListPortfolio;	//To remove extra procedure call from Analyzer page
	}
	public void save(ActionEvent actionEvent)
	{
		logger.debug("Commission Person Bean Portfolio Save Action.....");
		System.out.println("Commission Person Bean Portfolio Save Action.....");
		try
		{
			if(commissionPersonPortfolio.getPersonName() == null || commissionPersonPortfolio.getPersonName().trim().length() == 0)
			{
				FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, "Select Person Name to save");
				return;
			}
			
			if(commissionPersonPortfolio.getPortfolioCode() == null || commissionPersonPortfolio.getPortfolioCode().trim().length() == 0)
			{
				FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, "Select PortfolioCode to save");
				return;
			}
			String portfolioCodeDesc = commissionPersonPortfolio.getPortfolioCode();
			String[] arrOfStr = portfolioCodeDesc.split(":");
			portfolioCode = arrOfStr[0].toString();
			portfolioDescription = arrOfStr[1].toString();
			System.out.println("portfolioCode : " + portfolioCode);
			System.out.println("portfolioDescription : " + portfolioDescription);
	  
			int actionType = 1;
			
			String result = commissionPersonPortfolioService.addUpdateAnalyserCommissionPersonPortfolio(commissionPersonPortfolio.getPersonName(), "", FacesUtils.getCurrentUserId(), portfolioCode, portfolioDescription, actionType);
			System.out.println("Result is : " + result);
			String infoMessage="";
			
			if (result.equals("0"))
			{
				infoMessage = "Commission person portfolio was successfully created";
				initActiveUsersPortfolioList();
				this.commissionPersonPortfolio = new CommissionPersonPortfolio();
				this.getAllPortfolios(Constants.DEFAULT_COMPANY_CODE);				
				System.out.println(infoMessage);
			}
			else if (result.equals("1"))
			{
				infoMessage = "Could not create or modify Commission person portfolio -- General error ";
				System.out.println(infoMessage);
			}
			else if (result.equals("2"))
			{
				infoMessage = "Could not create Analyser -- Commission person portfolio already exist";
				System.out.println(infoMessage);
			}
			else if (result.equals("3"))
			{
				infoMessage = "Could not delete or modify -- Commission person portfolio exists ";
				System.out.println(infoMessage);
			}
			
			FacesUtils.addGlobalMessage(FacesMessageSeverity.INFO, infoMessage);
			this.filter();
		}
		catch (Exception e)
		{
			String errorMessage = "Error saving commission person portfolio";
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, errorMessage);
			logger.error(errorMessage);
		}
	}
	public void eraseFilter() {		
		this.companyCode = "DISYS";
		this.searchString = "ALL";	
		this.filter();
	}
	
	public void filter()
	{
		list = new ArrayList<CommissionPersonPortfolio>();
		
		String userId = FacesUtils.getCurrentUserId();
		String userList = "0";
		String orderBy = "PersonName";
		try
		{
			if (searchString == null || searchString.equals(""))
			{
				searchString = "ALL";
			}
		}
		catch (NullPointerException ex)
		{
			searchString = "ALL";
		}
		if(companyCode == null || companyCode.isEmpty())
		{
			companyCode = Constants.DEFAULT_COMPANY_CODE;
		}
		list = commissionPersonPortfolioService.getCommissionUsersPortfolioList(userId, userList, orderBy, searchString, companyCode);
		logger.info("Total list size is : " + list.size());
	}
	
	
	/**
	 * @return the list
	 */
	public List<CommissionPersonPortfolio> getList()
	{
		return list;
	}
	
	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<CommissionPersonPortfolio> list)
	{
		this.list = list;
	}
	
	/**
	 * @return the searchString
	 */
	public String getSearchString()
	{
		return searchString;
	}
	
	/**
	 * @param searchString
	 *            the searchString to set
	 */
	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
	}
	
	/**
	 * @return the selectedPerson
	 */
	public AnalyserCommisionPerson getSelectedPerson()
	{
		return selectedPerson;
	}
	
	/**
	 * @param selectedPerson
	 *            the selectedPerson to set
	 */
	public void setSelectedPerson(AnalyserCommisionPerson selectedPerson)
	{
		this.selectedPerson = selectedPerson;
	}	
	
	/**
	 * @return the filteredList
	 */
	public List<CommissionPersonPortfolio> getFilteredList()
	{
		return filteredList;
	}
	
	/**
	 * @param filteredList
	 *            the filteredList to set
	 */
	public void setFilteredList(List<CommissionPersonPortfolio> filteredList)
	{
		this.filteredList = filteredList;
	}
	
	public String getPersonName()
	{
		return personName;
	}
	
	public void setPersonName(String personName)
	{
		this.personName = personName;
	}
	
	public Integer getStatus()
	{
		return status;
	}
	
	public void setStatus(Integer status)
	{
		this.status = status;
	}
	
	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy()
	{
		return updatedBy;
	}
	
	/**
	 * @param updatedBy
	 *            the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	public List<UserDTO> getActiveUserListPortfolio() {
		return activeUserListPortfolio;
	}

	public void setActiveUserListPortfolio(List<UserDTO> activeUserListPortfolio) {
		this.activeUserListPortfolio = activeUserListPortfolio;
	}

	/**
	 * @return the userId
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	/**
	 * @return the action
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action)
	{
		this.action = action;
	}
	
	public List<SelectItem> getCommisionPersonList() {
		return commisionPersonList;
	}

	public void setCommisionPersonList(List<SelectItem> commisionPersonList) {
		this.commisionPersonList = commisionPersonList;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}	
	
	public String getPortfolioCode() {
		return portfolioCode;
	}

	public void setPortfolioCode(String portfolioCode) {
		this.portfolioCode = portfolioCode;
	}	

	public String getPortfolioDescription() {
		return portfolioDescription;
	}

	public void setPortfolioDescription(String portfolioDescription) {
		this.portfolioDescription = portfolioDescription;
	}

	public void onCompanyCodeChange(javax.faces.event.AjaxBehaviorEvent event) {
	}
	public List<SelectItem> getAllPortfolios(String companyCode) 
	{		
		portfolioList = new ArrayList<SelectItem>();
		if (!companyCode.equalsIgnoreCase(Constants.STRING_CONSTANT_ALL)) 
		{
			try
			{
				List<PortfolioDTO> portfolioLists = portfolioService.getAllPortfolios(FacesUtils.getCurrentUserId(), companyCode);
				if (portfolioLists != null)
				{
					for (PortfolioDTO p:portfolioLists)
					{						
						portfolioList.add(new SelectItem(p.getPortfolioCode().concat(":").concat(p.getPortfolioDescription()), p.getPortfolioCode().concat(" - ").concat(p.getPortfolioDescription())));
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("Exception in CommissionPersonPortfolioBean --> getAllPortfoliosList.");
				logger.debug("Exception in CommissionPersonPortfolioBean --> getAllPortfoliosList.");
				e.printStackTrace();
			}
		}
		return portfolioList;
			
	}
	
	/**
	 * @return the portfolioList
	 */
	
	public List<SelectItem> getPortfolioList() {
		return portfolioList;
	}
	/**
	 * @param portfolioList the portfolioList to set
	 */
	public void setPortfolioList(List<SelectItem> portfolioList) {
		this.portfolioList = portfolioList;
	}

	public CommissionPersonPortfolio getCommissionPersonPortfolio() {
		return commissionPersonPortfolio;
	}

	public void setCommissionPersonPortfolio(CommissionPersonPortfolio commissionPersonPortfolio) {
		this.commissionPersonPortfolio = commissionPersonPortfolio;
	}

	public void changeStatus()
	{
		
		FacesContext fc = FacesUtils.getFacesContext();
		ExternalContext externalContext = fc.getExternalContext();
		Map<String, String> requestMap = externalContext.getRequestParameterMap();
		requestMap.entrySet().forEach(entry ->
		{
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			if (entry.getKey().equals("personName"))
			{
				personName = entry.getValue();
			}
			if (entry.getKey().equals("status"))
			{
				status = Integer.valueOf(entry.getValue());
			}
			if (entry.getKey().equals("updatedBy"))
			{
				updatedBy = entry.getValue();
			}
		});

		if (status == 1)
		{
			status = 0;
		}
		else
		{
			status = 1;
		}
		
		String result = commissionPersonPortfolioService.changeCommissionPersonPortfolioStatus(FacesUtils.getCurrentUserId(), updatedBy, personName, status);
		System.out.println("Status Updated..." + result);
		logger.debug("Change status Commission Person Portfolio Bean");
		System.out.println("Change status Commission Person Portfolio Bean");
		// write a procedure to update status
		this.filter();
	}
	
	public void update()
	{
		
		if(commissionPersonPortfolio.getPortfolioCode() == null || commissionPersonPortfolio.getPortfolioCode().trim().length() == 0)
		{
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, "Select portfolioCode to update");
			return;
		}
		String portfolioCodeDesc = commissionPersonPortfolio.getPortfolioCode();
		String[] arrOfStr = portfolioCodeDesc.split(":");
		portfolioCode = arrOfStr[0].toString();
		portfolioDescription = arrOfStr[1].toString();
		System.out.println("portfolioDescription Update: " + portfolioDescription);
		System.out.println("portfolioCode Update: " + portfolioCode);
		
		Boolean result = commissionPersonPortfolioService.updateCommissionPersonPortfolio(FacesUtils.getCurrentUserId(), commissionPersonPortfolio.getId().getUserId(), commissionPersonPortfolio.getPersonName(), portfolioCode, portfolioDescription);
		System.out.println("Status Updated..." + result);
		logger.debug("Update Commission Person Portfolio Bean");
		System.out.println("Update Commission Person Portfolio Bean");
		FacesMessage message = null;
		if (result)
		{
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Commission Person Portfolio updated", "Operation is successfully completed!");
		}
		else
		{
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Couldn't update", "Operation not successfully!");
		}
			
		FacesUtils.redirect("/protected/commission-person-portfolio.xhtml", message);
		// write a procedure to update status
		this.filter();
	}
	
	public void discardChanges()
	{
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Commission Person Portfolio changes discarded", "Changes discarded!");
		FacesUtils.redirect("/protected/commission-person-portfolio.xhtml", message);
	}
	
	public void clearInputForm()
	{
		commissionPersonPortfolio = new CommissionPersonPortfolio();
	}
	
}
