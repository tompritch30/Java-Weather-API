package ic.doc;

import org.junit.Test;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static org.junit.Assert.assertTrue;

public class ClockTest {
  ExpiryChecker clock = new Clock();

  @Test
  public void correctlyDeterminesIfTimeIsPastExpiry() {
    Instant currentTime = clock.getCurrentTime();
    Instant expiredTime = currentTime.minus(60, ChronoUnit.MINUTES);
    assertTrue(clock.isPastExpiry(expiredTime, 60));
  }
}
