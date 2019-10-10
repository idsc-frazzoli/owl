// code by jph
package ch.ethz.idsc.sophus.itp;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.opt.AbstractInterpolation;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.sca.Floor;

/** Hint:
 * for {@link RnBiinvariantMean} the interpolation is identical to {@link LinearInterpolation} */
/* package */ class BiinvariantMeanInterpolation extends AbstractInterpolation implements Serializable {
  /** @param biinvariantMean
   * @param tensor
   * @return */
  public static Interpolation of(BiinvariantMean biinvariantMean, Tensor tensor) {
    return new BiinvariantMeanInterpolation(Objects.requireNonNull(biinvariantMean), tensor);
  }

  // ---
  private final BiinvariantMean biinvariantMean;
  private final Tensor tensor;

  public BiinvariantMeanInterpolation(BiinvariantMean biinvariantMean, Tensor tensor) {
    this.biinvariantMean = biinvariantMean;
    this.tensor = Unprotect.references(tensor);
  }

  @Override
  public Tensor get(Tensor index) {
    // TODO JPH implement
    throw new UnsupportedOperationException();
  }

  @Override
  public Tensor at(Scalar index) {
    Scalar floor = Floor.FUNCTION.apply(index);
    Scalar remain = index.subtract(floor);
    int below = floor.number().intValue();
    if (Scalars.isZero(remain))
      return tensor.get(below);
    return biinvariantMean.mean(tensor.extract(below, below + 2), Tensors.of(RealScalar.ONE.subtract(remain), remain));
  }
}
