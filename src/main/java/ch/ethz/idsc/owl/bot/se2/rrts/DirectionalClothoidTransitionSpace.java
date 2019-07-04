// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid1;
import ch.ethz.idsc.sophus.crv.clothoid.PseudoClothoidDistance;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.PadLeft;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;

public class DirectionalClothoidTransitionSpace implements Se2TransitionSpace, Serializable {
  public static final TransitionSpace INSTANCE = new DirectionalClothoidTransitionSpace();
  private DirectionalClothoidTransitionSpace() {
    // ---
  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    Tensor lengths = Tensors.of(PseudoClothoidDistance.INSTANCE.distance(start, end), //
        PseudoClothoidDistance.INSTANCE.distance(end, start));
    int index = ArgMin.of(lengths);
    return new AbstractTransition(start, end, lengths.Get(index)) {
      final boolean isForward = index == 0;
      final Transition transition = isForward //
          ? ClothoidTransitionSpace.INSTANCE.connect(start, end) //
          : ClothoidTransitionSpace.INSTANCE.connect(end, start);

      @Override // from Transition
      public Tensor sampled(Scalar minResolution) {
        return swap(transition.sampled(minResolution));
      }

      @Override // from Transition
      public Tensor sampled(int steps) {
        return swap(transition.sampled(steps));
      }

      private Tensor swap(Tensor samples) {
        if (isForward)
          return samples;
        return PadLeft.with(start, samples.length()).apply(Reverse.of(samples.extract(1, samples.length())));
      }

      @Override // from Transition
      public TransitionWrap wrapped(int steps) {
        if (steps < 1)
          throw TensorRuntimeException.of(length(), RealScalar.of(steps));
        Tensor samples = sampled(steps);
        Tensor spacing = Array.zeros(samples.length());
        IntStream.range(0, samples.length()).parallel().forEach(i -> spacing.set(i > 0 //
            ? distance(samples.get(i - 1), samples.get(i)) //
            : samples.Get(i, 0).zero(), i));
        return new TransitionWrap(samples, spacing);
      }
    };
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return Min.of(PseudoClothoidDistance.INSTANCE.distance(start, end), //
        PseudoClothoidDistance.INSTANCE.distance(end, start));
  }
}
