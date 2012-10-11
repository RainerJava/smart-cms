/*
 *
 * This is a simple Content Management System (CMS)
 * Copyright (C) 2011 Imran M Yousuf (imyousuf@smartitengineering.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.smartitengineering.cms.spi.lock.impl.distributed;

import com.smartitengineering.cms.api.factory.write.Lock;
import com.smartitengineering.cms.spi.lock.Key;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imyousuf
 */
public class ZKLock implements Lock, Watcher, LockTimeoutListener {

  protected final ZKConfig config;
  protected final Key key;
  protected String localLockId;
  protected final ReentrantLock lock = new ReentrantLock();
  protected final transient Logger logger = LoggerFactory.getLogger(getClass());

  protected ZKLock(ZKConfig config, Key key) {
    this.config = config;
    this.key = key;
  }

  public boolean isLockOwned() {
    lock.lock();
    try {
      return StringUtils.isNotBlank(localLockId);
    }
    finally {
      lock.unlock();
    }
  }

  public void lock() {
    boolean locked = false;
    try {
      locked = tryLock(1, TimeUnit.DAYS);
    }
    catch (Exception ex) {
      logger.warn(ex.getMessage(), ex);
    }
    if (!locked) {
      throw new IllegalStateException("Could not attain lock!");
    }
  }

  public boolean tryLock() {
    try {
      return tryLock(-1, TimeUnit.DAYS);
    }
    catch (Exception ex) {
      logger.warn(ex.getMessage(), ex);
      return false;
    }
  }

  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    if (logger.isDebugEnabled()) {
      logger.debug("Attempting to attain lock with wait option of " + time + " " + unit.name());
    }
    if (isLockOwned()) {
      return true;
    }
    lock.lock();
    try {
      final long waitInMilliSeconds = time > 0 ? TimeUnit.MILLISECONDS.convert(time, unit) : time;
      final long start = System.currentTimeMillis();
      String lockId = config.getRegistrar().lock(key, this, waitInMilliSeconds);
      final long availableMillisForRemoteLock = waitInMilliSeconds - (System.currentTimeMillis() - start);
      return tryRemoteLock(lockId, availableMillisForRemoteLock);
    }
    finally {
      lock.unlock();
    }
  }

  protected boolean tryRemoteLock(String lockId, final long availableMillisForRemoteLock) throws IllegalStateException,
                                                                                                 InterruptedException {
    final LocalLockRegistrar registrar = config.getRegistrar();
    final ZooKeeper keeper = config.getZooKeeper();
    final String node = getNode();
    if (logger.isDebugEnabled()) {
      logger.debug("Attained local lock " + lockId);
    }
    try {
      if (StringUtils.isNotBlank(lockId)) {
        keeper.create(node, org.apache.commons.codec.binary.StringUtils.getBytesUtf8(config.getNodeId()),
                      Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        keeper.exists(node, this);
        localLockId = lockId;
        return true;
      }
      else {
        return false;
      }
    }
    catch (KeeperException ke) {
      if (ke.code() == KeeperException.Code.NODEEXISTS) {
        logger.debug("Lock alrady exists!");
        if (availableMillisForRemoteLock > 0) {
          synchronized (ZKLock.this) {
            try {
              keeper.exists(node, new Watcher() {

                public void process(WatchedEvent event) {
                  if (event.getType().equals(Event.EventType.NodeDeleted)) {
                    synchronized (ZKLock.this) {
                      ZKLock.this.notifyAll();
                    }
                  }
                }
              });
            }
            catch (Exception ex) {
              logger.error("Could not attach watcher", ex);
            }
            final long remoteStart = System.currentTimeMillis();
            ZKLock.this.wait(availableMillisForRemoteLock);
            return tryRemoteLock(lockId, availableMillisForRemoteLock - (System.currentTimeMillis() - remoteStart));
          }
        }
        else {
          registrar.unlock(key, lockId);
          return false;
        }
      }
      else {
        logger.error(ke.getMessage(), ke);
        throw new IllegalStateException(ke);
      }
    }
    catch (Exception ex) {
      registrar.unlock(key, lockId);
      logger.error(ex.getMessage(), ex);
      throw new IllegalStateException(ex);
    }
  }

  public void unlock() {
    if (isLockOwned()) {
      lock.lock();
      try {
        ZooKeeper keeper = config.getZooKeeper();
        final String node = getNode();
        Stat stat = keeper.exists(node, false);
        String nodeId = org.apache.commons.codec.binary.StringUtils.newStringUtf8(keeper.getData(node, false, stat));
        if (config.getNodeId().equals(nodeId)) {
          keeper.delete(node, stat.getVersion());
          config.getRegistrar().unlock(key, localLockId);
        }
        else {
          logger.error("Lock is owned but remote lock is not owned! System inconsistency! Releasing local lock.");
          config.getRegistrar().unlock(key, localLockId);
          throw new IllegalStateException("Lock is owned but remote lock is not owned! Released local lock!");
        }
        localLockId = null;
      }
      catch (Exception ex) {
        logger.error(ex.getMessage(), ex);
        throw new IllegalStateException(ex);
      }
      finally {
        lock.unlock();
      }
    }
  }

  public void process(WatchedEvent event) {
    if (!event.getType().equals(Event.EventType.NodeDeleted)) {
      logger.warn("Remote lock changed unexpectedly! This may cause system inconsistency");
    }
  }

  public void lockTimedOut(Key key) {
    unlock();
  }

  protected String getNode() {
    return new StringBuilder(config.getRootNode()).append('/').append(key.getKeyStringRep()).toString();
  }
}
