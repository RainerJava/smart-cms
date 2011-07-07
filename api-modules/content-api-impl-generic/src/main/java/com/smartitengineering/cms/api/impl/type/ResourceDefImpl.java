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
package com.smartitengineering.cms.api.impl.type;

import com.smartitengineering.cms.api.type.MutableResourceDef;
import com.smartitengineering.cms.api.type.ResourceDef;
import com.smartitengineering.cms.api.type.ResourceUri;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author kaisar
 */
public class ResourceDefImpl implements MutableResourceDef {

  private String mimeType;
  private String name;
  private ResourceUri resourceUri;
  private final Map<String, String> params = new LinkedHashMap<String, String>();

  @Override
  public void setMIMEType(String mimeType) {
    this.mimeType = mimeType;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setResourceUri(ResourceUri resourceUri) {
    this.resourceUri = resourceUri;
  }

  @Override
  public String getMIMEType() {
    return this.mimeType;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public ResourceUri getResourceUri() {
    return this.resourceUri;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (ResourceDef.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    final ResourceDef other = (ResourceDef) obj;
    if ((this.name == null) ? (other.getName() != null) : !this.name.equals(other.getName())) {
      return false;
    }
    return true;
  }

  public void setParameters(Map<String, String> params) {
    this.params.clear();
    if (params != null && !params.isEmpty()) {
      this.params.putAll(params);
    }
  }

  public Map<String, String> getParameters() {
    return Collections.unmodifiableMap(params);
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 11 * hash + (this.name != null ? this.name.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return "ResourceDefImpl{" + "; mimeType=" + mimeType + "; name=" + name + "; resourceUri=" + resourceUri +
        "; params=" + params + '}';
  }
}
