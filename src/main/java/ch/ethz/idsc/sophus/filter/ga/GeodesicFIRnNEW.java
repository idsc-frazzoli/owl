// code by ob, jph
package ch.ethz.idsc.sophus.filter.ga;

import java.util.Objects;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.util.BoundedLinkedList;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** input to the operator are the individual elements of the sequence */
public class GeodesicFIRnNEW implements TensorUnaryOperator {
  /** @param geodesicExtrapolation
   * @param geodesicInterface
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of( //
      GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    return new GeodesicFIRnNEW( //
        Objects.requireNonNull(geodesicDisplay), //
        Objects.requireNonNull(smoothingKernel), //
        radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final TensorUnaryOperator geodesicExtrapolation;
  private final BoundedLinkedList<Tensor> boundedLinkedList;
  private final Scalar alpha;
  private final GeodesicInterface geodesicInterface;

  /* package */ GeodesicFIRnNEW( //
      GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    this.geodesicInterface = geodesicDisplay.geodesicInterface();
    this.geodesicExtrapolation = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel);
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
