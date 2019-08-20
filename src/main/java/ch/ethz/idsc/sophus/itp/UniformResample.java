// code by jph
package ch.ethz.idsc.sophus.itp;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.hs.r2.Se2UniformResample;
import ch.ethz.idsc.sophus.lie.rn.RnUniformResample;
import ch.ethz.idsc.sophus.math.Distances;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.BinaryAverage;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Resampling.html">Resampling</a> */
public class UniformResample implements CurveSubdivision, Serializable {
  /** @param tensorMetric
   * @param binaryAverage
   * @param spacing positive
   * @return
   * @see RnUniformResample
   * @see Se2UniformResample */
  public static CurveSubdivision of(TensorMetric tensorMetric, BinaryAverage binaryAverage, Scalar spacing) {
    return new UniformResample(tensorMetric, binaryAverage, Sign.requirePositive(spacing));
  }

  // ---
  private final TensorMetric tensorMetric;
  private final BinaryAverage binaryAverage;
  private final Scalar spacing;

  private UniformResample(TensorMetric tensorMetric, BinaryAverage binaryAverage, Scalar spacing) {
    this.tensorMetric = tensorMetric;
    this.binaryAverage = binaryAverage;
    this.spacing = spacing;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    return string(tensor.copy().append(tensor.get(0)));
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    Tensor distances = Distances.of(tensorMetric, tensor);
    ScalarTensorFunction scalarTensorFunction = ArcLengthParameterization.of(distances, binaryAverage, tensor);
    Scalar length = Total.ofVector(distances);
    int n = Scalars.intValueExact(Round.FUNCTION.apply(length.divide(spacing)));
    return Tensor.of(Subdivide.of(0, 1, n).stream() //
        .limit(n) //
        .map(Scalar.class::cast) //
        .map(scalarTensorFunction));
  }
}
