// code by jph
package ch.ethz.idsc.tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Integers.html">Integers</a> */
// TODO JPH TENSOR V078 obsolete
public enum Integers {
  ;
  /** @param value non-negative
   * @return value
   * @throws Exception if given value is negative */
  public static int requirePositiveOrZero(int value) {
    if (value < 0)
      throw new IllegalArgumentException(Integer.toString(value));
    return value;
  }

  /** @param value strictly positive
   * @return value
   * @throws Exception if given value is negative or zero */
  public static int requirePositive(int value) {
    if (value <= 0)
      throw new IllegalArgumentException(Integer.toString(value));
    return value;
  }
}
