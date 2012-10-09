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
package com.smartitengineering.cms.binder.guice;

import com.google.inject.PrivateModule;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.smartitengineering.cms.api.common.MediaType;
import com.smartitengineering.cms.api.content.Content;
import com.smartitengineering.cms.api.content.ContentId;
import com.smartitengineering.cms.api.content.Field;
import com.smartitengineering.cms.api.event.EventListener;
import com.smartitengineering.cms.api.factory.content.WriteableContent;
import com.smartitengineering.cms.api.factory.type.WritableContentType;
import com.smartitengineering.cms.api.impl.DomainIdInstanceProviderImpl;
import com.smartitengineering.cms.api.impl.PersistableDomainFactoryImpl;
import com.smartitengineering.cms.api.type.ContentType;
import com.smartitengineering.cms.api.type.ContentTypeId;
import com.smartitengineering.cms.api.workspace.Sequence;
import com.smartitengineering.cms.api.workspace.SequenceId;
import com.smartitengineering.cms.spi.content.ContentSearcher;
import com.smartitengineering.cms.spi.content.PersistentContentReader;
import com.smartitengineering.cms.spi.content.UriProvider;
import com.smartitengineering.cms.spi.impl.DefaultLockHandler;
import com.smartitengineering.cms.spi.impl.SearchBeanLoader;
import com.smartitengineering.cms.spi.impl.content.ContentAdapterHelper;
import com.smartitengineering.cms.spi.impl.content.ContentFieldsAdapterHelper;
import com.smartitengineering.cms.spi.impl.content.ContentObjectConverter;
import com.smartitengineering.cms.spi.impl.content.ContentPersistentService;
import com.smartitengineering.cms.spi.impl.content.ContentSearchBeanLoader;
import com.smartitengineering.cms.spi.impl.content.FieldsObjectConverter;
import com.smartitengineering.cms.spi.impl.content.PersistentContent;
import com.smartitengineering.cms.spi.impl.content.PersistentContentFields;
import com.smartitengineering.cms.spi.impl.content.guice.ContentFilterConfigsProvider;
import com.smartitengineering.cms.spi.impl.content.guice.ContentSchemaBaseConfigProvider;
import com.smartitengineering.cms.spi.impl.events.EventConsumerImpl;
import com.smartitengineering.cms.spi.impl.content.search.ContentEventListener;
import com.smartitengineering.cms.spi.impl.events.EventPublicationListener;
import com.smartitengineering.cms.spi.impl.content.search.ContentHelper;
import com.smartitengineering.cms.spi.impl.content.search.ContentIdentifierQueryImpl;
import com.smartitengineering.cms.spi.impl.content.search.ContentSearcherImpl;
import com.smartitengineering.cms.spi.impl.content.search.SearchFieldNameGeneratorImpl;
import com.smartitengineering.cms.spi.impl.type.ContentTypeAdapterHelper;
import com.smartitengineering.cms.spi.impl.type.ContentTypeObjectConverter;
import com.smartitengineering.cms.spi.impl.type.ContentTypePersistentService;
import com.smartitengineering.cms.spi.impl.type.ContentTypeSearchBeanLoader;
import com.smartitengineering.cms.spi.impl.type.guice.ContentTypeSchemaBaseConfigProvider;
import com.smartitengineering.cms.spi.impl.type.PersistentContentType;
import com.smartitengineering.cms.spi.impl.type.validator.XMLSchemaBasedTypeValidator;
import com.smartitengineering.cms.spi.impl.type.guice.ContentTypeFilterConfigsProvider;
import com.smartitengineering.cms.spi.impl.type.search.ContentTypeEventListener;
import com.smartitengineering.cms.spi.impl.type.search.ContentTypeHelper;
import com.smartitengineering.cms.spi.impl.type.search.ContentTypeIdentifierQueryImpl;
import com.smartitengineering.cms.spi.impl.type.search.ContentTypeSearcherImpl;
import com.smartitengineering.cms.spi.impl.type.validator.XMLContentTypeDefinitionParser;
import com.smartitengineering.cms.spi.impl.uri.UriProviderImpl;
import com.smartitengineering.cms.spi.impl.workspace.PersistentSequence;
import com.smartitengineering.cms.spi.impl.workspace.SequenceAdapterHelper;
import com.smartitengineering.cms.spi.impl.workspace.SequenceObjectConverter;
import com.smartitengineering.cms.spi.impl.workspace.SequenceSearchBeanLoader;
import com.smartitengineering.cms.spi.impl.workspace.search.SequenceEventListener;
import com.smartitengineering.cms.spi.impl.workspace.search.SequenceHelper;
import com.smartitengineering.cms.spi.impl.workspace.search.SequenceIdentifierQueryImpl;
import com.smartitengineering.cms.spi.impl.workspace.search.SequenceSearcherImpl;
import com.smartitengineering.cms.spi.lock.LockHandler;
import com.smartitengineering.cms.spi.lock.impl.distributed.LocalLockRegistrar;
import com.smartitengineering.cms.spi.lock.impl.distributed.LocalLockRegistrarImpl;
import com.smartitengineering.cms.spi.lock.impl.distributed.ZooKeeperLockHandler;
import com.smartitengineering.cms.spi.persistence.PersistableDomainFactory;
import com.smartitengineering.cms.spi.persistence.PersistentService;
import com.smartitengineering.cms.spi.persistence.PersistentServiceRegistrar;
import com.smartitengineering.cms.spi.type.ContentTypeDefinitionParser;
import com.smartitengineering.cms.spi.type.ContentTypeDefinitionParsers;
import com.smartitengineering.cms.spi.type.ContentTypeSearcher;
import com.smartitengineering.cms.spi.type.PersistentContentTypeReader;
import com.smartitengineering.cms.spi.type.SearchFieldNameGenerator;
import com.smartitengineering.cms.spi.type.TypeValidator;
import com.smartitengineering.cms.spi.type.TypeValidators;
import com.smartitengineering.cms.spi.workspace.SequenceSearcher;
import com.smartitengineering.common.dao.search.CommonFreeTextPersistentDao;
import com.smartitengineering.common.dao.search.CommonFreeTextPersistentTxDao;
import com.smartitengineering.common.dao.search.CommonFreeTextSearchDao;
import com.smartitengineering.common.dao.search.impl.CommonAsyncFreeTextPersistentDaoImpl;
import com.smartitengineering.common.dao.search.solr.SolrFreeTextPersistentDao;
import com.smartitengineering.common.dao.search.solr.SolrFreeTextPersistentTxDao;
import com.smartitengineering.common.dao.search.solr.SolrFreeTextSearchDao;
import com.smartitengineering.common.dao.search.solr.spi.ObjectIdentifierQuery;
import com.smartitengineering.dao.common.CommonReadDao;
import com.smartitengineering.dao.common.CommonWriteDao;
import com.smartitengineering.dao.common.cache.BasicKey;
import com.smartitengineering.dao.common.cache.dao.CacheableDao;
import com.smartitengineering.dao.common.cache.impl.CacheAPIFactory;
import com.smartitengineering.dao.impl.hbase.CommonDao;
import com.smartitengineering.dao.impl.hbase.spi.AsyncExecutorService;
import com.smartitengineering.dao.impl.hbase.spi.CellConfig;
import com.smartitengineering.dao.impl.hbase.spi.DomainIdInstanceProvider;
import com.smartitengineering.dao.impl.hbase.spi.FilterConfigs;
import com.smartitengineering.dao.impl.hbase.spi.LockAttainer;
import com.smartitengineering.dao.impl.hbase.spi.MergeService;
import com.smartitengineering.dao.impl.hbase.spi.ObjectRowConverter;
import com.smartitengineering.dao.impl.hbase.spi.RowCellIncrementor;
import com.smartitengineering.dao.impl.hbase.spi.SchemaInfoProvider;
import com.smartitengineering.dao.impl.hbase.spi.impl.CellConfigImpl;
import com.smartitengineering.dao.impl.hbase.spi.impl.DiffBasedMergeService;
import com.smartitengineering.dao.impl.hbase.spi.impl.LockAttainerImpl;
import com.smartitengineering.dao.impl.hbase.spi.impl.MixedExecutorServiceImpl;
import com.smartitengineering.dao.impl.hbase.spi.impl.RowCellIncrementorImpl;
import com.smartitengineering.dao.impl.hbase.spi.impl.SchemaInfoProviderBaseConfig;
import com.smartitengineering.dao.impl.hbase.spi.impl.SchemaInfoProviderImpl;
import com.smartitengineering.dao.impl.hbase.spi.impl.guice.GenericBaseConfigProvider;
import com.smartitengineering.dao.impl.hbase.spi.impl.guice.GenericFilterConfigsProvider;
import com.smartitengineering.dao.solr.MultivalueMap;
import com.smartitengineering.dao.solr.ServerConfiguration;
import com.smartitengineering.dao.solr.ServerFactory;
import com.smartitengineering.dao.solr.SolrWriteDao;
import com.smartitengineering.dao.solr.impl.DefaultSolrDao;
import com.smartitengineering.dao.solr.impl.ServerConfigurationImpl;
import com.smartitengineering.dao.solr.impl.SingletonRemoteServerFactory;
import com.smartitengineering.dao.solr.impl.SolrDao;
import com.smartitengineering.events.async.api.EventConsumer;
import com.smartitengineering.events.async.api.EventPublisher;
import com.smartitengineering.events.async.api.EventSubscriber;
import com.smartitengineering.events.async.api.UriStorer;
import com.smartitengineering.events.async.api.impl.hub.EventPublisherImpl;
import com.smartitengineering.events.async.api.impl.hub.EventSubscriberImpl;
import com.smartitengineering.events.async.api.impl.hub.FileSystemUriStorer;
import com.smartitengineering.util.bean.adapter.AbstractAdapterHelper;
import com.smartitengineering.util.bean.adapter.GenericAdapter;
import com.smartitengineering.util.bean.adapter.GenericAdapterImpl;
import com.smartitengineering.util.rest.client.ConnectionConfig;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPIModule extends PrivateModule {

  public static final String DEFAULT_LOCATION =
                             "http://github.com/smart-it/smart-cms/raw/master/" +
      "content-api-impl/src/main/resources/com/smartitengineering/cms/content/content-type-schema.xsd";
  public static final String DEFAULT_SOLR_URI = "http://localhost:8080/solr/";
  public static final String PREFIX_SEPARATOR_PROP_KEY = "com.smartitengineering.user.cache.prefixSeparator";
  public static final String PREFIX_SEPARATOR_PROP_DEFAULT = "|";
  private final String schemaLocationForContentType;
  private final String solrUri, uriPrefix, cacheConfigRsrc, cacheName, hubUri, atomFeedUri, cronExpression;
  private final String eventHubContextPath, eventHubBaseUri;
  private final String uriStoreFolder, uriStoreFileName;
  private final String zkConnectString, zkRootNode, zkNodeId;
  private final long waitTime, saveInterval, updateInterval, deleteInterval;
  private final int solrSocketTimeout, solrConnectionTimeout, localLockTimeout, zkTimeout;
  private final boolean enableAsyncEvent, enableEventConsumption, distributedLockingEnabled, enableCaching, enableDomainLockAwait;
  private final Properties properties;
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  public SPIModule(Properties properties) {
    if (logger.isInfoEnabled()) {
      logger.info("Properties received: " + properties);
    }
    if (properties != null) {
      this.properties = properties;
      schemaLocationForContentType = properties.getProperty("com.smartitengineering.cms.schemaLocationForContentType",
                                                            DEFAULT_LOCATION);
      solrUri = properties.getProperty("com.smartitengineering.cms.solrUri", DEFAULT_SOLR_URI);
      long toLong = NumberUtils.toLong(properties.getProperty("com.smartitengineering.cms.waitTimeInSec"), 10L);
      waitTime = toLong > 0 ? toLong : 10l;
      toLong = NumberUtils.toLong(properties.getProperty("com.smartitengineering.cms.saveIntervalInSec"), 60L);
      saveInterval = toLong > 0 ? toLong : 60l;
      toLong = NumberUtils.toLong(properties.getProperty("com.smartitengineering.cms.updateIntervalInSec"), 60L);
      updateInterval = toLong > 0 ? toLong : 60l;
      toLong = NumberUtils.toLong(properties.getProperty("com.smartitengineering.cms.deleteIntervalInSec"), 60L);
      solrSocketTimeout = NumberUtils.toInt(properties.getProperty("com.smartitengineering.cms.solr.socketTimeout"),
                                            1000);
      solrConnectionTimeout = NumberUtils.toInt(properties.getProperty(
          "com.smartitengineering.cms.solr.connectionTimeout"), 100);
      deleteInterval = toLong > 0 ? toLong : 60l;
      uriPrefix = properties.getProperty("com.smartitengineering.cms.uriPrefix", "/cms");
      cacheConfigRsrc = properties.getProperty("com.smartitengineering.cms.cache.resource",
                                               "com/smartitengineering/cms/binder/guice/ehcache.xml");
      cacheName = properties.getProperty("com.smartitengineering.cms.cache.name", "cmsCache");
      enableCaching = Boolean.parseBoolean(properties.getProperty("com.smartitengineering.cms.cache.enabled", "true"));
      enableDomainLockAwait = Boolean.parseBoolean(properties.getProperty("com.smartitengineering.cms.enableDomainLockAwait", "true"));
      enableAsyncEvent = Boolean.parseBoolean(properties.getProperty("com.smartitengineering.cms.event.async", "true"));
      enableEventConsumption = Boolean.parseBoolean(properties.getProperty(
          "com.smartitengineering.cms.event.async.subscribe", "true"));
      hubUri = properties.getProperty("com.smartitengineering.cms.event.hubUri",
                                      "http://localhost:10080/hub/api/channels/test/hub");
      atomFeedUri = properties.getProperty("com.smartitengineering.cms.event.atomFeedUri",
                                           "http://localhost:10080/hub/api/channels/test/events");
      cronExpression = properties.getProperty("com.smartitengineering.cms.event.consumerCronExp", "0/1 * * * * ?");
      eventHubContextPath = properties.getProperty("com.smartitengineering.cms.event.contextPath", "/hub");
      eventHubBaseUri = properties.getProperty("com.smartitengineering.cms.event.baseUri", "/api");
      uriStoreFolder = properties.getProperty("com.smartitengineering.cms.event.storeFolder", "./target/cms/");
      uriStoreFileName = properties.getProperty("com.smartitengineering.cms.event.storeFileName", "cmsPollUri.txt");
      distributedLockingEnabled = Boolean.parseBoolean(properties.getProperty(
          "com.smartitengineering.cms.distributedLocking.enabled"));
      localLockTimeout = NumberUtils.toInt(properties.getProperty(
          "com.smartitengineering.cms.distributedLocking.localLockTimeout"), -1);
      zkTimeout = NumberUtils.toInt(properties.getProperty("com.smartitengineering.cms.distributedLocking.zkTimeout"),
                                    90000);
      zkConnectString = properties.getProperty("com.smartitengineering.cms.distributedLocking.zkConnectString",
                                               "localhost:3882");
      zkRootNode = properties.getProperty("com.smartitengineering.cms.distributedLocking.zkRootNode", "/smart-cms");
      zkNodeId = properties.getProperty("com.smartitengineering.cms.distributedLocking.zkNodeId", "node-1");
    }
    else {
      this.properties = new Properties();
      distributedLockingEnabled = true;
      localLockTimeout = -1;
      zkTimeout = 90000;
      zkConnectString = "localhost:3882";
      zkRootNode = "/smart-cms";
      zkNodeId = "node-1";
      schemaLocationForContentType = DEFAULT_LOCATION;
      solrUri = DEFAULT_SOLR_URI;
      waitTime = 10l;
      saveInterval = updateInterval = deleteInterval = 60l;
      uriPrefix = "/cms";
      cacheConfigRsrc = "com/smartitengineering/cms/binder/guice/ehcache.xml";
      cacheName = "cmsCache";
      enableCaching = true;
      enableDomainLockAwait = true;
      enableAsyncEvent = true;
      enableEventConsumption = true;
      hubUri = "http://localhost:10080/hub/api/channels/test/hub";
      atomFeedUri = "http://localhost:10080/hub/api/channels/test/events";
      cronExpression = "0/1 * * * * ?";
      eventHubContextPath = "/hub";
      eventHubBaseUri = "/api";
      uriStoreFolder = "./target/cms/";
      uriStoreFileName = "cmsPollUri.txt";
      solrSocketTimeout = 1000;
      solrConnectionTimeout = 100;
    }
    logger.debug("SCHEMA Location " + schemaLocationForContentType);
  }

  @Override
  protected void configure() {
    bind(AsyncExecutorService.class).to(MixedExecutorServiceImpl.class).in(Singleton.class);
    binder().expose(AsyncExecutorService.class);
    bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());
    binder().expose(ExecutorService.class);
    bind(Integer.class).annotatedWith(Names.named("maxRows")).toInstance(new Integer(100));
    bind(Long.class).annotatedWith(Names.named("waitTime")).toInstance(waitTime);
    binder().expose(Long.class).annotatedWith(Names.named("waitTime"));
    bind(TimeUnit.class).annotatedWith(Names.named("unit")).toInstance(TimeUnit.SECONDS);
    binder().expose(TimeUnit.class).annotatedWith(Names.named("unit"));
    bind(Boolean.class).annotatedWith(Names.named("mergeEnabled")).toInstance(Boolean.TRUE);
    final Named named = Names.named("schemaLocationForContentTypeXml");
    bind(String.class).annotatedWith(named).toInstance(schemaLocationForContentType);
    binder().expose(String.class).annotatedWith(named);
    bind(DomainIdInstanceProvider.class).to(DomainIdInstanceProviderImpl.class).in(Scopes.SINGLETON);
    bind(SearchFieldNameGenerator.class).to(SearchFieldNameGeneratorImpl.class);
    binder().expose(SearchFieldNameGenerator.class);

    /*
     * Solr client
     * waitTime:long and ExecutorService.class from earlier config
     */
    bind(TimeUnit.class).annotatedWith(Names.named("waitTimeUnit")).toInstance(TimeUnit.SECONDS);
    bind(ServerFactory.class).to(SingletonRemoteServerFactory.class).in(Scopes.SINGLETON);
    bind(ServerConfiguration.class).to(ServerConfigurationImpl.class).in(Scopes.SINGLETON);
    bind(String.class).annotatedWith(Names.named("uri")).toInstance(solrUri);
    bind(Long.class).annotatedWith(Names.named("saveInterval")).toInstance(saveInterval);
    bind(Long.class).annotatedWith(Names.named("updateInterval")).toInstance(updateInterval);
    bind(Long.class).annotatedWith(Names.named("deleteInterval")).toInstance(deleteInterval);
    bind(TimeUnit.class).annotatedWith(Names.named("intervalTimeUnit")).toInstance(TimeUnit.SECONDS);

    bind(Integer.class).annotatedWith(Names.named("socketTimeout")).toInstance(solrSocketTimeout);
    bind(Integer.class).annotatedWith(Names.named("connectionTimeout")).toInstance(solrConnectionTimeout);
    binder().expose(Integer.class).annotatedWith(Names.named("socketTimeout"));
    binder().expose(Integer.class).annotatedWith(Names.named("connectionTimeout"));

    /*
     * Start injection specific to common dao of content type
     */
    bind(new TypeLiteral<ObjectRowConverter<PersistentContentType>>() {
    }).to(ContentTypeObjectConverter.class).in(Singleton.class);
    if (enableCaching) {
      bind(new TypeLiteral<CommonReadDao<PersistentContentType, ContentTypeId>>() {
      }).annotatedWith(Names.named("primaryCacheableReadDao")).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentContentType, ContentTypeId>>() {
      }).in(Singleton.class);
      bind(new TypeLiteral<CommonWriteDao<PersistentContentType>>() {
      }).annotatedWith(Names.named("primaryCacheableWriteDao")).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentContentType, ContentTypeId>>() {
      }).in(Singleton.class);
      binder().expose(new TypeLiteral<CommonWriteDao<PersistentContentType>>() {
      }).annotatedWith(Names.named("primaryCacheableWriteDao"));
      binder().expose(new TypeLiteral<CommonReadDao<PersistentContentType, ContentTypeId>>() {
      }).annotatedWith(Names.named("primaryCacheableReadDao"));
      bind(new TypeLiteral<CommonWriteDao<PersistentContentType>>() {
      }).to(new TypeLiteral<CacheableDao<PersistentContentType, ContentTypeId, String>>() {
      }).in(Singleton.class);
      bind(new TypeLiteral<CommonReadDao<PersistentContentType, ContentTypeId>>() {
      }).to(new TypeLiteral<CacheableDao<PersistentContentType, ContentTypeId, String>>() {
      }).in(Singleton.class);      
    }
    else {
      bind(new TypeLiteral<CommonReadDao<PersistentContentType, ContentTypeId>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentContentType, ContentTypeId>>() {
      }).in(Singleton.class);
      bind(new TypeLiteral<CommonWriteDao<PersistentContentType>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentContentType, ContentTypeId>>() {
      }).in(Singleton.class);
    }
    bind(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentContentType, ContentTypeId>>() {
      }).to(new TypeLiteral<CommonDao<PersistentContentType, ContentTypeId>>() {
      }).in(Singleton.class);
    final TypeLiteral<SchemaInfoProviderImpl<PersistentContentType, ContentTypeId>> typeLiteral =
                                                                                    new TypeLiteral<SchemaInfoProviderImpl<PersistentContentType, ContentTypeId>>() {
    };
    bind(new TypeLiteral<MergeService<PersistentContentType, ContentTypeId>>() {
    }).to(new TypeLiteral<DiffBasedMergeService<PersistentContentType, ContentTypeId>>() {
    });
    bind(new TypeLiteral<LockAttainer<PersistentContentType, ContentTypeId>>() {
    }).to(new TypeLiteral<LockAttainerImpl<PersistentContentType, ContentTypeId>>() {
    }).in(Scopes.SINGLETON);
    bind(new TypeLiteral<Class<ContentTypeId>>() {
    }).toInstance(ContentTypeId.class);
    bind(new TypeLiteral<SchemaInfoProvider<PersistentContentType, ContentTypeId>>() {
    }).to(typeLiteral).in(Singleton.class);
    bind(new TypeLiteral<SchemaInfoProviderBaseConfig<PersistentContentType>>() {
    }).toProvider(ContentTypeSchemaBaseConfigProvider.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<FilterConfigs<PersistentContentType>>() {
    }).toProvider(ContentTypeFilterConfigsProvider.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<GenericAdapter<WritableContentType, PersistentContentType>>() {
    }).to(new TypeLiteral<GenericAdapterImpl<WritableContentType, PersistentContentType>>() {
    }).in(Scopes.SINGLETON);
    bind(new TypeLiteral<AbstractAdapterHelper<WritableContentType, PersistentContentType>>() {
    }).to(ContentTypeAdapterHelper.class).in(Scopes.SINGLETON);
    bind(PersistentContentTypeReader.class).to(ContentTypePersistentService.class);
    binder().expose(PersistentContentTypeReader.class);
    bind(ContentTypeSearcher.class).to(ContentTypeSearcherImpl.class).in(Singleton.class);
    binder().expose(ContentTypeSearcher.class);
    /*
     * End injection specific to common dao of content type
     */
    /*
     * Start injection specific to common dao of content
     */

    /*
     * Write Dao
     */
    if (enableCaching) {
      bind(new TypeLiteral<CommonWriteDao<PersistentContent>>() {
      }).annotatedWith(Names.named("primaryCacheableWriteDao")).to(new TypeLiteral<CommonDao<PersistentContent, ContentId>>() {
      }).in(Singleton.class);
      binder().expose(new TypeLiteral<CommonWriteDao<PersistentContent>>() {
      }).annotatedWith(Names.named("primaryCacheableWriteDao"));
      bind(new TypeLiteral<CommonWriteDao<PersistentContent>>() {
      }).to(new TypeLiteral<CacheableDao<PersistentContent, ContentId, String>>() {
      }).in(Singleton.class);
    }
    else {
      bind(new TypeLiteral<CommonWriteDao<PersistentContent>>() {
      }).to(new TypeLiteral<CommonDao<PersistentContent, ContentId>>() {
      }).in(Singleton.class);
    }
    if (enableCaching) {
      bind(new TypeLiteral<CommonWriteDao<PersistentContentFields>>() {
      }).annotatedWith(Names.named("primaryCacheableWriteDao")).to(new TypeLiteral<CommonDao<PersistentContentFields, ContentId>>() {
      }).in(Singleton.class);
      binder().expose(new TypeLiteral<CommonWriteDao<PersistentContentFields>>() {
      }).annotatedWith(Names.named("primaryCacheableWriteDao"));
      bind(new TypeLiteral<CommonWriteDao<PersistentContentFields>>() {
      }).to(new TypeLiteral<CacheableDao<PersistentContentFields, ContentId, String>>() {
      }).in(Singleton.class);
    }
    else {
      bind(new TypeLiteral<CommonWriteDao<PersistentContentFields>>() {
      }).to(new TypeLiteral<CommonDao<PersistentContentFields, ContentId>>() {
      }).in(Singleton.class);

    }
    bind(new TypeLiteral<EventListener<Content>>() {
    }).to(ContentEventListener.class).in(Singleton.class);
    bind(new TypeLiteral<EventListener<Sequence>>() {
    }).to(SequenceEventListener.class).in(Singleton.class);
    bind(new TypeLiteral<EventListener<ContentType>>() {
    }).to(ContentTypeEventListener.class).in(Singleton.class);
    if (enableAsyncEvent) {
      Multibinder<EventListener> listenerBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<EventListener>() {
      });
      listenerBinder.addBinding().to(EventPublicationListener.class).in(Singleton.class);
      bind(new TypeLiteral<EventListener>() {
      }).annotatedWith(Names.named(ContentSearcherImpl.REINDEX_LISTENER_NAME)).to(EventPublicationListener.class).in(
          Singleton.class);
      bind(new TypeLiteral<EventListener>() {
      }).annotatedWith(Names.named(ContentTypeSearcherImpl.REINDEX_LISTENER_NAME)).to(EventPublicationListener.class).
          in(
          Singleton.class);
      bind(new TypeLiteral<EventListener>() {
      }).annotatedWith(Names.named(SequenceSearcherImpl.REINDEX_LISTENER_NAME)).to(EventPublicationListener.class).
          in(
          Singleton.class);
      bind(new TypeLiteral<Collection<EventListener>>() {
      }).to(new TypeLiteral<Set<EventListener>>() {
      });
      binder().expose(new TypeLiteral<Collection<EventListener>>() {
      });
      bind(String.class).annotatedWith(Names.named("channelHubUri")).toInstance(hubUri);
      bind(String.class).annotatedWith(Names.named("eventAtomFeedUri")).toInstance(atomFeedUri);
      bind(String.class).annotatedWith(Names.named("subscribtionCronExpression")).toInstance(cronExpression);
      bind(EventPublisher.class).to(EventPublisherImpl.class);
    }
    else {
      Multibinder<EventListener> listenerBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<EventListener>() {
      });
      listenerBinder.addBinding().to(ContentEventListener.class).in(Singleton.class);
      listenerBinder.addBinding().to(ContentTypeEventListener.class).in(Singleton.class);
      listenerBinder.addBinding().to(SequenceEventListener.class).in(Singleton.class);
      bind(new TypeLiteral<EventListener>() {
      }).annotatedWith(Names.named(ContentSearcherImpl.REINDEX_LISTENER_NAME)).to(ContentEventListener.class).in(
          Singleton.class);
      bind(new TypeLiteral<EventListener>() {
      }).annotatedWith(Names.named(ContentTypeSearcherImpl.REINDEX_LISTENER_NAME)).to(ContentTypeEventListener.class).
          in(Singleton.class);
      bind(new TypeLiteral<EventListener>() {
      }).annotatedWith(Names.named(SequenceSearcherImpl.REINDEX_LISTENER_NAME)).to(SequenceEventListener.class).
          in(Singleton.class);
      bind(new TypeLiteral<Collection<EventListener>>() {
      }).to(new TypeLiteral<Set<EventListener>>() {
      });
      binder().expose(new TypeLiteral<Collection<EventListener>>() {
      });
    }
    if (enableAsyncEvent && enableEventConsumption) {
      bind(SolrWriteDao.class).to(DefaultSolrDao.class).in(Scopes.SINGLETON);

      bind(new TypeLiteral<CommonFreeTextPersistentDao<Content>>() {
      }).to(new TypeLiteral<CommonFreeTextPersistentTxDao<Content>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonFreeTextPersistentTxDao<Content>>() {
      }).to(new TypeLiteral<SolrFreeTextPersistentTxDao<Content>>() {
      }).in(Scopes.SINGLETON);

      bind(new TypeLiteral<CommonFreeTextPersistentDao<ContentType>>() {
      }).to(new TypeLiteral<CommonFreeTextPersistentTxDao<ContentType>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonFreeTextPersistentTxDao<ContentType>>() {
      }).to(new TypeLiteral<SolrFreeTextPersistentTxDao<ContentType>>() {
      }).in(Scopes.SINGLETON);

      bind(new TypeLiteral<CommonFreeTextPersistentDao<Sequence>>() {
      }).to(new TypeLiteral<CommonFreeTextPersistentTxDao<Sequence>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<CommonFreeTextPersistentTxDao<Sequence>>() {
      }).to(new TypeLiteral<SolrFreeTextPersistentTxDao<Sequence>>() {
      }).in(Scopes.SINGLETON);

      ConnectionConfig config = new ConnectionConfig();
      config.setBasicUri(eventHubBaseUri);
      config.setContextPath(eventHubContextPath);
      URI hub = URI.create(this.hubUri);
      config.setHost(hub.getHost());
      config.setPort(hub.getPort());
      bind(ConnectionConfig.class).toInstance(config);
      bind(UriStorer.class).to(FileSystemUriStorer.class);
      bind(String.class).annotatedWith(Names.named("pathToFolderOfUriStorer")).toInstance(uriStoreFolder);
      bind(String.class).annotatedWith(Names.named("fileNameOfUriStorer")).toInstance(uriStoreFileName);
      /**
       * Start of subscriber cron configurations
       */
      bind(String.class).annotatedWith(Names.named("subscribePollName")).toInstance("cmsSubscriberPoll");
      bind(String.class).annotatedWith(Names.named("subscribePollJobName")).toInstance("cmsSubscriberPollJob");
      bind(String.class).annotatedWith(Names.named("subscribePollTriggerName")).toInstance("cmsSubscriberPollTrigger");
      bind(String.class).annotatedWith(Names.named("subscribePollListenerName")).toInstance("cmsSubscriberPollListener");
      bind(EventSubscriberImpl.PollNameConfig.class);
      logger.info("Inject custom names for EventSubscriberImpl");
      /**
       * End of subscriber cron configurations
       */
      bind(EventSubscriber.class).to(EventSubscriberImpl.class).asEagerSingleton();
      Multibinder<EventConsumer> listenerBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<EventConsumer>() {
      });
      listenerBinder.addBinding().to(EventConsumerImpl.class);
      bind(new TypeLiteral<Collection<EventConsumer>>() {
      }).to(new TypeLiteral<Set<EventConsumer>>() {
      });
    }
    else {
      bind(SolrWriteDao.class).to(SolrDao.class).in(Scopes.SINGLETON);
      TypeLiteral<CommonFreeTextPersistentDao<Content>> prodLit =
                                                        new TypeLiteral<CommonFreeTextPersistentDao<Content>>() {
      };
      bind(prodLit).to(new TypeLiteral<CommonAsyncFreeTextPersistentDaoImpl<Content>>() {
      }).in(Scopes.SINGLETON);
      bind(prodLit).annotatedWith(Names.named("primaryFreeTextPersistentDao")).to(new TypeLiteral<SolrFreeTextPersistentDao<Content>>() {
      }).in(Scopes.SINGLETON);

      TypeLiteral<CommonFreeTextPersistentDao<ContentType>> typeLit =
                                                            new TypeLiteral<CommonFreeTextPersistentDao<ContentType>>() {
      };
      bind(typeLit).to(new TypeLiteral<CommonAsyncFreeTextPersistentDaoImpl<ContentType>>() {
      }).in(Scopes.SINGLETON);
      bind(typeLit).annotatedWith(Names.named("primaryFreeTextPersistentDao")).to(new TypeLiteral<SolrFreeTextPersistentDao<ContentType>>() {
      }).in(Scopes.SINGLETON);

      TypeLiteral<CommonFreeTextPersistentDao<Sequence>> seqLit =
                                                         new TypeLiteral<CommonFreeTextPersistentDao<Sequence>>() {
      };
      bind(seqLit).to(new TypeLiteral<CommonAsyncFreeTextPersistentDaoImpl<Sequence>>() {
      }).in(Scopes.SINGLETON);
      bind(seqLit).annotatedWith(Names.named("primaryFreeTextPersistentDao")).to(new TypeLiteral<SolrFreeTextPersistentDao<Sequence>>() {
      }).in(Scopes.SINGLETON);
    }
    bind(new TypeLiteral<ObjectIdentifierQuery<Content>>() {
    }).to(ContentIdentifierQueryImpl.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<GenericAdapter<Content, MultivalueMap<String, Object>>>() {
    }).to(new TypeLiteral<GenericAdapterImpl<Content, MultivalueMap<String, Object>>>() {
    }).in(Scopes.SINGLETON);
    bind(new TypeLiteral<AbstractAdapterHelper<Content, MultivalueMap<String, Object>>>() {
    }).to(ContentHelper.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<CommonFreeTextSearchDao<Content>>() {
    }).to(new TypeLiteral<SolrFreeTextSearchDao<Content>>() {
    }).in(Scopes.SINGLETON);

    bind(new TypeLiteral<ObjectIdentifierQuery<ContentType>>() {
    }).to(ContentTypeIdentifierQueryImpl.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<GenericAdapter<ContentType, MultivalueMap<String, Object>>>() {
    }).to(new TypeLiteral<GenericAdapterImpl<ContentType, MultivalueMap<String, Object>>>() {
    }).in(Scopes.SINGLETON);
    bind(new TypeLiteral<AbstractAdapterHelper<ContentType, MultivalueMap<String, Object>>>() {
    }).to(ContentTypeHelper.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<CommonFreeTextSearchDao<ContentType>>() {
    }).to(new TypeLiteral<SolrFreeTextSearchDao<ContentType>>() {
    }).in(Scopes.SINGLETON);

    bind(new TypeLiteral<ObjectIdentifierQuery<Sequence>>() {
    }).to(SequenceIdentifierQueryImpl.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<GenericAdapter<Sequence, MultivalueMap<String, Object>>>() {
    }).to(new TypeLiteral<GenericAdapterImpl<Sequence, MultivalueMap<String, Object>>>() {
    }).in(Scopes.SINGLETON);
    bind(new TypeLiteral<AbstractAdapterHelper<Sequence, MultivalueMap<String, Object>>>() {
    }).to(SequenceHelper.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<CommonFreeTextSearchDao<Sequence>>() {
    }).to(new TypeLiteral<SolrFreeTextSearchDao<Sequence>>() {
    }).in(Scopes.SINGLETON);

    bind(ContentSearcher.class).to(ContentSearcherImpl.class).in(Scopes.SINGLETON);
    binder().expose(ContentSearcher.class);

    bind(new TypeLiteral<ObjectRowConverter<PersistentContent>>() {
    }).to(ContentObjectConverter.class).in(Singleton.class);
    if (enableCaching) {
      bind(new TypeLiteral<CommonReadDao<PersistentContent, ContentId>>() {
      }).annotatedWith(Names.named("primaryCacheableReadDao")).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentContent, ContentId>>() {
      }).in(Singleton.class);
      binder().expose(new TypeLiteral<CommonReadDao<PersistentContent, ContentId>>() {
      }).annotatedWith(Names.named("primaryCacheableReadDao"));
      bind(new TypeLiteral<CommonReadDao<PersistentContent, ContentId>>() {
      }).to(new TypeLiteral<CacheableDao<PersistentContent, ContentId, String>>() {
      }).in(Singleton.class);
    }
    else {
      bind(new TypeLiteral<CommonReadDao<PersistentContent, ContentId>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentContent, ContentId>>() {
      }).in(Singleton.class);
    }
    bind(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentContent, ContentId>>() {
    }).to(new TypeLiteral<CommonDao<PersistentContent, ContentId>>() {
    }).in(Singleton.class);
    bind(new TypeLiteral<Class<ContentId>>() {
    }).toInstance(ContentId.class);
    final TypeLiteral<SchemaInfoProvider<PersistentContent, ContentId>> contentSchema =
                                                                        new TypeLiteral<SchemaInfoProvider<PersistentContent, ContentId>>() {
    };
    bind(contentSchema).to(new TypeLiteral<SchemaInfoProviderImpl<PersistentContent, ContentId>>() {
    }).in(Singleton.class);
    bind(new TypeLiteral<MergeService<PersistentContent, ContentId>>() {
    }).to(new TypeLiteral<DiffBasedMergeService<PersistentContent, ContentId>>() {
    });
    bind(new TypeLiteral<LockAttainer<PersistentContent, ContentId>>() {
    }).to(new TypeLiteral<LockAttainerImpl<PersistentContent, ContentId>>() {
    }).in(Scopes.SINGLETON);
    binder().expose(contentSchema);
    bind(new TypeLiteral<SchemaInfoProviderBaseConfig<PersistentContent>>() {
    }).toProvider(ContentSchemaBaseConfigProvider.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<FilterConfigs<PersistentContent>>() {
    }).toProvider(ContentFilterConfigsProvider.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<GenericAdapter<WriteableContent, PersistentContent>>() {
    }).to(new TypeLiteral<GenericAdapterImpl<WriteableContent, PersistentContent>>() {
    }).in(Scopes.SINGLETON);
    bind(new TypeLiteral<AbstractAdapterHelper<WriteableContent, PersistentContent>>() {
    }).to(ContentAdapterHelper.class).in(Scopes.SINGLETON);
    /**
     * For persistent fields
     */
    bind(new TypeLiteral<ObjectRowConverter<PersistentContentFields>>() {
    }).to(FieldsObjectConverter.class).in(Singleton.class);
    if (enableCaching) {
      bind(new TypeLiteral<CommonReadDao<PersistentContentFields, ContentId>>() {
      }).annotatedWith(Names.named("primaryCacheableReadDao")).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentContentFields, ContentId>>() {
      }).in(Singleton.class);
      binder().expose(new TypeLiteral<CommonReadDao<PersistentContentFields, ContentId>>() {
      }).annotatedWith(Names.named("primaryCacheableReadDao"));
      bind(new TypeLiteral<CommonReadDao<PersistentContentFields, ContentId>>() {
      }).to(new TypeLiteral<CacheableDao<PersistentContentFields, ContentId, String>>() {
      }).in(Singleton.class);
    }
    else {
      bind(new TypeLiteral<CommonReadDao<PersistentContentFields, ContentId>>() {
      }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentContentFields, ContentId>>() {
      }).in(Singleton.class);
    }
    bind(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentContentFields, ContentId>>() {
    }).to(new TypeLiteral<CommonDao<PersistentContentFields, ContentId>>() {
    }).in(Singleton.class);
    final TypeLiteral<SchemaInfoProvider<PersistentContentFields, ContentId>> fieldSchema =
                                                                              new TypeLiteral<SchemaInfoProvider<PersistentContentFields, ContentId>>() {
    };
    bind(fieldSchema).to(new TypeLiteral<SchemaInfoProviderImpl<PersistentContentFields, ContentId>>() {
    }).in(Singleton.class);
    bind(new TypeLiteral<MergeService<PersistentContentFields, ContentId>>() {
    }).to(new TypeLiteral<DiffBasedMergeService<PersistentContentFields, ContentId>>() {
    });
    bind(new TypeLiteral<LockAttainer<PersistentContentFields, ContentId>>() {
    }).to(new TypeLiteral<LockAttainerImpl<PersistentContentFields, ContentId>>() {
    }).in(Scopes.SINGLETON);
    binder().expose(fieldSchema);
    bind(new TypeLiteral<FilterConfigs<PersistentContentFields>>() {
    }).toProvider(new GenericFilterConfigsProvider<PersistentContentFields>(
        "com/smartitengineering/cms/spi/impl/content/FieldsFilterConfigs.json")).in(Scopes.SINGLETON);
    bind(new TypeLiteral<SchemaInfoProviderBaseConfig<PersistentContentFields>>() {
    }).toProvider(new GenericBaseConfigProvider<PersistentContentFields>(
        "com/smartitengineering/cms/spi/impl/content/FieldsSchemaBaseConfig.json")).in(Scopes.SINGLETON);
    bind(new TypeLiteral<GenericAdapter<Map<String, Field>, PersistentContentFields>>() {
    }).to(new TypeLiteral<GenericAdapterImpl<Map<String, Field>, PersistentContentFields>>() {
    }).in(Scopes.SINGLETON);
    bind(new TypeLiteral<AbstractAdapterHelper<Map<String, Field>, PersistentContentFields>>() {
    }).to(ContentFieldsAdapterHelper.class).in(Scopes.SINGLETON);
    /**
     * Persistent fields end
     */
    /**
     * Persistent sequence start
     */
    bind(new TypeLiteral<Class<SequenceId>>() {
    }).toInstance(SequenceId.class);
    bind(new TypeLiteral<ObjectRowConverter<PersistentSequence>>() {
    }).to(SequenceObjectConverter.class).in(Singleton.class);
    bind(new TypeLiteral<CommonReadDao<PersistentSequence, SequenceId>>() {
    }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentSequence, SequenceId>>() {
    }).in(Singleton.class);
    binder().expose(new TypeLiteral<CommonReadDao<PersistentSequence, SequenceId>>() {
    });
    bind(new TypeLiteral<CommonWriteDao<PersistentSequence>>() {
    }).to(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentSequence, SequenceId>>() {
    }).in(Singleton.class);
    binder().expose(new TypeLiteral<CommonWriteDao<PersistentSequence>>() {
    });
    bind(new TypeLiteral<com.smartitengineering.dao.common.CommonDao<PersistentSequence, SequenceId>>() {
    }).to(new TypeLiteral<CommonDao<PersistentSequence, SequenceId>>() {
    }).in(Singleton.class);
    final TypeLiteral<SchemaInfoProvider<PersistentSequence, SequenceId>> seqSchema =
                                                                          new TypeLiteral<SchemaInfoProvider<PersistentSequence, SequenceId>>() {
    };
    bind(seqSchema).to(new TypeLiteral<SchemaInfoProviderImpl<PersistentSequence, SequenceId>>() {
    }).in(Singleton.class);
    bind(new TypeLiteral<MergeService<PersistentSequence, SequenceId>>() {
    }).to(new TypeLiteral<DiffBasedMergeService<PersistentSequence, SequenceId>>() {
    });
    bind(new TypeLiteral<LockAttainer<PersistentSequence, SequenceId>>() {
    }).to(new TypeLiteral<LockAttainerImpl<PersistentSequence, SequenceId>>() {
    }).in(Scopes.SINGLETON);
    binder().expose(seqSchema);
    bind(new TypeLiteral<FilterConfigs<PersistentSequence>>() {
    }).toProvider(new GenericFilterConfigsProvider<PersistentSequence>(
        "com/smartitengineering/cms/spi/impl/workspace/SequenceFilterConfigs.json")).in(Scopes.SINGLETON);
    bind(new TypeLiteral<SchemaInfoProviderBaseConfig<PersistentSequence>>() {
    }).toProvider(new GenericBaseConfigProvider<PersistentSequence>(
        "com/smartitengineering/cms/spi/impl/workspace/SequenceSchemaBaseConfig.json")).in(Scopes.SINGLETON);
    bind(new TypeLiteral<RowCellIncrementor<Sequence, PersistentSequence, SequenceId>>() {
    }).to(new TypeLiteral<RowCellIncrementorImpl<Sequence, PersistentSequence, SequenceId>>() {
    });
    binder().expose(new TypeLiteral<RowCellIncrementor<Sequence, PersistentSequence, SequenceId>>() {
    });
    CellConfigImpl<Sequence> cellConfig = new CellConfigImpl<Sequence>();
    cellConfig.setFamily(SequenceObjectConverter.FAMILY_SELF_STR);
    cellConfig.setQualifier(SequenceObjectConverter.CELL_VALUE_STR);
    bind(new TypeLiteral<CellConfig<Sequence>>() {
    }).toInstance(cellConfig);
    bind(new TypeLiteral<GenericAdapter<Sequence, PersistentSequence>>() {
    }).to(new TypeLiteral<GenericAdapterImpl<Sequence, PersistentSequence>>() {
    }).in(Scopes.SINGLETON);
    bind(new TypeLiteral<AbstractAdapterHelper<Sequence, PersistentSequence>>() {
    }).to(SequenceAdapterHelper.class).in(Scopes.SINGLETON);
    binder().expose(new TypeLiteral<GenericAdapter<Sequence, PersistentSequence>>() {
    });
    bind(SequenceSearcher.class).to(SequenceSearcherImpl.class).in(Scopes.SINGLETON);
    binder().expose(SequenceSearcher.class);
    bind(new TypeLiteral<SearchBeanLoader<Sequence, SequenceId>>() {
    }).to(SequenceSearchBeanLoader.class).in(Scopes.SINGLETON);
    /**
     * Persistent sequence end
     */
    bind(PersistentContentReader.class).to(ContentPersistentService.class);
    binder().expose(PersistentContentReader.class);
    /*
     * End injection specific to common dao of content
     */
    MapBinder<MediaType, TypeValidator> validatorBinder = MapBinder.newMapBinder(binder(), MediaType.class,
                                                                                 TypeValidator.class);
    validatorBinder.addBinding(MediaType.APPLICATION_XML).to(XMLSchemaBasedTypeValidator.class);
    bind(TypeValidators.class).to(com.smartitengineering.cms.spi.impl.type.validator.TypeValidators.class);
    binder().expose(TypeValidators.class);
    MapBinder<Class, PersistentService> serviceBinder = MapBinder.newMapBinder(binder(), Class.class,
                                                                               PersistentService.class);
    serviceBinder.addBinding(WritableContentType.class).to(ContentTypePersistentService.class);
    serviceBinder.addBinding(WriteableContent.class).to(ContentPersistentService.class);
    bind(PersistentServiceRegistrar.class).to(
        com.smartitengineering.cms.spi.impl.PersistentServiceRegistrar.class);
    binder().expose(PersistentServiceRegistrar.class);
    MapBinder<MediaType, ContentTypeDefinitionParser> parserBinder =
                                                      MapBinder.newMapBinder(binder(), MediaType.class,
                                                                             ContentTypeDefinitionParser.class);
    parserBinder.addBinding(MediaType.APPLICATION_XML).to(XMLContentTypeDefinitionParser.class);
    bind(ContentTypeDefinitionParsers.class).to(
        com.smartitengineering.cms.spi.impl.ContentTypeDefinitionParsers.class);
    if (distributedLockingEnabled) {
      bind(LockHandler.class).to(ZooKeeperLockHandler.class).in(Scopes.SINGLETON);
      bind(LocalLockRegistrar.class).to(LocalLockRegistrarImpl.class).in(Scopes.SINGLETON);
      if (localLockTimeout > 0) {
        bind(int.class).annotatedWith(Names.named("localLockTimeout")).toInstance(localLockTimeout);
      }
      bind(int.class).annotatedWith(Names.named("zkTimeout")).toInstance(zkTimeout);
      bind(String.class).annotatedWith(Names.named("zkConnectString")).toInstance(zkConnectString);
      bind(String.class).annotatedWith(Names.named("zkRootNode")).toInstance(zkRootNode);
      bind(String.class).annotatedWith(Names.named("zkNodeId")).toInstance(zkNodeId);
    }
    else {
      bind(LockHandler.class).to(DefaultLockHandler.class).in(Scopes.SINGLETON);
    }
    bind(boolean.class).annotatedWith(Names.named("domainLockAwaitEnabled")).toInstance(enableDomainLockAwait);
    bind(PersistableDomainFactory.class).to(PersistableDomainFactoryImpl.class).in(Scopes.SINGLETON);
    binder().expose(ContentTypeDefinitionParsers.class);
    binder().expose(LockHandler.class);
    binder().expose(PersistableDomainFactory.class);
    bind(UriProvider.class).to(UriProviderImpl.class);
    bind(URI.class).annotatedWith(Names.named("cmsBaseUri")).toInstance(URI.create(uriPrefix));
    binder().expose(UriProvider.class);
    /*
     * Configure Cache
     */
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(cacheConfigRsrc);
    if (inputStream == null) {
      throw new IllegalArgumentException("Cache configuration not available!");
    }
    CacheManager cacheManager = new CacheManager(inputStream);
    Cache cache = cacheManager.getCache(cacheName);
    if (cache == null) {
      throw new IllegalStateException("Could not retrieve cache!");
    }
    bind(CacheManager.class).toInstance(cacheManager);
    bind(Cache.class).annotatedWith(Names.named("defaultCache")).toInstance(cache);
    binder().expose(CacheManager.class);
    binder().expose(Cache.class).annotatedWith(Names.named("defaultCache"));

    bind(new TypeLiteral<SearchBeanLoader<Content, ContentId>>() {
    }).to(ContentSearchBeanLoader.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<SearchBeanLoader<ContentType, ContentTypeId>>() {
    }).to(ContentTypeSearchBeanLoader.class).in(Scopes.SINGLETON);
    if (enableCaching) {
      install(new SPIContentCacheModule(properties));
      install(new SPIContentFieldsCacheModule(properties));
      install(new SPIContentTypeCacheModule(properties));
    }
  }

  static <T extends Serializable> BasicKey<T> getKeyInstance(String keyPrefix, String prefixSeparator) {
    return CacheAPIFactory.<T>getBasicKey(keyPrefix, prefixSeparator);
  }
}
