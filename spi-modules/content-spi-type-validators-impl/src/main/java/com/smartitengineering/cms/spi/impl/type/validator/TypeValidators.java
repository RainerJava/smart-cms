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
package com.smartitengineering.cms.spi.impl.type.validator;

import com.google.inject.Inject;
import com.smartitengineering.cms.api.common.MediaType;
import com.smartitengineering.cms.spi.type.TypeValidator;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author imyousuf
 */
public final class TypeValidators implements com.smartitengineering.cms.spi.type.TypeValidators {

  private final Map<MediaType, TypeValidator> validators;

  @Inject
  public TypeValidators(Map<MediaType, TypeValidator> validators) {
    this.validators = validators;
  }

  @Override
  public Map<MediaType, TypeValidator> getValidators() {
    return Collections.unmodifiableMap(validators);
  }

  public void setValidators(Map<MediaType, TypeValidator> validators) {
    if (validators == null) {
      return;
    }
    this.validators.clear();
    this.validators.putAll(validators);
  }

  public void addValidator(MediaType mediaType, TypeValidator validator) {
    this.validators.put(mediaType, validator);
  }

  public void removeValidator(MediaType mediaType) {
    this.validators.remove(mediaType);
  }
}
