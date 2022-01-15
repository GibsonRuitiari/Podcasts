@file:OptIn(ExperimentalTime::class)
package config

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

// adapted from Jake Wharton's Sdk Search

@OptIn(ExperimentalTime::class)
fun <R> (suspend ()->R).memoize(expiration: Duration=Duration.INFINITE,
                                clock: TimeSource=TimeSource.Monotonic):suspend ()->R{
    require(!expiration.isNegative()) {"Duration cannot be negative"}
    return MemoizedSuspendingSupplier(expiration,clock,this)::invoke
}

private class MemoizedSuspendingSupplier<R> (private val expiration:Duration,
                                             clock:TimeSource, private val delegate:suspend ()->R){
 private val lock = Mutex()
  @Volatile private var value:Any?=null
    /**
     * A [TimeMark] for which an elapsed time of 0 or greater means [value] has expired and should
     * be (re)obtained from [delegate].
     */
    @Volatile private var expirationMark = clock.markNow()
    suspend operator fun invoke():R{
        val mark = expirationMark
        if (mark.hasPassedNow()){
            lock.withLock {
                // recheck for lost race
                if (mark == expirationMark){
                    val newValue=delegate()
                    value = newValue
                    expirationMark = mark+expiration
                    return newValue
                }
            }
        }
        @Suppress("UNCHECKED_CAST")
        return value as R
    }
}