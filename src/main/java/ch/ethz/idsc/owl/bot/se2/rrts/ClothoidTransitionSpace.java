// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.adapter.AbstractTransitionSpace;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSamplesWrap;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidCurve;
import ch.ethz.idsc.sophus.crv.clothoid.PseudoClothoidDistance;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Nest;

public class ClothoidTransitionSpace extends AbstractTransitionSpace implements Se2TransitionSpace {
  public static final TransitionSpace INSTANCE = new ClothoidTransitionSpace();
  public static final CurveSubdivision SUBDIVISION = new LaneRiesenfeldCurveSubdivision(ClothoidCurve.INSTANCE, 1);
  private static final double LOG2 = Math.log(2);

  private ClothoidTransitionSpace() {
    // ---
  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    return new AbstractTransition(this, start, end) {
      @Override // from Transition
      public TransitionSamplesWrap sampled(Scalar minResolution) {
        Tensor samples = Tensors.of(start, end);
        TransitionSamplesWrap wrap = wrap(samples);
        while (wrap.spacing().stream().map(Tensor::Get).anyMatch(s -> Scalars.lessThan(minResolution, s))) {
          samples = SUBDIVISION.string(samples);
          wrap = wrap(samples.extract(0, samples.length() - 1));
        }
        return wrap;
      }

      @Override // from Transition
      public TransitionSamplesWrap sampled(int steps) {
        if (steps < 1)
          throw TensorRuntimeException.of(RealScalar.of(steps));
        Tensor samples = Nest.of(SUBDIVISION::string, Tensors.of(start, end), (int) Math.ceil(Math.log(steps - 1) / LOG2));
        return wrap(samples.extract(0, samples.length() - 1));
      }

      private TransitionSamplesWrap wrap(Tensor samples) {
        Tensor spacing = Array.zeros(samples.length());
        IntStream.range(0, samples.length()).parallel().forEach(i -> spacing.set(i > 0 //
            ? PseudoClothoidDistance.INSTANCE.distance(samples.get(i - 1), samples.get(i)) //
            : samples.Get(i, 0).zero(), i));
        return new TransitionSamplesWrap(samples, spacing);
      }
    };
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return PseudoClothoidDistance.INSTANCE.distance(start, end);
  }
}
