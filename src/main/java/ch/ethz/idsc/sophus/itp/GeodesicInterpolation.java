// code by gjoel, jph
package ch.ethz.idsc.sophus.itp;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.AbstractInterpolation;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.sca.Floor;

/** univariate geodesic interpolation */
public class GeodesicInterpolation extends AbstractInterpolation {
  /** @param splitInterface
   * @param tensor
   * @return interpolation function for scalars from the interval [0, tensor.length() - 1] */
  public static Interpolation of(SplitInterface splitInterface, Tensor tensor) {
    return new GeodesicInterpolation(Objects.requireNonNull(splitInterface), Objects.requireNonNull(tensor));
  }

  // ---
  private final SplitInterface splitInterface;
  private final Tensor tensor;

  private GeodesicInterpolation(SplitInterface splitInterface, Tensor tensor) {
    this.splitInterface = splitInterface;
    this.tensor = tensor;
  }

  @Override // from Interpolation
  public Tensor get(Tensor index) {
    // TODO JPH allow multi-variate interpolation
    throw new UnsupportedOperationException();
  }

  @Override // from Interpolation
  public Tensor at(Scalar index) {
    Scalar floor = Floor.FUNCTION.apply(index);
    Scalar remain = index.subtract(floor);
    int below = floor.number().intValue();
    if (Scalars.isZero(remain))
      return tensor.get(below);
    return splitInterface.split( //
        tensor.get(below), //
        tensor.get(below + 1), //
        remain);
  }
}
