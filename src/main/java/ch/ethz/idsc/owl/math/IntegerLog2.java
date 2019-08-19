// code by jph
// https://stackoverflow.com/questions/3305059/how-do-you-calculate-log-base-2-in-java-for-integers
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Integers;

/** Quote from Java Integer:
 * "Note that this method is closely related to the logarithm base 2.
 * For all positive {@code int} values x:
 * floor(log<sub>2</sub>(x)) = {@code 31 - numberOfLeadingZeros(x)}
 * ceil(log<sub>2</sub>(x)) = {@code 32 - numberOfLeadingZeros(x - 1)} */
public enum IntegerLog2 {
  ;
  /** @param value positive
   * @return floor(log<sub>2</sub>(value))
   * @throws Exception if value is negative or zero */
  public static int floor(int value) {
    return 31 - Integer.numberOfLeadingZeros(Integers.requirePositive(value));
  }

  /** @param value positive
   * @return ceil(log<sub>2</sub>(value)) */
  public static int ceiling(int value) {
    return 32 - Integer.numberOfLeadingZeros(Integers.requirePositive(value) - 1);
  }
}
