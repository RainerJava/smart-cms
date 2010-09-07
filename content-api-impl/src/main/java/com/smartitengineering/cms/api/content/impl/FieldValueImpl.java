/*
 *
 * This is a simple Content Management System (CMS)
 * Copyright (C) 2009  Imran M Yousuf (imyousuf@smartitengineering.com)
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
package com.smartitengineering.cms.api.content.impl;

import com.smartitengineering.cms.api.content.FieldValue;
import com.smartitengineering.cms.api.content.MutableFieldValue;
import com.smartitengineering.cms.api.type.FieldValueType;

/**
 *
 * @author kaisar
 */
public class FieldValueImpl<V> implements FieldValue<V>, MutableFieldValue<V> {

  private FieldValueType fieldValueType;
  private V value;

  @Override
  public void setValue(V newV) {
    this.value = newV;
  }

  @Override
  public FieldValueType getDataType() {
    return this.fieldValueType;
  }

  @Override
  public V getValue() {
    return value;
  }
}
