/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitive.bp.poc.logger;

import java.io.PrintStream;
import org.drools.core.spi.KnowledgeHelper;
import org.slf4j.Logger;

/**
 *
 * @author esteban
 */
public class RulesLoggerHelper {
    
    private static PrintStream buffer;
    
    public static void setBuffer(PrintStream buffer ){
        RulesLoggerHelper.buffer = buffer;
    }
    
    private static String formatMessage(KnowledgeHelper helper, String originalMessageFormat){
        return formatMessage(helper.getRule().getName(), originalMessageFormat);
    }  
    
    private static String formatMessage(String tag, String originalMessageFormat){
        String result = "\t["+tag+"] ->   "+originalMessageFormat;
        if (buffer != null){
            buffer.println(result);
        }
        return result;
    }  
    
    public static void debug(Logger logger, String tag, String messageFormat, Object... args){
        logger.debug(formatMessage(tag, messageFormat), args);
    }
    
    public static void debug(Logger logger, KnowledgeHelper helper, String messageFormat, Object... args){
        logger.debug(formatMessage(helper, messageFormat), args);
    }
    
    public static void error(Logger logger, KnowledgeHelper helper, String messageFormat, Object... args){
        logger.error(formatMessage(helper, messageFormat), args);
    }
    
    public static void error(Logger logger, String tag, String messageFormat, Object... args){
        logger.error(formatMessage(tag, messageFormat), args);
    }
    
    public static void info(Logger logger, KnowledgeHelper helper, String messageFormat, Object... args){
        logger.info(formatMessage(helper, messageFormat), args);
    }
    
    public static void info(Logger logger, String tag, String messageFormat, Object... args){
        logger.info(formatMessage(tag, messageFormat), args);
    }
    
    public static void trace(Logger logger, KnowledgeHelper helper, String messageFormat, Object... args){
        logger.trace(formatMessage(helper, messageFormat), args);
    }
    
    public static void trace(Logger logger, String tag, String messageFormat, Object... args){
        logger.trace(formatMessage(tag, messageFormat), args);
    }
    
    public static void warn(Logger logger, KnowledgeHelper helper, String messageFormat, Object... args){
        logger.warn(formatMessage(helper, messageFormat), args);
    }
    
    public static void warn(Logger logger, String tag, String messageFormat, Object... args){
        logger.warn(formatMessage(tag, messageFormat), args);
    }
    
}
