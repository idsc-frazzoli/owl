// code by ob / jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.IntegerTensorFunction;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowCenterSampler;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** GeodesicCenter projects a sequence of points to their geodesic center
 * with each point weighted as provided by an external function.
 * 
 * <p>Careful: the implementation only supports sequences with ODD number of elements!
 * When a sequence of even length is provided an Exception is thrown. */
public class GeodesicAdaptiveCenter implements TensorUnaryOperator {
  private static final Scalar TWO = RealScalar.of(2);

  /** @param geodesicInterface
   * @param function that maps an extent to a time-dependant and pose dependant weight mask of maxmimum length == 2 * extent + 1
   * @return operator that maps a sequence of odd number of points to their geodesic center
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, IntegerTensorFunction function, Scalar timeInterval, Scalar poseInterval) {
    return new GeodesicAdaptiveCenter(geodesicInterface, Objects.requireNonNull(function), timeInterval, poseInterval);
  }

  /** @param geodesicInterface
   * @param windowFunction
   * @return
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, ScalarUnaryOperator windowFunction, Scalar timeInterval, Scalar poseInterval) {
    return new GeodesicAdaptiveCenter(geodesicInterface, new WindowCenterSampler(windowFunction), timeInterval, poseInterval);
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final Scalar timeInterval;
  private final Scalar poseInterval;

  private GeodesicAdaptiveCenter(GeodesicInterface geodesicInterface, IntegerTensorFunction function, Scalar timeInterval, Scalar poseInterval) {
    this.geodesicInterface = Objects.requireNonNull(geodesicInterface);
    this.timeInterval = timeInterval;
    this.poseInterval = poseInterval;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    if (tensor.length() % 2 != 1)
      throw TensorRuntimeException.of(tensor);
    int radius = (tensor.length() - 1) / 2;
    // Downsizes Filter by removing all elements that are further away than the threshold from the center
    // Keeps filter symmetric
    while (Scalars.lessEquals(poseInterval, Norm._2.between(tensor.get(0).extract(0, 2), tensor.get(radius).extract(0, 2)))
        || Scalars.lessEquals(poseInterval, Norm._2.between(tensor.get(tensor.length() - 1).extract(0, 2), tensor.get(radius).extract(0, 2)))) {
      radius = radius - 1;
      tensor = tensor.extract(1, tensor.length() - 1);
    }
    Tensor time = Tensors.empty();
    for (int i = 0; i < tensor.length(); i++) {
      time.append(tensor.get(i).Get(0));
    }
    // Create mask from nonuniform time-data
    Tensor mask = maskGen(time, timeInterval);
    System.out.println(mask);
    Tensor maskLeft = mask.extract(0, (mask.length() + 1) / 2);
    Tensor maskRight = mask.extract(0, (mask.length() + 1) / 2);
    // Normalizing left and right mask
    Scalar sumL = RealScalar.ZERO;
    Scalar sumR = RealScalar.ZERO;
    for (int i = 0; i < maskLeft.length(); i++) {
      sumL = maskLeft.Get(i).add(sumL);
      sumR = maskRight.Get(i).add(sumR);
    }
    maskLeft = maskLeft.divide(sumL);
    maskRight = maskRight.divide(sumR);
    // Create left and right splits
    Tensor splitsLeft = Reverse.of(splits(Reverse.of(maskLeft)));
    Tensor splitsRight = splits(Reverse.of(maskRight));
    Tensor pL = tensor.get(0).extract(1, 4);
    Tensor pR = tensor.get(2 * radius).extract(1, 4);
    for (int index = 0; index < radius - 1 && splitsLeft.length() > 0;) {
      Scalar scalarL = splitsLeft.Get(index);
      Scalar scalarR = splitsRight.Get(index++);
      pL = geodesicInterface.split(pL, tensor.get(index).extract(1, 4), scalarL);
      pR = geodesicInterface.split(pR, tensor.get(2 * radius - index).extract(1, 4), scalarR);
    }
    return geodesicInterface.split(pL, pR, RationalScalar.HALF);
  }

  /** @param one-sided mask of length 1 + radius. Last element has to be 1
   * @return weights of Kalman-style iterative moving average
   * @throws Exception if mask is not symmetric or has even number of elements */
  public static Tensor splits(Tensor mask) {
    Chop._12.requireClose(Total.of(mask), RealScalar.ONE);
    if (mask.length() == 0) {
      return Tensors.vector(0, 1);
    }
    Tensor splits = Tensors.empty();
    Scalar factor = mask.Get(0);
    for (int index = 1; index < mask.length() - 1; ++index) {
      factor = factor.add(mask.get(index));
      Scalar lambda = mask.Get(index).divide(factor);
      splits.append(lambda);
    }
    return splits;
  }

  /** @param time stamp of control sequence
   * @param time interval around midpoint with times considered
   * @return affine */
  /* package */ static Tensor maskGen(Tensor time, Scalar interval) {
    if (time.length() % 2 == 0)
      throw TensorRuntimeException.of(time);
    Scalar midtime;
    if (time.length() == 1)
      midtime = time.Get(0);
    else
      midtime = time.Get((time.length() - 1) / 2);
    Tensor mask = time.map(x -> (x.subtract(midtime).divide(interval.add(interval))));
    Tensor weights = mask.map(w -> SmoothingKernel.GAUSSIAN.apply(w));
    return weights;
  }
}
