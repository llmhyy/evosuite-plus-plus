package com.test;

import org.evosuite.EvoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.OptionHelper;

public class LoggerUtils {

	public static void setupLogger(String outputFolder, String projectId) {
		/* just to trigger Evosuite for it to setup Logger before we hack */
		@SuppressWarnings("unused")
		String base_dir_path = EvoSuite.base_dir_path; 
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		appendFileAppender(context, FileUtils.getFilePath(outputFolder + "/log/", projectId + ".log"), "prj-file");
		appendFileAppender(context, FileUtils.getFilePath(outputFolder + "/log/evoTest" + ".log"), "evoTest-file");
	}
	
	public static Logger getLogger(Class<?> clazz) {
		Logger log = LoggerFactory.getLogger(clazz);
		((ch.qos.logback.classic.Logger)log).setLevel(Level.ALL);
		return log;
	}
	
	private static void appendFileAppender(LoggerContext context, String logFile, String name) {
		RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<ILoggingEvent>();
		appender.setContext(context);
		appender.setName(name);
		appender.setFile(logFile);
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		String logPattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{0} - %msg%n";
		encoder.setPattern(OptionHelper.substVars(logPattern, context));
		encoder.setContext(context);
		encoder.start();
		appender.setEncoder(encoder);
		// rollingPolicy
		FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
		rollingPolicy.setFileNamePattern(logFile + ".%i");
		appender.setRollingPolicy(rollingPolicy);
		rollingPolicy.setParent(appender);
		// triggeringPolicy
		SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>();
		triggeringPolicy.setMaxFileSize("10MB");
		appender.setTriggeringPolicy(triggeringPolicy);
		
		appender.start();
		context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).addAppender(appender);
	}
}
