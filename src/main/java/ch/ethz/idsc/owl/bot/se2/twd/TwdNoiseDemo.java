// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import ch.ethz.idsc.owl.bot.r2.R2NoiseRegion;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

public class TwdNoiseDemo extends AbstractTwdDemo {
  private final TrajectoryRegionQuery trajectoryRegionQuery = //
      CatchyTrajectoryRegionQuery.timeInvariant(new R2NoiseRegion(RealScalar.of(0.1)));

  @Override // from AbstractTwdDemo
  TwdEntity configure(OwlyAnimationFrame owlyAnimationFrame) {
    TwdEntity twdEntity = TwdEntity.createDuckie(new StateTime(Tensors.vector(0, 0, 0), RealScalar.ZERO));
    owlyAnimationFrame.add(twdEntity);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    MouseGoal.simple(owlyAnimationFrame, twdEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(trajectoryRegionQuery));
    owlyAnimationFrame.configCoordinateOffset(400, 400);
    return twdEntity;
  }

  @Override // from AbstractTwdDemo
  Region<StateTime> getRegion() {
    return trajectoryRegionQuery;
  }

  public static void main(String[] args) {
    new TwdNoiseDemo().start().jFrame.setVisible(true);
  }
}
