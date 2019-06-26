// code by ob, jph
package ch.ethz.idsc.sophus.flt.ga;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.sophus.util.BoundedLinkedList;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** input to the operator are the individual elements of the sequence */
public class GeodesicFIRn implements TensorUnaryOperator {
  /** @param geodesicExtrapolation
   * @param splitInterface
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of( //
      TensorUnaryOperator geodesicExtrapolation, SplitInterface splitInterface, int radius, Scalar alpha) {
    return new GeodesicFIRn( //
        Objects.requireNonNull(geodesicExtrapolation), //
        Objects.requireNonNull(splitInterface), //
        radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final TensorUnaryOperator geodesicExtrapolation;
  private final SplitInterface splitInterface;
  private final Scalar alpha;
  private final BoundedLinkedList<Tensor> boundedLinkedList;

  /* package */ GeodesicFIRn( //
      TensorUnaryOperator geodesicExtrapolation, SplitInterface splitInterface, int radius, Scalar alpha) {
    this.geodesicExtrapolation = geodesicExtrapolation;
    this.splitInterface = splitInterface;
    this.alpha = alpha;
    this.boundedLinkedList = new BoundedLinkedList<>(radius);
  }

  @Override
  public Tensor apply(Tensor x) {
    Tensor value = boundedLinkedList.size() < 2 //
        ? x.copy()
        : splitInterface.split( //
            geodesicExtrapolation.apply(Tensor.of(boundedLinkedList.stream())), //
            x, //
            alpha);
    boundedLinkedList.add(x);
    return value;
  }
}
