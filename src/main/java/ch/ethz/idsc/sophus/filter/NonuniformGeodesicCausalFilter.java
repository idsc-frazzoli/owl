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

public class NonuniformGeodesicCausalFilter implements TensorUnaryOperator {
  /** @param geodesicCenter
   * @param (temporal) radius
   * @return
   * @throws Exception if given geodesicCenter is null */
  public static TensorUnaryOperator of(TensorUnaryOperator tensorUnaryOperator, Scalar radius) {
    return new NonuniformGeodesicCausalFilter(Objects.requireNonNull(tensorUnaryOperator), radius);
  }

  // ---
  private final TensorUnaryOperator tensorUnaryOperator;
  private final Scalar radius;

  private NonuniformGeodesicCausalFilter(TensorUnaryOperator tensorUnaryOperator, Scalar radius) {
    this.tensorUnaryOperator = tensorUnaryOperator;
    this.radius = radius;
  }

  private Scalar interval(Tensor control, Tensor state) {
    return Min.of(Abs.FUNCTION.apply(state.Get(0).subtract(control.get(0).Get(0))), radius);
  }

  // We select all elements of control which are (timewise) within a interval of the given state
  private static Tensor selection(Tensor control, Tensor state, Scalar interval) {
    // Make sure that the interval is always symmetric around the current state
    Tensor extracted = Tensors.empty();
    for (int index = 0; index < control.length(); ++index) {
      // check if t_i - I <= t_index <= 0
      if (Scalars.lessEquals(state.Get(0).subtract(interval), control.get(index).Get(0)) && Scalars.lessEquals(control.get(index).Get(0), state.Get(0))) {
        extracted.append(control.get(index));
      }
      // if tensor extracted is non-empty and the previous statement is false, then we passed the range of interest
      else if (!Tensors.isEmpty(extracted))
        break;
    }
    extracted.append(state);
    return extracted;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    // FIXME OB: zweiter eintrag ist doppelt
    // TODO OB: Nach refinement zieht sich control sequence zusammen.
    Tensor result = tensor.extract(0, 2);
    for (int index = 1; index < tensor.length() - 2; ++index) {
      Tensor state = tensor.get(index);
      Scalar interval = interval(result, state);
      // problem with selection!
      Tensor extracted = selection(result, state, interval);
      if (extracted.length() <= 1) {
        result.append(state);
      } else {
        Scalar samplings = tensor.get(index + 1).Get(0).subtract(tensor.get(index).Get(0));
        Tensor temp = tensorUnaryOperator.apply(Tensors.of(extracted, interval, samplings));
        result.append(temp);
      }
    }
    return result;
  }
}
