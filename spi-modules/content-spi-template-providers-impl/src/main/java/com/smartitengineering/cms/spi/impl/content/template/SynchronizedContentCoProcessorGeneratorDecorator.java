/*
 *
 * This is a simple Content Management System (CMS)
 * Copyright (C) 2012  Imran M Yousuf (imyousuf@smartitengineering.com)
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
package com.smartitengineering.cms.spi.impl.content.template;

import com.smartitengineering.cms.api.content.MutableContent;
import com.smartitengineering.cms.api.content.template.ContentCoProcessor;
import java.util.Map;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imyousuf
 */
public final class SynchronizedContentCoProcessorGeneratorDecorator implements ContentCoProcessor {

  private final ContentCoProcessor coProcessor;
  private final transient Logger logger = LoggerFactory.getLogger(getClass());
  private static final Semaphore MUTEX = new Semaphore(1);

  public SynchronizedContentCoProcessorGeneratorDecorator(ContentCoProcessor coProcessor) {
    this.coProcessor = coProcessor;
  }

  public void processContent(MutableContent content,
                             Map<String, String> params) {
    try {
      MUTEX.acquire();
    }
    catch (InterruptedException ex) {
      logger.warn("Could not acquire thus returning", ex);
      return;
    }
    try {
      this.coProcessor.processContent(content, params);
    }
    finally {
      MUTEX.release();
    }
  }
}
