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
import com.smartitengineering.common.dao.search.CommonFreeTextPersistentDao;

/**
 *
 * @author imyousuf
 */
@Singleton
public class ContentEventListener implements EventListener<Content> {

  @Inject
  private CommonFreeTextPersistentDao<Content> persistentDao;

  @Override
  public boolean accepts(Event<Content> event) {
    return event.getEventSourceType().equals(Type.CONTENT);
  }

  @Override
  public void notify(Event<Content> event) {
    switch (event.getEventType()) {
      case CREATE:
        persistentDao.save(event.getSource());
        break;
      case UPDATE:
        persistentDao.update(event.getSource());
        break;
      case DELETE:
        persistentDao.delete(event.getSource());
        break;
    }
  }
}
