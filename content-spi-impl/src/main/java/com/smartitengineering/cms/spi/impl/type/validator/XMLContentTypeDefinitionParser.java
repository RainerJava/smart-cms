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
package com.smartitengineering.cms.spi.impl.type.validator;

import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.cms.api.common.MediaType;
import com.smartitengineering.cms.api.type.MutableContentType;
import com.smartitengineering.cms.spi.type.ContentTypeDefinitionParser;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author kaisar
 */
public class XMLContentTypeDefinitionParser implements ContentTypeDefinitionParser {

  @Override
  public Collection<MutableContentType> parseStream(WorkspaceId workspaceId, InputStream inputStream) {
    XmlParser parser = new XmlParser(workspaceId, inputStream);
    return parser.parse();
  }

  @Override
  public Collection<MediaType> getSupportedTypes() {
    return Collections.singletonList(MediaType.APPLICATION_XML);
  }
}
