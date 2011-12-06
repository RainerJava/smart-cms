/*
 *
 * This is a simple Content Management System (CMS)
 * Copyright (C) 2011  Imran M Yousuf (imyousuf@smartitengineering.com)
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
package com.smartitengineering.cms.spi.impl.events;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.smartitengineering.cms.api.content.Content;
import com.smartitengineering.cms.api.content.ContentId;
import com.smartitengineering.cms.api.event.Event;
import com.smartitengineering.cms.api.event.Event.EventType;
import com.smartitengineering.cms.api.event.Event.Type;
import com.smartitengineering.cms.api.event.EventListener;
import com.smartitengineering.cms.api.factory.SmartContentAPI;
import com.smartitengineering.cms.api.type.ContentType;
import com.smartitengineering.cms.api.type.ContentTypeId;
import com.smartitengineering.cms.api.workspace.Sequence;
import com.smartitengineering.cms.api.workspace.SequenceId;
import com.smartitengineering.dao.solr.SolrWriteDao;
import com.smartitengineering.events.async.api.EventConsumer;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imyousuf
 */
@Singleton
public class EventConsumerImpl implements EventConsumer {

  @Inject
  private EventListener<Content> contentListener;
  @Inject
  private EventListener<ContentType> contentTypeListener;
  @Inject
  private EventListener<Sequence> sequenceListener;
  @Inject
  private SolrWriteDao solrWriteDao;
  private final transient Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void consume(String eventContentType, String eventMessage) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new StringReader(eventMessage));
      final Type sourceType = Type.valueOf(reader.readLine());
      final EventType type = EventType.valueOf(reader.readLine());
      if (logger.isDebugEnabled()) {
        logger.debug("Event source type " + sourceType);
        logger.debug("Event type " + type);
      }
      final StringBuilder idStr = new StringBuilder("");
      String line;
      do {
        line = reader.readLine();
        if (StringUtils.isNotBlank(line)) {
          idStr.append(line).append('\n');
        }
      }
      while (StringUtils.isNotBlank(line));
      final byte[] decodedIdString = Base64.decodeBase64(idStr.toString());
      switch (sourceType) {
        case CONTENT: {
          final ContentId contentId =
                          (ContentId) new ObjectInputStream(new ByteArrayInputStream(decodedIdString)).readObject();
          Content content = contentId.getContent();
          if (content == null && EventType.DELETE.equals(type)) {
            content =
            (Content) Proxy.newProxyInstance(Content.class.getClassLoader(), new Class[]{Content.class},
                                             new InvocationHandler() {

              @Override
              public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("getContentId")) {
                  return contentId;
                }
                return null;
              }
            });
          }
          final Event<Content> event = SmartContentAPI.getInstance().getEventRegistrar().<Content>createEvent(type,
                                                                                                              sourceType,
                                                                                                              content);
          contentListener.notify(event);
        }
        break;
        case CONTENT_TYPE: {
          final ContentTypeId typeId =
                              (ContentTypeId) new ObjectInputStream(new ByteArrayInputStream(decodedIdString)).
              readObject();
          ContentType contentType = typeId.getContentType();
          if (contentType == null && EventType.DELETE.equals(type)) {
            contentType = (ContentType) Proxy.newProxyInstance(ContentType.class.getClassLoader(), new Class[]{
                  ContentType.class}, new InvocationHandler() {

              @Override
              public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("getContentTypeID")) {
                  return typeId;
                }
                return null;
              }
            });
          }
          final Event<ContentType> event =
                                   SmartContentAPI.getInstance().getEventRegistrar().<ContentType>createEvent(type,
                                                                                                              sourceType,
                                                                                                              contentType);
          contentTypeListener.notify(event);
        }
        break;
        case SEQUENCE: {
          final SequenceId seqId =
                           (SequenceId) new ObjectInputStream(new ByteArrayInputStream(decodedIdString)).readObject();
          Sequence sequence = seqId.getSequence();
          if (sequence == null && EventType.DELETE.equals(type)) {
            sequence = (Sequence) Proxy.newProxyInstance(Sequence.class.getClassLoader(), new Class[]{
                  Sequence.class}, new InvocationHandler() {

              @Override
              public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("getSequenceId")) {
                  return seqId;
                }
                return null;
              }
            });
          }
          final Event<Sequence> event =
                                SmartContentAPI.getInstance().getEventRegistrar().<Sequence>createEvent(type,
                                                                                                        sourceType,
                                                                                                        sequence);
          sequenceListener.notify(event);
        }
        break;
        default:
          logger.info(new StringBuilder("Ignoring event source type ").append(sourceType).toString());
      }
    }
    catch (Exception ex) {
      logger.warn("Could not persist content ID!", ex);
      throw new RuntimeException(ex);
    }
    finally {
      try {
        reader.close();
      }
      catch (Exception ex) {
        logger.warn("Could not close reader!", ex);
      }
    }
  }

  @Override
  public void startConsumption() {
  }

  @Override
  public void endConsumption(boolean prematureEnd) {
    if (prematureEnd) {
      solrWriteDao.rollback();
    }
    else {
      solrWriteDao.commit();
    }
  }
}
