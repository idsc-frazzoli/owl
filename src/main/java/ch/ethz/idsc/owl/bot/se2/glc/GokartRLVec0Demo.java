// code by astoll
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.adapter.GoalConsumer;
import ch.ethz.idsc.owl.glc.adapter.SimpleGlcPlannerCallback;
import ch.ethz.idsc.owl.glc.adapter.SimpleGoalConsumer;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.region.PolygonRegionRender;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.PolygonRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class GokartRLVec0Demo extends GokartDemo {
  private static final Tensor MODEL2PIXEL = Tensors.matrixDouble(new double[][] { { 7.5, 0, 0 }, { 0, -7.5, 640 }, { 0, 0, 1 } });

  @Override
  protected void configure(OwlyAnimationFrame owlyAnimationFrame) {
    // initial state time
    final StateTime initial = new StateTime(Tensors.vector(0, 10, 0), RealScalar.ZERO);
    // goal
    Tensor goal = Tensors.vector(25, 10, 0);
    // slacks
    Tensor slacks = Tensors.vector(0, 0);
    // set up relaxed gokart entity
    GokartRelaxedEntity gokartEntity = GokartRelaxedEntity.createRelaxedGokartEntity(initial, slacks);
    // ---
    PlannerConstraint plannerConstraint = EmptyObstacleConstraint.INSTANCE;
    // ---
    owlyAnimationFrame.add(gokartEntity);
    owlyAnimationFrame.geometricComponent.setModel2Pixel(MODEL2PIXEL);
    Tensor polygon = Tensors.matrixFloat(new float[][] { { 3, 10 }, { 3, 0 }, { 10, 0 }, { 10, 15 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    owlyAnimationFrame.addBackground(new PolygonRegionRender(polygonRegion));
    // ---
    List<GlcPlannerCallback> list = new ArrayList<>();
    list.add(gokartEntity);
    list.add(new SimpleGlcPlannerCallback(gokartEntity));
    GoalConsumer goalconsumer = new SimpleGoalConsumer(gokartEntity, plannerConstraint, list);
    goalconsumer.accept(goal);
    MouseGoal.simple(owlyAnimationFrame, gokartEntity, plannerConstraint);
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(polygonRegion), //
          CarEntity.SHAPE, () -> gokartEntity.getStateTimeNow().time());
      owlyAnimationFrame.addBackground(renderInterface);
    }
  }

  public static void main(String[] args) {
    new GokartRLVec0Demo().start().jFrame.setVisible(true);
  }
}
