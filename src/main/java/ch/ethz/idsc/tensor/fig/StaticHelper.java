// code by gjoel
package ch.ethz.idsc.tensor.fig;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimePeriod;

import ch.ethz.idsc.tensor.Scalar;

/* package */ enum StaticHelper {
  ;
  // TODO the implementation can be improved by implementing TimePeriod
  private static final Calendar CALENDAR = Calendar.getInstance();

  /** @param time
   * @return */
  public static TimePeriod timePeriod(Scalar time) {
    long timeL = time.number().longValue();
    int hours = Math.toIntExact(TimeUnit.SECONDS.toHours(timeL));
    int minutes = Math.toIntExact(TimeUnit.SECONDS.toMinutes(timeL) - 60 * hours);
    int seconds = Math.toIntExact(TimeUnit.SECONDS.toSeconds(timeL) - minutes * 60 - hours * 3600);
    int day = 1;
    int month = CALENDAR.get(Calendar.MONTH) + 1; // Month are 0 based, thus it is necessary to add 1
    int year = CALENDAR.get(Calendar.YEAR);
    return new Second(seconds, minutes, hours, day, month, year); // month and year can not be zero
  }
}
