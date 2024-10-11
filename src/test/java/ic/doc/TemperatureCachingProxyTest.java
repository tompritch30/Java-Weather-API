package ic.doc;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class TemperatureCachingProxyTest {

  @Rule public JUnitRuleMockery context = new JUnitRuleMockery();

  WeatherService weatherService = context.mock(WeatherService.class);
  ExpiryChecker expiryChecker = context.mock(ExpiryChecker.class);
  Instant defaultTime = LocalDate.of(2023, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
  TemperatureCachingProxy temperatureCachingProxy =
      new TemperatureCachingProxy(weatherService, 2, 60, expiryChecker);

  @Test
  public void makesCallToGetTemperatureAdapterIfCacheMissOccurs() {
    String location = "Paris";
    DayOfWeek day = DayOfWeek.SUNDAY;

    context.checking(
        new Expectations() {
          {
            oneOf(weatherService).getTemperature(location, day);
            ignoring(expiryChecker);
          }
        });

    temperatureCachingProxy.getTemperature(location, day);
  }

  @Test
  public void getsTemperatureFromCacheWhenPresent() {
    String location = "London";
    DayOfWeek day = DayOfWeek.MONDAY;

    context.checking(
        new Expectations() {
          {
            oneOf(weatherService).getTemperature(location, day);
            ignoring(expiryChecker);
          }
        });

    temperatureCachingProxy.getTemperature(location, day);
    temperatureCachingProxy.getTemperature(location, day);
  }

  @Test
  public void cacheEvictsOldEntriesIfTheMaximumSizeIsReached() {
    String location = "London";
    context.checking(
        new Expectations() {
          {
            exactly(2).of(weatherService).getTemperature(location, DayOfWeek.SUNDAY);
            oneOf(weatherService).getTemperature(location, DayOfWeek.MONDAY);
            oneOf(weatherService).getTemperature(location, DayOfWeek.TUESDAY);
            ignoring(expiryChecker);
          }
        });
    temperatureCachingProxy.getTemperature(location, DayOfWeek.SUNDAY);
    temperatureCachingProxy.getTemperature(location, DayOfWeek.MONDAY);
    temperatureCachingProxy.getTemperature(location, DayOfWeek.TUESDAY);
    temperatureCachingProxy.getTemperature(location, DayOfWeek.SUNDAY);
  }

  @Test
  public void cacheRemovesAllExpiredEntriesWhenRequestIsMade() {
    String location = "Germany";

    context.checking(
        new Expectations() {
          {
            exactly(2).of(weatherService).getTemperature(location, DayOfWeek.TUESDAY);
            allowing(expiryChecker)
                .isPastExpiry(with(any(Instant.class)), with(any(Integer.class)));
            will(returnValue(true));
            allowing(expiryChecker).getCurrentTime();
            will(returnValue(defaultTime));
          }
        });
    temperatureCachingProxy.getTemperature(location, DayOfWeek.TUESDAY);
    temperatureCachingProxy.getTemperature(location, DayOfWeek.TUESDAY);
  }
}
