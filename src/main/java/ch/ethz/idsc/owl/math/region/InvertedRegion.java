// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;
import java.util.Objects;

public class InvertedRegion<T> implements Region<T>, Serializable {
  private final Region<T> region;

  public InvertedRegion(Region<T> region) {
    this.region = Objects.requireNonNull(region);
  }

  @Override // from Region
  public final boolean isMember(T tensor) {
    return !region.isMember(tensor);
  }
}
