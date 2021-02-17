// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.tensor.Tensor;

public class H1Display extends HnDisplay {
  public static final ManifoldDisplay INSTANCE = new H1Display();

  /***************************************************/
  private H1Display() {
    super(1);
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return p.copy();
  }
}
