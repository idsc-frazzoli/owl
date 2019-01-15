// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import java.util.Random;

import ch.ethz.idsc.owl.bot.rn.RnRrtsNodeCollection;
import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.sample.SphereRandomSample;
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

/* package */ enum R2NoiseDemo {
  ;
  private static final Random RANDOM = new Random();

  public static void main(String[] args) {
    Tensor min = Tensors.vector(-1, -3);
    Tensor max = Tensors.vector(-1 + 6, -3 + 6);
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(min, max);
    TransitionRegionQuery transitionRegionQuery = StaticHelper.noise1();
    // ---
    TransitionSpace transitionSpace = RnTransitionSpace.INSTANCE;
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.IDENTITY);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 5).get();
    RandomSampleInterface randomSampleInterface = //
        SphereRandomSample.of(Tensors.vector(2, 0), RealScalar.of(3));
    for (int c = 0; c < 1000; ++c)
      rrts.insertAsNode(randomSampleInterface.randomSample(RANDOM), 15);
    System.out.println("rewireCount=" + rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.IDENTITY);
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.configCoordinateOffset(122, 226);
    owlyFrame.jFrame.setBounds(100, 100, 500, 500);
    owlyFrame.setRrts(transitionSpace, root, transitionRegionQuery);
  }
}
