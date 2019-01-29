// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** filter blends extrapolated value with measurement */
public class GeodesicIIRnFilter implements TensorUnaryOperator {
  // ---
  private final GeodesicInterface geodesicInterface;
  private final BoundedLinkedList<Tensor> boundedLinkedList;
  private final Tensor splits;

  /** This filter uses the following procedure for prediction
   * [[p,q]_beta, r]_gamma
   * mask input shape [a1, a2, ... , an, alpha] with alpha the "kalman gain equivalent" **/
  public GeodesicIIRnFilter(GeodesicInterface geodesicInterface, Tensor mask) {
    this.geodesicInterface = geodesicInterface;
    this.boundedLinkedList = new BoundedLinkedList<>(mask.length() + 1);
    this.splits = StaticHelperCausal.splits(mask.extract(0, mask.length() - 1));
    Scalar temp = RealScalar.ONE;
    for (int index = 0; index < splits.length(); index++) {
      temp = temp.multiply(RealScalar.ONE.subtract(splits.Get(index))).add(RealScalar.ONE);
    }
    this.splits.append(temp.add(RealScalar.ONE).divide(temp));
    this.splits.append(mask.Get(mask.length() - 1));
  }

  @Override
  public synchronized Tensor apply(Tensor tensor) {
    boundedLinkedList.add(tensor);
    if (boundedLinkedList.size() < splits.length() + 1) {
      return tensor;
    }
    Tensor interpolate = boundedLinkedList.getFirst();
    for (int index = 0; index < splits.length() - 2; index++) {
      interpolate = geodesicInterface.split(interpolate, boundedLinkedList.get(index + 1), splits.Get(index));
    }
    Tensor extrapolate = geodesicInterface.split(interpolate, boundedLinkedList.get(boundedLinkedList.size() - 2), splits.Get(splits.length() - 2));
    Tensor update = geodesicInterface.split(extrapolate, boundedLinkedList.getLast(), splits.Get(splits.length() - 1));
    boundedLinkedList.set(boundedLinkedList.size() - 1, update);
    return update;
  }
}
