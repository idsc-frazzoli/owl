// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.r2.Se2ParametricDistance;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.rn.RnTransport;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.Tensor;

public class Se2GeodesicDisplay extends Se2AbstractGeodesicDisplay {
  public static final GeodesicDisplay INSTANCE = new Se2GeodesicDisplay();

  /***************************************************/
  private Se2GeodesicDisplay() {
    // ---
  }

  @Override // from GeodesicDisplay
  public HsTransport hsTransport() {
    return RnTransport.INSTANCE; // FIXME
  }

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return Se2Geodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor xym = xya.copy();
    xym.set(So2.MOD, 2);
    return xym;
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return Se2Group.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return Se2ParametricDistance.INSTANCE;
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
