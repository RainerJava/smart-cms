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
package com.smartitengineering.cms.api.impl.content;

import com.smartitengineering.cms.api.common.MediaType;
import com.smartitengineering.cms.api.content.MutableRepresentation;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author kaisar
 */
public class RepresentationImpl implements MutableRepresentation {

  private String name;
  private byte[] representation;
  private String mimeType;

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setRepresentation(byte[] representation) {
    this.representation = representation;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public byte[] getRepresentation() {
    return this.representation;
  }

  @Override
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  @Override
  public String getMimeType() {
    return StringUtils.isNotBlank(mimeType) ? mimeType : MediaType.APPLICATION_OCTET_STREAM.toString();
  }
}
