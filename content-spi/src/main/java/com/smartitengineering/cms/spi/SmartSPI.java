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
package com.smartitengineering.cms.spi;

import com.smartitengineering.cms.api.type.MutableContentType;
import com.smartitengineering.cms.api.type.PersistentWriter;
import com.smartitengineering.cms.api.SmartContentAPI;
import com.smartitengineering.cms.spi.lock.LockHandler;
import com.smartitengineering.cms.spi.persistence.PersistentService;
import com.smartitengineering.cms.spi.persistence.PersistentServiceRegistrar;
import com.smartitengineering.cms.spi.type.TypeValidator;
import com.smartitengineering.util.bean.BeanFactoryRegistrar;
import com.smartitengineering.util.bean.annotations.Aggregator;
import com.smartitengineering.util.bean.annotations.InjectableField;

/**
 * All SPI collection for SPI implementations.
 *
 */
@Aggregator(contextName = SmartSPI.SPI_CONTEXT)
public final class SmartSPI {

		public static final String SPI_CONTEXT = SmartContentAPI.CONTEXT_NAME +
																						 ".spi";
		/**
		 * The lock handler implementation to be used to receive lock implementations.
		 * Use <tt>lockHandler</tt> as bean name to be injected here.
		 */
		@InjectableField
		protected LockHandler lockHandler;

		/**
		 * The type validator implementation which validatates a content type
		 * definition file source. Use <tt>typeValidator</tt> as the bean name in
		 * bean factory to be injected here.
		 */
		@InjectableField
		protected TypeValidator typeValidator;

		/**
		 * The registrar for aggregating different implementations of
		 * {@link PersistentService} for diffent domain types. Use the bean name
		 * <tt>persistentServiceRegistrar</tt> for injecting it here.
		 */
		@InjectableField
		protected PersistentServiceRegistrar persistentServiceRegistrar;

		private PersistentServiceRegistrar getPersistentServiceRegistrar() {
				return persistentServiceRegistrar;
		}

		/**
		 * An operation for retrieving the concrete implementation of persistent
		 * service implementaion for the given persistable API bean.
		 * @param <T> Should represent the class to be used in concrete SPI
		 *					  implementations. For example, {@link MutableContentType}
		 * @param writerClass The class to look for in the registrar.
		 * @return Service for persisting the bean.
		 * @see PersistentServiceRegistrar#getPersistentService(java.lang.Class) 
		 */
		public <T extends PersistentWriter> PersistentService<T> getPersistentService(Class<T> writerClass) {
				return getPersistentServiceRegistrar().getPersistentService(writerClass);
		}

		public TypeValidator getTypeValidator() {
				return typeValidator;
		}

		public LockHandler getLockHandler() {
				return lockHandler;
		}

		private SmartSPI() {
		}
		private static SmartSPI spi;

		public static SmartSPI getInstance() {
				if (spi == null) {
						spi = new SmartSPI();
						BeanFactoryRegistrar.aggregate(spi);
				}
				return spi;
		}
}
