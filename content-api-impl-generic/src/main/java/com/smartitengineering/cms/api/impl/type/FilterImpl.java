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
package com.smartitengineering.cms.api.impl.type;

import com.smartitengineering.cms.api.type.ContentTypeId;
import com.smartitengineering.cms.api.type.Filter;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.dao.common.queryparam.QueryParameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author imyousuf
 */
public class FilterImpl implements Filter {

  private final Set<ContentTypeId> types = new HashSet<ContentTypeId>();
  private QueryParameter<Date> modifiedDateParameter;
  private QueryParameter<Date> creationDateParameter;
  private boolean disjunction;
  private int startFrom = 0;
  private int maxContents = 10;
  private WorkspaceId workspaceId;
  private String searchTerms;
  private boolean friendliesIncluded = true;
  private ContentTypeId parentId;

  @Override
  public void addInstanceOfContentTypeToFilter(ContentTypeId... types) {
    if (types != null) {
      this.types.addAll(Arrays.asList(types));
    }
  }

  @Override
  public void removeInstanceOfContentTypeFromFilter(ContentTypeId... types) {
    if (types != null) {
      this.types.removeAll(Arrays.asList(types));
    }
  }

  @Override
  public void setCreationDateFilter(QueryParameter<Date> creationDateParam) {
    this.creationDateParameter = creationDateParam;
  }

  @Override
  public void setLastModifiedDateFilter(QueryParameter<Date> modifiedDateParam) {
    this.modifiedDateParameter = modifiedDateParam;
  }

  @Override
  public Set<ContentTypeId> getInstanceOfContentTypeFilters() {
    return Collections.unmodifiableSet(types);
  }

  @Override
  public QueryParameter<Date> getCreationDateFilter() {
    return creationDateParameter;
  }

  @Override
  public QueryParameter<Date> getLastModifiedDateFilter() {
    return modifiedDateParameter;
  }

  @Override
  public boolean isDisjunction() {
    return disjunction;
  }

  @Override
  public void setDisjunction(boolean disjunction) {
    this.disjunction = disjunction;
  }

  @Override
  public void setWorkspaceId(WorkspaceId workspaceId) {
    this.workspaceId = workspaceId;
  }

  @Override
  public WorkspaceId getWorkspaceId() {
    return workspaceId;
  }

  @Override
  public int getMaxContents() {
    return maxContents;
  }

  @Override
  public int getStartFrom() {
    return startFrom;
  }

  @Override
  public void setMaxContents(int maxContents) {
    this.maxContents = maxContents;
  }

  @Override
  public void setStartFrom(int startFrom) {
    this.startFrom = startFrom;
  }

  @Override
  public String getSearchTerms() {
    return searchTerms;
  }

  @Override
  public void setSearchTerms(String searchTerms) {
    this.searchTerms = searchTerms;
  }

  @Override
  public boolean isFriendliesIncluded() {
    return friendliesIncluded;
  }

  @Override
  public void setFriendliesIncluded(boolean friendliesIncluded) {
    this.friendliesIncluded = friendliesIncluded;
  }

  @Override
  public void setChildOf(ContentTypeId parentType) {
    this.parentId = parentType;
  }

  @Override
  public ContentTypeId getChildOf() {
    return parentId;
  }
}
