// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import java.util.Random;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.rrts.RrtsNodeCollections;
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
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum R2InsideCharDemo {
  ;
  private static final Random RANDOM = new Random();

  private static void explore(ImageRegion imageRegion, Tensor start) throws Exception {
    RrtsNodeCollection rrtsNodeCollection = new RrtsNodeCollections(RnRrtsNdType.INSTANCE, imageRegion.origin(), imageRegion.range());
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery(imageRegion, RealScalar.of(0.1));
    // ---
    TransitionSpace transitionSpace = RnTransitionSpace.INSTANCE;
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(start, 5).get();
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.configCoordinateOffset(60, 477);
    owlyFrame.jFrame.setBounds(100, 100, 650, 550);
    owlyFrame.addBackground(RegionRenders.create(imageRegion));
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(imageRegion.origin(), imageRegion.range());
    int frame = 0;
    while (frame++ < 20 && owlyFrame.jFrame.isVisible()) {
      for (int count = 0; count < 50; ++count)
        rrts.insertAsNode(randomSampleInterface.randomSample(RANDOM), 15);
      owlyFrame.setRrts(transitionSpace, root, transitionRegionQuery);
      Thread.sleep(10);
    }
    System.out.println(rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.IDENTITY);
  }

  public static void _0b36() throws Exception {
    explore(R2ImageRegions.inside_0b36(), Tensors.vector(1.8, 2.7));
  }

  public static void _265b() throws Exception {
    explore(R2ImageRegions.inside_265b(), Tensors.vector(1.833, 2.5));
  }

  public static void _2182() throws Exception {
    explore(R2ImageRegions.inside_2182(), Tensors.vector(4.5, 3));
  }

  public static void main(String[] args) throws Exception {
    _0b36();
  }
}
