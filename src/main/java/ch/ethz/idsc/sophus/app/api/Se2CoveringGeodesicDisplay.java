// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;

import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.hs.r2.Se2CoveringParametricDistance;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringManifold;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class Se2CoveringGeodesicDisplay implements GeodesicDisplay, Serializable {
  private static final Tensor ARROWHEAD = Arrowhead.of(0.4);
  public static final GeodesicDisplay INSTANCE = new Se2CoveringGeodesicDisplay();

  /***************************************************/
  protected Se2CoveringGeodesicDisplay() {
    // ---
  }

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return Se2CoveringGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
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
  public LieGroup lieGroup() {
    return Se2CoveringGroup.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final LieExponential lieExponential() {
    return Se2CoveringExponential.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final FlattenLogManifold flattenLogManifold() {
    return Se2CoveringManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Scalar parametricDistance(Tensor p, Tensor q) {
    return Se2CoveringParametricDistance.INSTANCE.distance(p, q);
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return Se2CoveringBiinvariantMean.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "SE2C";
  }
}
