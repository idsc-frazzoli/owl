// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.crv.decim.HsLineDistance;
import ch.ethz.idsc.sophus.crv.decim.LineDistance;
import ch.ethz.idsc.sophus.crv.decim.SymmetricLineDistance;
import ch.ethz.idsc.sophus.hs.sn.SnLineDistance;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;

/* package */ enum SnLineDistances {
  DEFAULT(SnLineDistance.INSTANCE), //
  PROJECT(new HsLineDistance(SnManifold.INSTANCE)), //
  SYMMETR(new SymmetricLineDistance(new HsLineDistance(SnManifold.INSTANCE))), //
  ;

  private final LineDistance lineDistance;

  private SnLineDistances(LineDistance lineDistance) {
    this.lineDistance = lineDistance;
  }

  public LineDistance lineDistance() {
    return lineDistance;
  }
}
