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
package com.smartitengineering.cms.ws.resources.content;

import com.smartitengineering.cms.api.content.Content;
import com.smartitengineering.cms.api.content.Field;
import com.smartitengineering.cms.api.content.Variation;
import com.smartitengineering.cms.api.factory.SmartContentAPI;
import com.smartitengineering.util.rest.server.AbstractResource;
import com.smartitengineering.util.rest.server.ServerResourceInjectables;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 *
 * @author imyousuf
 */
public class VariationResource extends AbstractResource {

  private final Content content;
  private final Field field;
  private final String varName;

  public VariationResource(ServerResourceInjectables injectables, Content content, Field field, String varName) {
    super(injectables);
    this.content = content;
    this.field = field;
    this.varName = varName;
  }

  @GET
  public Response get() {
    ResponseBuilder builder;
    Variation var = SmartContentAPI.getInstance().getContentLoader().getVariation(content, field, varName);
    if (var == null) {
      builder = Response.status(Response.Status.NOT_FOUND);
    }
    else {
      final Date lastModifiedDate = var.getLastModifiedDate();
      builder = getContext().getRequest().evaluatePreconditions(lastModifiedDate);
      if (builder == null) {
        builder = Response.ok(var.getVariation()).type(MediaType.valueOf(var.getMimeType())).lastModified(
            lastModifiedDate);
        CacheControl control = new CacheControl();
        control.setMaxAge(900);
        builder.cacheControl(control);
      }
    }
    return builder.build();
  }
}
