// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

public class Se2Letter1Demo extends Se2CarDemo {
  @Override
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    CarEntity carEntity = CarEntity.createDefault(new StateTime(Tensors.vector(10, 5, 1), RealScalar.ZERO));
    ImageRegion imageRegion = R2ImageRegions.inside_0f5c();
    PlannerConstraint plannerConstraint = createConstraint(imageRegion);
    owlyAnimationFrame.add(carEntity);
    MouseGoal.simple(owlyAnimationFrame, carEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(line(imageRegion)), //
          CarEntity.SHAPE, () -> carEntity.getStateTimeNow().time());
      owlyAnimationFrame.addBackground(renderInterface);
    }
  }

  public static void main(String[] args) {
    new Se2Letter1Demo().start().jFrame.setVisible(true);
  }
}
