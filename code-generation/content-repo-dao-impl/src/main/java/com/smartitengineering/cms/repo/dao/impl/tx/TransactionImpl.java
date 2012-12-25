package com.smartitengineering.cms.repo.dao.impl.tx;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.smartitengineering.cms.repo.dao.tx.Transaction;
import com.smartitengineering.cms.repo.dao.tx.TransactionCompletionEvent;
import com.smartitengineering.cms.repo.dao.tx.TransactionCompletionEvent.CompletionEvent;
import com.smartitengineering.cms.repo.dao.tx.TransactionCompletionListener;
import com.smartitengineering.cms.repo.dao.tx.TransactionException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang.mutable.MutableBoolean;

/**
 *
 * @author imyousuf
 */
class TransactionImpl implements Transaction {

  private final String id;
  private final MutableBoolean completed;
  private final TransactionService service;
  private final Set<TransactionCompletionListener> listeners;
  private final boolean isolatedTransaction;

  @Inject
  public TransactionImpl(TransactionService service, @Assisted boolean isolatedTransaction) {
    this.service = service;
    this.id = this.service.getNextTransactionId();
    this.completed = new MutableBoolean(false);
    this.listeners = new LinkedHashSet<TransactionCompletionListener>();
    this.isolatedTransaction = isolatedTransaction;
  }

  public String getId() {
    return this.id;
  }

  public void commit() throws TransactionException {
    boolean successful = false;
    try {
      this.service.commit(id);
      successful = true;
    }
    catch (RuntimeException ex) {
      throw ex;
    }
    finally {
      completeTransaction(TransactionCompletionEvent.CompletionEvent.COMMIT, successful);
    }
  }

  public void rollback() throws TransactionException {
    boolean successful = false;
    try {
      this.service.rollback(id);
      successful = true;
    }
    catch (RuntimeException re) {
      throw re;
    }
    finally {
      completeTransaction(TransactionCompletionEvent.CompletionEvent.ROLLBACK, successful);
    }
  }

  private void completeTransaction(final CompletionEvent event, boolean successful) {
    this.completed.setValue(true);
    TransactionCompletionEvent cEvent = new TransactionCompletionEvent(event, successful, this);
    for (TransactionCompletionListener listener : this.listeners) {
      listener.transactionComplete(cEvent);
    }
  }

  public boolean isCompleted() {
    return this.completed.booleanValue();
  }

  public void addTransactionCompletionListener(TransactionCompletionListener completionListener) {
    this.listeners.add(completionListener);
  }

  public void removeTransactionCompletionListener(TransactionCompletionListener completionListener) {
    this.listeners.remove(completionListener);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Transaction)) {
      return false;
    }
    final Transaction other = (Transaction) obj;
    if ((this.id == null) ? (other.getId() != null) : !this.id.equals(other.getId())) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
    return hash;
  }

  public boolean isIsolatedTransaction() {
    return isolatedTransaction;
  }
}
