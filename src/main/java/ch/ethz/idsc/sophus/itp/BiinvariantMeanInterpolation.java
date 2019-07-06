// code by jph
package ch.ethz.idsc.sophus.itp;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.AbstractInterpolation;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;

/** Hint:
 * for {@link RnBiinvariantMean} the interpolation is identical to {@link LinearInterpolation} */
// TODO JPH implement
/* package */ class BiinvariantMeanInterpolation extends AbstractInterpolation {
  /** @param biinvariantMean
   * @param tensor
   * @return */
  public static Interpolation of(BiinvariantMean biinvariantMean, Tensor tensor) {
    return new BiinvariantMeanInterpolation(biinvariantMean, tensor);
  }

  // ---
  @SuppressWarnings("unused")
  private final BiinvariantMean biinvariantMean;
  @SuppressWarnings("unused")
  private final Tensor tensor;

  public BiinvariantMeanInterpolation(BiinvariantMean biinvariantMean, Tensor tensor) {
    this.biinvariantMean = biinvariantMean;
    this.tensor = tensor;
  }

  @Override
  public Tensor get(Tensor index) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  public Tensor at(Scalar index) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }
}
