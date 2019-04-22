// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
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
import ch.ethz.idsc.tensor.red.Total;

public class NonuniformCenterFilter {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);

  public static NonuniformCenterFilter of(GeodesicInterface geodesicInterface, Scalar interval, Tensor control) {
    return new NonuniformCenterFilter(Objects.requireNonNull(geodesicInterface), interval, control);
  }

  // ---
  public GeodesicInterface geodesicInterface;
  private Scalar interval;
  private Tensor control;

  /* package */ NonuniformCenterFilter(GeodesicInterface geodesicInterface, Scalar interval, Tensor control) {
    this.geodesicInterface = geodesicInterface;
    this.interval = interval;
    this.control = control;
  }

  private Tensor selection(Tensor state) {
    Tensor extracted = Tensors.empty();
    for (int index = 0; index < control.length(); ++index) {
      // check if t_i - I <= t_index <= t_i + I
      if (Scalars.lessEquals(state.Get(0).subtract(interval), control.get(index).Get(0))
          && Scalars.lessEquals(control.get(index).Get(0), state.Get(0).add(interval)))
        extracted.append(control.get(index));
      // if tensor extracted is non-empty and the previous statement is false, then we passed the range of interest
      else //
      if (!Tensors.isEmpty(extracted))
        break;
    }
    return extracted;
  }

  // Create the masks of the extracted
  private Tensor splits(Tensor extracted, Tensor state) {
    Tensor mL = Tensors.empty();
    Tensor mR = Tensors.empty();
    for (int index = 0; index < extracted.length(); ++index) {
      Scalar converted = extracted.get(index).Get(0).subtract(state.Get(0)).divide(interval.add(interval));
      if (Scalars.lessThan(converted, RealScalar.ZERO))
        mL.append(SmoothingKernel.GAUSSIAN.apply(converted));
      else //
      if (converted.equals(RealScalar.ZERO)) {
        // Here is to decide if the middle points weighs one or two
        mL.append(RationalScalar.HALF);
        mR.append(RationalScalar.HALF);
      } else
        mR.append(SmoothingKernel.GAUSSIAN.apply(converted));
    }
    Tensor splitsLeft = StaticHelperCausal.splits(NORMALIZE.apply(mL));
    Tensor splitsRight = StaticHelperCausal.splits(NORMALIZE.apply(mR));
    Tensor splitsFinal = Reverse.of(StaticHelperCausal.splits(NORMALIZE.apply(Tensors.of(Total.of(mL), Total.of(mR)))));
    return Tensors.of(splitsLeft, splitsFinal, splitsRight);
  }

  private Tensor apply(Tensor splits, Tensor extracted, Tensor state) {
    // FIXME OB not generic
    Tensor tempL = extracted.get(0).extract(1, 4);
    for (int index = 0; index < splits.get(0).length(); ++index)
      tempL = geodesicInterface.split(tempL, extracted.get(index).extract(1, 4), splits.get(0).Get(index));
    Tensor tempR = extracted.get(extracted.length() - 1).extract(1, 4);
    for (int index = 0; index < splits.get(2).length(); ++index)
      tempR = geodesicInterface.split(extracted.get(extracted.length() - 1 - index).extract(1, 4), tempR, RealScalar.ONE.subtract(splits.get(2).Get(index)));
    Tensor resultState = geodesicInterface.split(tempL, tempR, splits.get(1).Get(0));
    return Tensors.of(state.Get(0), resultState.Get(0), resultState.Get(1), resultState.Get(2));
  }

  // TODO OB convert main() to test, remove main()
  public static void main(String[] args) {
    // RawData: time and states with same length
    Tensor control = Tensors.fromString("{{0,0,0,0},{1,1,0,0},{2,2,0,0},{3,3,3,0},{3.5,4,5,0},{4,6,2,0},{5,3,3,0},{7,9,2,0}}");
    NonuniformCenterFilter nonuniformCenterFilter = new NonuniformCenterFilter(Se2Geodesic.INSTANCE, RealScalar.of(0.2), control);
    Tensor result = Tensors.empty();
    for (int i = 0; i < control.length(); ++i) {
      Tensor state = control.get(i);
      Tensor extracted = nonuniformCenterFilter.selection(state);
      Tensor splits = nonuniformCenterFilter.splits(extracted, state);
      result.append(nonuniformCenterFilter.apply(splits, extracted, state));
    }
    System.out.println(result);
  }
}
