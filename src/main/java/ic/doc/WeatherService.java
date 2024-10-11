package ic.doc;

import java.time.DayOfWeek;

public interface WeatherService {
  int getTemperature(String location, DayOfWeek day);
}
