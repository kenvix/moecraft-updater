//--------------------------------------------------
// Class UpdateCriticalException
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

public class UpdateCriticalException extends RuntimeException {
    private int exitCode;
    private Exception originalException;

    public UpdateCriticalException(String message, int exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    public UpdateCriticalException(String message, int exitCode, Exception originalException) {
        super(message, originalException);
        this.originalException = originalException;
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }

    public Exception getOriginalException() {
        return originalException;
    }
}
