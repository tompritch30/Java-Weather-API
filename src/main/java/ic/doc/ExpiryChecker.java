package ic.doc;

import java.time.Instant;

public interface ExpiryChecker {
  Instant getCurrentTime();

  boolean isPastExpiry(Instant cachedTime, int minutesToLive);
}
