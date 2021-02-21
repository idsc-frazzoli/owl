package ch.ethz.idsc.owl.sim;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/* package */ enum TimePlay {
  ;
  public static void main(String[] args) {
    LocalDateTime ldt1 = LocalDateTime.parse("2020-02-29T02:33:09.530");
    LocalDateTime ldt4 = LocalDateTime.parse("2023-02-28T02:33:09.530");
    LocalDateTime ldt2 = LocalDateTime.now();
    long difference_ms = ChronoUnit.MILLIS.between(ldt1, ldt2); // difference in millis
    LocalDateTime ldt3 = ldt1.plusYears(1); // plus 1 Jahr
    long difference_ms2 = ChronoUnit.NANOS.between(ldt1, ldt4); // difference in millis
    System.out.println(difference_ms2);
    LocalDateTime ldt4b = ldt1.plusNanos(difference_ms2);
    System.out.println(ldt4);
    System.out.println(ldt4b);
    System.out.println(ldt4.equals(ldt4b));
  }
}
