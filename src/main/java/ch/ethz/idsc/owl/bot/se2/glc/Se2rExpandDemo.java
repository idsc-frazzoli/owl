// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.GifAnimationWriter;

/** (x, y, theta) */
enum Se2rExpandDemo {
  ;
  public static void main(String[] args) throws Exception {
    TrajectoryPlanner trajectoryPlanner = Se2rAnimateDemo.trajectoryPlanner();
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(3), RealScalar.ZERO));
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.geometricComponent.setOffset(169, 71);
    owlyFrame.jFrame.setBounds(100, 100, 300, 200);
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures("se2r.gif"), 250, TimeUnit.MILLISECONDS)) {
      GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
      while (!trajectoryPlanner.getBest().isPresent() && owlyFrame.jFrame.isVisible()) {
        glcExpand.findAny(1);
        owlyFrame.setGlc(trajectoryPlanner);
        animationWriter.write(owlyFrame.offscreen());
        Thread.sleep(10);
      }
      int repeatLast = 6;
      while (0 < repeatLast--)
        animationWriter.write(owlyFrame.offscreen());
    }
    System.out.println("created gif");
  }
}
