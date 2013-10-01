/**
 *   (c) Copyright 2013 Telefonica, I+D. Printed in Spain (Europe). All Rights
 *   Reserved.
 * 
 *   The copyright to the software program(s) is property of Telefonica I+D.
 *   The program(s) may be used and or copied only with the express written
 *   consent of Telefonica I+D or in accordance with the terms and conditions
 *   stipulated in the agreement/contract under which the program(s) have
 *   been supplied.
 */

package com.telefonica.euro_iaas.sdc.it;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import com.telefonica.euro_iaas.sdc.client.exception.ResourceNotFoundException;
import com.telefonica.euro_iaas.sdc.it.util.ApplicationUtils;
import com.telefonica.euro_iaas.sdc.model.ApplicationRelease;
import com.telefonica.euro_iaas.sdc.model.Attribute;
import com.telefonica.euro_iaas.sdc.model.dto.EnvironmentDto;
import com.telefonica.euro_iaas.sdc.model.dto.ProductReleaseDto;

import cuke4duke.Table;
import cuke4duke.annotation.I18n.EN.Given;
import cuke4duke.annotation.I18n.EN.Then;
import cuke4duke.annotation.I18n.EN.When;

public class ApplicationSteps {

    private List<ProductReleaseDto> supportedProducts;
    private List<Attribute> attributes;
    private List<ApplicationRelease> transitableReleases;

    private ApplicationRelease existedApplication;
    private ApplicationRelease addedApplication;
    private ApplicationRelease updatedApplication;

    private EnvironmentDto environmentDto;

    // ADD Application
    @Given("^application default attributes:$")
    public void getAttributes(Table table) {
        attributes = new ArrayList<Attribute>();
        for (List<String> row : table.rows()) {
            attributes.add(new Attribute(row.get(0), row.get(1), row.get(2)));
        }
    }

    @Given("^the application transitable releases:$")
    public void getTransitableReleases(Table table) throws Exception {
        transitableReleases = new ArrayList<ApplicationRelease>();
        ApplicationUtils manager = new ApplicationUtils();
        for (List<String> row : table.rows()) {
            transitableReleases.add(manager.load(row.get(0), row.get(1)));
        }
    }

    @Given("^the application supported product release$")
    // public void getProductReleases(Table table) {
    public void getEnvironmentDto(Table table) {
        supportedProducts = new ArrayList<ProductReleaseDto>();
        for (List<String> row : table.rows()) {
            ProductReleaseDto productReleaseDto = new ProductReleaseDto();

            productReleaseDto.setProductName(row.get(0));
            productReleaseDto.setProductDescription("description");
            productReleaseDto.setVersion(row.get(1));
            supportedProducts.add(productReleaseDto);
        }
        environmentDto = new EnvironmentDto(supportedProducts);
    }

    @When("^I add application \"([^\"]*)\" \"([^\"]*)\" of type \"([^\"]*)\" \\(\"([^\"]*)\"\\)$")
    public void addApplicationToCatalog(String applicationName, String version, String type, String description) {
        ApplicationUtils manager = new ApplicationUtils();
        addedApplication = manager.add(applicationName, version, type, description, "", attributes, environmentDto,
                transitableReleases);
    }

    @Then("^I get application \"([^\"]*)\" \"([^\"]*)\" in the catalog")
    public void assertAddedApplication(String applicationName, String version) throws ResourceNotFoundException {
        ApplicationUtils manager = new ApplicationUtils();
        existedApplication = manager.load(applicationName, version);
        Assert.assertNotNull(existedApplication);
    }

    // UPDATE APPLICATION
    @When("^I update application \"([^\"]*)\" \"([^\"]*)\" of type \"([^\"]*)\" \\(\"([^\"]*)\"\\)$")
    public void updateApplicationToCatalog(String applicationName, String version, String type, String description) {

        ApplicationUtils manager = new ApplicationUtils();
        updatedApplication = manager.update(applicationName, version, type, description, "", attributes,
                environmentDto, transitableReleases);
    }

    @Then("^I get updated application \"([^\"]*)\" \"([^\"]*)\" in the catalog$")
    public void assertUpdatedApplication(String applicationName, String version) {

    }

    // DELETE APPLICATION
    @Given("^a application \"([^\"]*)\" \"([^\"]*)\" present in the Catalog")
    public void getApplicationFromCatalog(String applicationName, String version) throws Exception {
        System.out.println("application: " + applicationName + "version: " + version);
        ApplicationUtils manager = new ApplicationUtils();
        existedApplication = manager.load(applicationName, version);
        System.out.println("existed application: " + existedApplication.getApplication().getName() + "version: "
                + existedApplication.getVersion());
    }

    @When("^I delete application \"([^\"]*)\" \"([^\"]*)\"$")
    public void deleteApplicationFromCatalog(String applicationName, String version) throws Exception {
        ApplicationUtils manager = new ApplicationUtils();
        manager.delete(applicationName, version);
    }

    @Then("^there is not application \"([^\"]*)\" \"([^\"]*)\" in the catalog$")
    public void assertDeletedProduct(String applicationName, String version) throws ResourceNotFoundException {
        // ProductUtils manager = new ProductUtils();
        // Assert.assertNull(manager.load(productName, version));
    }

}