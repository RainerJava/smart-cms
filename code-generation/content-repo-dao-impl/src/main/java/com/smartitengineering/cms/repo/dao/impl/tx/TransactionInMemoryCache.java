package com.smartitengineering.cms.repo.dao.impl.tx;

/**
 * A SPI for interacting with transactional memory
 * @author imyousuf
 */
public interface TransactionInMemoryCache {

  public Pair<TransactionStoreKey, TransactionStoreValue> getValueForIsolatedTransaction(String txId, String objectType,
                                                                                         String objectId);

  public Pair<TransactionStoreKey, TransactionStoreValue> getValueForNonIsolatedTransacton(String objectType,
                                                                                           String objectId);

  public void storeTransactionValue(TransactionStoreKey key, TransactionStoreValue val);
}
