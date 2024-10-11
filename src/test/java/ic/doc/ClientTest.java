package ic.doc;

import com.weather.Day;
import com.weather.Forecaster;
import com.weather.Region;
import org.junit.Test;

import java.time.DayOfWeek;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClientTest {

  Forecaster forecaster = new Forecaster();
  WeatherServiceAdapter weatherService = new WeatherServiceAdapter(forecaster);

  TemperatureCachingProxy temperatureCachingProxy =
      new TemperatureCachingProxy(weatherService, 5, 60, new Clock());

  @Test
  public void clientGetsSameTemperatureForecastAsUnderlyingApiCall() {
    assertThat(
        temperatureCachingProxy.getTemperature("Edinburgh", DayOfWeek.WEDNESDAY),
        is(forecaster.forecastFor(Region.EDINBURGH, Day.WEDNESDAY).temperature()));
  }
}
