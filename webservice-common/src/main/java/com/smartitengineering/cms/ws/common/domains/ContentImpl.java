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
package com.smartitengineering.cms.ws.common.domains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author imyousuf
 */
public class ContentImpl implements Content {

  private Date creationDate, lastModifiedDate;
  private String contentTypeUri, parentContentUri, status;
  private List<Field> fields = new ArrayList<Field>();

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }

  @Override
  public String getContentTypeUri() {
    return contentTypeUri;
  }

  @Override
  public String getParentContentUri() {
    return parentContentUri;
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public Collection<Field> getFields() {
    return fields;
  }

  public void setContentTypeUri(String contentTypeUri) {
    this.contentTypeUri = contentTypeUri;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public void setFields(Collection<Field> fields) {
    this.fields.clear();
    if (fields != null) {
      this.fields.addAll(fields);
    }
  }

  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public void setParentContentUri(String parentContentUri) {
    this.parentContentUri = parentContentUri;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
