package com.telefonica.euro_iaas.sdc.manager;

import java.io.File;
import java.util.List;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.sdc.model.Product;
import com.telefonica.euro_iaas.sdc.model.ProductRelease;
import com.telefonica.euro_iaas.sdc.model.searchcriteria.ProductReleaseSearchCriteria;
import com.telefonica.euro_iaas.sdc.model.searchcriteria.ProductSearchCriteria;

/**
 * Defines the operations the system shall be able to do with Products.
 *
 * @author Sergio Arroyo
 *
 */
public interface ProductManager {

    /**
     * Find the ProductInstance using the given id.
     * @param name the product identifier
     * @return the product
     * @throws EntityNotFoundException if the product instance does not exists
     */
    Product load(String name) throws EntityNotFoundException;

    /**
     * Retrieve all Product created in the system.
     * @return the existent product instances.
     */
    List<Product> findAll();

    /**
     * Find the products that match with the given criteria.
     * @param criteria the search criteria
     * @return the list of elements that match with the criteria.
     */
    List<Product> findByCriteria(ProductSearchCriteria criteria);

    /**
     * Retrieve a Product release for a given product and version.
     * @param product the product
     * @param version the version
     * @return the product release that match with the criteria
     * @throws EntityNotFoundException if the product release does not exists
     */
    ProductRelease load(Product product, String version)
        throws EntityNotFoundException;

    /**
     * Find all product releases that match with the given criteria.
     * @param criteria the search criteria
     * @return the products releases.
     */
    List<ProductRelease> findReleasesByCriteria(
            ProductReleaseSearchCriteria criteria);
    
    /**
     * Insert the Product Release consisting on.
     * @param ProductRelease Object with name, version, transitableUploads..
     * @param The recipes to be uploaded to the chef server in a tar file
     * @param The scripts/packages required to install/uninstall/configure 
     * 		  the product
     * @return the products releases.
     */
    ProductRelease insert( ProductRelease productRelase, File recipes,
    		File installable)  
    		throws AlreadyExistsEntityException, InvalidEntityException;
    
    /**
     * Delete the Product Release consisting on.
     * @param ProductRelease Object with name, version, transitableUploads..
     * @return the products releases.
     */
    void delete( ProductRelease productRelase);
}
