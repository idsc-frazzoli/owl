// code by jph
// https://stackoverflow.com/questions/3305059/how-do-you-calculate-log-base-2-in-java-for-integers
package ch.ethz.idsc.owl.math;

/** Quote from Java Integer:
 * "Note that this method is closely related to the logarithm base 2.
 * For all positive {@code int} values x:
 * floor(log<sub>2</sub>(x)) = {@code 31 - numberOfLeadingZeros(x)}
 * ceil(log<sub>2</sub>(x)) = {@code 32 - numberOfLeadingZeros(x - 1)} */
public enum IntegerLog2 {
  ;
  /** @param value
   * @return floor(log<sub>2</sub>(value)) */
  public static int floor(int value) {
    if (0 < value)
      return 31 - Integer.numberOfLeadingZeros(value);
    throw new IllegalArgumentException("" + value);
  }

  /** @param value
   * @return ceil(log<sub>2</sub>(value)) */
  public static int ceiling(int value) {
    if (0 < value)
      return 32 - Integer.numberOfLeadingZeros(value - 1);
    throw new IllegalArgumentException("" + value);
  }
}
