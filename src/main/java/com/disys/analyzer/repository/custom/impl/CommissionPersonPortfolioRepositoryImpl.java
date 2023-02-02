/**
 * 
 */
package com.disys.analyzer.repository.custom.impl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
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

import com.disys.analyzer.config.database.DBConnection;
import com.disys.analyzer.config.util.FacesUtils;
import com.disys.analyzer.config.util.Util;
import com.disys.analyzer.model.CommissionPersonPortfolio;
import com.disys.analyzer.model.CommissionPersonPortfolioPK;
import com.disys.analyzer.model.dto.UserDTO;
import com.disys.analyzer.repository.custom.CommissionPersonPortfolioRepositoryCustom;

/**
 * @author Sajid
 * 
 */
@Repository
@Transactional(readOnly = true)
public class CommissionPersonPortfolioRepositoryImpl implements CommissionPersonPortfolioRepositoryCustom
{
	
	public static Logger logger = LoggerFactory.getLogger(CommissionPersonPortfolioRepositoryImpl.class);
	

	public static final String ADD = "ADD";
	public static final String UPDATE = "UPDATE";
	public static final String USERS_LIST = "portfolioUsers";
	public static final String PORTFOLIO_USERS_LIST = "portfolioUsersList";
	public static final String PORTFOLIO_MODIFY_INFO = "portfolioModifyInfo";
	
	
	@PersistenceContext EntityManager entityManager;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.disys.analyzer.repository.custom.
	 * AnalyserCommissionPersonRepositoryCustom#getCommissionUsersList(java.lang
	 * .String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String addUpdateAnalyserCommissionPersonPortfolio(String commPerson, String commPersonOld, String userId, String portfolioCode, String portfolioDescription, int actionType)
	{
		Session session = entityManager.unwrap(Session.class);
		CommissionPersonPortfolioWork work = new CommissionPersonPortfolioWork(commPerson, commPersonOld, userId, portfolioCode, portfolioDescription, actionType, "ADD");
		session.doWork(work);
		String result = work.getResult();
		return result;
	}
	@Override
	public Boolean updateCommissionPersonPortfolio(String updatedBy, String userId, String personName, String portfolioCode, String portfolioDescription)
	{
		Session session = entityManager.unwrap(Session.class);
		CommissionPersonPortfolioWork work = new CommissionPersonPortfolioWork(updatedBy, userId, personName,
				portfolioCode, portfolioDescription, UPDATE);
		session.doWork(work);
		String result = work.getResult();
		if(result.equals("1")){
			return true;
		}
		return false;
	}
	
	private static class CommissionPersonPortfolioWork implements Work
	{
		
		private Logger logger = LoggerFactory.getLogger(getClass());
		String commPerson;
		String commPersonOld;
		String userId;
		int actionType;
		String portfolioCode;
		String portfolioDescription;
		String result = "";		
		String action = "";		
		String updatedBy;
		String personName;
		public CommissionPersonPortfolioWork(String commPerson, String commPersonOld, String userId, String portfolioCode, String portfolioDescription, int actionType, String action)
		{
			super();
			this.commPerson = commPerson;
			this.commPersonOld = commPersonOld;
			this.userId = userId;
			this.portfolioCode = portfolioCode;
			this.portfolioDescription = portfolioDescription;
			this.actionType = actionType;
			this.action = action;			
		}
		public CommissionPersonPortfolioWork(String updatedBy, String userId, String personName, String portfolioCode, String portfolioDescription, String action)
		{
			this.updatedBy = updatedBy;
			this.userId = userId;
			this.portfolioCode = portfolioCode;
			this.portfolioDescription = portfolioDescription;
			this.personName = personName;
			this.action = action;			
		}
		
		@Override
		public void execute(Connection connection) throws SQLException
		{
			CallableStatement callStmt = null;
			ResultSet rs = null;
			result = "0";
			try
			{
				if (connection != null)
				{
					String query = "";
					
					if (action.equals(ADD))
					{
						query = "{call " + FacesUtils.SCHEMA_WIRELESS + ".spAddUpdateCommissionPersonPortfolio(" + actionType + ",'" + userId + "','"
						        + Util.removeSingleQuote(commPerson) + "','" + Util.removeSingleQuote(commPersonOld) + "','"+portfolioCode+"','"+portfolioDescription+"')}";
					}else if (action.equals(UPDATE))
					{
						query = "{call " + FacesUtils.SCHEMA_WIRELESS + ".spUpdateCommisionPersonsPortfolio('" + updatedBy + "','" + userId + "','"
						        + Util.removeSingleQuote(personName) + "','"+portfolioCode+"','"+portfolioDescription+"')}";
					}						
					
					System.out.println("Executing Query in spAddUpdateCommissionPersonPortfolio " + query);
					
					logger.info("CommissionPersonPortfolio Query is : " + query);
					
					callStmt = connection.prepareCall(query);
					rs = callStmt.executeQuery();
					if (rs != null)
					{
						if (action.equals(ADD))
						{
							while (rs.next())
							{
								result = "" + rs.getInt(1);
							}
						}else if (action.equals(UPDATE))
						{
							while (rs.next())
							{
								result = "" + rs.getInt(1);
							}
						}
						
						callStmt.close();
						rs.close();
						callStmt = null;
						rs = null;
					}
					else
					{
						throw new Exception("CommissionPersonPortfolio could not be inserted");
					}
				}
				else
				{
					if (action.equals(ADD))
					{
						throw new Exception("Connection is null");
					}else if (action.equals(UPDATE))
					{
						throw new Exception("Connection is null");
					}
					
				}
			}
			catch (Exception ex)
			{
				System.out.println("Exception in AddUpdateCommissionPersonPortfolio method");
				ex.printStackTrace();
				if (action.equals(ADD))
				{
					result = "1";
				}if (action.equals(UPDATE))
				{
					result = "1";
				}
				
			}
			finally
			{
				if (rs != null) rs.close();
				if (callStmt != null) callStmt.close();
			}
		}
		
		/**
		 * @return the result
		 */
		public String getResult()
		{
			return result;
		}
		
	}
	
	@Override
	public List<CommissionPersonPortfolio> getCommissionUsersPortfolioList(String userId, String userList, String orderBy, String searchString,  String companyCode)
	{
		Session session = entityManager.unwrap(Session.class);
		CommissionUsersPortfolioListWork work = new CommissionUsersPortfolioListWork(userId, userList, orderBy, searchString, PORTFOLIO_USERS_LIST, companyCode);
		session.doWork(work);
		List<CommissionPersonPortfolio> list = work.getList();
		return list;
	}
	
	@Override
	public CommissionPersonPortfolio getModifyCommissionPersonPortfolio(String userId, String commissionPersonId)
	{
		Session session = entityManager.unwrap(Session.class);
		CommissionUsersPortfolioListWork work = new CommissionUsersPortfolioListWork(userId, commissionPersonId, PORTFOLIO_MODIFY_INFO);
		session.doWork(work);
		CommissionPersonPortfolio portfolioPerson = work.getPortfolioUserList();
		return portfolioPerson;
	}
	private static class CommissionUsersPortfolioListWork implements Work
	{
		private List<CommissionPersonPortfolio> list;
		private CommissionPersonPortfolio portfolioUserList;
		
		String userId;
		String userList;
		String orderBy;
		String searchString;
		String companyCode;	
		String action;
		String commissionPersonId;
		
		CallableStatement cstmt = null;
		/**
		 * @param userId
		 * @param userList
		 * @param orderBy
		 * @param searchString
		 */
		public CommissionUsersPortfolioListWork(String userId, String userList, String orderBy, String searchString, String action, String companyCode)
		{
			super();
			this.userId = userId;
			this.userList = userList;
			this.orderBy = orderBy;
			this.searchString = searchString;
			this.companyCode = companyCode;
			this.action = action;
		}
		
		public CommissionUsersPortfolioListWork(String userId, String commissionPersonId, String action)
		{
			this.userId = userId;
			this.commissionPersonId = commissionPersonId;
			this.action = action;
		}
		@Override
		public void execute(Connection connection) throws SQLException
		{
			
			portfolioUserList = new CommissionPersonPortfolio();
			list = new ArrayList<CommissionPersonPortfolio>();

			if (action.equals(PORTFOLIO_USERS_LIST)){
				try
				{
					StringBuilder queryToAnalyserComm = new StringBuilder();
					queryToAnalyserComm.append(
					        "USE [Analyser] \n GO \n DECLARE	@return_value int \n EXEC	@return_value = [wireless].[spGetCommissionPersonPortfolioUserList] ");
					queryToAnalyserComm.append("\n @varLoggedOnUserID = N'" + userId + "',");
					queryToAnalyserComm.append("\n @varUserList = N'" + userList + "',");
					queryToAnalyserComm.append("\n @varOrderBy = N'" + orderBy + "',");
					queryToAnalyserComm.append("\n @varSearchString = N'" + searchString + "'");
					queryToAnalyserComm.append("\n @varCompanyCode = N'" + companyCode + "'");
					queryToAnalyserComm.append("\n SELECT	'Return Value' = @return_value \n GO ");
					System.out.println("Query :: " + queryToAnalyserComm.toString());
					cstmt = connection.prepareCall("{call " + FacesUtils.SCHEMA_WIRELESS + ".spGetCommissionPersonPortfolioUserList(?, ?, ?, ?, ?)}");
					cstmt.setString("varLoggedOnUserID", userId);
					cstmt.setString("varUserList", userList);
					cstmt.setString("varOrderBy", orderBy);
					cstmt.setString("varSearchString", searchString);
					cstmt.setString("varCompanyCode", companyCode);
				}
				catch (Exception e)
				{
					System.out.println("Exception in " + USERS_LIST + " of AnalyseCommissionPersonRepositoryImpl " + e.getLocalizedMessage());
				}
			}
			else  if (action.equals(PORTFOLIO_MODIFY_INFO)){
				try
				{
					StringBuilder queryToAnalyserCommissionPerson = new StringBuilder();
					queryToAnalyserCommissionPerson.append(
					        "USE [Analyser] \n GO \n DECLARE	@return_value int \n EXEC	@return_value = [wireless].[spGetCommissionPersonPortfolio] ");
					queryToAnalyserCommissionPerson.append("\n @varLoggedOnUserID = N'" + userId + "',");
					queryToAnalyserCommissionPerson.append("\n @varCommissionPersonId = N'" + commissionPersonId + "'");
					queryToAnalyserCommissionPerson.append("\n SELECT	'Return Value' = @return_value \n GO ");
					System.out.println("Query :: " + queryToAnalyserCommissionPerson.toString());
					cstmt = connection.prepareCall("{call " + FacesUtils.SCHEMA_WIRELESS + ".spGetCommissionPersonPortfolio(?, ?)}");
					cstmt.setString("varLoggedOnUserID", userId);
					cstmt.setString("varCommissionPersonId", commissionPersonId);
				}
				catch (Exception e)
				{
					System.out.println("Exception in " + USERS_LIST + " of AnalyseCommissionPersonRepositoryImpl " + e.getLocalizedMessage());
				}
			}
			ResultSet rs = cstmt.executeQuery();
			if (action.equalsIgnoreCase(PORTFOLIO_USERS_LIST))
			{
			ResultSetMetaData rsMeta;
			try
			{
				rsMeta = rs.getMetaData();
				int iRowCount = rsMeta.getColumnCount();				
				while (rs.next())
				{
					portfolioUserList = new CommissionPersonPortfolio();
					if (portfolioUserList.getId() == null)
					{
						portfolioUserList.setId(new CommissionPersonPortfolioPK());
					}
					for (int i = 0; i < iRowCount; i++)
					{
						String columnName = rsMeta.getColumnLabel(i + 1);
						// put values into the array list
						try
						{
							if (columnName.equalsIgnoreCase("PersonName"))
							{
								portfolioUserList.setPersonName((String) getRSColumnValue(rs, rsMeta, i + 1));
							}
							else if (columnName.equalsIgnoreCase("User_id"))
							{
								portfolioUserList.getId().setUserId((String) getRSColumnValue(rs, rsMeta, i + 1));
							}
							else if (columnName.equalsIgnoreCase("ADPCode"))
							{
								portfolioUserList.setAdpCode((String) getRSColumnValue(rs, rsMeta, i + 1));
							}
							else if (columnName.equalsIgnoreCase("PortfolioCode"))
							{
								portfolioUserList.setPortfolioCode((String) getRSColumnValue(rs, rsMeta, i + 1));
							}
							else if (columnName.equalsIgnoreCase("PortfolioDescription"))
							{
								portfolioUserList.setPortfolioDescription((String) getRSColumnValue(rs, rsMeta, i + 1));
							}
							else if (columnName.equalsIgnoreCase("Status"))
							{
								portfolioUserList.setStatus((Integer) getRSColumnValue(rs, rsMeta, i + 1));
							}
							else if (columnName.equalsIgnoreCase("Updated_By"))
							{
								portfolioUserList.setUpdatedBy((String) getRSColumnValue(rs, rsMeta, i + 1));
							}
							else if (columnName.equalsIgnoreCase("Updated_Date"))
							{
								portfolioUserList.setUpdatedDate((Timestamp) getRSColumnValue(rs, rsMeta, i + 1));
							}														
							if (columnName.equalsIgnoreCase("CompanyCode"))
							{
								portfolioUserList.setCompanyCode((String) getRSColumnValue(rs, rsMeta, i + 1));
							}						
						}
						catch (Exception ex)
						{
							continue;
						}
					}
					list.add(portfolioUserList);
				}
			}
			catch (SQLException e)
			{
				String sz = "An error occured while attempting to parse resultset column information from the database.  Error type :  "
				        + e.getSQLState() + ".  Error Code : " + e.getErrorCode();
				Exception err = new Exception(sz);
				try {
					throw err;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			}
			else if (action.equalsIgnoreCase(PORTFOLIO_MODIFY_INFO)) {
				try
				{
					portfolioUserList = new CommissionPersonPortfolio();
					if (portfolioUserList.getId() == null)
					{
						portfolioUserList.setId(new CommissionPersonPortfolioPK());
					}
					if (rs != null && rs.next())
					{
						try
						{								
							portfolioUserList.setPersonName(rs.getString("PersonName"));
							portfolioUserList.getId().setUserId(rs.getString("USER_ID"));
							//person.setAdpCode(rs.getString("ADPCode"));
							portfolioUserList.setPortfolioCode(rs.getString("PortfolioCode")+":"+rs.getString("PortfolioDescription"));
							portfolioUserList.setPortfolioDescription(rs.getString("PortfolioDescription"));
							portfolioUserList.setCompanyCode(rs.getString("CompanyCode"));
						}
						catch (SQLException ex)
						{
							String sz = "An SQL error occured while attempting to parse resultset from the database for getting commission person portfolio.  Error type :  "
							        + ex.getSQLState() + ".  Error Code : " + ex.getErrorCode();
							Exception err = new Exception(sz);
							try {
								throw err;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						catch (Exception ex)
						{
							String sz = "An exception occured while attempting to get commission person portfolio.  "+ex.getLocalizedMessage();
							Exception err = new Exception(sz);
							try {
								throw err;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				catch (SQLException e)
				{
					String sz = "An error occured while attempting to parse resultset column information from the database for CommisionPersonPortfolioWork.  Error type :  "
					        + e.getSQLState() + ".  Error Code : " + e.getErrorCode();
					Exception err = new Exception(sz);
					try {
						throw err;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
				
		}
		
		
		private Object getRSColumnValue(ResultSet rs, ResultSetMetaData rsMeta, int pos) throws Exception
		{
			try
			{
				int iType = (int) rsMeta.getColumnType(pos);
				switch (iType)
				{
					case Types.BIGINT:
						return new Long(rs.getLong(pos));
					case Types.NUMERIC:
					{
						java.math.BigDecimal bigDecimal = rs.getBigDecimal(pos);
						if (bigDecimal == null) return null;
						else if (bigDecimal.scale() == 0) return new Long(bigDecimal.longValue());
						else return new Double(bigDecimal.doubleValue());
					}
					
					case Types.CHAR:
					{
						String s = rs.getString(pos);
						// if (rs.wasNull()) return "";
						return s;
					}
					case Types.TIMESTAMP:
					{
						java.sql.Timestamp d = rs.getTimestamp(pos);
						return d;
					}
					case Types.DATE:
					{
						java.sql.Date d = rs.getDate(pos);
						return d;
					}
					case Types.DOUBLE:
						return new Double(rs.getDouble(pos));
					case Types.FLOAT:
						return new Float(rs.getFloat(pos));
					case Types.INTEGER:
						return new Integer(rs.getInt(pos));
					case Types.LONGVARCHAR:
						return rs.getString(pos);
					case Types.VARBINARY:
						return rs.getBytes(pos);
					case Types.VARCHAR:
					{
						String s = rs.getString(pos);
						return s;
					}
					case Types.DECIMAL:
					{
						BigDecimal s = rs.getBigDecimal(pos);
						return s;
					}
					default:
						return rs.getObject(pos);
				}
			}
			catch (SQLException e)
			{
				String sz = "An error occured while attempting to parse resultset column information from the database.  Error type :  "
				        + e.getSQLState() + ".  Error Code : " + e.getErrorCode();
				Exception err = new Exception(sz);
				throw err;
			}
			
		}
		
		/**
		 * @return the list
		 */
		public List<CommissionPersonPortfolio> getList()
		{
			return list;
		}


		public CommissionPersonPortfolio getPortfolioUserList() {
			return portfolioUserList;
		}				
	}
	

	@SuppressWarnings("unused")
	@Override
	public String changeCommissionPersonPortfolioStatus(String userId, String updatedBy, String personName, Integer status)
	{
		Connection con = null;
		CallableStatement callStmt = null;
		ResultSet rs = null;
		String result = "0";
		int res = 0;
		try
		{
			con = DBConnection.getConnection();
			if (con != null)
			{
				String query = null;
				query = "{call " + FacesUtils.SCHEMA_WIRELESS + ".spSetStatusCommisionPersonsPortfolio('" + userId + "', '" + updatedBy + "', '"
				        + personName + "', " + status + ")}";
				
				System.out.println("Executing Query in spSetStatusCommisionPersonsPortfolio -- :  " + query);
				logger.debug("Executing Query in spSetStatusCommisionPersonsPortfolio -- :  " + query);
				System.out.println("In DBHelper User ID = " + userId);
				System.out.println("In DBHelper Person Name = " + personName);
				System.out.println("In DBHelper Status = " + status);
				callStmt = con.prepareCall(query);
				res = callStmt.executeUpdate();
				
				if (res != 0) // return 0 if success or "" otherwise
				    result = "";
				
				con.close();
				callStmt.close();
				if (rs != null)
				{
					rs.close();
				}
				con = null;
				callStmt = null;
				rs = null;
				
			}
			else
			{
				throw new Exception("Connection is null");
			}
		}
		catch (Exception ex)
		{
			System.out.println("Exception in changeCommissionPersonPortfolioStatus method of CommissionPersonPortfolioRepositoryImpl");
			ex.printStackTrace();
			return "System Error";
		}
		finally
		{
			DBConnection.closeConnection(con, callStmt, rs);
		}
		
		return result;
	}
	
	@Override
	public List<UserDTO> spGetActiveUsersPortfolio(String userId, String companyCode)
	{
		Session session = entityManager.unwrap(Session.class);
		ActiveUsersPortfolioWork work = new ActiveUsersPortfolioWork(userId, companyCode);
		session.doWork(work);
		List<UserDTO> list = work.getList();
		return list;
	}
	
	private static class ActiveUsersPortfolioWork implements Work {
		public Logger logger = LoggerFactory.getLogger(getClass());
		private List<UserDTO> list;
		private UserDTO userDTO;
		String userId;		
		String companyCode;

		ResultSet rs = null;

		
		public ActiveUsersPortfolioWork(String userId, String companyCode) {
			super();
			this.userId = userId;
			this.companyCode = companyCode;
		}

		@Override
		public void execute(Connection connection) throws SQLException {
			
			list = new ArrayList<UserDTO>();
			userDTO = new UserDTO();
			CallableStatement cstmt = null;			
			String query = "{call " + FacesUtils.SCHEMA_WIRELESS
					+ ".spGetUserListForCommissionPersonPortfolio('" + userId + "', '" + companyCode + "')}";
			System.out.println("Query in spGetUserListForLiaison " + query);
			logger.debug("Query in spGetUserListForLiaison " + query);

			cstmt = connection.prepareCall(query);
			rs = cstmt.executeQuery();

			try {
				if (rs != null) {
					while (rs.next()) {
						userDTO.setName(rs.getString("Name"));						
						userDTO.setUserId(rs.getString("USER_ID"));
						userDTO.setApdCode(rs.getString("ADPCode"));												
						list.add(userDTO);
						userDTO = new UserDTO();
					}
				} else {
					list = new ArrayList<UserDTO>();					
				}

				System.out.println("List size in get user list " + list.size());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null)
					rs.close();
				if (cstmt != null)
					cstmt.close();
			}
		}

		public List<UserDTO> getList() {
			return list;
		}
	}
}