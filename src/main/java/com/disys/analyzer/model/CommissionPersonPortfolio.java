package com.disys.analyzer.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the Commission_Persons_Portfolio database table.
 * 
 */
@Entity
@Table(name = "Commission_Persons_Portfolio")//,schema="Analyser.dbo")
public class CommissionPersonPortfolio implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CommissionPersonPortfolioPK id;
	
	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy=GenerationType.AUTO)
	 * 
	 * @Column(name="CommissionPersonPortfolioId") private int
	 * commissionPersonPortfolioId;
	 */
	@Column(name = "PortfolioCode")
	private String portfolioCode;

	@Column(name = "PersonName")
	private String personName;

	@Column(name = "Updated_By")
	private String updatedBy;

	@Column(name = "Updated_Date")
	private Timestamp updatedDate;

	@Column(name = "PortfolioDescription")
	private String portfolioDescription;

	@Column(name = "Status")
	private Integer status;		
	
	@Column(name = "CompanyCode")
	private String companyCode;
	
	@Transient
	private String adpCode;
	
	public CommissionPersonPortfolio() {
	}
	public CommissionPersonPortfolio(CommissionPersonPortfolio commissionPersonPortfolio)
	{
		this.adpCode = commissionPersonPortfolio.getAdpCode();
		this.personName = commissionPersonPortfolio.getPersonName();
		this.portfolioCode=commissionPersonPortfolio.getPortfolioCode();
		this.portfolioDescription=commissionPersonPortfolio.getPortfolioDescription();
		this.companyCode=commissionPersonPortfolio.getCompanyCode();
	}
	/**
	 * @return the personName
	 */
	public String getPersonName() {
		return personName;
	}

	/**
	 * @param personName
	 *            the personName to set
	 */
	public void setPersonName(String personName) {
		this.personName = personName;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy
	 *            the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Integer getStatus()
	{
		return status;
	}

	public void setStatus(Integer status)
	{
		this.status = status;
	}

	public String getAdpCode()
	{
		return adpCode;
	}

	public void setAdpCode(String adpCode)
	{
		this.adpCode = adpCode;
	}

	/**
	 * @return the id
	 */
	public CommissionPersonPortfolioPK getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(CommissionPersonPortfolioPK id)
	{
		this.id = id;
	}	
	
	/**
	 * @return the companyCode
	 */
	public String getCompanyCode() {
		return companyCode;
	}
	/**
	 * @param companyCode the companyCode to set
	 */
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

	
}