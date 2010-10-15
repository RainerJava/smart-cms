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
package com.smartitengineering.cms.api.impl.workspace;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartitengineering.cms.api.common.TemplateType;
import com.smartitengineering.cms.api.workspace.RepresentationTemplate;
import com.smartitengineering.cms.api.workspace.ResourceTemplate;
import com.smartitengineering.cms.api.workspace.VariationTemplate;
import com.smartitengineering.cms.api.workspace.Workspace;
import com.smartitengineering.cms.api.factory.workspace.WorkspaceAPI;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.cms.spi.SmartContentSPI;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imyousuf
 */
public class WorkspaceAPIImpl implements WorkspaceAPI {

  private String globalNamespace;
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Inject
  public void setGlobalNamespace(@Named("globalNamespace") String globalNamespace) {
    this.globalNamespace = globalNamespace;
  }

  @Override
  public String getGlobalNamespace() {
    return globalNamespace;
  }

  @Override
  public WorkspaceId createWorkspace(String name) {
    WorkspaceId workspaceIdImpl = createWorkspaceId(name);
    return createWorkspace(workspaceIdImpl);
  }

  @Override
  public WorkspaceId createWorkspace(String globalNamespace, String name) {
    return createWorkspace(createWorkspaceId(globalNamespace, name));
  }

  @Override
  public WorkspaceId createWorkspace(WorkspaceId workspaceId) {
    SmartContentSPI.getInstance().getWorkspaceService().create(workspaceId);
    return workspaceId;
  }

  @Override
  public WorkspaceId createWorkspaceId(String name) {
    return createWorkspaceId(null, name);
  }

  @Override
  public WorkspaceId createWorkspaceId(final String namespace, String name) {
    final WorkspaceIdImpl workspaceIdImpl = new WorkspaceIdImpl();
    workspaceIdImpl.setGlobalNamespace(StringUtils.isBlank(namespace) ? getGlobalNamespace() : namespace);
    workspaceIdImpl.setName(name);
    return workspaceIdImpl;
  }

  @Override
  public WorkspaceId getWorkspaceIdIfExists(String name) {
    final WorkspaceId createdWorkspaceId = createWorkspaceId(name);
    return getWorkspaceIdIfExists(createdWorkspaceId);
  }

  @Override
  public WorkspaceId getWorkspaceIdIfExists(WorkspaceId workspaceId) {
    Workspace workspace = getWorkspace(workspaceId);
    if (workspace != null) {
      return workspaceId;
    }
    return null;
  }

  @Override
  public Workspace getWorkspace(WorkspaceId workspaceId) {
    return SmartContentSPI.getInstance().getWorkspaceService().load(workspaceId);
  }

  @Override
  public Collection<Workspace> getWorkspaces() {
    return SmartContentSPI.getInstance().getWorkspaceService().getWorkspaces();
  }

  @Override
  public RepresentationTemplate putRepresentationTemplate(WorkspaceId to, String name, TemplateType templateType,
                                                          InputStream stream)
      throws IOException {
    return putRepresentationTemplate(to, name, templateType, IOUtils.toByteArray(stream));
  }

  @Override
  public RepresentationTemplate putRepresentationTemplate(WorkspaceId to, String name, TemplateType templateType,
                                                          byte[] data) {
    return SmartContentSPI.getInstance().getWorkspaceService().putRepresentationTemplate(to, name, templateType, data);
  }

  @Override
  public VariationTemplate putVariationTemplate(WorkspaceId to, String name, TemplateType templateType,
                                                InputStream stream) throws
      IOException {
    return putVariationTemplate(to, name, templateType, IOUtils.toByteArray(stream));
  }

  @Override
  public VariationTemplate putVariationTemplate(WorkspaceId to, String name, TemplateType templateType, byte[] data) {
    return SmartContentSPI.getInstance().getWorkspaceService().putVariationTemplate(to, name, templateType, data);
  }

  @Override
  public void delete(RepresentationTemplate template) {
    SmartContentSPI.getInstance().getWorkspaceService().deleteRepresentation(template);
  }

  @Override
  public void delete(VariationTemplate template) {
    SmartContentSPI.getInstance().getWorkspaceService().deleteVariation(template);
  }

  @Override
  public Collection<WorkspaceId> getFriendlies(WorkspaceId workspaceId) {
    return SmartContentSPI.getInstance().getWorkspaceService().getFriendlies(workspaceId);
  }

  @Override
  public void addFriend(WorkspaceId to, WorkspaceId... workspaceIds) {
    SmartContentSPI.getInstance().getWorkspaceService().addFriend(to, workspaceIds);
  }

  @Override
  public void removeFriend(WorkspaceId from, WorkspaceId workspaceId) {
    SmartContentSPI.getInstance().getWorkspaceService().removeFriend(from, workspaceId);
  }

  @Override
  public void removeAllFriendlies(WorkspaceId workspaceId) {
    SmartContentSPI.getInstance().getWorkspaceService().removeAllFriendlies(workspaceId);
  }

  @Override
  public void removeAllRepresentationTemplates(WorkspaceId workspaceId) {
    SmartContentSPI.getInstance().getWorkspaceService().removeAllRepresentationTemplates(workspaceId);
  }

  @Override
  public void removeAllVariationTemplates(WorkspaceId workspaceId) {
    SmartContentSPI.getInstance().getWorkspaceService().removeAllVariationTemplates(workspaceId);
  }

  @Override
  public Collection<String> getRepresentationNames(WorkspaceId id, ResourceSortCriteria criteria, String startPoint,
                                                   int count) {
    if (count == 0 || startPoint == null) {
      return Collections.emptyList();
    }
    List<String> list = new ArrayList<String>(getRepresentationNames(id, criteria));
    if (logger.isDebugEnabled()) {
      logger.debug("All names " + list);
    }
    int index = Collections.binarySearch(list, startPoint);
    if (logger.isDebugEnabled()) {
      logger.debug("Index " + index);
    }
    if (index < 0) {
      index = index * - 1;
    }
    if (index >= list.size() && count > 0 && StringUtils.isNotBlank(startPoint)) {
      logger.debug("Index is equal to size and count is greater than 0");
      return Collections.emptyList();
    }
    if (index <= 0 && count < 0) {
      logger.debug("Index is zero to size and count is smaller than 0");
      return Collections.emptyList();
    }
    final int fromIndex;
    final int toIndex;
    if (count > 0) {
      fromIndex = StringUtils.isBlank(startPoint) ? 0 : index;
      toIndex = (fromIndex + count >= list.size()) ? list.size() : fromIndex + count;
    }
    else {
      toIndex = index;
      fromIndex = (toIndex + count >= 0) ? toIndex + count : 0;
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Sublisting starts at " + fromIndex + " and ends before " + toIndex);
    }
    final List<String> result = list.subList(fromIndex, toIndex);
    if (logger.isDebugEnabled()) {
      logger.debug("Returning " + result);
    }
    return result;
  }

  @Override
  public Collection<String> getVariationNames(WorkspaceId id, ResourceSortCriteria criteria, String startPoint,
                                              int count) {
    if (count == 0 || startPoint == null) {
      return Collections.emptyList();
    }
    List<String> list = new ArrayList<String>(getVariationNames(id, criteria));
    int index = Collections.binarySearch(list, startPoint);
    if (index < 0) {
      index = index * - 1;
    }
    if (index >= list.size() && count > 0 && StringUtils.isNotBlank(startPoint)) {
      return Collections.emptyList();
    }
    if (index <= 0 && count < 0) {
      return Collections.emptyList();
    }
    final int fromIndex;
    final int toIndex;
    if (count > 0) {
      fromIndex = StringUtils.isBlank(startPoint) ? 0 : index;
      toIndex = (fromIndex + count >= list.size()) ? list.size() : fromIndex + count;
    }
    else {
      toIndex = index;
      fromIndex = (toIndex + count >= 0) ? toIndex + count : 0;
    }
    return list.subList(fromIndex, toIndex);
  }

  @Override
  public Collection<String> getRepresentationNames(WorkspaceId id, ResourceSortCriteria criteria) {
    final Collection<? extends ResourceTemplate> repsWithoutData = SmartContentSPI.getInstance().
        getWorkspaceService().getRepresentationsWithoutData(id, criteria);
    return getResourceNames(repsWithoutData);
  }

  @Override
  public Collection<String> getVariationNames(WorkspaceId id, ResourceSortCriteria criteria) {
    final Collection<? extends ResourceTemplate> variationsWithoutData = SmartContentSPI.getInstance().
        getWorkspaceService().getVariationsWithoutData(id, criteria);
    return getResourceNames(variationsWithoutData);
  }

  protected Collection<String> getResourceNames(Collection<? extends ResourceTemplate> templates) {
    ArrayList<String> list = new ArrayList<String>(templates.size());
    for (ResourceTemplate template : templates) {
      list.add(template.getName());
    }
    return list;
  }

  @Override
  public Collection<String> getRepresentationNames(WorkspaceId id) {
    return getRepresentationNames(id, ResourceSortCriteria.BY_NAME);
  }

  @Override
  public Collection<String> getVariationNames(WorkspaceId id) {
    return getVariationNames(id, ResourceSortCriteria.BY_NAME);
  }

  @Override
  public Collection<String> getRepresentationNames(WorkspaceId id, String startPoint, int count) {
    return getRepresentationNames(id, ResourceSortCriteria.BY_NAME, startPoint, count);
  }

  @Override
  public Collection<String> getVariationNames(WorkspaceId id, String startPoint, int count) {
    return getVariationNames(id, ResourceSortCriteria.BY_NAME, startPoint, count);
  }

  @Override
  public RepresentationTemplate getRepresentationTemplate(WorkspaceId id, String name) {
    return SmartContentSPI.getInstance().getWorkspaceService().getRepresentationTemplate(id, name);
  }

  @Override
  public VariationTemplate getVariationTemplate(WorkspaceId id, String name) {
    return SmartContentSPI.getInstance().getWorkspaceService().getVariationTemplate(id, name);
  }

  @Override
  public String getEntityTagValueForResourceTemplate(ResourceTemplate template) {
    final String toString = new StringBuilder(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(template.
        getLastModifiedDate())).append(':').append(Arrays.toString(template.getTemplate())).append(':').append(template.
        getTemplateType().name()).toString();
    final String etag = DigestUtils.md5Hex(toString);
    if (logger.isDebugEnabled()) {
      logger.debug("Generated etag " + etag + " for " + template.getClass().getName() + " with name " +
          template.getName());
    }
    return etag;
  }
}
