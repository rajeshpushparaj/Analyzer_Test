/**
 * 
 */
package com.disys.analyzer.model.dto;

import java.io.Serializable;

/**
 * @author Reshma Rupanagudi
 */
public class PortfolioDownstreamResponseDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1029331799366053787L;
	
	private String rawResponse;
	private Integer statusCode;
	private String callRequestedTime;
	private String responseReceivedTime;
	private String timeTakenInMilliSec;
	private String comments;
	private String portfolio;
	private String portfolioAE1;
	private String portfolioREC1;
	
	public String getRawResponse() {
		return rawResponse;
	}



	public void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
	}



	public Integer getStatusCode() {
		return statusCode;
	}



	public void setStatusCode(Integer statusCode) {
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



	@Override
	public String toString()
	{
		return "PortfolioDownstreamResponseDTO [rawResponse=" + rawResponse + ", statusCode=" + statusCode + ", callRequestedTime=" + callRequestedTime 
				+ ", responseReceivedTime=" + responseReceivedTime + ", timeTakenInMilliSec=" + timeTakenInMilliSec +", comments=" + comments 
				+ ", portfolio=" + portfolio + ", portfolioAE1=" + portfolioAE1 +", portfolioREC1=" + portfolioREC1 +"]";
	}
}
