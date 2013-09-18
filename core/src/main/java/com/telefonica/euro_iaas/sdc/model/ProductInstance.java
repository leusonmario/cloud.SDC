package com.telefonica.euro_iaas.sdc.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.telefonica.euro_iaas.sdc.model.dto.VM;
/**
 *  Defines a product that is installed in the system.
 *
 * @author Sergio Arroyo
 * @version $Id: $
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductInstance extends InstallableInstance
    implements Comparable<ProductInstance>{

    public final static String VM_FIELD = "vm";
    public final static String STATUS_FIELD = "status";
    public final static String PRODUCT_FIELD = "product";


    @ManyToOne(optional=false)
    private ProductRelease product;

    @Embedded
    private VM vm;

    /**
     * <p>Constructor for ProductInstance.</p>
     */
    public ProductInstance() {
        super();
    }

    /**
     * <p>Constructor for ProductInstance.</p>
     * @param id the id
     */
    public ProductInstance(Long id) {
       super(id);
    }

    /**
     * <p>Constructor for ProductInstance.</p>
     *
     * @param application a {@link com.telefonica.euro_iaas.sdc.model.Product} object.
     * @param status a {@link com.telefonica.euro_iaas.sdc.model.ProductInstance.Status} object.
     */
    public ProductInstance(ProductRelease product, Status status, VM vm) {
        super(status);
        this.product = product;
        this.vm = vm;
    }

    /**
     * <p>Getter for the field <code>product</code>.</p>
     *
     * @return the application
     */
    public ProductRelease getProduct() {
        return product;
    }

    /**
     * <p>Setter for the field <code>product</code>.</p>
     *
     * @param prodcut the product to set
     */
    public void setProduct(ProductRelease product) {
        this.product = product;
    }

    /**
     * @return the vm
     */
    public VM getVM() {
        return vm;
    }

    /**
     * @param vm the vm to set
     */
    public void setVM(VM vm) {
        this.vm = vm;
    }


    @Override
    public int compareTo(ProductInstance o) {
        return this.getProduct().getProduct().getName().compareTo(
                o.getProduct().getProduct().getName());
    }

}
