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
import com.smartitengineering.cms.api.content.MutableRepresentation;
import com.smartitengineering.cms.api.content.MutableVariation;
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
import com.smartitengineering.cms.api.common.SearchResult;
import com.smartitengineering.cms.api.content.StringFieldValue;
import com.smartitengineering.cms.api.factory.content.WriteableContent;
import com.smartitengineering.cms.api.type.CollectionDataType;
import com.smartitengineering.cms.api.type.ContentDataType;
import com.smartitengineering.cms.api.type.ContentType;
import com.smartitengineering.cms.api.type.ContentTypeId;
import com.smartitengineering.cms.api.type.DataType;
import com.smartitengineering.cms.api.type.FieldDef;
import com.smartitengineering.cms.api.type.FieldValueType;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.cms.spi.SmartContentSPI;
import com.smartitengineering.cms.spi.content.PersistableContent;
import com.smartitengineering.dao.common.queryparam.QueryParameter;
import com.smartitengineering.dao.common.queryparam.QueryParameterFactory;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kaisar
 */
public class ContentLoaderImpl implements ContentLoader {

  protected final transient Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public MutableField createMutableField(ContentId contentId, FieldDef fieldDef) {
    if (fieldDef != null) {
      FieldImpl fieldImpl = new FieldImpl();
      fieldImpl.setFieldDef(fieldDef);
      fieldImpl.setName(fieldDef.getName());
      fieldImpl.setContent(contentId);
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
  public SearchResult<Content> search(Filter filter) {
    return SmartContentSPI.getInstance().getContentSearcher().search(filter);
  }

  @Override
  public MutableDateTimeFieldValue createDateTimeFieldValue() {
    final DateTimeFieldValueImpl dateTimeFieldValueImpl = new DateTimeFieldValueImpl();
    dateTimeFieldValueImpl.setFieldValueType(FieldValueType.DATE_TIME);
    return dateTimeFieldValueImpl;
  }

  @Override
  public MutableBooleanFieldValue createBooleanFieldValue() {
    final BooleanFieldValueImpl booleanFieldValueImpl = new BooleanFieldValueImpl();
    booleanFieldValueImpl.setFieldValueType(FieldValueType.BOOLEAN);
    return booleanFieldValueImpl;
  }

  @Override
  public MutableCollectionFieldValue createCollectionFieldValue() {
    final CollectionFieldValueImpl collectionFieldValueImpl = new CollectionFieldValueImpl();
    collectionFieldValueImpl.setFieldValueType(FieldValueType.COLLECTION);
    return collectionFieldValueImpl;
  }

  @Override
  public MutableContentFieldValue createContentFieldValue() {
    final ContentFieldValueImpl contentFieldValueImpl = new ContentFieldValueImpl();
    contentFieldValueImpl.setFieldValueType(FieldValueType.CONTENT);
    return contentFieldValueImpl;
  }

  @Override
  public MutableNumberFieldValue createDoubleFieldValue() {
    final NumberFieldValueImpl numberFieldValueImpl = new NumberFieldValueImpl();
    numberFieldValueImpl.setFieldValueType(FieldValueType.DOUBLE);
    return numberFieldValueImpl;
  }

  @Override
  public MutableNumberFieldValue createIntegerFieldValue() {
    final NumberFieldValueImpl numberFieldValueImpl = new NumberFieldValueImpl();
    numberFieldValueImpl.setFieldValueType(FieldValueType.INTEGER);
    return numberFieldValueImpl;
  }

  @Override
  public MutableNumberFieldValue createLongFieldValue() {
    final NumberFieldValueImpl numberFieldValueImpl = new NumberFieldValueImpl();
    numberFieldValueImpl.setFieldValueType(FieldValueType.LONG);
    return numberFieldValueImpl;
  }

  @Override
  public MutableOtherFieldValue createOtherFieldValue() {
    final OtherFieldValueImpl otherFieldValueImpl = new OtherFieldValueImpl();
    otherFieldValueImpl.setFieldValueType(FieldValueType.OTHER);
    return otherFieldValueImpl;
  }

  @Override
  public MutableStringFieldValue createStringFieldValue() {
    final StringFieldValueImpl stringFieldValueImpl = new StringFieldValueImpl();
    stringFieldValueImpl.setFieldValueType(FieldValueType.STRING);
    return stringFieldValueImpl;
  }

  @Override
  public Variation getVariation(Content content, Field field, String name) {
    return SmartContentSPI.getInstance().getVariationProvider().getVariation(name, content, field);
  }

  @Override
  public WriteableContent createContent(ContentType contentType) {
    return createContent(contentType, false);
  }

  @Override
  public WriteableContent createContent(ContentType contentType, boolean supressChecking) {
    PersistableContent content = SmartContentSPI.getInstance().getPersistableDomainFactory().createPersistableContent(
        supressChecking);
    content.setContentDefinition(contentType);
    return content;
  }

  @Override
  public WriteableContent getWritableContent(Content content) {
    return getWritableContent(content, false);
  }

  @Override
  public WriteableContent getWritableContent(Content content, boolean supressChecking) {
    PersistableContent mutableContent = SmartContentSPI.getInstance().getPersistableDomainFactory().
        createPersistableContent(supressChecking);
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
  public FieldValue getValueFor(String value, DataType dataType) {
    final FieldValue result;
    final FieldValueType type = dataType.getType();
    switch (type) {
      case COLLECTION:
        MutableCollectionFieldValue collectionFieldValue = createCollectionFieldValue();
        try {
          JsonNode node = CollectionFieldValueImpl.MAPPER.readTree(value);
          if (node instanceof ArrayNode) {
            ArrayNode arrayNode = (ArrayNode) node;
            int size = arrayNode.size();
            ArrayList<FieldValue> values = new ArrayList<FieldValue>(size);
            for (int i = 0; i < size; ++i) {
              String stringValue = arrayNode.get(i).getTextValue();
              values.add(getSimpleValueFor(stringValue, ((CollectionDataType) dataType).getItemDataType().
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
        if (logger.isInfoEnabled()) {
          logger.info("Content value: " + value);
        }
        try {
          DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(StringUtils.getBytesUtf8(value)));
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
        MutableNumberFieldValue integerFieldValue = createIntegerFieldValue();
        integerFieldValue.setValue(NumberUtils.toInt(value, Integer.MIN_VALUE));
        result = integerFieldValue;
        break;
      case DOUBLE:
        MutableNumberFieldValue doubleFieldValue = createDoubleFieldValue();
        doubleFieldValue.setValue(NumberUtils.toDouble(value, Double.MIN_VALUE));
        result = doubleFieldValue;
        break;
      case LONG:
        MutableNumberFieldValue longFieldValue = createLongFieldValue();
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
      case OTHER:
        MutableOtherFieldValue otherFieldValue = createOtherFieldValue();
        otherFieldValue.setValue(Base64.decodeBase64(value));
        result = otherFieldValue;
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
    return createContentId(workspaceId, StringUtils.getBytesUtf8(uid.toString()));
  }

  @Override
  public String getEntityTagValueForContent(Content content) {
    return DigestUtils.md5Hex(new StringBuilder(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(content.
        getLastModifiedDate())).append('~').append(content.getOwnFields().toString()).append('~').append(content.
        getStatus()).toString());
  }

  @Override
  public MutableRepresentation createMutableRepresentation(ContentId contentId) {
    return new RepresentationImpl(contentId);
  }

  @Override
  public MutableVariation createMutableVariation(ContentId contentId, FieldDef fieldDef) {
    return new VariationImpl(contentId, fieldDef);
  }

  @Override
  public boolean isValidContent(Content content) {
    if (!isValid(content)) {
      return false;
    }
    if (!checkForRelatedContents(content)) {
      return false;
    }
    if (!isValidByCustomValidator(content)) {
      return false;
    }
    return true;
  }

  protected boolean isValid(Content content) {
    if (logger.isDebugEnabled()) {
      logger.debug("Content: " + content);
      if (content != null) {
        logger.debug("Content ID: " + content.getContentId());
        logger.debug("Content Definition: " + content.getContentDefinition());
        if (content.getContentDefinition() != null) {
          logger.debug("Required fields present: " + isMandatoryFieldsPresent(content));
        }
      }
    }
    if (logger.isWarnEnabled() && !(content != null && content.getContentId() != null && content.getContentDefinition() !=
                                    null)) {
      logger.warn("Content or its ID or content definition is missing!");
    }
    return content != null && content.getContentId() != null && content.getContentDefinition() != null && isMandatoryFieldsPresent(
        content);
  }

  protected boolean isMandatoryFieldsPresent(Content content) {
    ContentType type = content.getContentDefinition();
    boolean valid = true;
    for (FieldDef def : type.getFieldDefs().values()) {
      if (logger.isDebugEnabled()) {
        logger.debug(def.getName() + " is required: " + def.isRequired());
        logger.debug(def.getName() + ": " + content.getField(def.getName()));
      }
      if (def.isRequired() && content.getField(def.getName()) == null) {
        if (logger.isWarnEnabled()) {
          logger.warn("Required field not present " + def.getName());
        }
        valid = valid && false;
      }
    }
    return valid;
  }

  protected boolean isValidByCustomValidator(Content content) {
    boolean valid = true;
    for (Field field : content.getFields().values()) {
      final boolean validField = SmartContentSPI.getInstance().getValidatorProvider().isValidField(content, field);
      if (!validField && logger.isWarnEnabled()) {
        logger.warn("Custom field validation failed for " + field.getName());
      }
      valid = valid && validField;
    }
    return valid;
  }

  protected void addQueryForContentId(final ContentId contentId, ContentTypeId instanceOfId, Filter filter) {
    QueryParameter<String> param = QueryParameterFactory.getStringLikePropertyParam("id", new StringBuilder().append(contentId.
        toString()).toString());
    filter.addFieldFilter(param);
    filter.addContentTypeToFilter(instanceOfId);
  }

  protected boolean checkForRelatedContents(Content content) {
    boolean valid = true;
    for (Field field : content.getFields().values()) {
      if (field != null && field.getFieldDef() != null) {
        FieldDef def = field.getFieldDef();
        switch (def.getValueDef().getType()) {
          case COLLECTION:
            if (((CollectionDataType) def.getValueDef()).getItemDataType().getType().equals(FieldValueType.CONTENT)) {
              DataType contentDataType = ((CollectionDataType) def.getValueDef()).getItemDataType();
              for (FieldValue val : ((CollectionFieldValue) field.getValue()).getValue()) {
                final boolean checkContentTypeValidity = checkContentTypeValidity(val, contentDataType);
                if (!checkContentTypeValidity && logger.isWarnEnabled()) {
                  logger.warn("Content relation failed in " + field.getName());
                }
                valid = valid && checkContentTypeValidity;
              }
            }
            break;
          case CONTENT: {
            final boolean checkContentTypeValidity = checkContentTypeValidity(field.getValue(), def.getValueDef());
            if (!checkContentTypeValidity && logger.isWarnEnabled()) {
              logger.warn("Content relation failed in " + field.getName());
            }
            valid = valid && checkContentTypeValidity;
          }
          break;
          default:
            break;
        }
      }
      if (!valid) {
        return valid;
      }
    }
    return valid;
  }

  protected boolean checkContentTypeValidity(FieldValue val, DataType contentDataType) {
    Filter filter = craeteFilter();
    addQueryForContentId(((ContentFieldValue) val).getValue(), ((ContentDataType) contentDataType).getTypeDef(), filter);
    filter.setDisjunction(false);
    Collection<Content> contents = search(filter).getResult();
    return !contents.isEmpty();
  }

  @Override
  public SearchResult<Content> createSearchResult(Collection<Content> result, long totalResultsCount) {
    SearchResultImpl<Content> resultImpl = new SearchResultImpl<Content>();
    resultImpl.setResult(result);
    resultImpl.setTotalResultsCount(totalResultsCount);
    return resultImpl;
  }

  @Override
  public void reIndex(ContentId contentId) {
    SmartContentSPI.getInstance().getContentSearcher().reIndex(contentId);
  }

  @Override
  public void reIndex(WorkspaceId workspaceId) {
    SmartContentSPI.getInstance().getContentSearcher().reIndex(workspaceId);
  }

  @Override
  public ContentId parseContentId(String contentIdStr) {
    byte[] idBytes = org.apache.commons.codec.binary.StringUtils.getBytesUtf8(contentIdStr);
    DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(idBytes));
    ContentIdImpl idImpl = new ContentIdImpl();
    try {
      idImpl.readExternal(inputStream);
    }
    catch (Exception ex) {
      logger.warn("Could not parse content id string " + contentIdStr, ex);
      return null;
    }
    return idImpl;
  }
}
