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
package com.smartitengineering.cms.ws.resources.workspace;

import com.smartitengineering.cms.api.SmartContentAPI;
import com.smartitengineering.cms.api.common.TemplateType;
import com.smartitengineering.cms.api.workspace.VariationTemplate;
import com.smartitengineering.cms.api.workspace.Workspace;
import com.smartitengineering.cms.api.workspace.WorkspaceAPI;
import com.smartitengineering.cms.ws.common.domains.WorkspaceId;
import com.smartitengineering.cms.ws.common.utils.Utils;
import com.smartitengineering.cms.ws.resources.domains.Factory;
import com.smartitengineering.util.rest.server.AbstractResource;
import com.smartitengineering.util.rest.server.ServerResourceInjectables;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author imyousuf
 */
public class WorkspaceVariationResource extends AbstractResource {

  private final String varName;
  private final VariationTemplate template;

  public WorkspaceVariationResource(String varName, Workspace workspace, ServerResourceInjectables injectables) {
    super(injectables);
    this.varName = varName;
    template = SmartContentAPI.getInstance().getWorkspaceApi().getVariationTemplate(workspace.getId(), varName);
  }
  @GET
  public Response get() {
    if (template == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    Date lastModifiedDate = template.getLastModifiedDate();
    EntityTag tag = new EntityTag(DigestUtils.md5Hex(Utils.getFormattedDate(lastModifiedDate)));
    ResponseBuilder builder = getContext().getRequest().evaluatePreconditions(lastModifiedDate, tag);
    if (builder == null) {
      builder = Response.ok();
      builder.entity(Factory.getResourceTemplate(template));
      builder.lastModified(template.getLastModifiedDate());
      CacheControl control = new CacheControl();
      control.setMaxAge(300);
      builder.cacheControl(control);
    }
    return builder.build();
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response put(com.smartitengineering.cms.ws.common.domains.ResourceTemplate template) {
    ResponseBuilder builder;
    if (this.template == null) {
      WorkspaceId id = template.getWorkspaceId();
      final WorkspaceAPI workspaceApi = SmartContentAPI.getInstance().getWorkspaceApi();
      VariationTemplate created = workspaceApi.putVariationTemplate(workspaceApi.createWorkspaceId(id.
          getGlobalNamespace(), id.getName()), varName, TemplateType.valueOf(template.getTemplateType()), template.
          getTemplate());
      if (created != null) {
        builder = Response.created(getUriInfo().getRequestUri());
      }
      else {
        builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
      }
    }
    else {
      Date lastModifiedDate = template.getLastModifiedDate();
      EntityTag entityTag = new EntityTag(DigestUtils.md5Hex(Utils.getFormattedDate(lastModifiedDate)));
      builder = getContext().getRequest().evaluatePreconditions(lastModifiedDate, entityTag);
      if (builder == null) {
        WorkspaceId id = template.getWorkspaceId();
        final WorkspaceAPI workspaceApi = SmartContentAPI.getInstance().getWorkspaceApi();
        VariationTemplate put = workspaceApi.putVariationTemplate(workspaceApi.createWorkspaceId(id.
            getGlobalNamespace(), id.getName()), varName, TemplateType.valueOf(template.getTemplateType()), template.
            getTemplate());
        if (put != null) {
          builder = Response.status(Status.ACCEPTED).location(getUriInfo().getRequestUri());
        }
        else {
          builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        }
      }
    }
    return builder.build();
  }

  @DELETE
  public Response delete() {
    if (template == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    Date lastModifiedDate = template.getLastModifiedDate();
    EntityTag entityTag = new EntityTag(DigestUtils.md5Hex(Utils.getFormattedDate(lastModifiedDate)));
    ResponseBuilder builder = getContext().getRequest().evaluatePreconditions(lastModifiedDate, entityTag);
    if (builder == null) {
      final WorkspaceAPI workspaceApi = SmartContentAPI.getInstance().getWorkspaceApi();
      try {
        workspaceApi.delete(template);
        builder = Response.status(Status.ACCEPTED);
      }
      catch (Exception ex) {
        builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
      }
    }
    return builder.build();
  }
}
