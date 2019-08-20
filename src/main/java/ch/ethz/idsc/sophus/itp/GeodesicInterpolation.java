// code by gjoel, jph
package ch.ethz.idsc.sophus.itp;

import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.AbstractInterpolation;
import ch.ethz.idsc.tensor.opt.BinaryAverage;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.sca.Floor;

/** univariate geodesic interpolation */
public class GeodesicInterpolation extends AbstractInterpolation {
  /** @param binaryAverage
   * @param tensor
   * @return interpolation function for scalars from the interval [0, tensor.length() - 1] */
  public static Interpolation of(BinaryAverage binaryAverage, Tensor tensor) {
    return new GeodesicInterpolation( //
        Objects.requireNonNull(binaryAverage), //
        Objects.requireNonNull(tensor));
  }

  // ---
  private final BinaryAverage binaryAverage;
  private final Tensor tensor;

  private GeodesicInterpolation(BinaryAverage binaryAverage, Tensor tensor) {
    this.binaryAverage = binaryAverage;
    this.tensor = tensor;
  }

  @Override // from Interpolation
  public Tensor get(Tensor index) {
    throw new UnsupportedOperationException();
  }

  @Override // from Interpolation
  public Tensor at(Scalar index) {
    Scalar floor = Floor.FUNCTION.apply(index);
    Scalar remain = index.subtract(floor);
    int below = floor.number().intValue();
    if (Scalars.isZero(remain))
      return tensor.get(below);
    return binaryAverage.split( //
        tensor.get(below), //
        tensor.get(below + 1), //
        remain);
  }
}
