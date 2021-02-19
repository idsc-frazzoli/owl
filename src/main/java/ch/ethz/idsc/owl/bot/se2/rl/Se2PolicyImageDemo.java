// code by jph
package ch.ethz.idsc.owl.bot.se2.rl;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.subare.core.td.SarsaType;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Se2PolicyImageDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    // ---
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._2181;
    Region<Tensor> region = r2ImageRegionWrap.region();
    // ---
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeInvariant(region);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    owlyAnimationFrame.addBackground(RegionRenders.create(trq));
    // ---
    {
      CarPolicyEntity carPolicyEntity = //
          new CarPolicyEntity(Tensors.vector(2.383, 2.567, 1.571), SarsaType.QLEARNING, trq);
      owlyAnimationFrame.add(carPolicyEntity);
    }
    {
      CarPolicyEntity twdPolicyEntity = //
          new CarPolicyEntity(Tensors.vector(1.3, 2.8, 1.57), SarsaType.EXPECTED, trq);
      owlyAnimationFrame.add(twdPolicyEntity);
    }
    // ---
    owlyAnimationFrame.geometricComponent.setOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new Se2PolicyImageDemo().start().jFrame.setVisible(true);
  }
}
