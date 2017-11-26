// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.bot.rn.R2xTNoiseStateTimeRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.ani.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

// TODO the visualization of the demo is poor
public class R2xTNoiseAnimationDemo implements DemoInterface {
  @Override
  public void start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.set(new R2xTEntity(Tensors.vector(0.2, 0.2), RealScalar.of(0.4)));
    Region<StateTime> region = new R2xTNoiseStateTimeRegion(RealScalar.of(0.5));
    owlyAnimationFrame.setObstacleQuery(new SimpleTrajectoryRegionQuery(region));
    owlyAnimationFrame.jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    new R2xTNoiseAnimationDemo().start();
  }
}
