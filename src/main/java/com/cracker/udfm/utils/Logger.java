
package com.cracker.udfm.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import com.cracker.udfm.exception.BaseUidmException;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * 自定义日志类
 *
 * @author lizh<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2022-07-03
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public final class Logger {
    public static boolean forceJuli = false;
    public static org.apache.log4j.Logger log4j;
    public static final String COMPANY = "kpsz";
    public static java.util.logging.Logger juli = java.util.logging.Logger.getLogger(COMPANY);
    public static boolean configuredManually = false;

    public Logger() {
    }

    public static void init(String basePath) {
        if (log4j == null) {
            String log4jPath = null;
            String log4jPathXml = String.format("%s/%s", basePath, "log4j.xml");
            File file = new File(log4jPathXml);
            if (file.exists()) {
                log4jPath = log4jPathXml;
            } else {
                String log4jPathProp = String.format("%s/%s", basePath, "log4j.properties");
                file = new File(log4jPathProp);
                if (file.exists()) {
                    log4jPath = log4jPathProp;
                }
            }

            if (log4jPath != null) {
                configuredManually = true;
                boolean isXmlConfig = log4jPath.endsWith(".xml");
                if (isXmlConfig) {
                    DOMConfigurator.configureAndWatch(log4jPath, 60000L);
                } else {
                    PropertyConfigurator.configureAndWatch(log4jPath, 60000L);
                }

                log4j = org.apache.log4j.Logger.getLogger(Logger.class);
            }

            URL log4jConf = null;
            if (log4jPath == null) {
                log4jConf = Logger.class.getResource("/log4j.properties");
                if (log4jConf != null) {
                    configuredManually = true;
                    PropertyConfigurator.configureAndWatch(log4jConf.getPath(), 60000L);
                } else {
                    Properties shutUp = new Properties();
                    shutUp.setProperty("log4j.rootLogger", "OFF");
                    PropertyConfigurator.configure(shutUp);
                }
            }

            if (log4jPath != null || log4jConf != null) {
                log4j = LogManager.getLogger(Logger.class);
            }

        }
    }

    public static org.apache.log4j.Logger getLogger(Class classtype) {
        return LogManager.getLogger(classtype);
    }

    public static org.apache.log4j.Logger getLogger(String classtype) {
        return LogManager.getLogger(classtype);
    }

    public static void setUp(String level) {
        if (!forceJuli && log4j != null) {
            log4j.setLevel(Level.toLevel(level));
        } else {
            juli.setLevel(toJuliLevel(level));
        }

    }

    static java.util.logging.Level toJuliLevel(String level) {
        java.util.logging.Level juliLevel = java.util.logging.Level.INFO;
        if ("ERROR".equals(level) || "FATAL".equals(level)) {
            juliLevel = java.util.logging.Level.SEVERE;
        }

        if ("WARN".equals(level)) {
            juliLevel = java.util.logging.Level.WARNING;
        }

        if ("DEBUG".equals(level)) {
            juliLevel = java.util.logging.Level.FINE;
        }

        if ("TRACE".equals(level)) {
            juliLevel = java.util.logging.Level.FINEST;
        }

        if ("ALL".equals(level)) {
            juliLevel = java.util.logging.Level.ALL;
        }

        if ("OFF".equals(level)) {
            juliLevel = java.util.logging.Level.OFF;
        }

        return juliLevel;
    }

    public static boolean isDebugEnabled() {
        return !forceJuli && log4j != null ? log4j.isDebugEnabled() : juli.isLoggable(java.util.logging.Level.FINE);
    }

    public static boolean isTraceEnabled() {
        return !forceJuli && log4j != null ? log4j.isTraceEnabled() : juli.isLoggable(java.util.logging.Level.FINEST);
    }

    public static boolean isEnabledFor(String level) {
        Level log4jLevel = Level.toLevel(level);
        if (!forceJuli && log4j != null) {
            return log4j.isEnabledFor(log4jLevel);
        } else {
            java.util.logging.Level julLevel = toJuliLevel(log4jLevel.toString());
            return juli.isLoggable(julLevel);
        }
    }

    public static void trace(String message, Object... args) {
        if (!forceJuli && log4j != null) {
            try {
                log4j.trace(format(message, args));
            } catch (Throwable var3) {
                log4j.error("Oops. Error in Logger !", var3);
            }
        } else {
            try {
                juli.finest(format(message, args));
            } catch (Throwable var4) {
                juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var4);
            }
        }

    }

    public static void debug(String message, Object... args) {
        if (isDebugEnabled()) {
            if (!forceJuli && log4j != null) {
                try {
                    log4j.debug(format(message, args));
                } catch (Throwable var3) {
                    log4j.error("Oops. Error in Logger !", var3);
                }
            } else {
                try {
                    juli.fine(format(message, args));
                } catch (Throwable var4) {
                    juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var4);
                }
            }

        }
    }

    public static void debug(Throwable e, String message, Object... args) {
        if (isDebugEnabled()) {
            if (!forceJuli && log4j != null) {
                try {
                    if (!niceThrowable(Level.DEBUG, e, message, args)) {
                        log4j.debug(format(message, args), e);
                    }
                } catch (Throwable var4) {
                    log4j.error("Oops. Error in Logger !", var4);
                }
            } else {
                try {
                    if (!niceThrowable(Level.DEBUG, e, message, args)) {
                        juli.log(java.util.logging.Level.CONFIG, format(message, args), e);
                    }
                } catch (Throwable var5) {
                    juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var5);
                }
            }

        }
    }

    public static void event(String logger, String message, Object... args) {
        if (!forceJuli && log4j != null) {
            try {
                org.apache.log4j.Logger.getLogger("hws." + logger).info(format(message, args));
            } catch (Throwable var4) {
                log4j.error("Oops. Error in Logger !", var4);
            }
        } else {
            try {
                java.util.logging.Logger.getLogger("hws." + logger).info(format(message, args));
            } catch (Throwable var5) {
                juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var5);
            }
        }

    }

    public static boolean isEventEnabled(String logger) {
        if (!forceJuli && log4j != null) {
            try {
                return LogManager.exists("hws." + logger) != null;
            } catch (Throwable var2) {
                log4j.error("Oops. Error in Logger !", var2);
                return false;
            }
        } else {
            try {
                return java.util.logging.Logger.getLogger("hws." + logger).isLoggable(java.util.logging.Level.INFO);
            } catch (Throwable var3) {
                juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var3);
                return false;
            }
        }
    }

    public static void info(String message, Object... args) {
        if (!forceJuli && log4j != null) {
            try {
                log4j.info(format(message, args));
            } catch (Throwable var3) {
                log4j.error("Oops. Error in Logger !", var3);
            }
        } else {
            try {
                juli.info(format(message, args));
            } catch (Throwable var4) {
                juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var4);
            }
        }

    }

    public static void info(Throwable e, String message, Object... args) {
        if (!forceJuli && log4j != null) {
            try {
                if (!niceThrowable(Level.INFO, e, message, args)) {
                    log4j.info(format(message, args), e);
                }
            } catch (Throwable var4) {
                log4j.error("Oops. Error in Logger !", var4);
            }
        } else {
            try {
                if (!niceThrowable(Level.INFO, e, message, args)) {
                    juli.log(java.util.logging.Level.INFO, format(message, args), e);
                }
            } catch (Throwable var5) {
                juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var5);
            }
        }

    }

    public static void warn(String message, Object... args) {
        if (!forceJuli && log4j != null) {
            try {
                log4j.warn(format(message, args));
            } catch (Throwable var3) {
                log4j.error("Oops. Error in Logger !", var3);
            }
        } else {
            try {
                juli.warning(format(message, args));
            } catch (Throwable var4) {
                juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var4);
            }
        }

    }

    public static void warn(Throwable e, String message, Object... args) {
        if (!forceJuli && log4j != null) {
            try {
                if (!niceThrowable(Level.WARN, e, message, args)) {
                    log4j.warn(format(message, args), e);
                }
            } catch (Throwable var4) {
                log4j.error("Oops. Error in Logger !", var4);
            }
        } else {
            try {
                if (!niceThrowable(Level.WARN, e, message, args)) {
                    juli.log(java.util.logging.Level.WARNING, format(message, args), e);
                }
            } catch (Throwable var5) {
                juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var5);
            }
        }

    }

    public static void error(String message, Object... args) {
        if (!forceJuli && log4j != null) {
            try {
                log4j.error(format(message, args));
            } catch (Throwable var3) {
                log4j.error("Oops. Error in Logger !", var3);
            }
        } else {
            try {
                juli.severe(format(message, args));
            } catch (Throwable var4) {
                juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var4);
            }
        }

    }

    public static void error(Throwable e, String message, Object... args) {
        if (!forceJuli && log4j != null) {
            try {
                if (!niceThrowable(Level.ERROR, e, message, args)) {
                    log4j.error(format(message, args), e);
                }
            } catch (Throwable var4) {
                log4j.error("Oops. Error in Logger !", var4);
            }
        } else {
            try {
                if (!niceThrowable(Level.ERROR, e, message, args)) {
                    juli.log(java.util.logging.Level.SEVERE, format(message, args), e);
                }
            } catch (Throwable var5) {
                juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var5);
            }
        }

    }

    public static void fatal(String message, Object... args) {
        if (!forceJuli && log4j != null) {
            try {
                log4j.fatal(format(message, args));
            } catch (Throwable var3) {
                log4j.error("Oops. Error in Logger !", var3);
            }
        } else {
            try {
                juli.severe(format(message, args));
            } catch (Throwable var4) {
                juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var4);
            }
        }

    }

    public static void fatal(Throwable e, String message, Object... args) {
        if (!forceJuli && log4j != null) {
            try {
                if (!niceThrowable(Level.FATAL, e, message, args)) {
                    log4j.fatal(format(message, args), e);
                }
            } catch (Throwable var4) {
                log4j.error("Oops. Error in Logger !", var4);
            }
        } else {
            try {
                if (!niceThrowable(Level.FATAL, e, message, args)) {
                    juli.log(java.util.logging.Level.SEVERE, format(message, args), e);
                }
            } catch (Throwable var5) {
                juli.log(java.util.logging.Level.SEVERE, "Oops. Error in Logger !", var5);
            }
        }

    }

    static boolean niceThrowable(Level level, Throwable e, String message, Object... args) {
        if (!(e instanceof Exception)) {
            return false;
        } else {
            Throwable toClean = e;

            for (int i = 0; i < 5; ++i) {
                List<StackTraceElement> cleanTrace = new ArrayList<>();
                StackTraceElement[] var7 = toClean.getStackTrace();
                int var8 = var7.length;

                for (int var9 = 0; var9 < var8; ++var9) {
                    StackTraceElement se = var7[var9];
                    if (se.getClassName().startsWith("kpsz.server.HwsHandler$NettyInvocation")) {
                        cleanTrace.add(new StackTraceElement("Invocation", "HTTP Request", "KPSZ", -1));
                        break;
                    }

                    if (se.getClassName().startsWith("kpsz.server.HwsHandler$SslNettyInvocation")) {
                        cleanTrace.add(new StackTraceElement("Invocation", "HTTP Request", "KPSZ", -1));
                        break;
                    }

                    if (se.getClassName().startsWith("kpsz.server.HwsHandler") && "messageReceived".equals(se.getMethodName())) {
                        cleanTrace.add(new StackTraceElement("Invocation", "Message Received", "KPSZ", -1));
                        break;
                    }

                    if (!se.getClassName().startsWith("sun.reflect.") && !se.getClassName().startsWith("java.lang.reflect.") && !se.getClassName().startsWith("com.mchange.v2.c3p0.") && !se.getClassName().startsWith("scala.tools.") && !se.getClassName().startsWith("scala.collection.")) {
                        cleanTrace.add(se);
                    }
                }

                toClean.setStackTrace(cleanTrace.toArray(new StackTraceElement[0]));
                toClean = toClean.getCause();
                if (toClean == null) {
                    break;
                }
            }

            StringWriter sw = new StringWriter();
            if (e instanceof BaseUidmException) {
                BaseUidmException exception = (BaseUidmException) e;
                PrintWriter errorOut = new PrintWriter(sw);
                errorOut.println("");
                errorOut.println("");
                errorOut.println("@" + exception.getId());
                errorOut.println(format(message, args));
                errorOut.println("");
                if (exception.isSourceAvailable()) {
                    errorOut.println(exception.getErrorTitle() + " (In " + exception.getSourceFile() + " around line " + exception.getLineNumber() + ")");
                } else {
                    errorOut.println(exception.getErrorTitle());
                }

                errorOut.println(exception.getErrorDescription().replaceAll("</?\\w+/?>", "").replace("\n", " "));
            } else {
                sw.append(format(message, args));
            }

            try {
                if (!forceJuli && log4j != null) {
                    log4j.log(level, sw.toString(), e);
                } else {
                    juli.log(toJuliLevel(level.toString()), sw.toString(), e);
                }
            } catch (Exception var11) {
                log4j.error("Oops. Error in Logger !", var11);
            }

            return true;
        }
    }

    static String format(String msg, Object... args) {
        try {
            return args != null && args.length > 0 ? String.format(msg, args) : msg;
        } catch (Exception var3) {
            return msg;
        }
    }

    static String getCallerClassName() {
        return getCallerClassName(4);
    }

    static String getCallerClassName(int level) {
        Logger.CallInfo ci = getCallerInformations(level);
        return ci.className;
    }

    static Logger.CallInfo getCallerInformations(int level) {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StackTraceElement caller = callStack[level];
        return new Logger.CallInfo(caller.getClassName(), caller.getMethodName());
    }

    public static class JuliToLog4jHandler extends Handler {
        public JuliToLog4jHandler() {
        }

        @Override
        public void publish(LogRecord record) {
            org.apache.log4j.Logger log4j = getTargetLogger(record.getLoggerName());
            Priority priority = this.toLog4j(record.getLevel());
            log4j.log(priority, this.toLog4jMessage(record), record.getThrown());
        }

        static org.apache.log4j.Logger getTargetLogger(String loggerName) {
            return org.apache.log4j.Logger.getLogger(loggerName);
        }

        public static org.apache.log4j.Logger getTargetLogger(Class<?> clazz) {
            return getTargetLogger(clazz.getName());
        }

        private String toLog4jMessage(LogRecord record) {
            String message = record.getMessage();

            try {
                Object[] parameters = record.getParameters();
                if (parameters != null && parameters.length != 0 && (message.contains("{0}") || message.contains("{1}") || message.contains("{2}") || message.contains("{3}"))) {
                    message = MessageFormat.format(message, parameters);
                }
            } catch (Exception var4) {
            }

            return message;
        }

        private Level toLog4j(java.util.logging.Level level) {
            if (java.util.logging.Level.SEVERE == level) {
                return Level.ERROR;
            } else if (java.util.logging.Level.WARNING == level) {
                return Level.WARN;
            } else if (java.util.logging.Level.INFO == level) {
                return Level.INFO;
            } else {
                return java.util.logging.Level.OFF == level ? Level.TRACE : Level.TRACE;
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }

    static class CallInfo {
        public String className;
        public String methodName;

        public CallInfo() {
        }

        public CallInfo(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }
    }
}
