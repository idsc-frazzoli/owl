// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegions;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Se2xTPlainDemo
// implements DemoInterface
{
  // @Override
  public void start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    CarxTEntity carxTEntity = new CarxTEntity(new StateTime(Tensors.vector(6.75, 5.4, 1 + Math.PI), RealScalar.ZERO));
    owlyAnimationFrame.set(carxTEntity);
    // ---
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    carxTEntity.extraCosts.add(r2ImageRegionWrap.costFunction());
    ImageRegion imageRegion = r2ImageRegionWrap.imageRegion();
    Region<Tensor> se2PointsVsRegion = Se2PointsVsRegions.line(Tensors.vector(0.2, 0.1, 0, -0.1), imageRegion);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(se2PointsVsRegion);
    carxTEntity.plannerConstraint = plannerConstraint;
    MouseGoal.simple(owlyAnimationFrame, carxTEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    // ---
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    owlyAnimationFrame.jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    new Se2xTPlainDemo().start();
  }
}
