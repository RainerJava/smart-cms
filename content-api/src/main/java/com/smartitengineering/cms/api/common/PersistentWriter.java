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
package com.smartitengineering.cms.api.common;

import java.io.IOException;

/**
 * Any object wanting to perform write operations on thyself should implement it.
 * This reduces dependency on external services for writing. This depdens on
 * {@link Lock} for ensuring that its the only thread trying to perform write
 * operation on it, especially when performing update and delete. If lock is
 * held exclusively then writers should not attain locks else lock should be
 * attained before performing update and delete operations, in that case preferably
 * {@link Lock#tryLock()} should be used.
 * @author imyousuf
 * @since 0.1
 */
public interface PersistentWriter
    extends Lock {

  /**
   * Persist this instance in the persistent storage.
   * @throws IOException If there is any error in the persistent procedure.
   */
  public void put()
      throws IOException;

  /**
   * Deletes the current instance from its persistent storage.
   * @throws IOException If there is any error in the persistent procedure.
   */
  public void delete()
      throws IOException;
}
