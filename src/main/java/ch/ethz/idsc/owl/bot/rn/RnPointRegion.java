// code by jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

public class RnPointRegion implements RegionWithDistance<Tensor> {
  private final Tensor tensor;

  public RnPointRegion(Tensor tensor) {
    this.tensor = tensor;
  }

  @Override // from RegionWithDistance
  public boolean isMember(Tensor element) {
    return element.equals(tensor);
  }

  @Override // from RegionWithDistance
  public Scalar distance(Tensor element) {
    return Norm._2.between(tensor, element);
  }
}
