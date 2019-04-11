// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class NonuniformGeodesicCenterFilter implements TensorUnaryOperator {
  /** @param geodesicCenter
   * @param radius
   * @return
   * @throws Exception if given geodesicCenter is null */
  public static TensorUnaryOperator of(TensorUnaryOperator nonuniformGeodesicCenter, Scalar interval) {
    return new NonuniformGeodesicCenterFilter(Objects.requireNonNull(nonuniformGeodesicCenter), interval);
  }

  // ---
  private final TensorUnaryOperator nonuniformGeodesicCenter;
  private final Scalar interval;

  private NonuniformGeodesicCenterFilter(TensorUnaryOperator nonuniformGeodesicCenter, Scalar interval) {
    this.nonuniformGeodesicCenter = nonuniformGeodesicCenter;
    this.interval = interval;
  }

  // We select all elements of control which are (timewise) within a interval of the given state
  private Tensor selection(Tensor control, Tensor state) {
    Tensor extracted = Tensors.empty();
    for (int index = 0; index < control.length(); ++index) {
      // check if t_i - I <= t_index <= t_i + I
      if (Scalars.lessEquals(state.Get(0).subtract(interval), control.get(index).Get(0))
          && Scalars.lessEquals(control.get(index).Get(0), state.Get(0).add(interval))) {
        extracted.append(control.get(index));
      }
      // if tensor extracted is non-empty and the previous statement is false, then we passed the range of interest
      else if (!Tensors.isEmpty(extracted))
        break;
    }
    return extracted;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor state = tensor.get(index);
      Tensor extracted = selection(tensor, state);
      // Is this cheated?
      result.append(nonuniformGeodesicCenter.apply(Tensors.of(extracted, state)));
    }
    return result;
  }

  public static void main(String[] args) {
    Tensor control = Tensors.fromString("{{0,0,0,0},{1,1,0,0},{2,2,0,0},{3,3,3,0},{3.5,4,5,0},{4,6,2,0},{5,3,3,0},{7,9,2,0}}");
    TensorUnaryOperator tensorUnaryOperator = NonuniformGeodesicCenter.of(Se2Geodesic.INSTANCE, RealScalar.ONE);
    NonuniformGeodesicCenterFilter nonuniformGeodesicCenterFilter = new NonuniformGeodesicCenterFilter(tensorUnaryOperator, RealScalar.ONE);
    System.out.println(nonuniformGeodesicCenterFilter.apply(control));
  }
}
