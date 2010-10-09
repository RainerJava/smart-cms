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
package com.smartitengineering.cms.api.type;

import java.util.Collection;

/**
 * Represents the definition of the fields of the {@link ContentType}
 * @author imyousuf
 * @since 0.1
 */
public interface FieldDef {

  /**
   * Retrieves the name of this field
   * @return the name
   */
  public String getName();

  /**
   * Retrieve the definition of the data type of this field
   * @return definition of the data type
   */
  public DataType getValueDef();

  /**
   * Returns whether the field is required or not
   * @return whether the field is required or not
   */
  public boolean isRequired();

  public Collection<VariationDef> getVariations();

  public ValidatorDef getCustomValidator();

  public SearchDef getSearchDefinition();

  public boolean isFieldStandaloneUpdateAble();
}
