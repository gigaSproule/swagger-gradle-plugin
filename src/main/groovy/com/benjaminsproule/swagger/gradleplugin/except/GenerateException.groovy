package com.benjaminsproule.swagger.gradleplugin.except

class GenerateException extends Exception {
    GenerateException(String errorMessage, Throwable cause) {
        super(errorMessage, cause)
    }

    GenerateException(String errorMessage) {
        super(errorMessage)
    }

    GenerateException(Exception e) {
        super(e)
    }
}
