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
package com.smartitengineering.cms.api.impl.content;

import com.smartitengineering.cms.api.content.BooleanFieldValue;
import com.smartitengineering.cms.api.content.CollectionFieldValue;
import com.smartitengineering.cms.api.content.Content;
import com.smartitengineering.cms.api.content.ContentFieldValue;
import com.smartitengineering.cms.api.content.ContentId;
import com.smartitengineering.cms.api.content.FieldValue;
import com.smartitengineering.cms.api.content.Variation;
import com.smartitengineering.cms.api.factory.content.ContentLoader;
import com.smartitengineering.cms.api.content.DateTimeFieldValue;
import com.smartitengineering.cms.api.content.Field;
import com.smartitengineering.cms.api.content.Filter;
import com.smartitengineering.cms.api.content.MutableBooleanFieldValue;
import com.smartitengineering.cms.api.content.MutableCollectionFieldValue;
import com.smartitengineering.cms.api.content.MutableContentFieldValue;
import com.smartitengineering.cms.api.content.MutableDateTimeFieldValue;
import com.smartitengineering.cms.api.content.MutableField;
import com.smartitengineering.cms.api.content.MutableNumberFieldValue;
import com.smartitengineering.cms.api.content.MutableOtherFieldValue;
import com.smartitengineering.cms.api.content.MutableStringFieldValue;
import com.smartitengineering.cms.api.content.NumberFieldValue;
import com.smartitengineering.cms.api.content.OtherFieldValue;
import com.smartitengineering.cms.api.content.StringFieldValue;
import com.smartitengineering.cms.api.factory.content.WriteableContent;
import com.smartitengineering.cms.api.type.CollectionDataType;
import com.smartitengineering.cms.api.type.ContentType;
import com.smartitengineering.cms.api.type.FieldDef;
import com.smartitengineering.cms.api.type.FieldValueType;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.cms.spi.SmartContentSPI;
import com.smartitengineering.cms.spi.content.PersistableContent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

/**
 *
 * @author kaisar
 */
public class ContentLoaderImpl implements ContentLoader {

  @Override
  public MutableField createMutableField(FieldDef fieldDef) {
    if (fieldDef != null) {
      FieldImpl fieldImpl = new FieldImpl();
      fieldImpl.setFieldDef(fieldDef);
      fieldImpl.setName(fieldDef.getName());
      return fieldImpl;
    }
    else {
      throw new IllegalArgumentException("Argument can not be null.");

    }
  }

  @Override
  public MutableField createMutableField(Field field) {
    if (field.getFieldDef() != null) {
      FieldImpl fieldImpl = new FieldImpl();
      fieldImpl.setName(field.getName());
      fieldImpl.setValue(field.getValue());
      fieldImpl.setFieldDef(field.getFieldDef());
      return fieldImpl;
    }
    else {
      throw new IllegalArgumentException("FieldDef can not be null.");
    }
  }

  @Override
  public MutableDateTimeFieldValue createDateTimeFieldValue(DateTimeFieldValue fieldValue) {
    if (fieldValue.getValue() != null) {
      DateTimeFieldValueImpl dateTimeFieldValueImpl = new DateTimeFieldValueImpl();
      dateTimeFieldValueImpl.setValue(fieldValue.getValue());
      return dateTimeFieldValueImpl;
    }
    else {
      throw new IllegalArgumentException("Argument can not be null.");
    }
  }

  @Override
  public MutableBooleanFieldValue createBooleanFieldValue(BooleanFieldValue fieldValue) {
    BooleanFieldValueImpl booleanFieldValueImpl = new BooleanFieldValueImpl();
    booleanFieldValueImpl.setValue(fieldValue.getValue());
    return booleanFieldValueImpl;
  }

  @Override
  public MutableCollectionFieldValue createCollectionFieldValue(CollectionFieldValue fieldValue) {
    if (fieldValue.getValue() != null) {
      CollectionFieldValueImpl collectionFieldValueImpl = new CollectionFieldValueImpl();
      collectionFieldValueImpl.setValue(fieldValue.getValue());
      return collectionFieldValueImpl;
    }
    else {
      throw new IllegalArgumentException("Argument can not be null.");
    }
  }

  @Override
  public MutableContentFieldValue createContentFieldValue(ContentFieldValue fieldValue) {
    if (fieldValue.getValue() != null) {
      ContentFieldValueImpl contentFieldValueImpl = new ContentFieldValueImpl();
      contentFieldValueImpl.setValue(fieldValue.getValue());
      return contentFieldValueImpl;
    }
    else {
      throw new IllegalArgumentException("Argument can not be null.");
    }
  }

  @Override
  public MutableNumberFieldValue createNumberFieldValue(NumberFieldValue fieldValue) {
    if (fieldValue.getValue() != null) {
      NumberFieldValueImpl fieldValueImpl = new NumberFieldValueImpl();
      fieldValueImpl.setValue(fieldValue.getValue());
      return fieldValueImpl;
    }
    else {
      throw new IllegalArgumentException("Argument can not be null.");
    }
  }

  @Override
  public MutableOtherFieldValue createOtherFieldValue(OtherFieldValue fieldValue) {
    if (fieldValue.getValue() != null) {
      OtherFieldValueImpl otherFieldValueImpl = new OtherFieldValueImpl();
      otherFieldValueImpl.setValue(fieldValue.getValue());
      return otherFieldValueImpl;
    }
    else {
      throw new IllegalArgumentException("Argument can not be null.");
    }
  }

  @Override
  public MutableStringFieldValue createStringFieldValue(StringFieldValue fieldValue) {
    if (fieldValue.getValue() != null) {
      StringFieldValueImpl stringFieldValueImpl = new StringFieldValueImpl();
      stringFieldValueImpl.setValue(fieldValue.getValue());
      return stringFieldValueImpl;
    }
    else {
      throw new IllegalArgumentException("Argument can not be null.");
    }
  }

  @Override
  public ContentId createContentId(WorkspaceId workspaceId, byte[] id) {
    if (workspaceId != null && id != null) {
      ContentIdImpl contentIdImpl = new ContentIdImpl();
      contentIdImpl.setId(id);
      contentIdImpl.setWorkspaceId(workspaceId);
      return contentIdImpl;
    }
    else {
      throw new IllegalArgumentException("Argument can not be null.");
    }
  }

  @Override
  public Content loadContent(ContentId contentId) {
    final Collection<Content> contents =
                              SmartContentSPI.getInstance().getContentReader().readContentsFromPersistentStorage(
        contentId);
    if (contents == null || contents.isEmpty()) {
      return null;
    }
    return contents.iterator().next();
  }

  @Override
  public Filter craeteFilter() {
    return new FilterImpl();
  }

  @Override
  public Set<Content> search(Filter filter) {
    return Collections.unmodifiableSet(new LinkedHashSet<Content>(SmartContentSPI.getInstance().getContentSearcher().
        search(filter)));
  }

  @Override
  public MutableDateTimeFieldValue createDateTimeFieldValue() {
    return new DateTimeFieldValueImpl();
  }

  @Override
  public MutableBooleanFieldValue createBooleanFieldValue() {
    return new BooleanFieldValueImpl();
  }

  @Override
  public MutableCollectionFieldValue createCollectionFieldValue() {
    return new CollectionFieldValueImpl();
  }

  @Override
  public MutableContentFieldValue createContentFieldValue() {
    return new ContentFieldValueImpl();
  }

  @Override
  public MutableNumberFieldValue createNumberFieldValue() {
    return new NumberFieldValueImpl();
  }

  @Override
  public MutableOtherFieldValue createOtherFieldValue() {
    return new OtherFieldValueImpl();
  }

  @Override
  public MutableStringFieldValue createStringFieldValue() {
    return new StringFieldValueImpl();
  }

  @Override
  public Variation getVariation(Field field, String name) {
    return SmartContentSPI.getInstance().getVariationProvider().getVariation(name, field.getFieldDef(), field);
  }

  @Override
  public WriteableContent createContent(ContentType contentType) {
    PersistableContent content = SmartContentSPI.getInstance().getPersistableDomainFactory().createPersistableContent();
    content.setContentDefinition(contentType);
    return content;
  }

  @Override
  public WriteableContent getWritableContent(Content content) {
    PersistableContent mutableContent = SmartContentSPI.getInstance().getPersistableDomainFactory().
        createPersistableContent();
    mutableContent.setContentDefinition(content.getContentDefinition());
    mutableContent.setContentId(content.getContentId());
    mutableContent.setCreationDate(content.getCreationDate());
    mutableContent.setLastModifiedDate(content.getLastModifiedDate());
    mutableContent.setParentId(content.getParentId());
    mutableContent.setStatus(content.getStatus());
    for (Field field : content.getFields().values()) {
      mutableContent.setField(field);
    }
    return mutableContent;
  }

  @Override
  public FieldValue getValueFor(String value, FieldDef fieldDef) {
    final FieldValue result;
    final FieldValueType type = fieldDef.getValueDef().getType();
    switch (type) {
      case COLLECTION:
        MutableCollectionFieldValue collectionFieldValue = new CollectionFieldValueImpl();
        try {
          JsonNode node = CollectionFieldValueImpl.MAPPER.readTree(value);
          if (node instanceof ArrayNode) {
            ArrayNode arrayNode = (ArrayNode) node;
            int size = arrayNode.size();
            ArrayList<FieldValue> values = new ArrayList<FieldValue>(size);
            for (int i = 0; i < size; ++i) {
              String stringValue = arrayNode.get(i).getTextValue();
              values.add(getSimpleValueFor(stringValue, ((CollectionDataType) fieldDef.getValueDef()).getItemDataType().
                  getType()));
            }
            collectionFieldValue.setValue(values);
          }
          else {
            throw new IllegalStateException("Collection must be of array of strings!");
          }
        }
        catch (Exception ex) {
          throw new RuntimeException(ex);
        }
        result = collectionFieldValue;
        break;
      default:
        result = getSimpleValueFor(value, type);
    }
    return result;
  }

  public FieldValue getSimpleValueFor(String value, FieldValueType type) {
    final FieldValue result;
    switch (type) {
      case BOOLEAN:
        MutableBooleanFieldValue booleanFieldValue = createBooleanFieldValue();
        result = booleanFieldValue;
        booleanFieldValue.setValue(Boolean.parseBoolean(value));
        break;
      case CONTENT:
        MutableContentFieldValue contentFieldValue = createContentFieldValue();
        try {
          DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(value.getBytes("UTF-8")));
          ContentIdImpl idImpl = new ContentIdImpl();
          idImpl.readExternal(inputStream);
          contentFieldValue.setValue(idImpl);
        }
        catch (Exception ex) {
          throw new RuntimeException(ex);
        }
        result = contentFieldValue;
        break;
      case INTEGER:
        MutableNumberFieldValue integerFieldValue = createNumberFieldValue();
        integerFieldValue.setValue(NumberUtils.toInt(value, Integer.MIN_VALUE));
        result = integerFieldValue;
        break;
      case DOUBLE:
        MutableNumberFieldValue doubleFieldValue = createNumberFieldValue();
        doubleFieldValue.setValue(NumberUtils.toDouble(value, Double.MIN_VALUE));
        result = doubleFieldValue;
        break;
      case LONG:
        MutableNumberFieldValue longFieldValue = createNumberFieldValue();
        longFieldValue.setValue(NumberUtils.toLong(value, Long.MIN_VALUE));
        result = longFieldValue;
        break;
      case DATE_TIME:
        MutableDateTimeFieldValue valueOf;
        try {
          valueOf = DateTimeFieldValueImpl.valueOf(value);
        }
        catch (Exception ex) {
          throw new RuntimeException(ex);
        }
        result = valueOf;
        break;
      case STRING:
      default:
        MutableStringFieldValue fieldValue = createStringFieldValue();
        fieldValue.setValue(value);
        result = fieldValue;
    }
    return result;
  }

  @Override
  public ContentId generateContentId(WorkspaceId workspaceId) {
    UUID uid = UUID.randomUUID();
    try {
      return createContentId(workspaceId, uid.toString().getBytes("UTF-8"));
    }
    catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }
}
