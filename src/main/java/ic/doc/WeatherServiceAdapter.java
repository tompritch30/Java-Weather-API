package ic.doc;

import com.weather.Day;
import com.weather.Forecaster;
import com.weather.Region;

import java.time.DayOfWeek;

public class WeatherServiceAdapter implements WeatherService {

  private final Forecaster forecaster;

  public WeatherServiceAdapter(Forecaster forecaster) {
    this.forecaster = forecaster;
  }

  @Override
  public int getTemperature(String location, DayOfWeek day) {
    return forecaster
        .forecastFor(
            Region.valueOf(location.toUpperCase()),
            Day.valueOf(day.toString().toUpperCase()))
        .temperature();
  }
}
