// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.Optional;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.adapter.AbstractTransitionSpace;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSamplesWrap;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparator;
import ch.ethz.idsc.sophus.crv.dubins.FixedRadiusDubins;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public class DubinsTransitionSpace extends AbstractTransitionSpace implements Se2TransitionSpace {
  private static final DubinsTransitionSpace INSTANCE = new DubinsTransitionSpace();

  public static DubinsTransitionSpace withRadius(Scalar radius) {
    DubinsTransitionSpace.INSTANCE.radius = radius;
    return DubinsTransitionSpace.INSTANCE;
  }
  // ---
  private Scalar radius;

  private DubinsTransitionSpace() {

  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    return new AbstractTransition(this, start, end) {
      DubinsPath dubinsPath = FixedRadiusDubins.of(start, end, radius).allValid().min(DubinsPathComparator.length()).get();

      @Override // from Transition
      public TransitionSamplesWrap sampled(int steps) {
        if (steps < 1)
          throw TensorRuntimeException.of(RealScalar.of(steps));
        Tensor samples = Array.zeros(steps);
        Tensor spacing = Array.zeros(steps);
        Scalar step = dubinsPath.length().divide(RealScalar.of(steps));
        ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(start());
        IntStream.range(0, steps).parallel().forEach(i -> {
          samples.set(scalarTensorFunction.apply(step.multiply(RealScalar.of(i))), i);
          spacing.set(i > 0 ? step : step.map(Scalar::zero), i);
        });
        return new TransitionSamplesWrap(samples, spacing);
      }
    };
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return dubinsPath(start, end).map(DubinsPath::length).get();
  }

  /** @param start
   * @param end
   * @return dubins path with minimal length if any */
  public Optional<DubinsPath> dubinsPath(Tensor start, Tensor end) {
    return FixedRadiusDubins.of(start, end, radius).allValid().min(DubinsPathComparator.length());
  }
}
