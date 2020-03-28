// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.r2.Se2ParametricDistance;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class Se2GeodesicDisplay extends Se2CoveringGeodesicDisplay {
  public static final GeodesicDisplay INSTANCE = new Se2GeodesicDisplay();

  /***************************************************/
  private Se2GeodesicDisplay() {
    // ---
  }

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return Se2Geodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return Se2Group.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Scalar parametricDistance(Tensor p, Tensor q) {
    return Se2ParametricDistance.INSTANCE.distance(p, q);
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return Se2BiinvariantMean.FILTER;
  }

  @Override // from Object
  public String toString() {
    return "SE2";
  }
}
