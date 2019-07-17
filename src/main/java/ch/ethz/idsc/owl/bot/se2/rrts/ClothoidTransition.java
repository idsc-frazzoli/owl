// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid1;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
import ch.ethz.idsc.sophus.crv.clothoid.PseudoClothoidDistance;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;

public class ClothoidTransition extends AbstractTransition {
  private static final int MAX_ITER = 8;
  static final TensorMetric TENSOR_METRIC = PseudoClothoidDistance.INSTANCE;
  private static final CurveSubdivision CURVE_SUBDIVISION = new LaneRiesenfeldCurveSubdivision(Clothoid1.INSTANCE, 1);

  public ClothoidTransition(Tensor start, Tensor end) {
    super(start, end, TENSOR_METRIC.distance(start, end));
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    Tensor samples = Tensors.of(start(), end());
    // TODO GJOEL/JPH implementation inefficient
    int iter = 0;
    while (iter < MAX_ITER) {
      boolean sufficient = Differences.of(samples).stream() //
          .map(Extract2D.FUNCTION) //
          .map(Norm._2::ofVector) //
          .allMatch(scalar -> Scalars.lessThan(scalar, minResolution));
      if (sufficient)
        break;
      samples = CURVE_SUBDIVISION.string(samples);
      ++iter;
    }
    return samples.extract(0, samples.length() - 1);
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    int steps = Ceiling.FUNCTION.apply(length().divide(minResolution)).number().intValue();
    Tensor samples = sampled(length().divide(RealScalar.of(steps)));
    Tensor spacing = Array.zeros(samples.length());
    IntStream.range(0, samples.length()).forEach(i -> spacing.set(i > 0 //
        ? TENSOR_METRIC.distance(samples.get(i - 1), samples.get(i)) //
        : samples.Get(i, 0).zero(), i));
    return new TransitionWrap(samples, spacing);
  }

  public ClothoidTerminalRatios terminalRatios() {
    return ClothoidTerminalRatios.of(start(), end());
  }

  @Override // from Transition
  public Tensor linearized(Scalar minResolution) {
    return sampled(minResolution).copy().append(end());
  }
}
