// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.stream.IntStream;

import ch.ethz.idsc.owl.math.IntegerLog2;
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
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;

public class ClothoidTransition extends AbstractTransition {
  static final TensorMetric TENSOR_METRIC = PseudoClothoidDistance.INSTANCE;
  private static final CurveSubdivision CURVE_SUBDIVISION = new LaneRiesenfeldCurveSubdivision(Clothoid1.INSTANCE, 1);

  public ClothoidTransition(Tensor start, Tensor end) {
    super(start, end, TENSOR_METRIC.distance(start, end));
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    Tensor samples = Tensors.of(start(), end());
    // TODO implementation inefficient
    while (Differences.of(samples).stream().map(Extract2D.FUNCTION).map(Norm._2::ofVector).anyMatch(s -> Scalars.lessThan(minResolution, s)))
      samples = CURVE_SUBDIVISION.string(samples);
    return samples.extract(0, samples.length() - 1);
  }

  @Override // from Transition
  public Tensor sampled(int steps) {
    if (steps < 1)
      throw TensorRuntimeException.of(length(), RealScalar.of(steps));
    Tensor samples = Nest.of(CURVE_SUBDIVISION::string, Tensors.of(start(), end()), IntegerLog2.ceil(steps));
    return samples.extract(0, samples.length() - 1);
  }

  @Override // from Transition
  public TransitionWrap wrapped(int steps) {
    Tensor samples = sampled(steps);
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
  public Tensor linearized(Scalar minResolution, int minSteps) {
    return (Scalars.lessThan(minResolution, length().divide(RealScalar.of(minSteps))) //
        ? sampled(minResolution).copy() //
        : sampled(minSteps).copy()).append(end());
  }
}
