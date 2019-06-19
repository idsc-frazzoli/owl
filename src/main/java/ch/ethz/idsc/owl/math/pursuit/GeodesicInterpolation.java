// code by gjoel, jph
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.AbstractInterpolation;
import ch.ethz.idsc.tensor.sca.Floor;

public class GeodesicInterpolation extends AbstractInterpolation {
  private final SplitInterface splitInterface;
  private final Tensor tensor;

  public GeodesicInterpolation(SplitInterface splitInterface, Tensor tensor) {
    this.splitInterface = Objects.requireNonNull(splitInterface);
    this.tensor = Objects.requireNonNull(tensor);
  }

  @Override
  public Tensor get(Tensor index) {
    throw new UnsupportedOperationException();
  }

  @Override
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
