// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.Random;

import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.ExhaustiveNodeCollection;
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
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum Se2ExpandDemo {
  ;
  private static final Random RANDOM = new Random();

  public static void main(String[] args) throws Exception {
    int wid = 7;
    Tensor min = Tensors.vector(0, 0, 0);
    Tensor max = Tensors.vector(wid, wid, 2 * Math.PI);
    TransitionSpace transitionSpace = new Se2TransitionSpace<>(DubinsTransition.class, RealScalar.ONE);
    RrtsNodeCollection rrtsNodeCollection = ExhaustiveNodeCollection.of(transitionSpace);
    TransitionRegionQuery transitionRegionQuery = EmptyTransitionRegionQuery.INSTANCE;
    // ---
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0, 0), 5).get();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(min, max);
    try (AnimationWriter animationWriter = AnimationWriter.of(HomeDirectory.Pictures("se2rrts.gif"), 250)) {
      OwlyFrame owlyFrame = OwlyGui.start();
      owlyFrame.configCoordinateOffset(42, 456);
      owlyFrame.jFrame.setBounds(100, 100, 500, 500);
      // owlyFrame.geometricComponent.addRenderInterface(renderInterface);
      int frame = 0;
      while (frame++ < 40 && owlyFrame.jFrame.isVisible()) {
        for (int count = 0; count < 5; ++count)
          rrts.insertAsNode(randomSampleInterface.randomSample(RANDOM), 20);
        owlyFrame.setRrts(transitionSpace, root, transitionRegionQuery);
        animationWriter.append(owlyFrame.offscreen());
      }
      int repeatLast = 3;
      while (0 < repeatLast--)
        animationWriter.append(owlyFrame.offscreen());
    }
    System.out.println(rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.IDENTITY);
  }
}
