// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicIIRnFilter implements TensorUnaryOperator {
  /** @param geodesicExtrapolation
   * @param geodesicInterface
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of(TensorUnaryOperator geodesicExtrapolation, GeodesicInterface geodesicInterface, int radius, Scalar alpha) {
    return new GeodesicIIRnFilter(geodesicExtrapolation, geodesicInterface, radius, alpha);
  }

  // ---
  private final TensorUnaryOperator geodesicExtrapolation;
  private final BoundedLinkedList<Tensor> boundedLinkedList;
  private final GeodesicInterface geodesicInterface;
  private final Scalar alpha;

  private GeodesicIIRnFilter(TensorUnaryOperator geodesicExtrapolation, GeodesicInterface geodesicInterface, int radius, Scalar alpha) {
    this.geodesicExtrapolation = Objects.requireNonNull(geodesicExtrapolation);
    this.geodesicInterface = Objects.requireNonNull(geodesicInterface);
    this.alpha = Objects.requireNonNull(alpha);
    this.boundedLinkedList = new BoundedLinkedList<>(radius);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Tensors.empty();
    // Initializing BL up until extrapolation is possible
    for (int index = 0; index < 2; ++index) {
      boundedLinkedList.add(tensor.get(index));
      result.append(tensor.get(index));
    }
    for (int index = 1; index < tensor.length() - 1; ++index) {
      // Extrapolation Step
      Tensor temp = geodesicExtrapolation.apply(Tensor.of(boundedLinkedList.stream()));
      // Measurement update step
      temp = geodesicInterface.split(temp, tensor.get(index + 1), alpha);
      boundedLinkedList.add(temp);
      result.append(temp);
    }
    return result;
  }
}