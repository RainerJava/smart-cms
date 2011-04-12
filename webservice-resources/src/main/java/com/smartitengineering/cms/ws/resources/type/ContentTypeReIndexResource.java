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
package com.smartitengineering.cms.ws.resources.type;

import com.smartitengineering.cms.api.factory.SmartContentAPI;
import com.smartitengineering.cms.api.type.ContentTypeId;
import com.smartitengineering.util.rest.server.AbstractResource;
import com.smartitengineering.util.rest.server.ServerResourceInjectables;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author imyousuf
 */
public class ContentTypeReIndexResource extends AbstractResource {

  private ContentTypeId typeId;

  public ContentTypeReIndexResource(ServerResourceInjectables injectables) {
    super(injectables);
  }

  public ContentTypeId getTypeId() {
    return typeId;
  }

  public void setTypeId(ContentTypeId typeId) {
    this.typeId = typeId;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String get() {
    return "POST to this resource to perform reindexing in asynchronous thread";
  }

  @POST
  public Response reIndex() {
    Response.ResponseBuilder builder;
    if (typeId != null) {
      SmartContentAPI.getInstance().getContentTypeLoader().reIndexType(typeId);
      builder = Response.status(Response.Status.ACCEPTED);
    }
    else {
      builder = Response.status(Response.Status.NOT_FOUND);
    }
    return builder.build();
  }
}
