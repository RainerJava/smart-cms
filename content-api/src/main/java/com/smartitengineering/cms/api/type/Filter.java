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
package com.smartitengineering.cms.api.type;

import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.dao.common.queryparam.QueryParameter;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author imyousuf
 */
public interface Filter {

  void setChildOf(ContentTypeId parentType);

  ContentTypeId getChildOf();

  void addInstanceOfContentTypeToFilter(ContentTypeId... types);

  void removeInstanceOfContentTypeFromFilter(ContentTypeId... types);

  Set<ContentTypeId> getInstanceOfContentTypeFilters();

  void setCreationDateFilter(QueryParameter<Date> creationDateParam);

  QueryParameter<Date> getCreationDateFilter();

  void setLastModifiedDateFilter(QueryParameter<Date> creationDateParam);

  QueryParameter<Date> getLastModifiedDateFilter();

  void setWorkspaceId(WorkspaceId workspaceId);

  WorkspaceId getWorkspaceId();

  int getMaxContents();

  int getStartFrom();

  void setMaxContents(int maxContents);

  void setStartFrom(int startFrom);

  boolean isDisjunction();

  void setDisjunction(boolean disjunction);

  String getSearchTerms();

  void setSearchTerms(String searchTerms);

  void setFriendliesIncluded(boolean friendliesIncluded);

  boolean isFriendliesIncluded();
}
