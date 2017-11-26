// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** member check is carried on the input tensor mapped by the given operator */
public class MappedRegion implements Region<Tensor> {
  private final Region<Tensor> region;
  private final TensorUnaryOperator function;

  public MappedRegion(Region<Tensor> region, TensorUnaryOperator function) {
    this.region = region;
    this.function = function;
  }

  @Override // from Region
  public boolean isMember(Tensor tensor) {
    return region.isMember(function.apply(tensor));
  }
}
