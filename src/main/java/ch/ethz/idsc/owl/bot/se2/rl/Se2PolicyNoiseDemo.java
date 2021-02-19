// code by jph
package ch.ethz.idsc.owl.bot.se2.rl;

import ch.ethz.idsc.owl.bot.r2.R2NoiseRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.subare.core.td.SarsaType;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Se2PolicyNoiseDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    // ---
    final Scalar threshold = RealScalar.of(0.6);
    Region<Tensor> region = new R2NoiseRegion(threshold);
    // ---
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeInvariant(region);
    owlyAnimationFrame.addBackground(RegionRenders.create(trq));
    // ---
    Tensor start = Tensors.vector(2.000, 3.317, 0.942).unmodifiable();
    owlyAnimationFrame.add(new CarPolicyEntity(start, SarsaType.QLEARNING, trq));
    owlyAnimationFrame.add(new CarPolicyEntity(start, SarsaType.EXPECTED, trq));
    owlyAnimationFrame.add(new CarPolicyEntity(start, SarsaType.ORIGINAL, trq));
    // ---
    owlyAnimationFrame.geometricComponent.setOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new Se2PolicyNoiseDemo().start().jFrame.setVisible(true);
  }
}
