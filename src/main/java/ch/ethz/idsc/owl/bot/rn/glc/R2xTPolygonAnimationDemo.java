// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.Arrays;

import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.bot.r2.CogPoints;
import ch.ethz.idsc.owl.bot.r2.R2ExamplePolygons;
import ch.ethz.idsc.owl.bot.r2.R2xTPolygonStateTimeRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.map.BijectionFamily;
import ch.ethz.idsc.owl.math.map.Se2Family;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

public class R2xTPolygonAnimationDemo implements DemoInterface {
  private static final Scalar DELAY = RealScalar.of(1.5);

  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(1.2, 2.2), RealScalar.ZERO));
    TrajectoryEntity abstractEntity = new R2xTEntity(episodeIntegrator, DELAY);
    owlyAnimationFrame.add(abstractEntity);
    // ---
    BijectionFamily rigid1 = new Se2Family( //
        scalar -> Tensors.of( //
            Cos.FUNCTION.apply(scalar.multiply(RealScalar.of(0.1))).multiply(RealScalar.of(2.0)), //
            Sin.FUNCTION.apply(scalar.multiply(RealScalar.of(0.1))).multiply(RealScalar.of(2.0)), //
            scalar.multiply(RealScalar.of(0.15))));
    Region<StateTime> region1 = new R2xTPolygonStateTimeRegion( //
        R2ExamplePolygons.CORNER_CENTERED, rigid1, () -> abstractEntity.getStateTimeNow().time());
    // ---
    BijectionFamily rigid2 = new Se2Family( //
        R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(5, 9, 4), 0.1, 2.0));
    Tensor polygon = CogPoints.of(4, RealScalar.of(1.5), RealScalar.of(0.5));
    Region<StateTime> region2 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid2, () -> abstractEntity.getStateTimeNow().time());
    PlannerConstraint plannerConstraint = //
        RegionConstraints.stateTime(RegionUnion.wrap(Arrays.asList(region1, region2)));
    MouseGoal.simple(owlyAnimationFrame, abstractEntity, plannerConstraint);
    owlyAnimationFrame.addBackground((RenderInterface) region1);
    owlyAnimationFrame.addBackground((RenderInterface) region2);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2xTPolygonAnimationDemo().start().jFrame.setVisible(true);
  }
}
