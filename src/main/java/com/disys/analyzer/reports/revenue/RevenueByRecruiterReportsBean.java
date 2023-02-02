/**
 * 
 */
package com.disys.analyzer.reports.revenue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.jsf.FacesContextUtils;

import com.disys.analyzer.config.util.Constants;
import com.disys.analyzer.config.util.FacesMessageSeverity;
import com.disys.analyzer.config.util.FacesUtils;
import com.disys.analyzer.config.util.WriteToFile;
import com.disys.analyzer.model.dto.AnalyserCommisionPersonDTO;
import com.disys.analyzer.model.dto.DealTypeDTO;
import com.disys.analyzer.model.dto.EmployeeCategoryDTO;
import com.disys.analyzer.model.dto.OfficeDTO;
import com.disys.analyzer.model.dto.VerticalDTO;
import com.disys.analyzer.reports.util.ReportCriteria;
import com.disys.analyzer.reports.util.ReportUtil;
import com.disys.analyzer.service.AnalyserCommisionPersonService;
import com.disys.analyzer.service.AnalyserService;
import com.disys.analyzer.service.DealTypeService;
import com.disys.analyzer.service.EmployeeCategoryService;
import com.disys.analyzer.service.OfficeService;
import com.disys.analyzer.service.VerticalService;

/**
 * @author Sajid
 * 
 */
@ManagedBean
@ViewScoped
public class RevenueByRecruiterReportsBean extends SpringBeanAutowiringSupport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private List<SelectItem> commissionPersons;

	private String managingDirector;
	private List<SelectItem> managingDirectorList;

	private String vertical;
	private List<SelectItem> verticalList;

	@Autowired
	private AnalyserCommisionPersonService service;

	private String commissionPerson;
	private String employeeType;

	private String dealType;

	private List<Map<String, Object>> reportData;
	private List<Map<String, Object>> filteredList;

	private final static String FILE_NAME = "RevenueByRecruiterReport";
	private final static String FILE_EXTENSION = ".xlsx";

	private String completeFilePath;

	private StreamedContent generatedExcelFile;

	private String newFileName;

	private String branch;
	private List<SelectItem> branchesList;
	
	
	/* Start the changes by Kumar Palanisamy on 02-Nov-2022 for Story STRY0017795 */
   
	/*   End the changes by Kumar Palanisamy on 02-Nov-2022 for Story STRY0017795 */
	@Autowired
	private AnalyserService analyserService;

	@PostConstruct
	public void init() {
		try {
			FacesContextUtils.getRequiredWebApplicationContext(FacesContext.getCurrentInstance())
					.getAutowireCapableBeanFactory().autowireBean(this);
			// TODO check if both the services are autowired or not
		} catch (Exception ex) {
			logger.debug(ex.getMessage());
		}
	}

	public RevenueByRecruiterReportsBean() {
		this.companyCode = Constants.DEFAULT_COMPANY_CODE;
		logger.debug("Inside RevenueByRecruiterReportsBean");
	}

	public void eraseFilter() {
		this.reportData = null;
		this.companyCode = Constants.DEFAULT_COMPANY_CODE;
		employeeType = "ALL";
		branch="ALL";
		commissionPerson = "ALL";
		vertical ="ALL";
		dealType ="ALL";
		managingDirector="ALL";
		this.reportData = null;

	}
	

	public String exportToExcel() throws Exception {
		if (reportData == null || reportData.size() == 0) {
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, "No data to export",
					"There is no data to export to excel!");
			throw new Exception("No data to export");
		}

		List<String> header = new ArrayList<String>();
		header.add("Recruiter");
		header.add("Branch");
		header.add("No of Consultants");
		header.add("Sort Order");
		header.add("Total Billing");
		header.add("Total Profit");
		header.add("Recruiter EmplId");
		header.add("Company Code");
		// Recruiter Branch No of Consultants Total Billing Total Profit

		XSSFWorkbook workbook = new XSSFWorkbook();

		CellStyle cellStyle1 = workbook.createCellStyle();
		cellStyle1.setFillForegroundColor(IndexedColors.RED.index);

		XSSFSheet sheet = workbook.createSheet("REVENUE BY RECRUITER");

		int rowNum = 0;
		int colNum = 0;
		System.out.println("Creating excel");

		Row row = sheet.createRow(rowNum++);
		// create excel file header
		/*
		 * for (String cellHeader : header) { Cell cell =
		 * row.createCell(colNum++); cell.setCellValue(cellHeader); }
		 */

		List<Map<String, Object>> reportDataCopy = reportData;

		/*
		 * 
		 * To print column information from the result set
		 */
		int count = 0;
		for (Map<String, Object> map : reportDataCopy) {
			row = sheet.createRow(rowNum++);
			colNum = 0;
			Cell cell = null;
			Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
			count++;
			if (count > 1) {
				break;
			}
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = iterator.next();
				try {
					cell = row.createCell(colNum++);
					String val = "";
					if (entry != null) {
						val = entry.toString();
						val = val.substring(0, val.indexOf('='));
					}
					cell.setCellValue(val);
				} catch (Exception ex) {
					logger.debug("Exception in RevenueByRecruiterReportsBean while writing to the file for : "+entry.toString() +" by user id : "+FacesUtils.getCurrentUserId() + ex.getLocalizedMessage());
					continue;
				}
			}
		}

		for (Map<String, Object> map : reportDataCopy) {
			row = sheet.createRow(rowNum++);
			colNum = 0;
			Cell cell = null;
			Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = iterator.next();
				try {
					cell = row.createCell(colNum++);
					if(entry.getValue()!=null){
						//check if the value is a numeric or double
						if(FacesUtils.checkIfValueIsDouble(entry.getValue().toString())){
							cell.setCellValue(Double.parseDouble(entry.getValue().toString()));
						}else{
							cell.setCellValue(entry.getValue().toString());
						}
					}
				} catch (Exception ex) {
					logger.debug("Exception in RevenueByRecruiterReportsBean while writing to the file for : "+entry.toString() +" by user id : "+FacesUtils.getCurrentUserId() + ex.getLocalizedMessage());
					continue;
				}
			}
		}

		try {
			String realPath = FacesUtils.getTempFilePath();

			Long dateTime = System.currentTimeMillis();

			// D:\Softwares\Apache\tomcat-8.0.23\webapps\Analyzer\

			completeFilePath = realPath + FILE_NAME + "-" + dateTime + FILE_EXTENSION;
			this.setCompleteFilePath(completeFilePath);
			this.setNewFileName(FILE_NAME + "-" + dateTime + FILE_EXTENSION);
			logger.debug("File for downloading is : " + completeFilePath);
			System.out.println("File for downloading is : " + completeFilePath);

			WriteToFile objWriteToFile = new WriteToFile();
			File file = objWriteToFile.createFile(completeFilePath);

			FileOutputStream outputStream = new FileOutputStream(file);

			workbook.write(outputStream);
			workbook.close();

			outputStream.flush();
			outputStream.close();
			// generateReportFile(file);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

	}

	public void downloadExcelFile(String filePath) {
		try {
			if (filePath == null || filePath.equals("")) {
				filePath = newFileName;
			}
			// This points to the root.
			InputStream stream = new FileInputStream(FacesUtils.getTempFilePath() + filePath);
			generatedExcelFile = new DefaultStreamedContent(stream, "application/vnd.ms-excel", filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void print() {
		if (reportData == null || reportData.size() == 0) {
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, "No data to print", "There is no data to print!");
			return;
		}
	}

	public void generateReport() {
		logger.debug("About to create Revenue by Recruiter report");

		String reportName = "REVENUE_BY_RECRUITER_REPORT";

		// String optionalParams = (String)request.getParameter("op");
		String optionalParams = null;

		if (optionalParams == null) {
			optionalParams = "";
		}

		// always get the userid for security reasons

		String reportParam = FacesUtils.getCurrentUserId();

		// String excelType = request.getParameter("rx");
		
		System.out.println(" managingDirector ==>"+ managingDirector);
		System.out.println(" commissionPerson ==>"+ commissionPerson);
		System.out.println(" employeeType ==>"+ employeeType);
		System.out.println(" vertical ==>"+ vertical);
		System.out.println(" dealType ==>"+ dealType);
		System.out.println(" branch ==>"+ branch);
		reportParam = reportParam + "|" + managingDirector + "|" + commissionPerson + "|" + employeeType + "|"
				+ vertical + "|" + dealType + "|" +  branch + "|" +  companyCode;

		
		System.out.println(" Line No 299 "+ reportParam);
		ReportCriteria rc = null;
		reportData = new ArrayList<Map<String, Object>>();
		try {
			rc = new ReportCriteria(reportName, reportParam);
			reportData = rc.getReport();// get the data from the arraylis
			System.out.println(reportData.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the commissionPersons
	 */
	public List<SelectItem> getCommissionPersons() {
		if (commissionPersons == null) {
			commissionPersons = new ArrayList<SelectItem>();

			ReportUtil report = new ReportUtil();
			List<Map<String, Object>> dataType = null;
			try {
				dataType = report.getAEs(FacesUtils.getCurrentUserId(), "0");
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i = 0; i < dataType.size(); i++) {
				Map<String, Object> map = dataType.get(i);
				String id = (String) map.get("PERSONNAME");
				String desc = (String) map.get("PERSONNAME");
				commissionPersons.add(new SelectItem(id, desc));
			}

		}
		return commissionPersons;
	}

	/**
	 * @param commissionPersons
	 *            the commissionPersons to set
	 */
	public void setCommissionPersons(List<SelectItem> commissionPersons) {
		this.commissionPersons = commissionPersons;
	}

	/**
	 * @return the commissionPerson
	 */
	public String getCommissionPerson() {
		return commissionPerson;
	}

	/**
	 * @param commissionPerson
	 *            the commissionPerson to set
	 */
	public void setCommissionPerson(String commissionPerson) {
		this.commissionPerson = commissionPerson;
	}

	/**
	 * @return the employeeType
	 */
	public String getEmployeeType() {
		return employeeType;
	}

	/**
	 * @param employeeType
	 *            the employeeType to set
	 */
	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}

	/**
	 * @return the reportData
	 */
	public List<Map<String, Object>> getReportData() {
		return reportData;
	}

	/**
	 * @param reportData
	 *            the reportData to set
	 */
	public void setReportData(List<Map<String, Object>> reportData) {
		this.reportData = reportData;
	}

	public static void main(String[] args) {
		List<HashMap<String, Object>> reportDataCopy = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> data = new HashMap<String, Object>();

		String key = "RECRUITER";
		String value = "Aashish Antil";
		data.put(key, value);

		key = "BRANCH";
		value = "Arizona";
		data.put(key, value);

		key = "NUMBER";
		value = "1";
		data.put(key, value);

		key = "SORTORDER";
		value = "0";
		data.put(key, value);

		key = "BILL";
		value = "65.0";
		data.put(key, value);

		key = "PROFIT";
		value = "-61.85";
		data.put(key, value);

		reportDataCopy.add(data);

		data = new HashMap<String, Object>();

		key = "RECRUITER";
		value = "Aaron Bessett";
		data.put(key, value);

		key = "BRANCH";
		value = "Clorado";
		data.put(key, value);

		key = "NUMBER";
		value = "4";
		data.put(key, value);

		key = "SORTORDER";
		value = "0";
		data.put(key, value);

		key = "BILL";
		value = "85.0";
		data.put(key, value);

		key = "PROFIT";
		value = "31.45";
		data.put(key, value);

		reportDataCopy.add(data);

		System.out.println(reportDataCopy.size());

		List<String> header = new ArrayList<String>();
		header.add("RECRUITER");
		header.add("BRANCH");
		header.add("NUMBER");
		header.add("SORTORDER");
		header.add("BILL");
		header.add("PROFIT");

		XSSFWorkbook workbook = new XSSFWorkbook();

		CellStyle cellStyle1 = workbook.createCellStyle();
		cellStyle1.setFillForegroundColor(IndexedColors.RED.index);

		XSSFSheet sheet = workbook.createSheet("REVENUE BY RECRUITER");

		int rowNum = 0;
		int colNum = 0;
		System.out.println("Creating excel");

		Row row = sheet.createRow(rowNum++);
		// create excel file header
		for (String cellHeader : header) {
			Cell cell = row.createCell(colNum++);
			cell.setCellValue(cellHeader);
		}

		for (Map<String, Object> map : reportDataCopy) {
			row = sheet.createRow(rowNum++);
			colNum = 0;
			Cell cell = null;
			Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = iterator.next();
				cell = row.createCell(colNum++);
				//check if the value is a numeric or double
				if(FacesUtils.checkIfValueIsDouble(entry.getValue().toString())){
					cell.setCellValue(Double.parseDouble(entry.getValue().toString()));
				}else{
					cell.setCellValue(entry.getValue().toString());
				}
				// cell.setCellStyle(cellStyle1);
			}
		}

		try {
			File file = new File(FILE_NAME);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the completeFilePath
	 */
	public String getCompleteFilePath() {
		return completeFilePath;
	}

	/**
	 * @param completeFilePath
	 *            the completeFilePath to set
	 */
	public void setCompleteFilePath(String completeFilePath) {
		this.completeFilePath = completeFilePath;
	}

	/**
	 * @return the newFileName
	 */
	public String getNewFileName() {
		return newFileName;
	}

	/**
	 * @param newFileName
	 *            the newFileName to set
	 */
	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}

	/**
	 * @return the generatedExcelFile
	 */
	public StreamedContent getGeneratedExcelFile() {
		return generatedExcelFile;
	}

	/**
	 * @return the filteredList
	 */
	public List<Map<String, Object>> getFilteredList() {
		return filteredList;
	}

	/**
	 * @param filteredList
	 *            the filteredList to set
	 */
	public void setFilteredList(List<Map<String, Object>> filteredList) {
		this.filteredList = filteredList;
	}

	/**
	 * @return the managingDirector
	 */
	public String getManagingDirector() {
		return managingDirector;
	}

	/**
	 * @param managingDirector
	 *            the managingDirector to set
	 */
	public void setManagingDirector(String managingDirector) {
		this.managingDirector = managingDirector;
	}

	/**
	 * @return the managingDirectorList
	 */
	public List<SelectItem> getManagingDirectorList() {
		if (managingDirectorList == null) {
			Map<String, String> commPersonMap = new HashMap<String, String>();
			String userId = FacesUtils.getCurrentUserId();
			String type = "MD";
			managingDirectorList = new ArrayList<SelectItem>();
			commPersonMap = service.getCommPersonList(userId, type);
			Iterator<Map.Entry<String, String>> iterator = commPersonMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> obj = iterator.next();
				if (obj.getValue() == null) {
					//managingDirectorList.add(new SelectItem(obj.getKey(), ""));
				} else {
					managingDirectorList.add(new SelectItem(obj.getKey(), obj.getValue().toString()));
				}
			}
		}
		return managingDirectorList;
	}

	/**
	 * @param managingDirectorList
	 *            the managingDirectorList to set
	 */
	public void setManagingDirectorList(List<SelectItem> managingDirectorList) {
		this.managingDirectorList = managingDirectorList;
	}

	/**
	 * @return the vertical
	 */
	public String getVertical() {
		return vertical;
	}

	/**
	 * @param vertical
	 *            the vertical to set
	 */
	public void setVertical(String vertical) {
		this.vertical = vertical;
	}

	/**
	 * @return the verticalList
	 */
	public List<SelectItem> getVerticalList() {
		// if (verticalList == null) {
		// verticalList = new ArrayList<SelectItem>();
		// verticalList.add(new SelectItem("Banking", "Banking"));
		// verticalList.add(new SelectItem("Diversified", "Diversified"));
		// verticalList.add(new SelectItem("Energy", "Energy"));
		// verticalList.add(new SelectItem("Finance and Accounting",
		// "Finance and Accounting"));
		// verticalList.add(new SelectItem("Health", "Health"));
		// verticalList.add(new SelectItem("Hi Tech", "Hi Tech"));
		// }
		// return verticalList;

		if (verticalList == null)
		{
			verticalList = new ArrayList<SelectItem>(10);
			verticalList.add(new SelectItem("AllWOFNA", "All without F&A"));
			List<String> list = analyserService.getVerticalsList();
			for (String ver : list)
			{
				verticalList.add(new SelectItem(ver, ver));
			}
		}
		return verticalList;
	}

	/**
	 * @param verticalList
	 *            the verticalList to set
	 */
	public void setVerticalList(List<SelectItem> verticalList) {
		this.verticalList = verticalList;
	}

	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public List<SelectItem> getBranchesList() {
		if (branchesList == null) {
			branchesList = new ArrayList<SelectItem>();

			ReportUtil report = new ReportUtil();
			List<Map<String, Object>> dataType = null;
			try {
				dataType = report.getBranches(FacesUtils.getCurrentUserId(), "0");
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i = 0; i < dataType.size(); i++) {
				Map<String, Object> map = dataType.get(i);
				String id = (String) map.get("BRANCHNAME");
				String desc = (String) map.get("BRANCHNAME");
				branchesList.add(new SelectItem(id, desc));
			}

		}
		return branchesList;
	}

	public void setBranchesList(List<SelectItem> branchesList) {
		this.branchesList = branchesList;
	}

	/* Start the changes by Kumar Palanisamy on 02-Nov-2022 for Story STRY0017795 */
	 private String companyCode;
	    
	    private List<SelectItem> officeList;
	    
		@Autowired
		OfficeService officeService;
		
		@Autowired
		VerticalService verticalService;
		
		@Autowired
		EmployeeCategoryService employeeCategoryService;
		
		@Autowired 
		private DealTypeService dealTypeService;
		
		private String userId=FacesUtils.getCurrentUserId();
		
		private List<SelectItem> mdCommisionPersonList;
		
		private List<SelectItem> commisionPersonList;
		
		private List<SelectItem> employeeCategoryList;
		
		private List<SelectItem> verticalLists;
		
		private List<SelectItem> dealTypeList;
		
		public List<SelectItem> getDealTypeList() {
			return dealTypeList;
		}
		
		public void setDealTypeList(List<SelectItem> dealTypeList) {
			this.dealTypeList = dealTypeList;
		}
	
	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public List<SelectItem> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<SelectItem> officeList) {
		this.officeList = officeList;
	}

	public void onCompanyCodeChange(javax.faces.event.AjaxBehaviorEvent event) {
		System.out.println(" Inside the onCompanyCodeChange ");
		employeeType = "ALL";
		branch="ALL";
		commissionPerson = "ALL";
		vertical ="ALL";
		dealType ="ALL";
		managingDirector="ALL";
		this.reportData = null;
}

	public List<SelectItem> getOfficeByCompanyCodeList(String companyCode) 
	{
		officeList = new ArrayList<SelectItem>();
		if (!companyCode.equalsIgnoreCase(Constants.STRING_CONSTANT_ALL)) 
		{
			try
			{
				List<OfficeDTO> officeLists = officeService.getOfficeList(userId, companyCode);
				if (officeLists != null)
				{
					for (OfficeDTO c:officeLists)
					{						
						officeList.add(new SelectItem(c.getOfficeDescription(), c.getOfficeDescription()));
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("Exception in EndDateReportsBean --> getOfficeByCompanyCodeList.");
				logger.debug("Exception in EndDateReportsBean --> getOfficeByCompanyCodeList.");
				e.printStackTrace();
			}
		}
		return officeList;
	}
	public List<SelectItem> getMDCommissionPersonList(String companyCode) 
	{
		mdCommisionPersonList = new ArrayList<SelectItem>();
		if (!companyCode.equalsIgnoreCase(Constants.STRING_CONSTANT_ALL)) 
		{
			try
			{
				List<AnalyserCommisionPersonDTO> commisionPersonLists = service.getMDCommissionPersonList(userId, companyCode);
				if (commisionPersonLists != null)
				{
					for (AnalyserCommisionPersonDTO c:commisionPersonLists)
					{						
						mdCommisionPersonList.add(new SelectItem(c.getPersonName(), c.getPersonName()));
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("Exception in EndDateReportsBean --> getMDCommissionPersonList.");
				logger.debug("Exception in EndDateReportsBean --> getMDCommissionPersonList.");
				e.printStackTrace();
			}
		}
		return mdCommisionPersonList;
	}
	public List<SelectItem> getAllCommissionPersonList(String companyCode) 
	{
		commisionPersonList = new ArrayList<SelectItem>();
		if (!companyCode.equalsIgnoreCase(Constants.STRING_CONSTANT_ALL)) {
			try {
				List<AnalyserCommisionPersonDTO> commisionPersonLists = service.getAllCommissionPersonList(userId,
						companyCode);
				if (commisionPersonLists != null) {
					for (AnalyserCommisionPersonDTO c : commisionPersonLists) {
						commisionPersonList.add(new SelectItem(c.getPersonName(), c.getPersonName()));
					}
				}
			} catch (Exception e) {
				System.out.println("Exception in AnalyserActivityReportsBean --> getAllCommissionPersonList .");
				logger.debug("Exception in AnalyserActivityReportsBean --> getAllCommissionPersonList .");
				e.printStackTrace();
			}
		}
		return commisionPersonList;
	}
	public List<SelectItem> getEmployeeCategoryByCompanyCodeList(String companyCode) 
	{
		employeeCategoryList = new ArrayList<SelectItem>();
		if (!companyCode.equalsIgnoreCase(Constants.STRING_CONSTANT_ALL)) 
		{
			try
			{
				List<EmployeeCategoryDTO> employeeCategoryLists = employeeCategoryService.getEmployeeCategoryList(userId, companyCode);
				if (employeeCategoryLists != null)
				{
					for (EmployeeCategoryDTO c:employeeCategoryLists)
					{
						
						employeeCategoryList.add(new SelectItem(c.getEmployeeCategoryCode(), c.getEmployeeCategoryDescription()));
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("Exception in EndDateReportsBean --> getEmployeeCategoryByCompanyCodeList.");
				logger.debug("Exception in EndDateReportsBean --> getEmployeeCategoryByCompanyCodeList.");
				e.printStackTrace();
			}
		}
		return employeeCategoryList;
	}	
	public List<SelectItem> getVerticalByCompanyCodeList(String companyCode) 
	{
		verticalLists = new ArrayList<SelectItem>();
		if (!companyCode.equalsIgnoreCase(Constants.STRING_CONSTANT_ALL)) 
		{
			try
			{
				List<VerticalDTO> vertical = verticalService.getVerticalList(userId, companyCode);
				if (vertical != null)
				{
					/**
					* @@TODO
					* If the Constants.DEFAULT_COMPANY_CODE is DISYS we are adding below list value in the VerticalLists list selectItem 
					*/
					if (companyCode.equalsIgnoreCase(Constants.DEFAULT_COMPANY_CODE))
					{
						verticalLists.add(new SelectItem("AllWOFNA", "All without F&A"));
					}
					for (VerticalDTO c:vertical)
					{						
						verticalLists.add(new SelectItem(c.getVerticalDescription(), c.getVerticalDescription()));
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("Exception in AnalyserActivityReportsBean --> getVerticalByCompanyCodeList.");
				logger.debug("Exception in AnalyserActivityReportsBean --> getVerticalByCompanyCodeList.");
				e.printStackTrace();
			}
		}
		return verticalLists;
	}	
	public List<SelectItem> getDealTypeByCompanyCodeList(String compCode) 
	{
		if (dealTypeList == null) 
		{
			dealTypeList = new ArrayList<SelectItem>();
			try
			{
				List<DealTypeDTO> dealTypeLists = dealTypeService.getDealTypeList(userId, compCode);
				if (dealTypeLists != null)
				{
					for (DealTypeDTO c:dealTypeLists)
					{
						
						dealTypeList.add(new SelectItem(c.getDealTypeCode(), c.getDealTypeDescription()));
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("Exception in DealTypeBean --> getDealTypeByCompanyCodeList.");
				logger.debug("Exception in DealTypeBean --> getDealTypeByCompanyCodeList.");
				e.printStackTrace();
			}
		}
		return dealTypeList;
	}	
	/*   End the changes by Kumar Palanisamy on 02-Nov-2022 for Story STRY0017795 */	
	
}
