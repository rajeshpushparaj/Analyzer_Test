/**
 * 
 */
package com.disys.analyzer.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import com.disys.analyzer.model.CommissionPersonPortfolio;
import com.disys.analyzer.repository.custom.CommissionPersonPortfolioRepositoryCustom;

/**
 * @author Sajid
 * 
 */
public interface CommissionPersonPortfolioRepository extends
		PagingAndSortingRepository<CommissionPersonPortfolio, Integer>,
		CommissionPersonPortfolioRepositoryCustom {

}
