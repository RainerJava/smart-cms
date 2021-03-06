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

import org.apache.zookeeper.ZooKeeper;

/**
 *
 * @author imyousuf
 */
public class ZKConfig {

  private final ZooKeeper zooKeeper;
  private final String rootNode;
  private final String nodeId;
  private final LocalLockRegistrar registrar;

  public ZKConfig(ZooKeeper zooKeeper, String rootNode, String nodeId, LocalLockRegistrar registrar) {
    this.zooKeeper = zooKeeper;
    this.rootNode = rootNode;
    this.nodeId = nodeId;
    this.registrar = registrar;
  }

  public LocalLockRegistrar getRegistrar() {
    return registrar;
  }

  public String getNodeId() {
    return nodeId;
  }

  public String getRootNode() {
    return rootNode;
  }

  public ZooKeeper getZooKeeper() {
    return zooKeeper;
  }
}
