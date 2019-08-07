// code by ynager
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Tse2Car0Demo extends Tse2CarDemo {
  @Override
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    Tse2CarEntity tse2CarEntity = Tse2CarEntity.createDefault(new StateTime(Tensors.vector(6, 5, 1, 0), RealScalar.ZERO));
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    Region<Tensor> region = r2ImageRegionWrap.imageRegion();
    PlannerConstraint plannerConstraint = createConstraint(region);
    owlyAnimationFrame.add(tse2CarEntity);
    // LONGTERM add option to select goal velocity / range
    MouseGoal.simple(owlyAnimationFrame, tse2CarEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(line(region)), //
          Tse2CarEntity.SHAPE, () -> tse2CarEntity.getStateTimeNow().time());
      owlyAnimationFrame.addBackground(renderInterface);
    }
  }

  public static void main(String[] args) {
    new Tse2Car0Demo().start().jFrame.setVisible(true);
  }
}
