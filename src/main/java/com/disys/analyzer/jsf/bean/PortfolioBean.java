package com.disys.analyzer.jsf.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.disys.analyzer.config.util.Constants;
import com.disys.analyzer.config.util.FacesMessageSeverity;
import com.disys.analyzer.config.util.FacesUtils;
import com.disys.analyzer.model.UpdatePortfolio;
import com.disys.analyzer.model.dto.PortfolioDTO;
import com.disys.analyzer.service.PortfolioService;

@SuppressWarnings("deprecation")
@ManagedBean
@ViewScoped
public class PortfolioBean extends SpringBeanAutowiringSupport implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4209133766296292860L;

	private Logger logger = LoggerFactory.getLogger(PortfolioBean.class);
	
	private List<SelectItem> selectedPortfolio;
	
	@Autowired 
	private PortfolioService service;
	
	private String customerId;
	private String verticalCode;
	private String productCode;
	private String geoLocationCode;
	private String mdEmplId;
	private String recruitingTeam;
	private String dealType;
	private String url;
	private String ae1EmplId;
	private String rec1EmplId;
	
	
	private String rawResponse;
	private String statusCode;
	private String callRequestedTime;
	private String responseReceivedTime;
	private String timeTakenInMilliSec;
	private String comments;
	private String portfolio;
	private String portfolioAE1;
	private String portfolioREC1;
	private List<UpdatePortfolio> dataList;
	private List<UpdatePortfolio> filteredList;
	private UpdatePortfolio updatePortFolio;
	private Boolean showUpdateButton;
	private Integer actionType;
	
	public PortfolioBean() 
	{
		System.out.println("PortfolioBean constructor called....");
		logger.info("PortfolioBean constructor called....");
		initilizeDefaultValues();
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		this.customerId =  params.get("customerId");
		this.verticalCode = params.get("verticalCode");
		this.productCode = params.get("productCode");
		this.geoLocationCode = params.get("geoLocationCode");
		this.mdEmplId = params.get("mdEmplId");
		this.recruitingTeam = params.get("recruitingTeam");
		this.dealType = params.get("dealType");
		this.productCode = params.get("productCode");
		this.geoLocationCode = params.get("geoLocationCode");
		this.mdEmplId = params.get("mdEmplId");
		this.url = params.get("url");
		this.ae1EmplId = params.get("ae1EmplId");
		this.rec1EmplId = params.get("rec1EmplId");
		this.mdEmplId = params.get("mdEmplId");
		
		this.rawResponse = params.get("rawResponse");
		this.statusCode = params.get("statusCode");
		this.callRequestedTime = params.get("callRequestedTime");
		this.responseReceivedTime = params.get("responseReceivedTime");
		this.timeTakenInMilliSec = params.get("timeTakenInMilliSec");
		this.comments = params.get("comments");
		this.portfolio = params.get("portfolio");
		this.portfolioAE1 = params.get("portfolioAE1");
		this.portfolioREC1 = params.get("portfolioREC1");
	}
	
	/**
	 * @author Saravanan.Dhurairaj  
	 * getPortfolio is list string method.
	 * It is used to get all portfoiloCode from PortfolioData table
	 * Procedure used dbo.getAllPortfolios
	 * Table used PortfolioData
	 * 
	 */
	public List<SelectItem> getAllPortfolios(String companyCode) 
	{
		if(companyCode == null || companyCode.isEmpty())
		{
			companyCode = Constants.DEFAULT_COMPANY_CODE;			
		}
		if (selectedPortfolio == null) 
		{
			selectedPortfolio = new ArrayList<SelectItem>();
			try
			{
				List<PortfolioDTO> portfolio = service.getAllPortfolios(FacesUtils.getCurrentUserId(), companyCode);

				if (portfolio != null)
				{
					for (PortfolioDTO p:portfolio)
					{
						selectedPortfolio.add(new SelectItem(p.getPortfolioCode(), p.getPortfolioCode().concat(" - ").concat(p.getPortfolioDescription())));
						//selectedPortfolio.add(new SelectItem(p.getPortfolioCode(), p.getPortfolioDescription()));
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("Exception in PortfolioBean --> getPortfolioList.");
				logger.debug("Exception in PortfolioBean --> getPortfolioList.");
				e.printStackTrace();
			}
		}
		return selectedPortfolio;
	}
	
	public void editUpdatePortfolio(UpdatePortfolio updatePortFolio){
		this.showUpdateButton = true;
		this.updatePortFolio = new UpdatePortfolio(updatePortFolio);
		this.actionType = 2;
	}	
	
	public void clearForm(){
		try
		{
			initilizeDefaultValues();
		}
		catch (Exception e)
		{
			displayMessage("Erro in Sick Leave Cost " + e.getMessage(), true);
		}
		
	}	
	public void addUpdateUpdatePortfolioDTO(){
		String msg = null;
		try
		{
			if (validateRecord(updatePortFolio, (actionType == 1)))
			{
			
				String result = service.spAddUpdatePortFolioData(FacesUtils.getCurrentUserId(), this.updatePortFolio,this.actionType);
				if (actionType == 1)
				{
					initilizeDefaultValues();
					msg = result.equalsIgnoreCase("2") ? " Duplicate PortFolio is not Allowed" : 
						  result.equalsIgnoreCase("0") ? " New Portfolio Request Data Successfully saved" : "Cannot Save New PortFolio Request";
				}
				else
				{
					this.dataList = service.getAllPortfolios(FacesUtils.getCurrentUserId());
					msg = result.equalsIgnoreCase("0") ? "Update Portfolio Request Data saved" : "Cannot Update PortFolio Request";
				}
				displayMessage(msg, result.equalsIgnoreCase("1"));
			}
		}
		catch (Exception e)
		{
			msg = actionType == 2 ?  " Duplicate PortFolio is not Allowed" : actionType == 1 ? "Error on save PortFolio Request" : "Error on update PortFolio Request";
			displayMessage(msg, true);
			e.printStackTrace();
		}
	}

	private void initilizeDefaultValues(){
		this.dataList = new ArrayList<UpdatePortfolio>(0);
		this.updatePortFolio = new UpdatePortfolio();
		this.showUpdateButton = false;
		this.actionType = 1;
		this.dataList = service.getAllPortfolios(FacesUtils.getCurrentUserId());
	}	
	
	private void displayMessage(String msg, boolean error){
		FacesUtils.addGlobalMessage(error ? FacesMessageSeverity.ERROR : FacesMessageSeverity.INFO, msg);
	}
	
	private boolean validateRecord(UpdatePortfolio updatePortfolio, boolean addMode){
		if (updatePortfolio.getCompanyCode() == null || updatePortfolio.getCompanyCode().trim().length() == 0)
		{
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, "Please select the Company Name");
			return false;
		}
		if (updatePortfolio.getPortfolioDescription() == null || updatePortfolio.getPortfolioDescription().trim().length() == 0)
		{
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, "Please enter the Portfolio Description");
			return false;
		}
		if (updatePortfolio.getStatus() < 0)
		{
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, "Please enter the Status");
			return false;
		}

		return true;
	}
	
	
	
	/**
	 * @return the selectedState
	 */
	public List<SelectItem> getSelectedPortfolio() {
		return selectedPortfolio;
	}
	/**
	 * @param selectedState the selectedState to set
	 */
	public void setSelectedPortfolio(List<SelectItem> selectedPortfolio) {
		this.selectedPortfolio = selectedPortfolio;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getVerticalCode() {
		return verticalCode;
	}

	public void setVerticalCode(String verticalCode) {
		this.verticalCode = verticalCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getGeoLocationCode() {
		return geoLocationCode;
	}

	public void setGeoLocationCode(String geoLocationCode) {
		this.geoLocationCode = geoLocationCode;
	}

	public String getMdEmplId() {
		return mdEmplId;
	}

	public void setMdEmplId(String mdEmplId) {
		this.mdEmplId = mdEmplId;
	}

	public String getRecruitingTeam() {
		return recruitingTeam;
	}

	public void setRecruitingTeam(String recruitingTeam) {
		this.recruitingTeam = recruitingTeam;
	}

	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAe1EmplId() {
		return ae1EmplId;
	}

	public void setAe1EmplId(String ae1EmplId) {
		this.ae1EmplId = ae1EmplId;
	}

	public String getRec1EmplId() {
		return rec1EmplId;
	}

	public void setRec1EmplId(String rec1EmplId) {
		this.rec1EmplId = rec1EmplId;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public String getRawResponse() {
		return rawResponse;
	}

	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getCallRequestedTime() {
		return callRequestedTime;
	}

	public void setCallRequestedTime(String callRequestedTime) {
		this.callRequestedTime = callRequestedTime;
	}

	public String getResponseReceivedTime() {
		return responseReceivedTime;
	}

	public void setResponseReceivedTime(String responseReceivedTime) {
		this.responseReceivedTime = responseReceivedTime;
	}

	public String getTimeTakenInMilliSec() {
		return timeTakenInMilliSec;
	}

	public void setTimeTakenInMilliSec(String timeTakenInMilliSec) {
		this.timeTakenInMilliSec = timeTakenInMilliSec;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(String portfolio) {
		this.portfolio = portfolio;
	}

	public String getPortfolioAE1() {
		return portfolioAE1;
	}

	public void setPortfolioAE1(String portfolioAE1) {
		this.portfolioAE1 = portfolioAE1;
	}

	public String getPortfolioREC1() {
		return portfolioREC1;
	}

	public void setPortfolioREC1(String portfolioREC1) {
		this.portfolioREC1 = portfolioREC1;
	}	
	
	public List<UpdatePortfolio> getDataList(){
		return dataList;
	}
	
	public void setDataList(List<UpdatePortfolio> dataList){
		this.dataList = dataList;
	}
	
	public List<UpdatePortfolio> getFilteredList(){
		return filteredList;
	}
	
	public void setFilteredList(List<UpdatePortfolio> filteredList){
		this.filteredList = filteredList;
	}
	
	
	public UpdatePortfolio getUpdatePortFolio() {
		return updatePortFolio;
	}

	public void setUpdatePortFolio(UpdatePortfolio updatePortFolio) {
		this.updatePortFolio = updatePortFolio;
	}

	public Boolean getShowUpdateButton(){
		return showUpdateButton;
	}
	
	public void setShowUpdateButton(Boolean showUpdateButton){
		this.showUpdateButton = showUpdateButton;
	}

	public Integer getActionType() {
		return actionType;
	}

	public void setActionType(Integer actionType) {
		this.actionType = actionType;
	}	
}
