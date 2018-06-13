// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** two wheel drive entity with state space augmented with time */
/* package */ class TwdxTEntity extends TwdEntity {
  public TwdxTEntity(TwdDuckieFlows twdConfig, StateTime stateTime) {
    super(stateTime, new TwdTrajectoryControl(), twdConfig); // LONGTERM choice of traj ctrl was not thorough
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.of(1.1);
  }

  @Override
  protected StateTimeRaster stateTimeRaster() {
    Scalar dt = FIXEDSTATEINTEGRATOR.getTimeStepTrajectory();
    return new EtaRaster(PARTITIONSCALE.copy().append(dt.reciprocal()), StateTime::joined);
  }
}
