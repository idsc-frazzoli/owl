// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

public class Se2Letter2Demo extends Se2CarDemo {
  @Override
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    CarEntity carEntity = CarEntity.createDefault(new StateTime(Tensors.vector(6, 6, 1), RealScalar.ZERO));
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    ImageRegion imageRegion = r2ImageRegionWrap.imageRegion();
    PlannerConstraint plannerConstraint = createConstraint(imageRegion);
    carEntity.plannerConstraint = plannerConstraint;
    owlyAnimationFrame.add(carEntity);
    MouseGoal.simple(owlyAnimationFrame, carEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
  }

  public static void main(String[] args) {
    new Se2Letter2Demo().start().jFrame.setVisible(true);
  }
}
