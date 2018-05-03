// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.Arrays;

import ch.ethz.idsc.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.SimpleTranslationFamily;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.map.BijectionFamily;
import ch.ethz.idsc.owl.math.map.Se2Family;
import ch.ethz.idsc.owl.math.noise.NativeContinuousNoise;
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
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

public class R2xTEllipsoidsAnimationDemo implements DemoInterface {
  public static ScalarTensorFunction wrap1DTensor(NativeContinuousNoise nativeContinuousNoise, Tensor offset, double period, double amplitude) {
    return scalar -> Tensor.of(offset.stream().map(Scalar.class::cast)
        .map(value -> RealScalar.of(amplitude * nativeContinuousNoise.at(scalar.number().doubleValue() * period, value.number().doubleValue()))));
  }

  @SuppressWarnings("unused")
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(1.2, 2), RealScalar.ZERO));
    TrajectoryEntity abstractEntity = new R2xTEntity(episodeIntegrator, RealScalar.of(0.6));
    owlyAnimationFrame.set(abstractEntity);
    // ---
    BijectionFamily shiftx = new SimpleTranslationFamily( //
        scalar -> Tensors.of(Sin.FUNCTION.apply(scalar.multiply(RealScalar.of(0.2))), RealScalar.ZERO));
    BijectionFamily shifty = new SimpleTranslationFamily( //
        scalar -> Tensors.of(RealScalar.ZERO, //
            Cos.FUNCTION.apply(scalar.multiply(RealScalar.of(0.27)).multiply(RealScalar.of(2)))));
    BijectionFamily circle = new SimpleTranslationFamily( //
        scalar -> AngleVector.of(scalar.multiply(RealScalar.of(0.2))).multiply(RealScalar.of(2)));
    BijectionFamily noise = new SimpleTranslationFamily( //
        R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(0, 2), 0.1, 1.3));
    BijectionFamily rigidm = new Se2Family( //
        R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(5, 9, 4), 0.1, 2.0));
    // ---
    Region<StateTime> region1 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.7, 0.9), circle, () -> abstractEntity.getStateTimeNow().time());
    Region<StateTime> region2 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.8, 0.5), rigidm, () -> abstractEntity.getStateTimeNow().time());
    Region<StateTime> region3 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.6, 0.6), noise, () -> abstractEntity.getStateTimeNow().time());
    Region<StateTime> union = RegionUnion.wrap(Arrays.asList(region1, region2, region3));
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(new SimpleTrajectoryRegionQuery(union));
    MouseGoal.simple(owlyAnimationFrame, abstractEntity, plannerConstraint);
    owlyAnimationFrame.addBackground((RenderInterface) region1);
    owlyAnimationFrame.addBackground((RenderInterface) region2);
    owlyAnimationFrame.addBackground((RenderInterface) region3);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2xTEllipsoidsAnimationDemo().start().jFrame.setVisible(true);
  }
}
