// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.Arrays;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.R2xTPolygonStateTimeRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.map.BijectionFamily;
import ch.ethz.idsc.owl.math.map.Se2Family;
import ch.ethz.idsc.owl.math.map.So2Family;
import ch.ethz.idsc.owl.math.planar.CogPoints;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.EuclideanTrajectoryControl;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Rice2dxTGearDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    Scalar mu = RealScalar.of(-.5);
    Collection<Flow> controls = Rice2Controls.create2d(mu, 1, 15);
    TrajectoryControl trajectoryControl = EuclideanTrajectoryControl.INSTANCE;
    Rice2dEntity abstractEntity = new Rice2dEntity(mu, Tensors.vector(2, 2, 0, 0), trajectoryControl, controls);
    abstractEntity.delayHint = RealScalar.of(1.6);
    owlyAnimationFrame.set(abstractEntity);
    Tensor polygon = CogPoints.of(4, RealScalar.of(1.0), RealScalar.of(0.3));
    // ---
    BijectionFamily rigid2 = new So2Family(s -> s.multiply(RealScalar.of(0.25)));
    Region<StateTime> cog0 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid2, () -> abstractEntity.getStateTimeNow().time());
    // ---
    BijectionFamily rigid3 = new Se2Family(s -> Tensors.of( //
        RealScalar.of(1.7), RealScalar.ZERO, RealScalar.of(-1.2).add(s.multiply(RealScalar.of(-.25)))));
    Region<StateTime> cog1 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid3, () -> abstractEntity.getStateTimeNow().time());
    // ---
    BijectionFamily rigid1 = new Se2Family(s -> Tensors.of( //
        RealScalar.of(1.7 * 2), RealScalar.ZERO, s.multiply(RealScalar.of(0.25))));
    Region<StateTime> cog2 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid1, () -> abstractEntity.getStateTimeNow().time());
    TrajectoryRegionQuery trq = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(Arrays.asList(cog0, cog1, cog2)));
    // ---
    owlyAnimationFrame.setPlannerConstraint(new TrajectoryObstacleConstraint(trq));
    owlyAnimationFrame.addBackground((RenderInterface) cog0);
    owlyAnimationFrame.addBackground((RenderInterface) cog1);
    owlyAnimationFrame.addBackground((RenderInterface) cog2);
    // ---
    owlyAnimationFrame.configCoordinateOffset(350, 350);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new Rice2dxTGearDemo().start().jFrame.setVisible(true);
  }
}
