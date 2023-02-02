package com.disys.analyzer.repository.custom.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.disys.analyzer.config.util.FacesUtils;
import com.disys.analyzer.model.UpdatePortfolio;
import com.disys.analyzer.model.dto.PortfolioDTO;
import com.disys.analyzer.repository.custom.PortfolioRepositoryCustom;

@Repository
@Transactional(readOnly = true)
public class PortfolioRepositoryImpl implements PortfolioRepositoryCustom{

	@PersistenceContext
	EntityManager entityManager;

	/* (non-Javadoc)
	 * @see com.disys.analyzer.repository.custom.PortfolioRepositoryWork#getAllPortfolios()
	 */

	@Override
	public List<PortfolioDTO> getAllPortfolios(String userId, String companyCode) 
	{
		Session session = entityManager.unwrap(Session.class);
		PortfolioRepositoryWorks work = new PortfolioRepositoryWorks(userId, companyCode);	
		session.doWork(work);
		List<PortfolioDTO> list = work.getList();
		return list;
	}
	
	private static class PortfolioRepositoryWorks implements Work {
		public Logger logger = LoggerFactory.getLogger(getClass());
		private List<PortfolioDTO> list;
		private PortfolioDTO portfolio;
		String userId;
		String companyCode;
		CallableStatement cstmt = null;
		ResultSet rs = null;

		/**
		 * @param userId
		 */
		public PortfolioRepositoryWorks(String userId, String companyCode) {
			super();
			this.userId = userId;
			this.companyCode = companyCode;
		}

		@Override
		public void execute(Connection connection) throws SQLException {

			list = new ArrayList<PortfolioDTO>();
			portfolio = new PortfolioDTO();

				String query = "{call " + FacesUtils.SCHEMA_DBO
						+ ".getAllPortfolios('" + userId + "','" + companyCode + "')}";

				System.out.println("Query in PortfolioRepositoryWorks " + query);

				logger.debug("Query in PortfolioRepositoryWorks " + query);

				cstmt = connection.prepareCall(query);
				rs = cstmt.executeQuery();

			try {
					if (rs != null) 
					{
						while (rs.next()) 
						{
							portfolio.setPortfolioCode(rs.getString("PortfolioCode"));
							portfolio.setPortfolioDescription(rs.getString("PortfolioDescription"));
							
							
							list.add(portfolio);
							portfolio = new PortfolioDTO();
						}
					} 
					else 
					{
						list = new ArrayList<PortfolioDTO>();
					}
					System.out.println("List size in PortfolioRepositoryWorks " + list.size());
					logger.debug("List size in PortfolioRepositoryWorks " + list.size());
			} 
			catch (Exception e) 
			{
				System.out.println("Exception in PortfolioRepositoryWorks --> execute.");
				logger.debug("Exception in PortfolioRepositoryWorks --> execute.");
				e.printStackTrace();
			} 
			finally 
			{
				if (rs != null)
					rs.close();
				if (cstmt != null)
					cstmt.close();
			}
		}

		/**
		 * @return the list
		 */
		public List<PortfolioDTO> getList() {
			return list;
		}

	
	}

	@Override
	public List<UpdatePortfolio> getAllPortfolios(String userId){
		Session session = entityManager.unwrap(Session.class);
		UpdatePortfolioRepositoryWorks work = new UpdatePortfolioRepositoryWorks(userId);	
		session.doWork(work);
		List<UpdatePortfolio> list = work.getList();
		return list;
	}

	private static class UpdatePortfolioRepositoryWorks implements Work {
		public Logger logger = LoggerFactory.getLogger(getClass());
		private List<UpdatePortfolio> list;
		private UpdatePortfolio portfolio;
		String userId;
		CallableStatement cstmt = null;
		ResultSet rs = null;

		/**
		 * @param userId
		 */
		public UpdatePortfolioRepositoryWorks(String userId) {
			super();
			this.userId = userId;
		}

		@Override
		public void execute(Connection connection) throws SQLException {

			list = new ArrayList<UpdatePortfolio>();
			portfolio = new UpdatePortfolio();

				String query = "{call " + FacesUtils.SCHEMA_DBO
						+ ".getUpdateAllPortfolios('" + userId + "')}";

				System.out.println("Query in UpdatePortfolioRepositoryWorks " + query);

				logger.debug("Query in UpdatePortfolioRepositoryWorks " + query);

				cstmt = connection.prepareCall(query);
				rs = cstmt.executeQuery();

			try {
					if (rs != null) 
					{
						while (rs.next()) 
						{
							portfolio.setPortFolioId(rs.getInt("PortfolioId"));
							portfolio.setPortfolioCode(rs.getString("PortfolioCode"));
							portfolio.setPortfolioDescription(rs.getString("PortfolioDescription"));
							portfolio.setStatus(rs.getInt("Status"));
							portfolio.setCompanyCode(rs.getString("CompanyCode"));
							list.add(portfolio);
							portfolio = new UpdatePortfolio();
						}
					} 
					else 
					{
						list = new ArrayList<UpdatePortfolio>();
					}
					System.out.println("List size in UpdatePortfolioRepositoryWorks " + list.size());
					logger.debug("List size in UpdatePortfolioRepositoryWorks " + list.size());
			} 
			catch (Exception e) 
			{
				System.out.println("Exception in UpdatePortfolioRepositoryWorks --> execute.");
				logger.debug("Exception in UpdatePortfolioRepositoryWorks --> execute.");
				e.printStackTrace();
			} 
			finally 
			{
				if (rs != null)
					rs.close();
				if (cstmt != null)
					cstmt.close();
			}
		}

		/**
		 * @return the list
		 */
		public List<UpdatePortfolio> getList() {
			return list;
		}
	}
	
	@Override
	public String spAddUpdatePortFolioData(String userId, UpdatePortfolio updatePortfolio, Integer actionType)
	{
		Session session = entityManager.unwrap(Session.class);
		UpdatePortfolioDTOAddUpdateWork work = new UpdatePortfolioDTOAddUpdateWork(userId, updatePortfolio, actionType);
		session.doWork(work);
		String result = work.getResult();
		return result;
	}
	
	private static class UpdatePortfolioDTOAddUpdateWork implements Work{
		public Logger logger = LoggerFactory.getLogger(getClass());
		String userId;
		UpdatePortfolio updatePortfolio;
		Integer actionType;
		String result = "1";
		
		public UpdatePortfolioDTOAddUpdateWork(String userId, UpdatePortfolio updatePortfolio, Integer actionType){
			super();
			this.actionType = actionType;
			this.userId = userId;
			this.updatePortfolio = updatePortfolio;
		}
		
		@Override
		public void execute(Connection connection) throws SQLException{
			CallableStatement cs = null;
			ResultSet rs = null;
			try
			{
				String insertStoreProc = "{call " + FacesUtils.SCHEMA_DBO + ".spAddUpdatePortFolioData(?, ?, ?, ?, ?, ?, ?)}";
				cs = connection.prepareCall(insertStoreProc);
				
				StringBuilder queryToAddUpdateResource = new StringBuilder();
				queryToAddUpdateResource.append("USE [Analyser] GO DECLARE	@return_value int EXEC	@return_value = [wireless].[spAddUpdatePortFolioData]");
				
				// @intActionType
				cs.setInt(1, actionType);
				queryToAddUpdateResource.append("@intActionType = " + actionType + ",");
				
				// @varLoggedUserID
				cs.setString(2, userId);
				queryToAddUpdateResource.append("@varLoggedUserID = N'" + userId + "',");
				
				// @portfolioCode
				cs.setString(3, updatePortfolio.getPortfolioCode().trim());
				queryToAddUpdateResource.append("@portfolioCode = N'" + updatePortfolio.getPortfolioCode().trim() + "',");
				
				// @portfolioDescription
				cs.setString(4, updatePortfolio.getPortfolioDescription().trim());
				queryToAddUpdateResource.append("@portfolioDescription = N'" + updatePortfolio.getPortfolioDescription().trim() + "',");
				
				// @status
				Integer status = (updatePortfolio.getStatus());
				cs.setInt(5, status);
				queryToAddUpdateResource.append("@status = N'" +status+ "',");
				
				// @companyCode
				cs.setString(6, updatePortfolio.getCompanyCode().trim());
				queryToAddUpdateResource.append("@companyCode = N'" +updatePortfolio.getCompanyCode().trim()+ "',");
				
				// @PortFolioId
				if(actionType > 0){
					cs.setInt(7, actionType  == 2 ? updatePortfolio.getPortFolioId() : 0);
					queryToAddUpdateResource.append("@PortfolioId = N'" +updatePortfolio.getPortFolioId()+ "',");
				}
				queryToAddUpdateResource.append("SELECT	'Return Value' = @return_value GO ");
				System.out.println(queryToAddUpdateResource.toString());
				logger.debug("Query to add update resource is : " + queryToAddUpdateResource.toString());
				rs = cs.executeQuery();
				
				if (rs != null)
				{
					while (rs.next())
					{
						result = String.valueOf(rs.getInt(1));
					}
				}
				else
				{
					result = "-1";
				}
				
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				result = "1";
			}
			finally
			{
				if (rs != null) rs.close();
				if (cs != null) cs.close();
			}
			
		}
		
		public String getResult()
		{
			return result;
		}
	}	
	
	
}
