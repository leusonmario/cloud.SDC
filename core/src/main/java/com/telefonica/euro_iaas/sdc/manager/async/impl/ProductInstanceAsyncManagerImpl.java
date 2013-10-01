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

package com.telefonica.euro_iaas.sdc.manager.async.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.sdc.exception.AlreadyInstalledException;
import com.telefonica.euro_iaas.sdc.exception.FSMViolationException;
import com.telefonica.euro_iaas.sdc.exception.InvalidInstallProductRequestException;
import com.telefonica.euro_iaas.sdc.exception.NodeExecutionException;
import com.telefonica.euro_iaas.sdc.exception.NotTransitableException;
import com.telefonica.euro_iaas.sdc.exception.NotUniqueResultException;
import com.telefonica.euro_iaas.sdc.manager.ProductInstanceManager;
import com.telefonica.euro_iaas.sdc.manager.async.ProductInstanceAsyncManager;
import com.telefonica.euro_iaas.sdc.manager.async.TaskManager;
import com.telefonica.euro_iaas.sdc.model.Attribute;
import com.telefonica.euro_iaas.sdc.model.ProductInstance;
import com.telefonica.euro_iaas.sdc.model.ProductRelease;
import com.telefonica.euro_iaas.sdc.model.Task;
import com.telefonica.euro_iaas.sdc.model.Task.TaskStates;
import com.telefonica.euro_iaas.sdc.model.TaskError;
import com.telefonica.euro_iaas.sdc.model.TaskReference;
import com.telefonica.euro_iaas.sdc.model.dto.VM;
import com.telefonica.euro_iaas.sdc.model.searchcriteria.ProductInstanceSearchCriteria;
import com.telefonica.euro_iaas.sdc.util.SystemPropertiesProvider;
import com.telefonica.euro_iaas.sdc.util.TaskNotificator;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Async;


import static com.telefonica.euro_iaas.sdc.util.SystemPropertiesProvider.PRODUCT_INSTANCE_BASE_URL;

/**
 * Default implementation for ProductInstanceAsyncManager
 * 
 * @author Sergio Arroyo
 */
public class ProductInstanceAsyncManagerImpl implements ProductInstanceAsyncManager {
    private static Logger LOGGER = Logger.getLogger(ProductInstanceAsyncManagerImpl.class.getName());
    private ProductInstanceManager productInstanceManager;
    private TaskManager taskManager;
    private SystemPropertiesProvider propertiesProvider;
    private TaskNotificator taskNotificator;

    /**
     * {@inheritDoc}
     */
    @Async
    @Override
    public void install(VM vm, String vdc, ProductRelease product, List<Attribute> attributes, Task task,
            String callback) {
        try {
            ProductInstance productInstance = productInstanceManager.install(vm, vdc, product, attributes);
            updateSuccessTask(task, productInstance);
            LOGGER.info("Product " + product.getProduct().getName() + '-' + product.getVersion()
                    + " installed successfully");
        } catch (NodeExecutionException e) {
            String errorMsg = "The product " + product.getProduct().getName() + "-" + product.getVersion()
                    + " can not be installed in" + vm + "due to a problem when executing in node";
            ProductInstance instance = getInstaslledProduct(product, vm);
            if (instance != null) {
                updateErrorTask(instance, task, errorMsg, e);
            } else {
                updateErrorTask(task, errorMsg, e);
            }
        } catch (InvalidInstallProductRequestException e) {
            String errorMsg = e.getMessage();
            ProductInstance instance = getInstaslledProduct(product, vm);
            if (instance != null) {
                updateErrorTask(instance, task, errorMsg, e);
            } else {
                updateErrorTask(task, errorMsg, e);
            }
        } catch (AlreadyInstalledException e) {
            String errorMsg = e.getMessage();
            ProductInstance instance = getInstaslledProduct(product, vm);
            if (instance != null) {
                updateErrorTask(instance, task, errorMsg, e);
            } else {
                updateErrorTask(task, errorMsg, e);
            }

        } catch (Throwable e) {
            String errorMsg = "The product " + product.getProduct().getName() + "-" + product.getVersion()
                    + " can not be installed in" + vm;
            ProductInstance instance = getInstaslledProduct(product, vm);
            if (instance != null) {
                updateErrorTask(instance, task, errorMsg, e);
            } else {
                updateErrorTask(task, errorMsg, e);
            }
        } finally {
            notifyTask(callback, task);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Async
    @Override
    public void configure(ProductInstance productInstance, List<Attribute> configuration, Task task, String callback) {
        try {
            productInstanceManager.configure(productInstance, configuration);
            updateSuccessTask(task, productInstance);
            LOGGER.info("Product " + productInstance.getProductRelease().getProduct().getName() + '-'
                    + productInstance.getProductRelease().getVersion() + " configured successfully");
        } catch (FSMViolationException e) {
            updateErrorTask(productInstance, task, "The product "
                    + productInstance.getProductRelease().getProduct().getName() + "-"
                    + productInstance.getProductRelease().getVersion()
                    + " can not be configured due to previous status", e);
        } catch (NodeExecutionException e) {
            updateErrorTask(productInstance, task, "The product " + productInstance.getId()
                    + " can not be configured due to an error executing in node.", e);
        } catch (Throwable e) {
            updateErrorTask(productInstance, task, "The product " + productInstance.getId()
                    + " can not be configured due to an unexpected error.", e);
        } finally {
            notifyTask(callback, task);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Async
    @Override
    public void upgrade(ProductInstance productInstance, ProductRelease productRelease, Task task, String callback) {
        try {
            productInstanceManager.upgrade(productInstance, productRelease);
            updateSuccessTask(task, productInstance);
            LOGGER.info("Product " + productInstance.getProductRelease().getProduct().getName() + "-"
                    + productInstance.getProductRelease().getVersion() + " upgraded successfully");
        } catch (NotTransitableException e) {
            updateErrorTask(productInstance, task, "The product "
                    + productInstance.getProductRelease().getProduct().getName() + "-"
                    + productInstance.getProductRelease().getVersion() + " can not be updated to version "
                    + productRelease.getVersion(), e);

        } catch (FSMViolationException e) {
            updateErrorTask(productInstance, task, "The product " + productInstance.getId()
                    + " can not be uninstalled due to previous status", e);
        } catch (NodeExecutionException e) {
            updateErrorTask(productInstance, task, "The product " + productInstance.getId()
                    + " can not be upgrade to version " + productRelease.getVersion()
                    + "due to an error executing in node.", e);
        } catch (Throwable e) {
            updateErrorTask(productInstance, task, "The product " + productInstance.getId()
                    + " can not be upgrade to version " + productRelease.getVersion() + "due to unexpected error.", e);
        } finally {
            notifyTask(callback, task);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Async
    @Override
    public void uninstall(ProductInstance productInstance, Task task, String callback) {
        try {
            productInstanceManager.uninstall(productInstance);
            updateSuccessTask(task, productInstance);
            LOGGER.info("Product " + productInstance.getProductRelease().getProduct().getName() + "-"
                    + productInstance.getProductRelease().getVersion() + " uninstalled successfully");
        } catch (FSMViolationException e) {
            updateErrorTask(productInstance, task, "The product " + productInstance.getId()
                    + " can not be uninstalled due to previous status", e);
        } catch (NodeExecutionException e) {
            updateErrorTask(productInstance, task, "The product " + productInstance.getId()
                    + " can not be uninstalled due to an error executing in node.", e);
        } catch (Throwable e) {
            updateErrorTask(productInstance, task, "The product " + productInstance.getId()
                    + " can not be uninstalled due to unexpected error.", e);
        } finally {
            notifyTask(callback, task);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductInstance load(String vdc, Long id) throws EntityNotFoundException {
        return productInstanceManager.load(vdc, id);
    }

    @Override
    public ProductInstance load(String vdc, String name) throws EntityNotFoundException {

        return productInstanceManager.load(vdc, name);
    }

    @Override
    public ProductInstance loadByCriteria(ProductInstanceSearchCriteria criteria) throws EntityNotFoundException,
            NotUniqueResultException {
        return productInstanceManager.loadByCriteria(criteria);
    }

    public List<ProductInstance> findByCriteria(ProductInstanceSearchCriteria criteria) {
        return productInstanceManager.findByCriteria(criteria);
    }

    // //////// PRIVATE METHODS ///////////

    /*
     * Update the task with necessary information when the task is success.
     */
    private void updateSuccessTask(Task task, ProductInstance productInstance) {
        String piResource = MessageFormat.format(propertiesProvider.getProperty(PRODUCT_INSTANCE_BASE_URL),
                productInstance.getName(), // the name
                productInstance.getVm().getHostname(), // the hostname
                productInstance.getVm().getDomain(), // the domain
                productInstance.getProductRelease().getProduct().getName(), productInstance.getVdc()); // the product
        task.setResult(new TaskReference(piResource));
        task.setEndTime(new Date());
        task.setStatus(TaskStates.SUCCESS);
        taskManager.updateTask(task);
    }

    /*
     * Update the task with necessary information when the task is wrong and the product instance exists in the system.
     */
    private void updateErrorTask(ProductInstance productInstance, Task task, String message, Throwable t) {
        String piResource = MessageFormat.format(propertiesProvider.getProperty(PRODUCT_INSTANCE_BASE_URL),
        // productInstance.getId(), // the id
                productInstance.getName(), // the id
                productInstance.getVm().getHostname(), // the hostname
                productInstance.getVm().getDomain(), // the domain
                productInstance.getProductRelease().getProduct().getName(), productInstance.getVdc()); // the product
        task.setResult(new TaskReference(piResource));
        updateErrorTask(task, message, t);
    }

    /*
     * Update the task with necessary information when the task is wrong.
     */
    private void updateErrorTask(Task task, String message, Throwable t) {
        TaskError error = new TaskError(message);
        error.setMajorErrorCode(t.getMessage());
        error.setMinorErrorCode(t.getClass().getSimpleName());
        task.setEndTime(new Date());
        task.setStatus(TaskStates.ERROR);
        task.setError(error);
        taskManager.updateTask(task);
        LOGGER.info("An error occurs while executing a product action. See task " + task.getHref()
                + "for more information");
    }

    private ProductInstance getInstaslledProduct(ProductRelease product, VM vm) {
        ProductInstanceSearchCriteria criteria = new ProductInstanceSearchCriteria();
        criteria.setVm(vm);
        criteria.setProductRelease(product);
        try {
            return productInstanceManager.loadByCriteria(criteria);
        } catch (EntityNotFoundException e) {
            return null;
        } catch (NotUniqueResultException e) {
            return null;
        }
    }

    private void notifyTask(String url, Task task) {
        if (!StringUtils.isEmpty(url)) {
            taskNotificator.notify(url, task);
        }
    }

    // ////////// I.O.C ////////////

    /**
     * @param productInstanceManager
     *            the productInstanceManager to set
     */
    public void setProductInstanceManager(ProductInstanceManager productInstanceManager) {
        this.productInstanceManager = productInstanceManager;
    }

    /**
     * @param taskManager
     *            the taskManager to set
     */
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    /**
     * @param propertiesProvider
     *            the propertiesProvider to set
     */
    public void setPropertiesProvider(SystemPropertiesProvider propertiesProvider) {
        this.propertiesProvider = propertiesProvider;
    }

    /**
     * @param taskNotificator
     *            the taskNotificator to set
     */
    public void setTaskNotificator(TaskNotificator taskNotificator) {
        this.taskNotificator = taskNotificator;
    }

}