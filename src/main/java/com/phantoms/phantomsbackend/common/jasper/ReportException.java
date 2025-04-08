package com.phantoms.phantomsbackend.common.jasper;

public class ReportException extends RuntimeException{
    private static final long serialVersionUID = 1713805722071616375L;

    public ReportException() {
    }

    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportException(String message) {
        super(message);
    }

    public ReportException(Throwable cause) {
        super(cause);
    }
}
