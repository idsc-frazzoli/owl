// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.owl.glc.adapter.ConstraintViolationCost;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.adapter.SimpleGlcPlannerCallback;
import ch.ethz.idsc.owl.glc.adapter.SimpleGoalConsumer;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalConsumer;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.region.PolygonRegionRender;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.planar.ConeRegion;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** demo to show effect of lexocographic vector cost comparison
 * 1. Cost: Time
 * 2. Cost: Polygon region penalty */
public class GokartVec0Demo extends GokartDemo {
  private static final Tensor MODEL2PIXEL = Tensors.matrixDouble(new double[][] { { 7.5, 0, 0 }, { 0, -7.5, 640 }, { 0, 0, 1 } });

  @Override
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    final StateTime initial = new StateTime(Tensors.vector(0, 10, 0), RealScalar.ZERO);
    GokartVecEntity gokartEntity = new GokartVecEntity(initial) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, RealScalar.of(Math.PI / 10));
      }
    };
    // define cost funcion hierarchy
    Tensor polygon = Tensors.matrixFloat(new float[][] { { 7, 10 }, { 10, 0 }, { 13, 10 }, { 10, 20 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint regionConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCost = ConstraintViolationCost.of(regionConstraint);
    gokartEntity.setCostVector(Arrays.asList(regionCost), Arrays.asList(0.0));
    gokartEntity.addTimeCost(0, 0.2);
    // ---
    PlannerConstraint plannerConstraint = EmptyObstacleConstraint.INSTANCE;
    // ---
    owlyAnimationFrame.add(gokartEntity);
    owlyAnimationFrame.geometricComponent.setModel2Pixel(MODEL2PIXEL);
    owlyAnimationFrame.addBackground(new PolygonRegionRender(polygonRegion));
    // ---
    List<GlcPlannerCallback> list = new ArrayList<>();
    list.add((GlcPlannerCallback) gokartEntity);
    list.add(new SimpleGlcPlannerCallback(gokartEntity));
    GoalConsumer goalconsumer = new SimpleGoalConsumer(gokartEntity, plannerConstraint, list);
    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
    }
    goalconsumer.accept(Tensors.vector(20, 10, 0));
  }

  public static void main(String[] args) {
    new GokartVec0Demo().start().jFrame.setVisible(true);
  }
}