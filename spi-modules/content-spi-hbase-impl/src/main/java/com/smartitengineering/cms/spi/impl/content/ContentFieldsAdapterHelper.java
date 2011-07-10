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
package com.smartitengineering.cms.spi.impl.content;

import com.smartitengineering.cms.api.content.Field;
import com.smartitengineering.util.bean.adapter.AbstractAdapterHelper;
import java.util.Map;

/**
 *
 * @author imyousuf
 */
public class ContentFieldsAdapterHelper extends AbstractAdapterHelper<Map<String, Field>, PersistentContentFields> {

  @Override
  protected PersistentContentFields newTInstance() {
    return new PersistentContentFields();
  }

  @Override
  protected void mergeFromF2T(Map<String, Field> fromBean, PersistentContentFields toBean) {
    toBean.setFields(fromBean);
  }

  @Override
  protected Map<String, Field> convertFromT2F(PersistentContentFields toBean) {
    return toBean.getFields();
  }
}
