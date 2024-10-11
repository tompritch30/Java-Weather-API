package ic.doc;

import static org.hamcrest.CoreMatchers.is;
import com.weather.Day;
import com.weather.Forecaster;
import com.weather.Region;
import org.junit.Test;
import static org.junit.Assert.assertThat;

import java.time.DayOfWeek;

public class WeatherServiceAdapterTest {

  Forecaster forecaster = new Forecaster();
  WeatherServiceAdapter weatherService = new WeatherServiceAdapter(forecaster);

  @Test
  public void convertsTypesFromAdapterCallToTypesExpectedByApiCorrectly() {
    assertThat(
        weatherService.getTemperature("Birmingham", DayOfWeek.THURSDAY),
        is(forecaster.forecastFor(Region.BIRMINGHAM, Day.THURSDAY).temperature()));
  }
}
