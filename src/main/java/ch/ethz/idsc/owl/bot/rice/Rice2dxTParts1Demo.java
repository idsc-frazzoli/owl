// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.Arrays;
import java.util.Collection;

import ch.ethz.idsc.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.ethz.idsc.owl.bot.r2.R2xTPolygonStateTimeRegion;
import ch.ethz.idsc.owl.bot.rn.glc.R2xTEllipsoidsAnimationDemo;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.sophus.hs.r2.SimpleR2TranslationFamily;
import ch.ethz.idsc.sophus.hs.r2.So2Family;
import ch.ethz.idsc.sophus.math.BijectionFamily;
import ch.ethz.idsc.sophus.ply.CogPoints;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;

public class Rice2dxTParts1Demo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    Scalar mu = RealScalar.of(-.5);
    Collection<Tensor> controls = Rice2Controls.create2d(1).getFlows(15);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    Rice2dEntity abstractEntity = new Rice2dEntity(mu, Tensors.vector(2, 2, 0, 0), trajectoryControl, controls);
    abstractEntity.delayHint = RealScalar.of(1.6);
    owlyAnimationFrame.add(abstractEntity);
    ScalarTensorFunction stf1 = R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(4, 2), 0.03, 2.5);
    BijectionFamily noise1 = new SimpleR2TranslationFamily(stf1);
    Region<StateTime> region1 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.4, 0.5), noise1, () -> abstractEntity.getStateTimeNow().time());
    ScalarTensorFunction stf2 = R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(1, 3), 0.03, 2.5);
    BijectionFamily noise2 = new SimpleR2TranslationFamily(stf2);
    Region<StateTime> region2 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.5, 0.6), noise2, () -> abstractEntity.getStateTimeNow().time());
    BijectionFamily rigid2 = new So2Family(s -> s.multiply(RealScalar.of(0.25)));
    Tensor polygon = CogPoints.of(4, RealScalar.of(1.0), RealScalar.of(0.3));
    Region<StateTime> region3 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid2, () -> abstractEntity.getStateTimeNow().time());
    TrajectoryRegionQuery trq = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(Arrays.asList(region1, region2, region3)));
    // abstractEntity.obstacleQuery = trq;
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trq);
    MouseGoal.simple(owlyAnimationFrame, abstractEntity, plannerConstraint);
    // owlyAnimationFrame.addRegionRender(imageRegion);
    owlyAnimationFrame.addBackground((RenderInterface) region1);
    owlyAnimationFrame.addBackground((RenderInterface) region2);
    owlyAnimationFrame.addBackground((RenderInterface) region3);
    owlyAnimationFrame.geometricComponent.setOffset(350, 350);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new Rice2dxTParts1Demo().start().jFrame.setVisible(true);
  }
}
