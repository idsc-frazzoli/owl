// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO: OB TEST
/** filter blends extrapolated value with measurement */
public class GeodesicIIRnFilter implements TensorUnaryOperator {
  // ---
  private final GeodesicInterface geodesicInterface;
  private final BoundedLinkedList<Tensor> boundedLinkedList;
  private final Tensor splits;

  /** This filter uses the following procedure for prediction
   * [[p,q]_beta, r]_gamma
   * mask shape [a1, a2, ... , an, alpha] with alpha the "kalman gain equivalent" **/
  public GeodesicIIRnFilter(GeodesicInterface geodesicInterface, Tensor mask) {
    this.geodesicInterface = geodesicInterface;
    this.boundedLinkedList = new BoundedLinkedList<>(mask.length() + 1);
    this.splits = StaticHelperCausal.splits(mask.extract(0, mask.length() - 1));
    Scalar temp = RealScalar.ONE;
    for (int index = 0; index < mask.length() - 2; index++) {
      temp = RealScalar.ONE.add(temp.multiply(RealScalar.ONE.subtract(mask.Get(index))));
    }
    this.splits.append(temp.add(temp.reciprocal()));
    this.splits.append(mask.Get(mask.length() - 1));
  }

  @Override
  public synchronized Tensor apply(Tensor tensor) {
    boundedLinkedList.add(tensor);
    if (boundedLinkedList.size() == 1) {
      return tensor;
    }
    Tensor interpolate = boundedLinkedList.getFirst();
    for (int i = 0; i < boundedLinkedList.size() - 1; i++) {
      interpolate = geodesicInterface.split(interpolate, boundedLinkedList.get(i + 1), splits.Get(i));
    }
    Tensor extrapolate = geodesicInterface.split(interpolate, boundedLinkedList.get(boundedLinkedList.size() - 2), splits.Get(splits.length() - 2));
    Tensor update = geodesicInterface.split(extrapolate, boundedLinkedList.get(boundedLinkedList.size() - 1), splits.Get(splits.length() - 1));
    boundedLinkedList.set(boundedLinkedList.size() - 1, update);
    return update;
  }
}
