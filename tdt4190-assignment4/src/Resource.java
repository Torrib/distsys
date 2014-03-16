
/**
 * A resource with an associated lock that can be held by only one transaction at a time.
 */
class Resource
{
  static final int NOT_LOCKED = -1;

  /**
   * The transaction currently holding the lock to this resource
   */
  private int lockOwner;

  /**
   * Creates a new resource.
   */
  Resource()
  {
    lockOwner = NOT_LOCKED;
  }

  /**
   * Gives the lock of this resource to the requesting transaction. Blocks
   * the caller until the lock could be acquired.
   *
   * @param transactionId The ID of the transaction that wants the lock.
   * @return Whether or not the lock could be acquired.
   */
  synchronized boolean lock(int transactionId)
  {
   /* if (lockOwner == transactionId) {
      System.err.println("Error: Transaction " + transactionId + " tried to lock a resource it already has locked!");
      return false;
    }

    while (lockOwner != NOT_LOCKED) {
      try {
          if(Globals.PROBING_ENABLED) {
            wait();
          }   else {
            wait(Globals.TIMEOUT_INTERVAL);
          }
      } catch (InterruptedException ie) {
          return false;
      }
    }

    lockOwner = transactionId;
    return true;        */
      if(lockOwner == NOT_LOCKED) {
          lockOwner = new Integer(transactionId);
          return true;
      }
      else {
          // Wait for the lock
          try	{
              /**
               * When we are requesting a lock when probing is used
               * we can potentially wait for ever. Deadlocks are resolved
               * by sending probe messages.
               */
              if(Globals.PROBING_ENABLED){
                  wait();
              }else {
                  /**
                   * Timeouts are a simple approach; Just wait for some specified
                   * amount of time. If the lock is acquired during this time interval
                   * we presume that there exist a deadlock. (This may or may not be correct)
                   */
                  wait(Globals.TIMEOUT_INTERVAL);
              }
          } catch (InterruptedException ie) {
              return false;
          }

          if(lockOwner == NOT_LOCKED){
              lockOwner = new Integer(transactionId);
              return true;
          }else {
              return false;
          }


      }
  }

  /**
   * Releases the lock of this resource.
   *
   * @param transactionId The ID of the transaction that wants to release lock.
   *                      If this transaction doesn't currently own the lock an
   *                      error message is displayed.
   * @return Whether or not the lock could be released.
   */
  synchronized boolean unlock(int transactionId)
  {
    if (lockOwner == NOT_LOCKED || lockOwner != transactionId) {
      System.err.println("Error: Transaction " + transactionId + " tried to unlock a resource without owning the lock!");
      return false;
    }

    lockOwner = NOT_LOCKED;
    // Notify a waiting thread that it can acquire the lock
    notifyAll();
    return true;
  }

  /**
   * Gets the current owner of this resource's lock.
   *
   * @return An Integer containing the ID of the transaction currently
   * holding the lock, or NOT_LOCKED if the resource is unlocked.
   */
  synchronized int getLockOwner()
  {
    return lockOwner;
  }

  /**
   * Unconditionally releases the lock of this resource.
   */
  synchronized void forceUnlock()
  {
    lockOwner = NOT_LOCKED;
    // Notify a waiting thread that it can acquire the lock
    notifyAll();
  }

  /**
   * Checks if this resource's lock is held by a transaction running on the specified server.
   *
   * @param serverId The ID of the server.
   * @return Whether or not the current lock owner is running on that server.
   */
  synchronized boolean isLockedByServer(int serverId)
  {
    return lockOwner != NOT_LOCKED && ServerImpl.getTransactionOwner(lockOwner) == serverId;
  }
}
