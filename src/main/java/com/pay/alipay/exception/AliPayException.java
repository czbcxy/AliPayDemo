package com.pay.alipay.exception;

import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.ContainerElementNodeBuilderCustomizableContext;

/**
 * Author: {@link ContainerElementNodeBuilderCustomizableContext}
 */
@SuppressWarnings("serial")
public class AliPayException extends RuntimeException {
    public AliPayException() {
        super();
    }

    public AliPayException(String message) {
        super(message);
    }

    public AliPayException(String message, Throwable cause) {
        super(message, cause);
    }

    public AliPayException(Throwable cause) {
        super(cause);
    }

    protected AliPayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
