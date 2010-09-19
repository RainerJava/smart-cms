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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.smartitengineering.cms.api.type.ContentType;
import com.smartitengineering.cms.api.type.ContentTypeId;
import com.smartitengineering.cms.api.type.MutableContentType;
import com.smartitengineering.cms.spi.persistence.PersistentService;
import com.smartitengineering.cms.spi.type.PersistentContentTypeReader;
import com.smartitengineering.dao.common.CommonReadDao;
import com.smartitengineering.dao.common.CommonWriteDao;
import com.smartitengineering.util.bean.adapter.GenericAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author imyousuf
 */
@Singleton
public class ContentTypePersistentService implements PersistentService<MutableContentType>, PersistentContentTypeReader {

  @Inject
  private GenericAdapter<MutableContentType, PersistableContentType> adapter;
  @Inject
  private CommonReadDao<PersistableContentType, String> commonReadDao;
  @Inject
  private CommonWriteDao<PersistableContentType> commonWriteDao;

  public GenericAdapter<MutableContentType, PersistableContentType> getAdapter() {
    return adapter;
  }

  public CommonReadDao<PersistableContentType, String> getCommonReadDao() {
    return commonReadDao;
  }

  public CommonWriteDao<PersistableContentType> getCommonWriteDao() {
    return commonWriteDao;
  }

  @Override
  public void create(MutableContentType bean) throws Exception {
    commonWriteDao.save(getAdapter().convert(bean));
  }

  @Override
  public void update(MutableContentType bean) throws Exception {
    commonWriteDao.update(getAdapter().convert(bean));
  }

  @Override
  public void delete(MutableContentType bean) throws Exception {
    commonWriteDao.delete(getAdapter().convert(bean));
  }

  @Override
  public Collection<? extends ContentType> readContentTypeFromPersistentStorage(ContentTypeId... contentTypeId) {
    List<String> list = new ArrayList<String>();
    for (ContentTypeId id : contentTypeId) {
      list.add(id.toString());
    }
    final Set<PersistableContentType> byIds = commonReadDao.getByIds(list);
    return getAdapter().convertInversely(byIds.toArray(new PersistableContentType[byIds.size()]));
  }
}
