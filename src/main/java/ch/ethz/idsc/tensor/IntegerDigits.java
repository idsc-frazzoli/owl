// code by jph
package ch.ethz.idsc.tensor;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/** IntegerDigits is consistent with Mathematica::IntegerDigits except for input zero:
 * <pre>
 * Tensor-Lib.::IntegerDigits[0] == {}
 * Mathematica::IntegerDigits[0] == {0}
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/IntegerDigits.html">IntegerDigits</a> */
// TODO JPH TENSOR v070
public enum IntegerDigits {
  ;
  /** @param scalar
   * @return vector with integer digits of given scalar
   * @throws Exception if given scalar is not an integer
   * @see IntegerQ */
  public static Tensor of(Scalar scalar) {
    return Tensors.vector(of(Scalars.bigIntegerValueExact(scalar)));
  }

  static List<Integer> of(BigInteger bigInteger) {
    bigInteger = bigInteger.abs();
    Deque<Integer> deque = new ArrayDeque<>();
    while (!bigInteger.equals(BigInteger.ZERO)) {
      BigInteger[] bigIntegers = bigInteger.divideAndRemainder(BigInteger.TEN);
      bigInteger = bigIntegers[0];
      deque.push(bigIntegers[1].intValue());
    }
    return deque.stream().collect(Collectors.toList());
  }
}
