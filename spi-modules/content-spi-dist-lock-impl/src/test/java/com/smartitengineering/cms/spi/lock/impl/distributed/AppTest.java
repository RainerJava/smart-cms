package com.smartitengineering.cms.spi.lock.impl.distributed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.Socket;
import junit.framework.Assert;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.NIOServerCnxn;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppTest {

  public static final String ROOT_NODE = "/smart-cms";
  private static NIOServerCnxn.Factory standaloneServerFactory;
  private static final int CLIENT_PORT = 3882;
  private static final int CONNECTION_TIMEOUT = 30000;

  @BeforeClass
  public static void startZooKeeperServer() {
    try {
      File snapDir = new File("./target/zk/");
      snapDir.mkdirs();
      ZooKeeperServer server = new ZooKeeperServer(snapDir, snapDir, 2000);
      standaloneServerFactory = new NIOServerCnxn.Factory(new InetSocketAddress(CLIENT_PORT));
      standaloneServerFactory.startup(server);
      if (!waitForServerUp(CLIENT_PORT, CONNECTION_TIMEOUT)) {
        throw new IOException("Waiting for startup of standalone server");
      }
    }
    catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  @AfterClass
  public static void shutdownZooKeeperServer() {
    standaloneServerFactory.shutdown();
    if (!waitForServerDown(CLIENT_PORT, CONNECTION_TIMEOUT)) {
      throw new IllegalStateException("Waiting for shutdown of standalone server");
    }
  }

  // XXX: From o.a.zk.t.ClientBase
  private static boolean waitForServerUp(int port, long timeout) {
    long start = System.currentTimeMillis();
    while (true) {
      try {
        Socket sock = new Socket("localhost", port);
        BufferedReader reader = null;
        try {
          OutputStream outstream = sock.getOutputStream();
          outstream.write("stat".getBytes());
          outstream.flush();

          Reader isr = new InputStreamReader(sock.getInputStream());
          reader = new BufferedReader(isr);
          String line = reader.readLine();
          if (line != null && line.startsWith("Zookeeper version:")) {
            return true;
          }
        }
        finally {
          sock.close();
          if (reader != null) {
            reader.close();
          }
        }
      }
      catch (IOException e) {
        // ignore as this is expected
      }

      if (System.currentTimeMillis() > start + timeout) {
        break;
      }
      try {
        Thread.sleep(250);
      }
      catch (InterruptedException e) {
        // ignore
      }
    }
    return false;
  }

  // XXX: From o.a.zk.t.ClientBase
  private static boolean waitForServerDown(int port, long timeout) {
    long start = System.currentTimeMillis();
    while (true) {
      try {
        Socket sock = new Socket("localhost", CLIENT_PORT);
        try {
          OutputStream outstream = sock.getOutputStream();
          outstream.write("stat".getBytes());
          outstream.flush();
        }
        finally {
          sock.close();
        }
      }
      catch (IOException e) {
        return true;
      }

      if (System.currentTimeMillis() > start + timeout) {
        break;
      }
      try {
        Thread.sleep(250);
      }
      catch (InterruptedException e) {
        // ignore
      }
    }
    return false;
  }

  @Test
  public void testInitialization() throws Exception {
    final String connectString = "localhost:" + CLIENT_PORT;
    ZooKeeper zooKeeper = new ZooKeeper(connectString, CONNECTION_TIMEOUT, new Watcher() {

      public void process(WatchedEvent event) {
      }
    });
    Stat stat = zooKeeper.exists(ROOT_NODE, false);
    Assert.assertNull(stat);
    ZooKeeperLockHandler handler = new ZooKeeperLockHandler(connectString, ROOT_NODE, CONNECTION_TIMEOUT);
    stat = zooKeeper.exists(ROOT_NODE, false);
    Assert.assertNotNull(stat);
  }
}
