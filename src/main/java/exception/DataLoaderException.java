package exception;

import log.DataLogger;

public class DataLoaderException extends Exception {
    public DataLoaderException(Class<?> where, String what) {
        super(what);
        DataLogger.error(where, what);
    }
}
