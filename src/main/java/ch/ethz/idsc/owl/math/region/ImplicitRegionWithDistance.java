// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Ramp;

public abstract class ImplicitRegionWithDistance extends ImplicitFunctionRegion //
    implements RegionWithDistance<Tensor> {
  @Override // from DistanceFunction<Tensor>
  public final Scalar distance(Tensor tensor) {
    return Ramp.FUNCTION.apply(signedDistance(tensor));
  }
}
