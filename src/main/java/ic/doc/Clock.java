package ic.doc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Clock implements ExpiryChecker {

  @Override
  public Instant getCurrentTime() {
    return Instant.now();
  }

  @Override
  public boolean isPastExpiry(Instant cachedTime, int minutesToLive) {
    return getCurrentTime().isAfter(cachedTime.plus(minutesToLive, ChronoUnit.MINUTES));
  }
}
