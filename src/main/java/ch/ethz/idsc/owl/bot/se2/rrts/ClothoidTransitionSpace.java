// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
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
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;

public class ClothoidTransitionSpace implements Se2TransitionSpace, Serializable {
  public static final TransitionSpace INSTANCE = new ClothoidTransitionSpace();
  // ---
  private static final CurveSubdivision CURVE_SUBDIVISION = new LaneRiesenfeldCurveSubdivision(Clothoid1.INSTANCE, 1);
  private static final double LOG2 = Math.log(2);

  private ClothoidTransitionSpace() {
    // ---
  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    Scalar length = PseudoClothoidDistance.INSTANCE.distance(start, end);
    return new AbstractTransition(start, end, length) {
      @Override // from Transition
      public Tensor sampled(Scalar minResolution) {
        Tensor samples = Tensors.of(start, end);
        while (Differences.of(samples).stream().parallel().map(Extract2D.FUNCTION).map(Norm._2::ofVector).anyMatch(s -> Scalars.lessThan(minResolution, s)))
          samples = CURVE_SUBDIVISION.string(samples);
        return samples.extract(0, samples.length() - 1);
      }

      @Override // from Transition
      public Tensor sampled(int steps) {
        if (steps < 1)
          throw TensorRuntimeException.of(RealScalar.of(steps));
        Tensor samples = Nest.of(CURVE_SUBDIVISION::string, Tensors.of(start, end), (int) Math.ceil(Math.log(steps - 1) / LOG2));
        return samples.extract(0, samples.length() - 1);
      }
    };
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return PseudoClothoidDistance.INSTANCE.distance(start, end);
  }
}
