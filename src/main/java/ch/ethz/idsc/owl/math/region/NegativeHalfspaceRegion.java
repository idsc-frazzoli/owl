// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

/** open region consisting of all states with a negative coordinate at a given index
 * 
 * region in R^n */
public class NegativeHalfspaceRegion implements Region<Tensor>, Serializable {
  private final int index;

  /** @param index of state coordinate that when negative is inside the region */
  public NegativeHalfspaceRegion(int index) {
    this.index = index;
  }

  @Override // from Region
  public boolean isMember(Tensor tensor) {
    return Sign.isNegative(tensor.Get(index));
  }
}
