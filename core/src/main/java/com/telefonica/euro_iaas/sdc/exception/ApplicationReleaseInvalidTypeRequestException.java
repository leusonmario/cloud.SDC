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
 * Exception thrown when the type of file to upload is incorrect.
 * 
 * @author Jesus M. Movilla
 */

@SuppressWarnings("serial")
public class ApplicationReleaseInvalidTypeRequestException extends Exception {

    public ApplicationReleaseInvalidTypeRequestException() {
        super();
    }

    public ApplicationReleaseInvalidTypeRequestException(String msg) {
        super(msg);
    }

    public ApplicationReleaseInvalidTypeRequestException(Throwable e) {
        super(e);
    }

    public ApplicationReleaseInvalidTypeRequestException(String msg, Throwable e) {
        super(msg, e);
    }
}