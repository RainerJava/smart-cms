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
package com.smartitengineering.cms.client.api;

import com.smartitengineering.util.rest.client.PaginatedResource;
import com.smartitengineering.util.rest.client.Resource;
import java.util.Collection;
import org.apache.abdera.model.Feed;

/**
 *
 * @author imyousuf
 */
public interface ContentTypeSearchResultResource extends Resource<Feed>,
                                                         PaginatedResource<ContentTypeSearchResultResource> {

  public int getTotalItemCount();

  public Collection<ContentTypeResource> getContentTypes();

  public Collection<ContentTypeFeedResource> getContentTypeFeeds();
}
