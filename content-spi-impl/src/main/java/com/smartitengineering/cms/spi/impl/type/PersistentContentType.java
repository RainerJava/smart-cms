/*
 *
 * This is a simple Content Management System (CMS)
 * Copyright (C) 2010  Imran M Yousuf (imyousuf@smartitengineering.com)
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
package com.smartitengineering.cms.spi.impl.type;

import com.smartitengineering.cms.api.factory.type.WritableContentType;
import com.smartitengineering.cms.api.type.ContentTypeId;
import com.smartitengineering.domain.AbstractGenericPersistentDTO;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
public class PersistentContentType extends AbstractGenericPersistentDTO<PersistentContentType, ContentTypeId, Long> {

  private WritableContentType mutableContentType;

  public PersistentContentType() {
  }

  public WritableContentType getMutableContentType() {
    return mutableContentType;
  }

  public void setMutableContentType(WritableContentType mutableContentType) {
    this.mutableContentType = mutableContentType;
  }

  @Override
  public boolean isValid() {
    if (mutableContentType == null || mutableContentType.getContentTypeID() == null || StringUtils.isBlank(mutableContentType.
        getContentTypeID().getName()) || StringUtils.isBlank(mutableContentType.getContentTypeID().getNamespace()) || mutableContentType.
        getFieldDefs().isEmpty() || mutableContentType.getStatuses().isEmpty() || mutableContentType.getContentTypeID().
        getWorkspace() == null || StringUtils.isBlank(mutableContentType.getContentTypeID().getWorkspace().
        getGlobalNamespace()) || StringUtils.isBlank(mutableContentType.getContentTypeID().getWorkspace().getName())) {
      return false;
    }
    return true;
  }

  @Override
  public ContentTypeId getId() {
    if (getMutableContentType() == null) {
      return null;
    }
    return getMutableContentType().getContentTypeID();
  }

  @Override
  @Deprecated
  public void setId(ContentTypeId id) {
    throw new UnsupportedOperationException("Do not use this operation!");
  }
}
