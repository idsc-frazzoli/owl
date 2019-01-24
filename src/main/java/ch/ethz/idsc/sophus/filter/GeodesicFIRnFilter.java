// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO: OB TEST
/** filter blends extrapolated value with measurement */
public class GeodesicFIRnFilter implements TensorUnaryOperator {
  // ---
  private final GeodesicInterface geodesicInterface;
  private final BoundedLinkedList<Tensor> boundedLinkedList;
  private final Tensor splits;
  private final Scalar lambda;

  /** This filter uses the following procedure for prediction
   * [[p,q]_beta, r]_gamma **/
  public GeodesicFIRnFilter(GeodesicInterface geodesicInterface, Tensor mask) {
    this.geodesicInterface = geodesicInterface;
    this.boundedLinkedList = new BoundedLinkedList<>(mask.length() + 2);
    this.splits = StaticHelperCausal.splits(mask);
    Scalar temp = RealScalar.ONE;
    for (int index = 0; index < mask.length() - 1; index++) {
      temp = RealScalar.ONE.add(temp.multiply(RealScalar.ONE.subtract(mask.Get(index))));
    }
    this.lambda = temp.add(temp.reciprocal());
  }

  @Override
  public synchronized Tensor apply(Tensor tensor) {
    boundedLinkedList.add(tensor);
    if (boundedLinkedList.size() == 1) {
      return tensor;
    }
    Tensor interpolate = boundedLinkedList.getFirst();
    for (int i = 1; i < boundedLinkedList.size() - 1; i++) {
      interpolate = geodesicInterface.split(interpolate, boundedLinkedList.get(i), splits.Get(i - 1));
    }
    Tensor extrapolate = geodesicInterface.split(interpolate, boundedLinkedList.get(boundedLinkedList.size() - 2), lambda);
    // TODO: OB hier variation von Scalar alpha zulassen
    Tensor result = geodesicInterface.split(extrapolate, boundedLinkedList.get(boundedLinkedList.size() - 2), RationalScalar.HALF);
    return result;
  }

  public static void main(String[] args) {
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + //
        "0w/20180702T133612_1" + ".csv").stream().map(row -> row.extract(1, 4)));
    Tensor mask = Tensors.vector(.5);
    TensorUnaryOperator geodesicCenterFilter = new GeodesicFIRnFilter(Se2Geodesic.INSTANCE, mask);
    final Tensor refined = Tensor.of(control.stream().map(geodesicCenterFilter));
  }
}
