// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;

public class NonuniformGeodesicExtrapolation implements TensorUnaryOperator {
  // TODO OB: @params
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, SmoothingKernel smoothingKernel) {
    return new NonuniformGeodesicExtrapolation(geodesicInterface, smoothingKernel);
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final SmoothingKernel smoothingKernel;

  private NonuniformGeodesicExtrapolation(GeodesicInterface geodesicInterface, SmoothingKernel smoothingKernel) {
    this.geodesicInterface = Objects.requireNonNull(geodesicInterface);
    this.smoothingKernel = smoothingKernel;
  }

  /* package */ static Tensor maskToSplits(Tensor mask, Scalar samplings) {
    // check for affinity
    Chop._12.requireClose(Total.of(mask), RealScalar.ONE);
    // no extrapolation possible
    if (mask.length() == 1)
      return Tensors.vector(1);
    Tensor splits = Tensors.empty();
    Scalar factor = mask.Get(0);
    // Calculate interpolation splits
    for (int index = 1; index < mask.length() - 1; ++index) {
      factor = factor.add(mask.get(index));
      Scalar lambda = mask.Get(index).divide(factor);
      splits.append(lambda);
    }
    // Calculate extrapolation split
    Scalar temp = RealScalar.ONE;
    for (int index = 0; index < splits.length(); ++index)
      temp = temp.multiply(RealScalar.ONE.subtract(splits.Get(index))).add(RealScalar.ONE);
    // Samplings is the number of sampling frequencies to the next step
    splits.append(samplings.add(temp.reciprocal()));
    return splits;
  }

  private Tensor tensorToSplit(Tensor extracted, Scalar interval, Scalar samplings) {
    // extract times
    Tensor mask = Tensors.empty();
    Tensor state = Last.of(extracted);
    Scalar interval2 = interval.add(interval);
    for (int index = 0; index < extracted.length(); ++index) {
      Scalar converted = extracted.Get(index, 0).subtract(state.Get(0)).divide(interval2);
      mask.append(smoothingKernel.apply(converted));
    }
    // TODO OB possibly use Total::ofVector for normalization, because some kernels contain negative values
    mask = Normalize.with(Norm._1).apply(mask);
    return maskToSplits(mask, samplings);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    Tensor extracted = tensor.get(0);
    Scalar interval = tensor.Get(1);
    Scalar samplings = tensor.Get(2);
    Tensor splits = tensorToSplit(extracted, interval, samplings);
    // TODO OB not generic, also below
    Tensor result = extracted.get(0).extract(1, 4);
    for (int index = 0; index < splits.length(); ++index)
      result = geodesicInterface.split(result, extracted.get(index + 1).extract(1, 4), splits.Get(index));
    return Tensors.of(extracted.get(extracted.length() - 1).Get(0), result.Get(0), result.Get(1), result.Get(2));
  }
}
