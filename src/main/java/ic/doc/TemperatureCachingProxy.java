package ic.doc;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;

public class TemperatureCachingProxy implements WeatherService {

  private final int maxCacheSize;
  private final int minutesToLive;
  private final WeatherService weatherService;

  private final ExpiryChecker expiryChecker;

  private final Map<LocationAndDayPair, TemperatureAndTimeAddedPair> cache =
      new LinkedHashMap<LocationAndDayPair, TemperatureAndTimeAddedPair>() {
        @Override
        protected boolean removeEldestEntry(
            Map.Entry<LocationAndDayPair, TemperatureAndTimeAddedPair> eldest) {
          return size() > maxCacheSize;
        }
      };

  public TemperatureCachingProxy(
      WeatherService weatherService,
      int maxCacheSize,
      int minutesToLive,
      ExpiryChecker expiryChecker) {
    this.weatherService = weatherService;
    this.maxCacheSize = maxCacheSize;
    this.minutesToLive = minutesToLive;
    this.expiryChecker = expiryChecker;
  }

  private void removeExpiredItemsFromCache() {
    Iterator<Map.Entry<LocationAndDayPair, TemperatureAndTimeAddedPair>> iterator =
        cache.entrySet().iterator();

    while (iterator.hasNext()) {
      Map.Entry<LocationAndDayPair, TemperatureAndTimeAddedPair> entry = iterator.next();

      TemperatureAndTimeAddedPair pair = entry.getValue();
      if (expiryChecker.isPastExpiry(pair.time, this.minutesToLive)) {
        iterator.remove();
      } else {
        break;
      }
    }
  }

  @Override
  public int getTemperature(String location, DayOfWeek day) {
    LocationAndDayPair locationAndDayPair = new LocationAndDayPair(location, day);

    removeExpiredItemsFromCache();

    if (cache.containsKey(locationAndDayPair)) {
      return cache.get(locationAndDayPair).temperature;
    }

    int temperature = weatherService.getTemperature(location, day);
    Instant time = expiryChecker.getCurrentTime();

    TemperatureAndTimeAddedPair temperatureAndTimeAddedPair =
        new TemperatureAndTimeAddedPair(temperature, time);
    cache.put(locationAndDayPair, temperatureAndTimeAddedPair);
    return temperature;
  }

  private record LocationAndDayPair(String location, DayOfWeek day) {}

  private record TemperatureAndTimeAddedPair(int temperature, Instant time) {}
}
