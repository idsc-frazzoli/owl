// code by jph
// https://stackoverflow.com/questions/3305059/how-do-you-calculate-log-base-2-in-java-for-integers
package ch.ethz.idsc.owl.math;

public enum IntegerLog2 {
  ;
  public static int of(int value) {
    if (0 < value)
      return 31 - Integer.numberOfLeadingZeros(value);
    throw new IllegalArgumentException("" + value);
  }
}
