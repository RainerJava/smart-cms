/*
 *
 * This is a simple Content Management System (CMS)
 * Copyright (C) 2009  Imran M Yousuf (imyousuf@smartitengineering.com)
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
package com.smartitengineering.cms.api.content.impl;

import com.smartitengineering.cms.api.WorkspaceId;
import com.smartitengineering.cms.api.content.ContentId;

/**
 *
 * @author kaisar
 */
public class ContentIdImpl implements ContentId {

  private WorkspaceId workspaceIdI;
  private byte[] id;

  public void setWorkspaceId(WorkspaceId workspaceId){
    this.workspaceIdI=workspaceId;
  }

  public void setId(byte[] id){
    this.id=id;
  }

  public WorkspaceId getWorkspaceId() {
    return this.workspaceIdI;
  }

  public byte[] getId() {
    return this.id;
  }
}
