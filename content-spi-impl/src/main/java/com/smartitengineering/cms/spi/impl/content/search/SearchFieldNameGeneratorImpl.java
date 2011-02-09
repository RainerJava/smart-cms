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
package com.smartitengineering.cms.spi.impl.content.search;

import com.smartitengineering.cms.api.type.CollectionDataType;
import com.smartitengineering.cms.api.type.FieldDef;
import com.smartitengineering.cms.api.type.FieldValueType;
import com.smartitengineering.cms.api.type.SearchDef;
import com.smartitengineering.cms.spi.type.SearchFieldNameGenerator;

/**
 *
 * @author imyousuf
 */
public class SearchFieldNameGeneratorImpl implements SearchFieldNameGenerator {

  @Override
  public String getSearchFieldName(FieldDef def, boolean nestedFieldDef) {
    final SearchDef searchDefinition = def.getSearchDefinition();
    if (searchDefinition != null && (searchDefinition.isIndexed() || searchDefinition.isStored())) {
      StringBuilder indexFieldName = new StringBuilder(def.getName());
      final FieldValueType type = def.getValueDef().getType();
      final FieldValueType mainType;
      final String multi;
      if (type.equals(FieldValueType.COLLECTION)) {
        mainType = ((CollectionDataType) def.getValueDef()).getItemDataType().getType();
        multi = "m";
      }
      else if (nestedFieldDef) {
        mainType = type;
        multi = "m";
      }
      else {
        mainType = type;
        multi = "";
      }
      indexFieldName.append('_').append(mainType.name()).append('_').append(multi);
      if (searchDefinition.isIndexed()) {
        indexFieldName.append('i');
      }
      if (searchDefinition.isStored()) {
        indexFieldName.append('s');
      }
      return indexFieldName.toString();
    }
    else {
      return null;
    }
  }

  @Override
  public String getSearchFieldName(FieldDef fieldDef) {
    return getSearchFieldName(fieldDef, false);
  }
}
