package log;

import org.apache.log4j.Logger;

public class DataLogger {


    private static final Logger logger = Logger.getLogger(DataLogger.class);

    private static String constructMessage(Class<?> where, String what) {
        return where.getName() + " : " + what;
    }

    public static void info(Class<?> where, String what) {

        logger.info(constructMessage(where, what));
    }

    public static void info(String what) {
        logger.info(what);
    }

    public static void error(Class<?> where, String what) {
        logger.error(constructMessage(where, what));
    }

    public static void warn(Class<?> where, String what) {
        logger.warn(constructMessage(where, what));
    }
}
