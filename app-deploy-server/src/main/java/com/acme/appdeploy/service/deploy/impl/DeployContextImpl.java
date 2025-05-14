package com.acme.appdeploy.service.deploy.impl;

import com.acme.appdeploy.dao.deploylog.IDeployLogDao;
import com.acme.appdeploy.dao.deploylog.entity.TDeployLogEvent;
import com.acme.appdeploy.dao.deploylog.model.DeployStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.StringWriter;

public class DeployContextImpl implements IDeployContext {

    private static final Logger LOG = LoggerFactory.getLogger( DeployContextImpl.class );

    private final String deployId;
    private final IDeployLogDao deployLogDao;

    public DeployContextImpl(String deployId, IDeployLogDao deployLogDao) {
        this.deployId     = deployId;
        this.deployLogDao = deployLogDao;
    }

    @Override
    public void updateStatus(DeployStatus aStatus) {
        deployLogDao.updateStatus(deployId, aStatus);
        debug("Changed status to {}", aStatus);
    }

    @Override
    public void updateStatusWithMessage(DeployStatus aStatus, String aMessage) {
        deployLogDao.updateStatusWithMessage(deployId, aStatus, aMessage);
        debug("Changed status to {} {}", aStatus, aMessage);
    }

    @Override
    public void debug(String aTemplate, Object ... args) {
        LOG.debug(aTemplate, args);
        log("DEBUG", aTemplate, args);
    }

    @Override
    public void error(String aTemplate, Object... args) {
        LOG.error(aTemplate, args);
        log("ERROR", aTemplate, args);
    }

    private void log(String aLevel, String aTemplate, Object ... args) {
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(aTemplate, args);

        String message = getMessageWithException(formattingTuple);

        TDeployLogEvent event = TDeployLogEvent.builder()
                .deployId(deployId)
                .level(aLevel)
                .epoch(System.currentTimeMillis())
                .message(message)
                .build();

        deployLogDao.addDeployLog(event);
    }

    private String getMessageWithException(FormattingTuple formattingTuple) {
        if (formattingTuple.getThrowable() == null) {
            return formattingTuple.getMessage();
        }

        StringWriter writer = new StringWriter();
        writer.write(formattingTuple.getMessage());
        writer.write("\n");
        formattingTuple.getThrowable().printStackTrace(new java.io.PrintWriter(writer));
        return writer.toString();
    }
}
