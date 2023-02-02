package com.disys.analyzer.jsf.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.disys.analyzer.config.util.Constants;
import com.disys.analyzer.config.util.FacesUtils;
import com.disys.analyzer.model.dto.PortfolioDTO;
import com.disys.analyzer.service.PortfolioService;

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
	
	public PortfolioBean() 
	{
		System.out.println("PortfolioBean constructor called....");
		logger.info("PortfolioBean constructor called....");
		
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
		splitRawResponseData();
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
	
	public void splitRawResponseData(){
		this.portfolio = "";
		this.portfolioAE1 = "";
		this.portfolioREC1 ="";
		String defaultPortfolio = FacesUtils.getDefaultPortfolio();
		String errorPortfolio = FacesUtils.getPortfolioDefaultErrorMsg();
		if( getRawResponse() == null || getStatusCode().equalsIgnoreCase("500")){
			portfolio = errorPortfolio;
			portfolioAE1 = errorPortfolio;
			portfolioREC1 = errorPortfolio;
		}
		JSONArray jsonArray = new JSONArray(rawResponse);
		JSONObject myResponse = jsonArray.getJSONObject(0);
		this.portfolio = (!myResponse.has("Portfolio")) || StringUtils.isBlank((String) myResponse.get("Portfolio"))  ? defaultPortfolio : (String) myResponse.get("Portfolio");
		this.portfolioAE1 = (!myResponse.has("AE1Portfolio")) || StringUtils.isBlank((String) myResponse.get("AE1Portfolio")) ? defaultPortfolio : (String) myResponse.get("AE1Portfolio");
		this.portfolioREC1 = (!myResponse.has("Rec1Portfolio")) || StringUtils.isBlank((String) myResponse.get("Rec1Portfolio")) ? defaultPortfolio :(String) myResponse.get("Rec1Portfolio");
		setPortfolio(portfolio);
		setPortfolioAE1(portfolioAE1);
		setPortfolioREC1(portfolioREC1);
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
	
}
