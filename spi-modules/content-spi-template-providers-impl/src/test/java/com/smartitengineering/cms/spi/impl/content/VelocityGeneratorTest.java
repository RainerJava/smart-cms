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
package com.smartitengineering.cms.spi.impl.content;

import com.smartitengineering.cms.api.common.TemplateType;
import com.smartitengineering.cms.api.content.Content;
import com.smartitengineering.cms.api.content.ContentId;
import com.smartitengineering.cms.api.content.Field;
import com.smartitengineering.cms.api.content.FieldValue;
import com.smartitengineering.cms.api.content.Representation;
import com.smartitengineering.cms.api.content.Variation;
import com.smartitengineering.cms.api.factory.SmartContentAPI;
import com.smartitengineering.cms.api.factory.workspace.WorkspaceAPI;
import com.smartitengineering.cms.api.impl.workspace.WorkspaceAPIImpl;
import com.smartitengineering.cms.api.type.ContentType;
import com.smartitengineering.cms.api.type.FieldDef;
import com.smartitengineering.cms.api.type.RepresentationDef;
import com.smartitengineering.cms.api.type.ResourceUri;
import com.smartitengineering.cms.api.type.VariationDef;
import com.smartitengineering.cms.api.workspace.RepresentationTemplate;
import com.smartitengineering.cms.api.workspace.VariationTemplate;
import com.smartitengineering.cms.api.workspace.WorkspaceId;
import com.smartitengineering.cms.spi.content.RepresentationProvider;
import com.smartitengineering.cms.spi.content.VariationProvider;
import com.smartitengineering.cms.spi.content.template.TypeRepresentationGenerator;
import com.smartitengineering.cms.spi.content.template.TypeVariationGenerator;
import com.smartitengineering.cms.spi.impl.content.template.VelocityRepresentationGenerator;
import com.smartitengineering.cms.spi.impl.content.template.VelocityVariationGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.JUnit3Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imyousuf
 */
public class VelocityGeneratorTest {

  public static final String CONTENT = "content";
  private Mockery mockery;
  public static final String REP_NAME = "test";
  private final transient Logger logger = LoggerFactory.getLogger(getClass());

  @Before
  public void setupAPIAndSPI() throws ClassNotFoundException {
    mockery = new JUnit3Mockery();
    GroovyGeneratorTest.setupAPI(mockery);
  }

  @Test
  public void testVelocityRepGeneration() throws IOException {
    TypeRepresentationGenerator generator = new VelocityRepresentationGenerator();
    final RepresentationTemplate template = mockery.mock(RepresentationTemplate.class);
    WorkspaceAPIImpl impl = new WorkspaceAPIImpl() {

      @Override
      public RepresentationTemplate getRepresentationTemplate(WorkspaceId id, String name) {
        return template;
      }
    };
    impl.setRepresentationGenerators(Collections.singletonMap(TemplateType.VELOCITY, generator));
    RepresentationProvider provider = new RepresentationProviderImpl();
    final WorkspaceAPI api = impl;
    registerBeanFactory(api);
    final Content content = mockery.mock(Content.class);
    final Field field = mockery.mock(Field.class);
    final FieldValue value = mockery.mock(FieldValue.class);
    final Map<String, Field> fieldMap = mockery.mock(Map.class);
    final ContentType type = mockery.mock(ContentType.class);
    final Map<String, RepresentationDef> reps = mockery.mock(Map.class, "repMap");
    final RepresentationDef def = mockery.mock(RepresentationDef.class);
    mockery.checking(new Expectations() {

      {
        exactly(1).of(template).getTemplateType();
        will(returnValue(TemplateType.VELOCITY));
        exactly(1).of(template).getTemplate();
        will(returnValue(
            IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("scripts/velocity/test-template.vm"))));
        exactly(1).of(template).getName();
        will(returnValue(REP_NAME));
        exactly(1).of(value).getValue();
        will(returnValue(CONTENT));
        exactly(1).of(field).getValue();
        will(returnValue(value));
        exactly(1).of(fieldMap).get(with(Expectations.<String>anything()));
        will(returnValue(field));
        exactly(1).of(content).getFields();
        will(returnValue(fieldMap));
        exactly(1).of(content).getContentDefinition();
        will(returnValue(type));
        final ContentId contentId = mockery.mock(ContentId.class);
        exactly(2).of(content).getContentId();
        will(returnValue(contentId));
        final WorkspaceId wId = mockery.mock(WorkspaceId.class);
        exactly(1).of(contentId).getWorkspaceId();
        will(returnValue(wId));
        exactly(2).of(type).getRepresentationDefs();
        will(returnValue(reps));
        exactly(2).of(reps).get(with(REP_NAME));
        will(returnValue(def));
        exactly(1).of(def).getParameters();
        will(returnValue(Collections.emptyMap()));
        exactly(1).of(def).getMIMEType();
        will(returnValue(GroovyGeneratorTest.MIME_TYPE));
        final ResourceUri rUri = mockery.mock(ResourceUri.class);
        exactly(1).of(def).getResourceUri();
        will(returnValue(rUri));
        exactly(1).of(rUri).getValue();
        will(returnValue("iUri"));
      }
    });
    Representation representation = provider.getRepresentation(REP_NAME, type, content);
    Assert.assertNotNull(representation);
    Assert.assertEquals(REP_NAME, representation.getName());
    Assert.assertEquals(CONTENT, StringUtils.newStringUtf8(representation.getRepresentation()));
    Assert.assertEquals(GroovyGeneratorTest.MIME_TYPE, representation.getMimeType());
  }

  @Test
  public void testVelocityVarGeneration() throws IOException {
    TypeVariationGenerator generator = new VelocityVariationGenerator();
    final VariationTemplate template = mockery.mock(VariationTemplate.class);
    WorkspaceAPIImpl impl = new WorkspaceAPIImpl() {

      @Override
      public VariationTemplate getVariationTemplate(WorkspaceId id, String name) {
        return template;
      }
    };
    impl.setVariationGenerators(Collections.singletonMap(TemplateType.VELOCITY, generator));
    VariationProvider provider = new VariationProviderImpl();
    registerBeanFactory(impl);
    final Field field = mockery.mock(Field.class, "varField");
    final FieldValue value = mockery.mock(FieldValue.class, "varFieldVal");
    final FieldDef fieldDef = mockery.mock(FieldDef.class);
    final Map<String, VariationDef> vars = mockery.mock(Map.class, "varMap");
    final VariationDef def = mockery.mock(VariationDef.class);
    final Content content = mockery.mock(Content.class, "varContent");
    mockery.checking(new Expectations() {

      {
        exactly(1).of(template).getTemplateType();
        will(returnValue(TemplateType.VELOCITY));
        exactly(1).of(template).getTemplate();
        will(returnValue(
            IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("scripts/velocity/var-template.vm"))));
        exactly(1).of(template).getName();
        will(returnValue(REP_NAME));
        exactly(1).of(value).getValue();
        will(returnValue(CONTENT));
        exactly(1).of(field).getValue();
        will(returnValue(value));
        exactly(1).of(field).getFieldDef();
        will(returnValue(fieldDef));
        final ContentId contentId = mockery.mock(ContentId.class, "varId");
        exactly(2).of(content).getContentId();
        will(returnValue(contentId));
        final WorkspaceId wId = mockery.mock(WorkspaceId.class, "varWId");
        exactly(1).of(contentId).getWorkspaceId();
        will(returnValue(wId));
        exactly(1).of(fieldDef).getVariations();
        will(returnValue(vars));
        exactly(1).of(vars).get(with(REP_NAME));
        will(returnValue(def));
        exactly(1).of(def).getMIMEType();
        will(returnValue(GroovyGeneratorTest.MIME_TYPE));
        exactly(1).of(def).getParameters();
        will(returnValue(Collections.emptyMap()));
        final ResourceUri rUri = mockery.mock(ResourceUri.class, "varRUri");
        exactly(1).of(def).getResourceUri();
        will(returnValue(rUri));
        exactly(1).of(rUri).getValue();
        will(returnValue("iUri"));
      }
    });
    Variation representation = provider.getVariation(REP_NAME, content, field);
    Assert.assertNotNull(representation);
    Assert.assertEquals(REP_NAME, representation.getName());
    Assert.assertEquals(GroovyGeneratorTest.MIME_TYPE, representation.getMimeType());
    Assert.assertEquals(CONTENT, StringUtils.newStringUtf8(representation.getVariation()));
  }

  @Test
  public void testMultiVelocityRepGeneration() throws IOException {
    TypeRepresentationGenerator generator = new VelocityRepresentationGenerator();
    final RepresentationTemplate template = mockery.mock(RepresentationTemplate.class);
    WorkspaceAPIImpl impl = new WorkspaceAPIImpl() {

      @Override
      public RepresentationTemplate getRepresentationTemplate(WorkspaceId id, String name) {
        return template;
      }
    };
    impl.setRepresentationGenerators(Collections.singletonMap(TemplateType.VELOCITY, generator));
    final RepresentationProvider provider = new RepresentationProviderImpl();
    final WorkspaceAPI api = impl;
    registerBeanFactory(api);
    final Content content = mockery.mock(Content.class);
    final Field field = mockery.mock(Field.class);
    final FieldValue value = mockery.mock(FieldValue.class);
    final Map<String, Field> fieldMap = mockery.mock(Map.class);
    final ContentType type = mockery.mock(ContentType.class);
    final Map<String, RepresentationDef> reps = mockery.mock(Map.class, "repMap");
    final RepresentationDef def = mockery.mock(RepresentationDef.class);
    final int threadCount = new Random().nextInt(100);
    logger.info("Number of parallel threads " + threadCount);
    mockery.checking(new Expectations() {

      {
        exactly(threadCount).of(template).getTemplateType();
        will(returnValue(TemplateType.VELOCITY));
        exactly(threadCount).of(template).getTemplate();
        final byte[] toByteArray =
                     IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(
            "scripts/velocity/test-template.vm"));
        will(returnValue(
            toByteArray));
        exactly(threadCount).of(template).getName();
        will(returnValue(REP_NAME));
        for (int i = 0; i < threadCount; ++i) {
          exactly(1).of(value).getValue();
          will(returnValue(String.valueOf(i)));
        }
        exactly(threadCount).of(field).getValue();
        will(returnValue(value));
        exactly(threadCount).of(fieldMap).get(with(Expectations.<String>anything()));
        will(returnValue(field));
        exactly(threadCount).of(content).getFields();
        will(returnValue(fieldMap));
        exactly(threadCount).of(content).getContentDefinition();
        will(returnValue(type));
        final ContentId contentId = mockery.mock(ContentId.class);
        exactly(2 * threadCount).of(content).getContentId();
        will(returnValue(contentId));
        final WorkspaceId wId = mockery.mock(WorkspaceId.class);
        exactly(threadCount).of(contentId).getWorkspaceId();
        will(returnValue(wId));
        exactly(2 * threadCount).of(type).getRepresentationDefs();
        will(returnValue(reps));
        exactly(2 * threadCount).of(reps).get(with(REP_NAME));
        will(returnValue(def));
        exactly(threadCount).of(def).getParameters();
        will(returnValue(Collections.emptyMap()));
        exactly(threadCount).of(def).getMIMEType();
        will(returnValue(GroovyGeneratorTest.MIME_TYPE));
        final ResourceUri rUri = mockery.mock(ResourceUri.class);
        exactly(threadCount).of(def).getResourceUri();
        will(returnValue(rUri));
        exactly(threadCount).of(rUri).getValue();
        will(returnValue("iUri"));
      }
    });
    final Set<String> set = Collections.synchronizedSet(new LinkedHashSet<String>(threadCount));
    final List<String> list = Collections.synchronizedList(new ArrayList<String>(threadCount));
    final AtomicInteger integer = new AtomicInteger(0);
    Threads group = new Threads();
    for (int i = 0; i < threadCount; ++i) {
      group.addThread(new Thread(new Runnable() {

        public void run() {
          Representation representation = provider.getRepresentation(REP_NAME, type, content);
          Assert.assertNotNull(representation);
          Assert.assertEquals(REP_NAME, representation.getName());
          final String rep = StringUtils.newStringUtf8(representation.getRepresentation());
          list.add(rep);
          set.add(rep);
          Assert.assertEquals(GroovyGeneratorTest.MIME_TYPE, representation.getMimeType());
          integer.addAndGet(1);
        }
      }));
    }
    group.start();
    try {
      group.join();
    }
    catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
    }
    logger.info("Generated reps list: " + list);
    logger.info("Generated reps set: " + set);
    Assert.assertEquals(threadCount, integer.get());
    Assert.assertEquals(threadCount, list.size());
    Assert.assertEquals(threadCount, set.size());
  }

  @Test
  public void testContinuousVelocityRepGeneration() throws IOException {
    TypeRepresentationGenerator generator = new VelocityRepresentationGenerator();
    final RepresentationTemplate template = mockery.mock(RepresentationTemplate.class);
    WorkspaceAPIImpl impl = new WorkspaceAPIImpl() {

      @Override
      public RepresentationTemplate getRepresentationTemplate(WorkspaceId id, String name) {
        return template;
      }
    };
    impl.setRepresentationGenerators(Collections.singletonMap(TemplateType.VELOCITY, generator));
    final RepresentationProvider provider = new RepresentationProviderImpl();
    final WorkspaceAPI api = impl;
    registerBeanFactory(api);
    final Content content = mockery.mock(Content.class);
    final Field field = mockery.mock(Field.class);
    final FieldValue value = mockery.mock(FieldValue.class);
    final Map<String, Field> fieldMap = mockery.mock(Map.class);
    final ContentType type = mockery.mock(ContentType.class);
    final Map<String, RepresentationDef> reps = mockery.mock(Map.class, "repMap");
    final RepresentationDef def = mockery.mock(RepresentationDef.class);
    final int threadCount = new Random().nextInt(100);
    logger.info("Number of parallel threads " + threadCount);
    mockery.checking(new Expectations() {

      {
        exactly(threadCount).of(template).getTemplateType();
        will(returnValue(TemplateType.VELOCITY));
        exactly(threadCount).of(template).getTemplate();
        final byte[] toByteArray =
                     IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(
            "scripts/velocity/test-template.vm"));
        will(returnValue(
            toByteArray));
        exactly(threadCount).of(template).getName();
        will(returnValue(REP_NAME));
        for (int i = 0; i < threadCount; ++i) {
          exactly(1).of(value).getValue();
          will(returnValue(String.valueOf(Integer.MAX_VALUE)));
        }
        exactly(threadCount).of(field).getValue();
        will(returnValue(value));
        exactly(threadCount).of(fieldMap).get(with(Expectations.<String>anything()));
        will(returnValue(field));
        exactly(threadCount).of(content).getFields();
        will(returnValue(fieldMap));
        exactly(threadCount).of(content).getContentDefinition();
        will(returnValue(type));
        final ContentId contentId = mockery.mock(ContentId.class);
        exactly(2 * threadCount).of(content).getContentId();
        will(returnValue(contentId));
        final WorkspaceId wId = mockery.mock(WorkspaceId.class);
        exactly(threadCount).of(contentId).getWorkspaceId();
        will(returnValue(wId));
        exactly(2 * threadCount).of(type).getRepresentationDefs();
        will(returnValue(reps));
        exactly(2 * threadCount).of(reps).get(with(REP_NAME));
        will(returnValue(def));
        exactly(threadCount).of(def).getParameters();
        will(returnValue(Collections.emptyMap()));
        exactly(threadCount).of(def).getMIMEType();
        will(returnValue(GroovyGeneratorTest.MIME_TYPE));
        final ResourceUri rUri = mockery.mock(ResourceUri.class);
        exactly(threadCount).of(def).getResourceUri();
        will(returnValue(rUri));
        exactly(threadCount).of(rUri).getValue();
        will(returnValue("iUri"));
      }
    });
    final Set<String> set = Collections.synchronizedSet(new LinkedHashSet<String>(threadCount));
    final List<String> list = Collections.synchronizedList(new ArrayList<String>(threadCount));
    final AtomicInteger integer = new AtomicInteger(0);
    Threads group = new Threads();
    for (int i = 0; i < threadCount; ++i) {
      group.addThread(new Thread(new Runnable() {

        public void run() {
          Representation representation = provider.getRepresentation(REP_NAME, type, content);
          Assert.assertNotNull(representation);
          Assert.assertEquals(REP_NAME, representation.getName());
          final String rep = StringUtils.newStringUtf8(representation.getRepresentation());
          list.add(rep);
          set.add(rep);
          Assert.assertEquals(GroovyGeneratorTest.MIME_TYPE, representation.getMimeType());
          integer.addAndGet(1);
        }
      }));
    }
    group.start();
    try {
      group.join();
    }
    catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
    }
    logger.info("Generated reps list: " + list);
    logger.info("Generated reps set: " + set);
    Assert.assertEquals(threadCount, integer.get());
    Assert.assertEquals(threadCount, list.size());
    Assert.assertEquals(1, set.size());
  }

  static class Threads {

    private final List<Thread> threads = new ArrayList<Thread>();
    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    public void addThread(Thread thread) {
      threads.add(thread);
    }

    public void remvoeThread(Thread thread) {
      threads.remove(thread);
    }

    public void start() {
      for (Thread thread : threads) {
        thread.start();
      }
    }

    public void join() throws InterruptedException {
      int count = 0;
      for (Thread thread : threads) {
        logger.debug("Waiting for join index " + count++);
        thread.join();
      }
    }
  }

  protected void registerBeanFactory(final WorkspaceAPI api) {
    try {
      SmartContentAPI mainApi = SmartContentAPI.getInstance();
      Class apiClass = mainApi.getClass();
      java.lang.reflect.Field field = apiClass.getDeclaredField("workspaceApi");
      field.setAccessible(true);
      field.set(mainApi, api);
    }
    catch (Exception ex) {
      throw new IllegalArgumentException(ex);
    }
  }
}
