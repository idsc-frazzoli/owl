// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicExtrapolationFilter implements TensorUnaryOperator {
  /** @param geodesicExtrapolation
   * @param radius
   * @return */
  public static TensorUnaryOperator of(TensorUnaryOperator geodesicExtrapolation, GeodesicInterface geodesicInterface, int radius) {
    return new GeodesicExtrapolationFilter(geodesicExtrapolation, geodesicInterface, radius);
  }

  // ---
  private final TensorUnaryOperator geodesicExtrapolation;
  private final GeodesicInterface geodesicInterface;
  // private final int radius;
  private final BoundedLinkedList<Tensor> boundedLinkedList;

  private GeodesicExtrapolationFilter(TensorUnaryOperator geodesicExtrapolation, GeodesicInterface geodesicInterface, int radius) {
    this.geodesicExtrapolation = Objects.requireNonNull(geodesicExtrapolation);
    this.geodesicInterface = geodesicInterface;
    this.boundedLinkedList = new BoundedLinkedList<>(radius);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Tensors.empty();
    // Initializing BL up until extrapolation is possible
    for (int i = 0; i < 2; i++) {
      boundedLinkedList.add(tensor.get(i));
      result.append(tensor.get(i));
    }
    for (int index = 1; index < tensor.length() - 1; index++) {
      // Extrapolation Step
      Tensor temp = geodesicExtrapolation.apply(Tensor.of(boundedLinkedList.stream()));
      // Measurement update step
      Scalar alpha = RealScalar.of(0.2);
      temp = geodesicInterface.split(temp, tensor.get(index + 1), alpha);
      boundedLinkedList.add(temp);
      result.append(temp);
    }
    return result;
  }
}