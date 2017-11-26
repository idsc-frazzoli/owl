// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.util.Arrays;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.bot.util.TrajectoryTranslationFamily;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ani.AbstractEntity;
import ch.ethz.idsc.owl.gui.ani.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;

public class DeltaxTAnimationDemo implements DemoInterface {
  @Override
  public void start() {
    Tensor image = ResourceData.of("/io/delta_uxy.png");
    Tensor range = Tensors.vector(12.6, 9.1).unmodifiable(); // overall size of map
    Scalar amp = RealScalar.of(-.05); // direction and strength of river flow
    // ---
    ImageGradient imageGradient_fast = ImageGradient.linear(image, range, amp);
    AbstractEntity abstractEntity = new DeltaxTEntity(imageGradient_fast, Tensors.vector(10, 3.5));
    Supplier<Scalar> supplier = () -> abstractEntity.getStateTimeNow().time();
    // ---
    ImageGradient imageGradient_slow = ImageGradient.nearest(image, range, amp);
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradient_slow);
    Flow flow = StateSpaceModels.createFlow(stateSpaceModel, DeltaEntity.FALLBACK_CONTROL);
    Region<StateTime> region1 = create(RealScalar.of(0.4), Tensors.vector(2, 1.5), flow, supplier);
    Region<StateTime> region2 = create(RealScalar.of(0.5), Tensors.vector(6, 6), flow, supplier);
    Region<StateTime> region3 = create(RealScalar.of(0.3), Tensors.vector(2, 7), flow, supplier);
    Region<StateTime> region4 = create(RealScalar.of(0.3), Tensors.vector(1, 8), flow, supplier);
    // ---
    Tensor obstacleImage = ResourceData.of("/io/delta_free.png");
    ImageRegion imageRegion = new ImageRegion(obstacleImage, range, true);
    TrajectoryRegionQuery obstacleQuery = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(Arrays.asList(new TimeInvariantRegion(imageRegion), region1, region2, region3, region4)));
    // ---
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.set(abstractEntity);
    owlyAnimationFrame.setObstacleQuery(obstacleQuery);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    owlyAnimationFrame.addBackground((RenderInterface) region1);
    owlyAnimationFrame.addBackground((RenderInterface) region2);
    owlyAnimationFrame.addBackground((RenderInterface) region3);
    owlyAnimationFrame.addBackground((RenderInterface) region4);
    owlyAnimationFrame.addBackground(DeltaHelper.vectorFieldRender(stateSpaceModel, range, imageRegion, RealScalar.of(0.5)));
    owlyAnimationFrame.jFrame.setVisible(true);
    owlyAnimationFrame.configCoordinateOffset(50, 600);
  }

  private static Region<StateTime> create(Scalar radius, Tensor pos, Flow flow, Supplier<Scalar> supplier) {
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, RationalScalar.of(1, 10), 120 * 10);
    return new R2xTEllipsoidStateTimeRegion(Tensors.of(radius, radius), //
        TrajectoryTranslationFamily.create(stateIntegrator, new StateTime(pos, RealScalar.ZERO), flow), //
        supplier);
  }

  public static void main(String[] args) throws Exception {
    new DeltaxTAnimationDemo().start();
  }
}
