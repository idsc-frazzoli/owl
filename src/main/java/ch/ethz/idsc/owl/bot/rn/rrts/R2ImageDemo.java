// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.rn.RnRrtsNodeCollection;
import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.adapter.RrtsNodes;
import ch.ethz.idsc.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum R2ImageDemo {
  ;
  private static final TransitionSpace TRANSITION_SPACE = RnTransitionSpace.INSTANCE;

  public static void main(String[] args) throws Exception {
    ImageRegion imageRegion = //
        ImageRegions.loadFromRepository("/io/track0_100.png", Tensors.vector(7, 7), false);
    RrtsNodeCollection nc = new RnRrtsNodeCollection(imageRegion.origin(), imageRegion.range());
    TransitionRegionQuery trq = new SampledTransitionRegionQuery( //
        CatchyTrajectoryRegionQuery.timeInvariant(imageRegion), RealScalar.of(0.1));
    // ---
    Rrts rrts = new DefaultRrts(TRANSITION_SPACE, nc, trq, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 5).get();
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.configCoordinateOffset(60, 477);
    owlyFrame.jFrame.setBounds(100, 100, 550, 550);
    owlyFrame.addBackground(RegionRenders.create(imageRegion));
    BoxRandomSample rnUniformSampler = new BoxRandomSample(imageRegion.origin(), imageRegion.range());
    int frame = 0;
    while (frame++ < 20 && owlyFrame.jFrame.isVisible()) {
      for (int c = 0; c < 50; ++c)
        rrts.insertAsNode(rnUniformSampler.randomSample(), 15);
      owlyFrame.setRrts(root, trq);
      Thread.sleep(10);
    }
    System.out.println(rrts.rewireCount());
    RrtsNodes.costConsistency(root, TRANSITION_SPACE, LengthCostFunction.IDENTITY);
  }
}
