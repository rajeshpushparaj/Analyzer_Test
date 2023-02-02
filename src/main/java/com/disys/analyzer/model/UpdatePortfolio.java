/**
 * 
 */
package com.disys.analyzer.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Sajid
 * @
 */
@Entity
//@Table(schema="Analyser.dbo")
@Table(name="portfoliodata")//,schema="Analyser.dbo")
public class UpdatePortfolio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3286164223994095623L;

	@Id
	@Column(name = "PortfolioId")
	private Integer portFolioId;

	@Column(name = "portfolioCode")
	private String portfolioCode;
	
	@Column(name = "portfolioDescription")
	private String  portfolioDescription;
	
	@Column(name = "Status")
	private Integer status;
	
	@Column(name = "companyCode")
	private String companyCode;
	
	public UpdatePortfolio()
	{
		// TODO Auto-generated constructor stub
	}
	
	
	public UpdatePortfolio(UpdatePortfolio updatePortfolio)
	{
		this.portFolioId = updatePortfolio.getPortFolioId();
		this.portfolioCode = updatePortfolio.getPortfolioCode();
		this.portfolioDescription = updatePortfolio.getPortfolioDescription();
		this.status = updatePortfolio.getStatus();
		this.companyCode = updatePortfolio.getCompanyCode();
	}

	

	public Integer getPortFolioId() {
		return portFolioId;
	}


	public void setPortFolioId(Integer portFolioId) {
		this.portFolioId = portFolioId;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	
}
