/**
 * 
 */
package com.disys.analyzer.repository.custom;

import java.util.List;

import org.springframework.data.jpa.repository.query.Procedure;

import com.disys.analyzer.model.AnalyserClientSite;
import com.disys.analyzer.model.dto.AnalyserClientSiteDTO;

/**
 * @author Sajid
 * 
 */
public interface AnalyserClientSiteRepositoryCustom {

	@Procedure
	List<AnalyserClientSiteDTO> getAnalyserAllSites(String userId,
			String userList, String orderBy, String searchString);
	
	
	String addUpdateAnalyserSite(AnalyserClientSite analyserClientSite, String userId, int actionType);
	
	AnalyserClientSite getAnalyserClientSiteInfo(String analyserSiteId);
}
