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
import com.smartitengineering.cms.api.workspace.Workspace;
import com.smartitengineering.cms.api.workspace.WorkspaceAPI;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.cms.ws.resources.domains.Factory;
import java.net.URI;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
@Path("/ws/{" + WorkspaceResource.PARAM_NAMESPACE + "}/{" + WorkspaceResource.PARAM_NAME + "}")
public class WorkspaceResource {

  public static final int MAX_AGE = 1 * 60 * 60;
  public static final String PARAM_NAMESPACE = "ns";
  public static final String PARAM_NAME = "wsName";
  public static final String REL_WORKSPACE_CONTENT = "workspaceContent";
  public static final Pattern PATTERN = Pattern.compile("(/)?ws/(\\w+)/(\\w+)");
  private final String namespace;
  private final String workspaceName;
  @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE)
  private Date ifModifiedSince;

  public WorkspaceResource(@PathParam(PARAM_NAMESPACE) String namespace, @PathParam(PARAM_NAME) String workspaceName) {
    this.namespace = namespace;
    this.workspaceName = workspaceName;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getWorkspace() {
    final WorkspaceAPI workspaceApi = SmartContentAPI.getInstance().getWorkspaceApi();
    final Workspace workspace = workspaceApi.getWorkspace(workspaceApi.createWorkspaceId(namespace, workspaceName));
    if (ifModifiedSince == null || ifModifiedSince.before(workspace.getCreationDate())) {
      ResponseBuilder builder = Response.ok(Factory.getWorkspace(workspace));
      builder.lastModified(workspace.getCreationDate());
      CacheControl control = new CacheControl();
      control.setMaxAge(MAX_AGE);
      builder.cacheControl(control);
      return builder.build();
    }
    else {
      return Response.status(Response.Status.NOT_MODIFIED).build();
    }
  }

  @Path("friendlies")
  public WorkspaceFriendliesResource getFriendliesResource() {
    return new WorkspaceFriendliesResource(namespace, workspaceName);
  }

  public static URI getWorkspaceURI(UriBuilder builder, String namespace, String name) {
    if (builder != null) {
      builder.path(WorkspaceResource.class);
      return builder.build(namespace, name);
    }
    return null;
  }

  public static WorkspaceId parseWorkspaceId(UriInfo uriInfo, URI uri) {
    String path = uri.getPath();
    String basePath = uriInfo.getBaseUri().getPath();
    if (StringUtils.isBlank(path) || !path.startsWith(basePath)) {
      return null;
    }
    String pathToWorkspace = path.substring(0, basePath.length());
    Matcher matcher = PATTERN.matcher(pathToWorkspace);
    if (matcher.matches()) {
      final WorkspaceAPI workspaceApi = SmartContentAPI.getInstance().getWorkspaceApi();
      return workspaceApi.getWorkspaceIdIfExists(workspaceApi.createWorkspaceId(matcher.group(2), matcher.group(3)));
    }
    else {
      return null;
    }
  }
}
