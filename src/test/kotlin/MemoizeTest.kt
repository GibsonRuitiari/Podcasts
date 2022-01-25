import com.google.common.truth.Truth.assertThat
import config.memoize
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.ExperimentalTime
import kotlin.time.TestTimeSource

class MemoizeTest{

    @OptIn(ExperimentalTime::class)
    @Test fun negativeExpirationDurationDisallowed(){
        val negativeExpiration = (-1).minutes
        val exception= assertThrows<IllegalArgumentException> {
            suspend {  }.memoize(negativeExpiration)
        }
        assertEquals("Duration must be positive: $negativeExpiration", exception.message)
    }

    @OptIn(ExperimentalTime::class)
    @Test fun supplierInvokedImmediately() = runBlocking {
        var calls=0
        val supplier = suspend {
            ++calls

        }.memoize()
        assertThat(calls).isEqualTo(0)
        assertThat(supplier.invoke()).isEqualTo(1)
        assertThat(supplier.invoke()).isEqualTo(1)
    }

    @OptIn(ExperimentalTime::class)
    @Test fun whenDefaultExpirationIsInfinity() = runBlocking {
        val clock = TestTimeSource()
        var calls=0
        val supplier = suspend {
            ++calls
        }.memoize(clock=clock)
        assertThat(supplier.invoke()).isEqualTo(1)
        clock += Long.MAX_VALUE.nanoseconds
        assertThat(supplier.invoke()).isEqualTo(1)
    }

}