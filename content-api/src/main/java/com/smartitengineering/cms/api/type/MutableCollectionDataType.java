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
package com.smartitengineering.cms.api.type;

/**
 * The editable version of {@link CollectionDataType}
 * @author imyousuf
 * @since 0.1
 */
public interface MutableCollectionDataType
				extends CollectionDataType {

		/**
		 * Set the maximum size of the collection. If non-positive that means there
		 * is no upper bound.
		 * @param maxSize The new maximum size of the collection.
		 */
		public void setSize(int maxSize);

		/**
		 * The new data type of collection's items.
		 * @param newDataType New data type
		 * @throws IllegalArgumentException If newDataType is null.
		 */
		public void setItemDataType(DataType newDataType)
						throws IllegalArgumentException;
}
