package com.foresee.logging;

import com.foresee.interfaces.logging.LoggerAbstraction;

/**
 * Created by bradley.bax on 11/2/2015.
 */
public class LogManager {
    private LoggerAbstraction _logger;
    public LogManager(LoggerAbstraction logger){
        _logger = logger;
    }

    public void info(String message){
        if(_logger == null){
            return;
        }
        _logger.info(message);
    }

    public void error(String message){
        if(_logger == null){
            return;
        }
        _logger.error(message);
    }

    public void error(String message, Exception exc){
        if(_logger == null){
            return;
        }
        _logger.error(message, exc);
    }

    public void warn(String message){
        if(_logger == null){
            return;
        }
        _logger.warn(message);
    }

    public void debug(String message){
        if(_logger == null){
            return;
        }
        _logger.debug(message);
    }
}
