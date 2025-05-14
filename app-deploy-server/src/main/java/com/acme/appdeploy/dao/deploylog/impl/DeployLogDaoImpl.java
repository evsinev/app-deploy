package com.acme.appdeploy.dao.deploylog.impl;

import com.acme.appdeploy.dao.deploylog.IDeployLogDao;
import com.acme.appdeploy.dao.deploylog.entity.TDeploy;
import com.acme.appdeploy.dao.deploylog.entity.TDeployLogEvent;
import com.acme.appdeploy.dao.deploylog.model.DeployStatus;
import com.acme.appdeploy.util.GsonFile;
import com.acme.appdeploy.util.ReadWriteLockSupport;
import com.acme.appdeploy.util.SafeFiles;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static com.acme.appdeploy.util.SafeFiles.listSortedFiles;
import static com.acme.appdeploy.util.Sequences.nextDeployLogId;

public class DeployLogDaoImpl implements IDeployLogDao {


    private final GsonFile<TDeploy>         deployFile = new GsonFile<>(TDeploy.class);
    private final GsonFile<TDeployLogEvent> eventFile  = new GsonFile<>(TDeployLogEvent.class);

    private final ReadWriteLockSupport lock = new ReadWriteLockSupport();

    private final File baseDir;

    public DeployLogDaoImpl(File baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void createDeploy(TDeploy aDeploy) {
        lock.write(() -> {
            File dir = getDeployDir(aDeploy.getDeployId());
            deployFile.save(aDeploy, new File(dir, "_info.json"));
        });
    }

    @Override
    public void saveDeploy(TDeploy aDeploy) {
        lock.write(() -> {
            File dir = getDeployDir(aDeploy.getDeployId());
            deployFile.save(aDeploy, new File(dir, "_info.json"));
        });
    }

    private File getDeployDir(String aDeployId) {
        return SafeFiles.createDirs(new File(baseDir, aDeployId));
    }

    @Override
    public void addDeployLog(TDeployLogEvent aEvent) {
        File dir = getDeployDir(aEvent.getDeployId());
        File file = new File(dir, "event-" + nextDeployLogId() + ".json");
        eventFile.save(aEvent, file);
    }

    @Override
    public TDeploy getDeployById(String aDeployId) {
        return lock.read(() -> {
            File dir = getDeployDir(aDeployId);
            return loadFromDeployDir(dir);
        });
    }

    private TDeploy loadFromDeployDir(File dir) {
        File file = new File(dir, "_info.json");
        return deployFile.load(file).orElseThrow(() -> new IllegalArgumentException("No deploy found for id: " + dir.getName()));
    }

    @Override
    public List<TDeployLogEvent> getDeployLogEvents(String aDeployId) {
        File dir = getDeployDir(aDeployId);

        return listSortedFiles(dir, file -> file.isFile() && file.getName().startsWith("event-"))
                .stream()
                .map(file -> eventFile.load(file).orElseThrow(() -> new IllegalStateException("Cannot load event file: " + file)))
                .toList();
    }

    @Override
    public void updateStatusWithMessage(String aDeployId, DeployStatus aStatus, String aMessage) {
        saveDeploy(
                getDeployById(aDeployId).toBuilder()
                        .status(aStatus)
                        .statusErrorMessage(aMessage)
                        .build()
        );
    }

    @Override
    public void updateStatus(String aDeployId, DeployStatus aStatus) {
        saveDeploy(
                getDeployById(aDeployId).toBuilder()
                .status(aStatus)
                .build()
        );
    }

    @Override
    public List<TDeploy> listRecentDeploys(int aLimit) {
        return listSortedFiles(baseDir, File::isDirectory)
                .stream()
                .sorted((left, right) -> right.getName().compareTo(left.getName()))
                .limit(aLimit)
                .map(this::loadFromDeployDir)
                .toList();
    }
}
