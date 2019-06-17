// code by ob, jph
package ch.ethz.idsc.tensor;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.mat.Fourier;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SpectrogramArray.html">SpectrogramArray</a> */
public class SpectrogramArray implements TensorUnaryOperator {
  private static final ScalarUnaryOperator LOG2 = Log.base(RealScalar.of(2));

  /** Mathematica default
   * 
   * @param tensor
   * @return */
  public static Tensor of(Tensor tensor) {
    int num = Scalars.intValueExact(Round.FUNCTION.apply(LOG2.apply(Sqrt.FUNCTION.apply(RealScalar.of(tensor.length())))));
    int windowLength = 1 << ++num;
    return of(windowLength, Scalars.intValueExact(Round.FUNCTION.apply(RationalScalar.of(windowLength, 3)))).apply(tensor);
  }

  /***************************************************/
  /** @param windowDuration
   * @param samplingFrequency
   * @param offset positive
   * @return */
  public static TensorUnaryOperator of(Scalar windowDuration, Scalar samplingFrequency, int offset) {
    return of(Scalars.intValueExact(Round.FUNCTION.apply(windowDuration.multiply(samplingFrequency))), offset);
  }

  /** @param windowLength
   * @param offset positive and not greater than windowLength
   * @return */
  public static TensorUnaryOperator of(int windowLength, int offset) {
    if (offset <= 0 || windowLength < offset)
      throw new RuntimeException(windowLength + " " + offset);
    return new SpectrogramArray(windowLength, offset);
  }

  // ---
  private final int windowLength;
  private final int offset;
  private final TensorUnaryOperator tensorUnaryOperator;

  private SpectrogramArray(int windowLength, int offset) {
    this.windowLength = windowLength;
    this.offset = offset;
    int highestOneBit = Integer.highestOneBit(windowLength);
    tensorUnaryOperator = windowLength == highestOneBit //
        ? t -> t //
        : PadRight.zeros(highestOneBit * 2);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor signal) {
    int total = signal.length() - windowLength;
    int limit = total / offset + 1;
    return Tensor.of(IntStream.iterate(0, index -> index + offset) //
        .limit(limit) //
        .mapToObj(index -> signal.extract(index, index + windowLength)) //
        .map(tensorUnaryOperator) //
        .map(Fourier::of));
  }
}