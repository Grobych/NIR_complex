package nir.util.logging;

import nir.util.getcallerclass.ThrowableStackClassMethod;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    public final static Logger logger = Logger.getLogger(Log.class);
    public static LogString logString = new LogString();

    public static void log(String info, String from){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String time = formatter.format(date);

        String log = time + "  " + from + "    " + info;
        logString.setLog(log);
    }
    public static void info(Object info){
        String from = new ThrowableStackClassMethod().getCallerClassName(2);
        logger.info(from + " :   " + info.toString());
        log(info.toString(),from);
    }

    public static void debug(Object debug){
        String from = new ThrowableStackClassMethod().getCallerClassName(2);
        logger.debug(from + " :   " + debug.toString());
        log(debug.toString(),from);
    }

    public static void error(Object error){
        String from = new ThrowableStackClassMethod().getCallerClassName(2);
        logger.error(from + " :   " + error.toString());
        log(error.toString(),from);
    }
}
