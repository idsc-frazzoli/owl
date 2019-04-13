// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;

public class NonuniformGeodesicCenter implements TensorUnaryOperator {
  /** @param geodesicInterface
   * @param function that maps the (temporally) neighborhood of a control point to a weight mask
   * @return operator that maps a sequence of points to their geodesic center
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, SmoothingKernel smoothingKernel) {
    return new NonuniformGeodesicCenter(Objects.requireNonNull(geodesicInterface), smoothingKernel);
  }

  // ---
  public final GeodesicInterface geodesicInterface;
  private final SmoothingKernel smoothingKernel;

  /* package */ NonuniformGeodesicCenter(GeodesicInterface geodesicInterface, SmoothingKernel smoothingKernel) {
    this.geodesicInterface = geodesicInterface;
    this.smoothingKernel = smoothingKernel;
  }

  // Map t-I of to x = -1/2 of windowfunction
  private Tensor splitsMethod1(Tensor extracted, Tensor state, Scalar interval) {
    Tensor mL = Tensors.empty();
    Tensor mR = Tensors.empty();
    for (int index = 0; index < extracted.length(); ++index) {
      Scalar converted = extracted.get(index).Get(0).subtract(state.Get(0)).divide(interval.add(interval));
      if (Scalars.lessThan(converted, RealScalar.ZERO))
        mL.append(smoothingKernel.apply(converted));
      else if (converted.equals(RealScalar.ZERO)) {
        // Here is to decide if the middle points weighs one or two
        mL.append(RationalScalar.HALF);
        mR.append(RationalScalar.HALF);
      } else
        mR.append(smoothingKernel.apply(converted));
    }
    Tensor splitsLeft = StaticHelperCausal.splits(Normalize.with(Norm._1).apply(mL));
    Tensor splitsRight = StaticHelperCausal.splits(Normalize.with(Norm._1).apply(Reverse.of(mR)));
    Tensor splitsFinal = StaticHelperCausal.splits(Normalize.with(Norm._1).apply(Tensors.of(Total.of(mR), Total.of(mL))));
    return Tensors.of(splitsLeft, splitsFinal, splitsRight);
  }

  // Map t_min of to x = -1/2 of windowfunction
  private Tensor splitsMethod2(Tensor extracted, Tensor state, Scalar interval) {
    Tensor mL = Tensors.empty();
    Tensor mR = Tensors.empty();
    Scalar denum = Min.of(interval.add(interval), extracted.get(extracted.length() - 1).Get(0).subtract(extracted.get(0).Get(0)));
    for (int index = 0; index < extracted.length(); ++index) {
      Scalar converted = extracted.get(index).Get(0).subtract(state.Get(0)).divide(denum);
      if (Scalars.lessThan(converted, RealScalar.ZERO))
        mL.append(smoothingKernel.apply(converted));
      else if (converted.equals(RealScalar.ZERO)) {
        // Here is to decide if the middle points weighs one or two
        mL.append(RationalScalar.HALF);
        mR.append(RationalScalar.HALF);
      } else
        mR.append(smoothingKernel.apply(converted));
    }
    Tensor splitsLeft = StaticHelperCausal.splits(Normalize.with(Norm._1).apply(mL));
    Tensor splitsRight = StaticHelperCausal.splits(Normalize.with(Norm._1).apply(Reverse.of(mR)));
    Tensor splitsFinal = StaticHelperCausal.splits(Normalize.with(Norm._1).apply(Tensors.of(Total.of(mR), Total.of(mL))));
    return Tensors.of(splitsLeft, splitsFinal, splitsRight);
  }

  @Override
  public Tensor apply(Tensor t) {
    Tensor extracted = t.get(0);
    Tensor state = t.get(1);
    Scalar interval = t.Get(2);
    //
    // Tensor splits = splits(extracted, state, interval);
    Tensor splits = splitsMethod2(extracted, state, interval);
    Tensor tempL = extracted.get(0).extract(1, 4);
    for (int index = 0; index < splits.get(0).length(); ++index) {
      tempL = geodesicInterface.split(tempL, extracted.get(index).extract(1, 4), splits.get(0).Get(index));
    }
    Tensor tempR = extracted.get(extracted.length() - 1).extract(1, 4);
    for (int index = 0; index < splits.get(2).length(); ++index) {
      tempR = geodesicInterface.split(extracted.get(extracted.length() - 1 - index).extract(1, 4), tempR, RealScalar.ONE.subtract(splits.get(2).Get(index)));
    }
    Tensor resultState = geodesicInterface.split(tempL, tempR, splits.get(1).Get(0));
    Tensor result = Tensors.of(state.Get(0), resultState.Get(0), resultState.Get(1), resultState.Get(2));
    return result;
  }
}