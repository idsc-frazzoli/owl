// code by ob, jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** input to the operator are the individual elements of the sequence */
public class GeodesicFIRn implements TensorUnaryOperator {
  /** @param geodesicExtrapolation
   * @param geodesicInterface
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of( //
      TensorUnaryOperator geodesicExtrapolation, GeodesicInterface geodesicInterface, int radius, Scalar alpha) {
    return new GeodesicFIRn( //
        Objects.requireNonNull(geodesicExtrapolation), //
        Objects.requireNonNull(geodesicInterface), //
        radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final TensorUnaryOperator geodesicExtrapolation;
  private final BoundedLinkedList<Tensor> boundedLinkedList;
  private final GeodesicInterface geodesicInterface;
  private final Scalar alpha;

  /* package */ GeodesicFIRn( //
      TensorUnaryOperator geodesicExtrapolation, GeodesicInterface geodesicInterface, int radius, Scalar alpha) {
    this.geodesicExtrapolation = geodesicExtrapolation;
    this.geodesicInterface = geodesicInterface;
    this.alpha = alpha;
    this.boundedLinkedList = new BoundedLinkedList<>(radius);
  }

  @Override
  public Tensor apply(Tensor x) {
    Tensor value = boundedLinkedList.size() < 2 //
        ? x.copy()
        : geodesicInterface.split(geodesicExtrapolation.apply(Tensor.of(boundedLinkedList.stream())), x, alpha);
    boundedLinkedList.add(x);
    return value;
  }
}
