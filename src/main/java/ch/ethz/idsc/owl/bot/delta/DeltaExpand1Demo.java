// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.GifAnimationWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/** simple animation of small boat driving upstream, or downstream in a river delta
 * 
 * records to animated gif */
/* package */ enum DeltaExpand1Demo {
  ;
  public static void main(String[] args) throws Exception {
    DeltaExample deltaDemo = new DeltaExample(RealScalar.of(0.5));
    // ---
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.addBackground(RegionRenders.create(DeltaExample.REGION));
    owlyFrame.addBackground(RegionRenders.create(DeltaExample.SPHERICAL_REGION));
    // owlyFrame.addBackground(RenderElements.create(plannerConstraint));
    owlyFrame.addBackground(deltaDemo.vf(0.05));
    owlyFrame.configCoordinateOffset(33, 416);
    owlyFrame.jFrame.setBounds(100, 100, 620, 475);
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures("delta_s.gif"), 250, TimeUnit.MILLISECONDS)) {
      GlcExpand glcExpand = new GlcExpand(deltaDemo.trajectoryPlanner);
      while (!deltaDemo.trajectoryPlanner.getBest().isPresent() && owlyFrame.jFrame.isVisible()) {
        glcExpand.findAny(40);
        owlyFrame.setGlc(deltaDemo.trajectoryPlanner);
        animationWriter.write(owlyFrame.offscreen());
        Thread.sleep(1);
      }
      int repeatLast = 6;
      while (0 < repeatLast--)
        animationWriter.write(owlyFrame.offscreen());
    }
    System.out.println("created gif");
  }
}
