package com.benjaminsproule.swagger.gradleplugin.logger

import org.codehaus.plexus.logging.Logger
import org.slf4j.LoggerFactory

/**
 * An slf4j wrapper for maven.
 */
class Slf4jWrapper implements Logger {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger('swagger-gradle-plugin')

    @Override
    void debug(String s) {
        LOG.debug(s)
    }

    @Override
    void debug(String s, Throwable throwable) {
        LOG.debug(s, throwable)
    }

    @Override
    boolean isDebugEnabled() {
        return LOG.isDebugEnabled()
    }

    @Override
    void info(String s) {
        LOG.info(s)
    }

    @Override
    void info(String s, Throwable throwable) {
        LOG.info(s, throwable)
    }

    @Override
    boolean isInfoEnabled() {
        return LOG.isInfoEnabled()
    }

    @Override
    void warn(String s) {
        LOG.warn(s)
    }

    @Override
    void warn(String s, Throwable throwable) {
        LOG.warn(s, throwable)
    }

    @Override
    boolean isWarnEnabled() {
        return LOG.isWarnEnabled()
    }

    @Override
    void error(String s) {
        LOG.error(s)
    }

    @Override
    void error(String s, Throwable throwable) {
        LOG.error(s, throwable)
    }

    @Override
    boolean isErrorEnabled() {
        return LOG.isErrorEnabled()
    }

    @Override
    void fatalError(String s) {
        LOG.error(s)
    }

    @Override
    void fatalError(String s, Throwable throwable) {
        LOG.error(s, throwable)
    }

    @Override
    boolean isFatalErrorEnabled() {
        return LOG.isErrorEnabled()
    }

    @Override
    Logger getChildLogger(String s) {
        return this
    }

    @Override
    int getThreshold() {
        return 0
    }

    @Override
    String getName() {
        return LOG.getName()
    }
}
