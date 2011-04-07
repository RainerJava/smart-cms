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
package com.smartitengineering.cms.spi.impl.content.search;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.smartitengineering.cms.api.content.Content;
import com.smartitengineering.cms.api.event.Event;
import com.smartitengineering.cms.api.event.Event.Type;
import com.smartitengineering.cms.api.event.EventListener;
import com.smartitengineering.cms.api.type.ContentType;
import com.smartitengineering.events.async.api.EventPublisher;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imyousuf
 */
@Singleton
public class ContentEventPublicationListener implements EventListener {

  @Inject
  private EventPublisher publisher;
  private final transient Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public boolean accepts(Event event) {
    return event.getEventSourceType().equals(Type.CONTENT) || event.getEventSourceType().equals(Type.CONTENT_TYPE);
  }

  @Override
  public void notify(Event event) {
    String hexedContentId;
    ObjectOutputStream stream = null;
    try {
      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      stream = new ObjectOutputStream(byteArrayOutputStream);
      if (event.getEventSourceType().equals(Type.CONTENT)) {
        stream.writeObject(((Content) event.getSource()).getContentId());
      }
      else if (event.getEventSourceType().equals(Type.CONTENT_TYPE)) {
        stream.writeObject(((ContentType) event.getSource()).getContentTypeID());
      }
      else {
        logger.warn("Unrecognized event source type!");
      }
      hexedContentId = Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
    }
    catch (Exception ex) {
      logger.warn("Could not serialize content ID!", ex);
      hexedContentId = null;
    }
    finally {
      try {
        stream.close();
      }
      catch (Exception ex) {
        logger.warn("Could not close stream!", ex);
      }
    }
    if (StringUtils.isNotBlank(hexedContentId)) {
      String message = new StringBuilder(event.getEventSourceType().name()).append('\n').append(event.getEventType().
          name()).append('\n').append(hexedContentId).toString();
      if (logger.isInfoEnabled()) {
        logger.info("Publishing message " + message);
      }
      publisher.publishEvent("text/plain", message);
    }
  }
}
