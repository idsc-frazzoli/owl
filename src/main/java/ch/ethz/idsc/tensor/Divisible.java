// code by jph
package ch.ethz.idsc.tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Divisible.html">Divisible</a> */
// TODO TENSOR V068 OBSOLETE
public enum Divisible {
  ;
  /** @param n numerator in exact precision
   * @param m denominator in exact precision
   * @return true only if n / m is an integer
   * @throws Exception if given n or m are not in exact precision */
  public static boolean of(Scalar n, Scalar m) {
    Scalar quotient = n.divide(m);
    if (ExactScalarQ.of(quotient))
      return IntegerQ.of(quotient);
    throw TensorRuntimeException.of(n, m);
  }
}
