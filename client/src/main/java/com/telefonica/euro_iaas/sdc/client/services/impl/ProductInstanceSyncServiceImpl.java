/**
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U <br>
 * This file is part of FI-WARE project.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * </p>
 * <p>
 * You may obtain a copy of the License at:<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * </p>
 * <p>
 * See the License for the specific language governing permissions and limitations under the License.
 * </p>
 * <p>
 * For those usages not covered by the Apache version 2.0 License please contact with opensource@tid.es
 * </p>
 */

package com.telefonica.euro_iaas.sdc.client.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.sdc.client.exception.InvalidExecutionException;
import com.telefonica.euro_iaas.sdc.client.exception.MaxTimeWaitingExceedException;
import com.telefonica.euro_iaas.sdc.client.exception.ResourceNotFoundException;
import com.telefonica.euro_iaas.sdc.client.services.ProductInstanceService;
import com.telefonica.euro_iaas.sdc.client.services.ProductInstanceSyncService;
import com.telefonica.euro_iaas.sdc.client.services.TaskService;
import com.telefonica.euro_iaas.sdc.model.Attribute;
import com.telefonica.euro_iaas.sdc.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.sdc.model.ProductInstance;
import com.telefonica.euro_iaas.sdc.model.Task;
import com.telefonica.euro_iaas.sdc.model.Task.TaskStates;
import com.telefonica.euro_iaas.sdc.model.dto.ProductInstanceDto;

/**
 * Default @link ProductInsatnceSyncService implementation using active waiting
 * 
 * @author Sergio Arroyo
 */
public class ProductInstanceSyncServiceImpl implements ProductInstanceSyncService {
	private static Logger log = LoggerFactory.getLogger(ProductInstanceSyncServiceImpl.class.getName());
    private ProductInstanceService productInstanceService;
    private TaskService taskService;

    /**
     * @param productInstanceService
     * @param taskService
     */
    public ProductInstanceSyncServiceImpl(ProductInstanceService productInstanceService, TaskService taskService) {
        this.productInstanceService = productInstanceService;
        this.taskService = taskService;
    }

    /**
     * {@inheritDoc}
     */

    public ProductInstanceDto upgrade(String vdc, String name, String version, String token) throws MaxTimeWaitingExceedException,
            InvalidExecutionException {
        Task task = productInstanceService.upgrade(vdc, name, version, null, token);
        return this.waitForTask(task, token);
    }

    /**
     * {@inheritDoc}
     */

    public ProductInstanceDto configure(String vdc, String name, List<Attribute> arguments, String token)
            throws MaxTimeWaitingExceedException, InvalidExecutionException {
        Task task = productInstanceService.configure(vdc, name, null, arguments, token);
        return this.waitForTask(task, token);
    }

    /**
     * {@inheritDoc}
     */
 
    public ProductInstanceDto uninstall(String vdc, String name, String token) throws MaxTimeWaitingExceedException,
            InvalidExecutionException {
        Task task = productInstanceService.uninstall(vdc, name, null, token);
        return this.waitForTask(task, token);
    }

    /**
     * {@inheritDoc}
     */

    public ProductInstanceDto loadUrl(String url, String token, String tenant) throws ResourceNotFoundException {
        return productInstanceService.loadUrl(url, token, tenant);
    }

    /**
     * {@inheritDoc}
     */

    public ProductInstanceDto install(String vdc, ProductInstanceDto product, String token) throws MaxTimeWaitingExceedException,
            InvalidExecutionException {
    	log.debug ("vdc " + vdc + " product" + product.getProduct().getName());
        Task task = productInstanceService.install(vdc, product, null, token);
        log.debug (task.getHref());
        log.debug (task.getStatus().name());
        return this.waitForTask(task, token);
    }

    /**
     * {@inheritDoc}
     */

    public List<ProductInstanceDto> findAll(String hostname, String domain, String ip, String fqn, Integer page,
            Integer pageSize, String orderBy, String orderType, Status status, String vdc, String productName, String token) {
        return productInstanceService.findAll(hostname, domain, ip, fqn, page, pageSize, orderBy, orderType, status,
                vdc, productName, token);
    }

    /**
     * {@inheritDoc}
     */

    public ProductInstanceDto load(String vdc, String name, String token) throws ResourceNotFoundException {
        return productInstanceService.loadUrl(vdc, name, token);
    }

    private ProductInstanceDto waitForTask(Task task, String token) throws MaxTimeWaitingExceedException, InvalidExecutionException {
        task = taskService.waitForTask(task.getHref(), task.getVdc(), token);
        if (!task.getStatus().equals(TaskStates.SUCCESS)) {
            throw new InvalidExecutionException(task.getError().getMessage());
        }
        try {
            return productInstanceService.load(task.getResult().getHref(),task.getVdc(), token);
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
