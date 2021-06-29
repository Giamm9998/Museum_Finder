package model;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
    private static Log instance = null;
    private Logger logger = null;

    Log() {
        try {
            setupLogger();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }

    public static String getStringStackTrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    private void setupLogger() throws IOException {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %2$s %5$s%6$s%n");
        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        //logger.setUseParentHandlers(false);
        logger.setLevel(Level.FINE);
        FileHandler fileHandler = new FileHandler("log/museumFinder.log");
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);

        logger.addHandler(fileHandler);
    }

    public Logger getLogger() {
        return logger;
    }
}
