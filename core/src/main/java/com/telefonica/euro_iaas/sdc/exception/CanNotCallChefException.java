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

package com.telefonica.euro_iaas.sdc.exception;

/**
 * Exception thrown when Chef rest API return unexpected results.
 * 
 * @author Sergio Arroyo
 */
@SuppressWarnings("serial")
public class CanNotCallChefException extends Exception {
    public CanNotCallChefException() {
        super();
    }

    public CanNotCallChefException(String msg) {
        super(msg);
    }

    public CanNotCallChefException(Throwable e) {
        super(e);
    }

    public CanNotCallChefException(String msg, Throwable e) {
        super(msg, e);
    }

}