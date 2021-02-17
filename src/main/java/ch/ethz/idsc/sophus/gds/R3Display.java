// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public class R3Display extends RnDisplay {
  private static final Tensor CIRCLE = Arrowhead.of(RealScalar.of(0.3)).unmodifiable();
  // ---
  public static final ManifoldDisplay INSTANCE = new R3Display();

  private R3Display() {
    super(3);
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return CIRCLE;
  }

  @Override
  public Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor xya) {
    return Se2Matrix.of(xya);
  }
}
