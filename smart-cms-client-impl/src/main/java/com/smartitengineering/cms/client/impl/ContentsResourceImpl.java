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
package com.smartitengineering.cms.client.impl;

import com.smartitengineering.cms.client.api.ContentResource;
import com.smartitengineering.cms.client.api.ContentsResource;
import com.smartitengineering.cms.ws.common.domains.Content;
import com.smartitengineering.util.rest.atom.AbstractFeedClientResource;
import com.smartitengineering.util.rest.atom.AtomClientUtil;
import com.smartitengineering.util.rest.client.Resource;
import com.smartitengineering.util.rest.client.ResourceLink;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.apache.abdera.model.Entry;

/**
 *
 * @author kaisar
 */
public class ContentsResourceImpl extends AbstractFeedClientResource<ContentsResource> implements
    ContentsResource {

  public ContentsResourceImpl(Resource referrer, ResourceLink link) {
    super(referrer, link);
  }

  @Override
  protected void processClientConfig(ClientConfig clientConfig) {
  }

  @Override
  protected ContentsResource instantiatePageableResource(ResourceLink link) {
    return new ContentsResourceImpl(this, link);
  }

  @Override
  public Collection<ContentResource> getContentResources() {
    final List<Entry> entries = getLastReadStateOfEntity().getEntries();
    if (entries == null || entries.isEmpty()) {
      return Collections.emptyList();
    }
    List<ContentResource> resources = new ArrayList<ContentResource>(entries.size());
    for (Entry entry : entries) {
      resources.add(new ContentResourceImpl(this, AtomClientUtil.convertFromAtomLinkToResourceLink(entry.
          getAlternateLink())));
    }
    return Collections.unmodifiableCollection(resources);
  }

  @Override
  public ContentResource createContentResource(Content content) {
    ClientResponse response = post(MediaType.APPLICATION_JSON, content, ClientResponse.Status.CREATED);
    return new ContentResourceImpl(this, response.getLocation());
  }
}
