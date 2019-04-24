// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Abs;

public class NonuniformGeodesicCenterFilter implements TensorUnaryOperator {
  /** @param geodesicCenter
   * @param (temporal) radius
   * @return
   * @throws Exception if given geodesicCenter is null */
  public static TensorUnaryOperator of(TensorUnaryOperator tensorUnaryOperator, Scalar radius) {
    return new NonuniformGeodesicCenterFilter(Objects.requireNonNull(tensorUnaryOperator), radius);
  }

  // ---
  private final TensorUnaryOperator tensorUnaryOperator;
  private final Scalar radius;

  private NonuniformGeodesicCenterFilter(TensorUnaryOperator tensorUnaryOperator, Scalar radius) {
    this.tensorUnaryOperator = tensorUnaryOperator;
    this.radius = radius;
  }

  private Scalar interval(Tensor control, Tensor state) {
    Scalar lo = Min.of(Abs.FUNCTION.apply(state.Get(0).subtract(control.get(0).Get(0))), radius);
    Scalar hi = Min.of(Abs.FUNCTION.apply(state.Get(0).subtract(control.get(control.length() - 1).Get(0))), radius);
    return Min.of(lo, hi);
  }

  // We select all elements of control which are (timewise) within a interval of the given state
  private static Tensor selection(Tensor control, Tensor state, Scalar interval) {
    // Make sure that the interval is always symmetric around the current state
    Tensor extracted = Tensors.empty();
    for (int index = 0; index < control.length(); ++index) {
      // check if t_i - I <= t_index <= t_i + I
      if (Scalars.lessEquals(state.Get(0).subtract(interval), control.get(index).Get(0)) && //
          Scalars.lessEquals(control.get(index).Get(0), state.Get(0).add(interval)))
        extracted.append(control.get(index));
      // if tensor extracted is non-empty and the previous statement is false, then we passed the range of interest
      else //
      if (Tensors.nonEmpty(extracted))
        break;
    }
    return extracted;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor state = tensor.get(index);
      Scalar interval = interval(tensor, state);
      Tensor extracted = selection(tensor, state, interval);
      if (extracted.length() == 1)
        // FIXME OB not generic
        result.append(state.extract(1, 4));
      else
        // FIXME OB not generic
        result.append(tensorUnaryOperator.apply(Tensors.of(extracted, state, interval)).extract(1, 4));
    }
    System.err.println(result);
    return result;
  }
}
