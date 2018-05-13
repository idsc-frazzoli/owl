// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;

public class InvertedRegion<T> implements Region<T>, Serializable {
  private final Region<T> region;

  public InvertedRegion(Region<T> region) {
    this.region = region;
  }

  @Override // from Region
  public final boolean isMember(T tensor) {
    return !region.isMember(tensor);
  }
}
