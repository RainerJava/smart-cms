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
package com.smartitengineering.cms.spi.impl.type;

import com.smartitengineering.dao.impl.hbase.spi.ExecutorService;
import com.smartitengineering.dao.impl.hbase.spi.impl.AbstactObjectRowConverter;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

/**
 *
 * @author imyousuf
 */
public class ContentTypeObjectConverter extends AbstactObjectRowConverter<PersistableContentType> {

  @Override
  protected String[] getTablesToAttainLock() {
    return new String[]{getInfoProvider().getMainTableName()};
  }

  @Override
  protected void getPutForTable(PersistableContentType instance, ExecutorService service, Put put) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected void getDeleteForTable(PersistableContentType instance, ExecutorService service, Delete put) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public PersistableContentType rowsToObject(Result startRow, ExecutorService executorService) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
