// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ abstract class Se2CarDemo implements DemoInterface {
  private static final Tensor PROBE_X = Tensors.vector(0.2, 0.1, 0, -0.1);

  static Region<Tensor> line(Region<Tensor> region) {
    return Se2PointsVsRegions.line(PROBE_X, region);
  }

  static PlannerConstraint createConstraint(Region<Tensor> region) {
    return RegionConstraints.timeInvariant(line(region));
  }

  abstract void configure(OwlyAnimationFrame owlyAnimationFrame);

  @Override
  public final OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    configure(owlyAnimationFrame);
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }
}
