// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.adapter.RrtsNodes;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.AnimationWriter;

/* package */ enum Se2ExpandDemo {
  ;
  public static void main(String[] args) throws Exception {
    int wid = 7;
    Tensor min = Tensors.vector(0, 0, 0);
    Tensor max = Tensors.vector(wid, wid, 2 * Math.PI);
    RrtsNodeCollection nc = new Se2NodeCollection(min, max);
    TransitionRegionQuery trq = EmptyTransitionRegionQuery.INSTANCE;
    // ---
    TransitionSpace transitionSpace = new Se2TransitionSpace(RealScalar.ONE);
    Rrts rrts = new DefaultRrts(transitionSpace, nc, trq, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0, 0), 5).get();
    BoxRandomSample rnUniformSampler = new BoxRandomSample(min, max);
    try (AnimationWriter gsw = AnimationWriter.of(UserHome.Pictures("se2rrts.gif"), 250)) {
      OwlyFrame owlyFrame = OwlyGui.start();
      owlyFrame.configCoordinateOffset(42, 456);
      owlyFrame.jFrame.setBounds(100, 100, 500, 500);
      int frame = 0;
      while (frame++ < 40 && owlyFrame.jFrame.isVisible()) {
        for (int c = 0; c < 10; ++c)
          rrts.insertAsNode(rnUniformSampler.randomSample(), 20);
        owlyFrame.setRrts(root, trq);
        gsw.append(owlyFrame.offscreen());
        Thread.sleep(100);
      }
      int repeatLast = 3;
      while (0 < repeatLast--)
        gsw.append(owlyFrame.offscreen());
    }
    System.out.println(rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.IDENTITY);
  }
}
