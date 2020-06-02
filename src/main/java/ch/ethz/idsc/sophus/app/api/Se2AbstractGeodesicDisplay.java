// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieVectorLogManifold;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringTransport;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Tensor;

public abstract class Se2AbstractGeodesicDisplay implements GeodesicDisplay, Serializable {
  private static final Tensor ARROWHEAD = Arrowhead.of(0.4).unmodifiable();

  @Override // from GeodesicDisplay
  public final int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public final Tensor shape() {
    return ARROWHEAD;
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
  public final HsExponential hsExponential() {
    return LieExponential.of(lieGroup(), Se2CoveringExponential.INSTANCE);
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return Se2CoveringTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final VectorLogManifold flattenLogManifold() {
    return LieVectorLogManifold.of(lieGroup(), Se2CoveringExponential.INSTANCE::log);
  }

  @Override
  public final LineDistance lineDistance() {
    return null; // TODO line distance
  }

  @Override // from Object
  public abstract String toString();
}
