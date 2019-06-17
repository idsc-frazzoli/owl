// code by jph
package ch.ethz.idsc.sophus.math;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public class SplitParametricCurve implements GeodesicInterface, Serializable {
  private final SplitInterface splitInterface;

  public SplitParametricCurve(SplitInterface splitInterface) {
    this.splitInterface = Objects.requireNonNull(splitInterface);
  }

  @Override // from ParametricCurve
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    return scalar -> splitInterface.split(p, q, scalar);
  }

  @Override // from SplitInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return splitInterface.split(p, q, scalar);
  }
}
