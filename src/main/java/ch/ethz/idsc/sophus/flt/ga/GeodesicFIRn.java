// code by ob, jph
package ch.ethz.idsc.sophus.flt.ga;

import java.util.Objects;

import ch.ethz.idsc.sophus.util.BoundedLinkedList;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.BinaryAverage;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** input to the operator are the individual elements of the sequence */
public class GeodesicFIRn implements TensorUnaryOperator {
  /** @param geodesicExtrapolation
   * @param binaryAverage
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of( //
      TensorUnaryOperator geodesicExtrapolation, BinaryAverage binaryAverage, int radius, Scalar alpha) {
    return new GeodesicFIRn( //
        Objects.requireNonNull(geodesicExtrapolation), //
        Objects.requireNonNull(binaryAverage), //
        radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final TensorUnaryOperator geodesicExtrapolation;
  private final BinaryAverage binaryAverage;
  private final Scalar alpha;
  private final BoundedLinkedList<Tensor> boundedLinkedList;

  /* package */ GeodesicFIRn( //
      TensorUnaryOperator geodesicExtrapolation, BinaryAverage binaryAverage, int radius, Scalar alpha) {
    this.geodesicExtrapolation = geodesicExtrapolation;
    this.binaryAverage = binaryAverage;
    this.alpha = alpha;
    this.boundedLinkedList = new BoundedLinkedList<>(radius);
  }

  @Override
  public Tensor apply(Tensor x) {
    Tensor value = boundedLinkedList.size() < 2 //
        ? x.copy()
        : binaryAverage.split( //
            geodesicExtrapolation.apply(Tensor.of(boundedLinkedList.stream())), //
            x, //
            alpha);
    boundedLinkedList.add(x);
    return value;
  }
}
