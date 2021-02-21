// code by jph
package ch.ethz.idsc.sophus.opt;

import ch.ethz.idsc.sophus.decim.HsLineDistance;
import ch.ethz.idsc.sophus.decim.LineDistance;
import ch.ethz.idsc.sophus.decim.SymmetricLineDistance;
import ch.ethz.idsc.sophus.hs.sn.SnLineDistance;
import ch.ethz.idsc.sophus.hs.sn.SnLineDistanceAlt;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;

public enum SnLineDistances {
  DEFAULT(SnLineDistance.INSTANCE), //
  DEF_ALT(SnLineDistanceAlt.INSTANCE), //
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
