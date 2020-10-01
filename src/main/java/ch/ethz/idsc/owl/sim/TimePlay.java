package ch.ethz.idsc.owl.sim;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/* package */ enum TimePlay {
  ;
  public static void main(String[] args) throws InterruptedException {
    LocalDateTime ldt1 = LocalDateTime.parse("2020-02-29T02:33:09.530");
    LocalDateTime ldt2 = LocalDateTime.now();
    long difference_ms = ChronoUnit.MILLIS.between(ldt1, ldt2); // difference in millis
    LocalDateTime ldt3 = ldt1.plusYears(1); // plus 1 Jahr
  }
}
