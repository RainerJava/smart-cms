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
import com.smartitengineering.cms.api.workspace.WorkspaceAPI.ResourceSortCriteria;
import com.smartitengineering.cms.ws.common.domains.ResourceTemplate;
import com.smartitengineering.util.rest.atom.server.AbstractResource;
import com.smartitengineering.util.rest.server.ServerResourceInjectables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author imyousuf
 */
public class WorkspaceVariationsResource extends AbstractResource {

  private final Workspace workspace;
  private final int count;

  public WorkspaceVariationsResource(Workspace workspace, int count, ServerResourceInjectables injectables) {
    super(injectables);
    this.workspace = workspace;
    this.count = count;
  }

  @GET
  public Response getFirstPage() {
    return getAfter("");
  }

  @GET
  @Path("after/{name}")
  public Response getAfter(@PathParam("name") @DefaultValue("") final String startPointName) {
    return getResponseForVarNames(SmartContentAPI.getInstance().getWorkspaceApi().getVariationNames(
        workspace.getId(), ResourceSortCriteria.BY_NAME, startPointName, count));
  }

  @GET
  @Path("before/{name}")
  public Response getBefore(@PathParam("name") @DefaultValue("") String startPointName) {
    return getResponseForVarNames(SmartContentAPI.getInstance().getWorkspaceApi().getVariationNames(
        workspace.getId(), ResourceSortCriteria.BY_NAME, startPointName, -1 * count));
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response putRepresentations(ResourceTemplate template) {
    if (StringUtils.isBlank(template.getName())) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    WorkspaceVariationResource resource = new WorkspaceVariationResource(template.getName(), workspace,
                                                                         getInjectables());
    return resource.put(template);
  }

  protected Response getResponseForVarNames(Collection<String> names) {
    if (names == null || names.isEmpty()) {
      return Response.noContent().build();
    }
    final Date date = new Date();
    Feed feed = getFeed(new StringBuilder("vars-").append(workspace.getId().toString()).toString(),
                        "Variations of a feed", date);
    ArrayList<String> nameList = new ArrayList<String>(names.size());
    final String first = nameList.get(0);
    final String last = nameList.get(nameList.size() - 1);
    Link previousLink = getLink(getUriInfo().getBaseUriBuilder().path(WorkspaceResource.class).path(
        WorkspaceResource.REL_VARIATIONS).path("before").path(first).build(), Link.REL_PREVIOUS,
                                MediaType.APPLICATION_ATOM_XML);
    Link nextLink = getLink(getUriInfo().getBaseUriBuilder().path(WorkspaceResource.class).path(
        WorkspaceResource.REL_VARIATIONS).path("after").path(last).build(), Link.REL_NEXT,
                            MediaType.APPLICATION_ATOM_XML);
    Link firstLink = getLink(getUriInfo().getBaseUriBuilder().path(WorkspaceResource.class).path(
        WorkspaceResource.REL_VARIATIONS).build(), Link.REL_FIRST, MediaType.APPLICATION_ATOM_XML);
    feed.addLink(firstLink);
    feed.addLink(previousLink);
    feed.addLink(nextLink);
    for (String name : nameList) {
      Link nameLink = getLink(getUriInfo().getBaseUriBuilder().path(WorkspaceResource.class).path(
          WorkspaceResource.REL_VARIATIONS).path("name").path(name).build(), Link.REL_NEXT,
                              MediaType.APPLICATION_ATOM_XML);
      Entry entry = getEntry(name, name, date, nameLink);
      feed.addEntry(entry);
    }
    return Response.ok(feed).build();
  }

  @Override
  protected String getAuthor() {
    return "Smart CMS";
  }
}
