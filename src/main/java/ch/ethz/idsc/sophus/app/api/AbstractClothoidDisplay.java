// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Tensor;

public abstract class AbstractClothoidDisplay implements GeodesicDisplay, Serializable {
  private static final Tensor ARROWHEAD = Arrowhead.of(0.4);

  @Override
  public final int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public final Tensor shape() {
    return ARROWHEAD;
  }

  @Override // from GeodesicDisplay
  public final Tensor project(Tensor xya) {
    return xya;
  }

  @Override // from GeodesicDisplay
  public final Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public final Tensor matrixLift(Tensor p) {
    return Se2Matrix.of(p);
  }

  @Override // from GeodesicDisplay
  public final LieGroup lieGroup() {
    return Se2Group.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final LieExponential lieExponential() {
    return Se2CoveringExponential.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return Se2BiinvariantMean.LINEAR;
  }

  @Override
  public final BarycentricCoordinate barycentricCoordinate() {
    throw new UnsupportedOperationException();
  }

  @Override
  public abstract String toString();
}
