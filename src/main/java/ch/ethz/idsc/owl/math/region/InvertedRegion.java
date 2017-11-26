// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.tensor.Tensor;

public class InvertedRegion implements Region<Tensor> {
  private final Region<Tensor> region;

  public InvertedRegion(Region<Tensor> region) {
    this.region = region;
  }

  @Override // from Region
  public final boolean isMember(Tensor tensor) {
    return !region.isMember(tensor);
  }
}
