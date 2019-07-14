// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.data.TimeKeeper;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.math.lane.LaneConsumer;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.SimpleLaneConsumer;
import ch.ethz.idsc.owl.rrts.adapter.TransitionRegionQueryUnion;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.sim.CameraEmulator;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.sophus.app.curve.LaneConsumptionDemo;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;

class ClothoidRrtsLaneDemo implements DemoInterface {
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-90), Degree.of(90), 32), Subdivide.of(0, 5, 30));
  // ---
  private final LaneConsumptionDemo laneConsumptionDemo;

  public ClothoidRrtsLaneDemo() {
    super();
    ImageRegion imageRegion = R2ImageRegions._GTOB.imageRegion();
    TrajectoryRegionQuery trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    TransitionRegionQuery transitionRegionQuery = TransitionRegionQueryUnion.wrap( //
        new SampledTransitionRegionQuery(imageRegion, RealScalar.of(0.05)), //
        new TransitionCurvatureQuery(5.));
    StateTime stateTime = new StateTime(Tensors.vector(6, 5, Math.PI / 4), RealScalar.ZERO);
    ClothoidLaneRrtsEntity entity = new ClothoidLaneRrtsEntity(stateTime, transitionRegionQuery, imageRegion.origin(), imageRegion.range());
    LaneConsumer laneConsumer = new SimpleLaneConsumer(entity, null, Collections.singleton(entity));
    laneConsumptionDemo = new LaneConsumptionDemo(laneConsumer);
    laneConsumptionDemo.setControlPointsSe2(Tensors.of(stateTime.state()));
    laneConsumptionDemo.timerFrame.geometricComponent.addRenderInterfaceBackground(RegionRenders.create(imageRegion));
    laneConsumptionDemo.timerFrame.geometricComponent.addRenderInterface(entity);
    /** TODO rework; currently taken over from {@link OwlyAnimationFrame}
     * shorter variant, that does not close properly
     * OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
     * owlyAnimationFrame.add(entity); */
    {
      Timer timer = new Timer();
      { // periodic task for rendering
        TimerTask timerTask = new TimerTask() {
          @Override
          public void run() {
            laneConsumptionDemo.timerFrame.geometricComponent.jComponent.repaint();
          }
        };
        timer.schedule(timerTask, 100, 50);
      }
      laneConsumptionDemo.timerFrame.jFrame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent windowEvent) {
          timer.cancel();
        }
      });
      { // periodic task for integration
        TimerTask timerTask = new TimerTask() {
          TimeKeeper timeKeeper = new TimeKeeper();

          @Override
          public void run() {
            Scalar now = timeKeeper.now();
            entity.integrate(now);
          }
        };
        timer.schedule(timerTask, 100, 20);
      }
    }
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), entity::getStateTimeNow, trajectoryRegionQuery);
      laneConsumptionDemo.timerFrame.geometricComponent.addRenderInterfaceBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, entity::getStateTimeNow, trajectoryRegionQuery);
      laneConsumptionDemo.timerFrame.geometricComponent.addRenderInterfaceBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(Se2PointsVsRegions.line(Tensors.vector(0.2, 0.1, 0, -0.1), imageRegion)), //
          ClothoidRrtsEntity.SHAPE, () -> entity.getStateTimeNow().time());
      laneConsumptionDemo.timerFrame.geometricComponent.addRenderInterfaceBackground(renderInterface);
    }
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    BaseFrame baseFrame = laneConsumptionDemo.start();
    baseFrame.configCoordinateOffset(50, 700);
    baseFrame.jFrame.setBounds(100, 100, 1200, 900);
    baseFrame.jFrame.setTitle(getClass().getSimpleName());
    return baseFrame;
  }

  public static void main(String[] args) {
    new ClothoidRrtsLaneDemo().start().jFrame.setVisible(true);
  }
}
