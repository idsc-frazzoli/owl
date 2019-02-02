// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** filter blends extrapolated value with measurement */
public class GeodesicIIRnFilter implements TensorUnaryOperator {
  private final GeodesicInterface geodesicInterface;
  private final BoundedLinkedList<Tensor> boundedLinkedList;
  private final Tensor splits;

  /** TODO OB state conditions on mask!
   * 
   * @param mask input shape [a1, a2, ..., an, alpha] with a1, a2, ... ,an a affine combination and
   * alpha the measurement update gain (0: only measurement, 1: only prediction) */
  public GeodesicIIRnFilter(GeodesicInterface geodesicInterface, Tensor mask) {
    this.geodesicInterface = geodesicInterface;
    this.boundedLinkedList = new BoundedLinkedList<>(mask.length() + 1);
    splits = StaticHelperCausal.splits(mask.extract(0, mask.length() - 1));
    Scalar temp = RealScalar.ONE;
    for (int index = 0; index < splits.length(); ++index)
      temp = temp.multiply(RealScalar.ONE.subtract(splits.Get(index))).add(RealScalar.ONE);
    splits.append(temp.add(RealScalar.ONE).divide(temp));
    splits.append(mask.Get(mask.length() - 1));
  }

  @Override
  public Tensor apply(Tensor tensor) {
    boundedLinkedList.add(tensor);
    if (boundedLinkedList.size() == 1) {
      return tensor;
    }
    if (boundedLinkedList.size() < splits.length() + 1)
      return tensor;
    Tensor sequence = Tensor.of(boundedLinkedList.stream());
    // // Tensor interpolate = boundedLinkedList.getFirst();
    // Tensor interpolate = sequence.get(0);
    // for (int index = 0; index < splits.length() - 2; ++index)
    // // interpolate = geodesicInterface.split(interpolate, boundedLinkedList.get(index + 1), splits.Get(index));
    // interpolate = geodesicInterface.split(interpolate, sequence.get(index + 1), splits.Get(index));
    // // Tensor extrapolate = geodesicInterface.split(interpolate, boundedLinkedList.get(boundedLinkedList.size() - 2), splits.Get(splits.length() - 2));
    // Tensor extrapolate = geodesicInterface.split(interpolate, sequence.get(sequence.length() - 2), splits.Get(splits.length() - 2));
    // // Tensor update = geodesicInterface.split(extrapolate, boundedLinkedList.getLast(), splits.Get(splits.length() - 1));
    // Tensor update = geodesicInterface.split(extrapolate, Last.of(sequence), splits.Get(splits.length() - 1));
    Tensor update = update(sequence);
    boundedLinkedList.set(boundedLinkedList.size() - 1, update);
    return update;
  }

  public Tensor update(Tensor sequence) {
    if (sequence.length() <= 3) {
      return sequence.get(sequence.length() - 1);
    }
    if (sequence.length() != splits.length() + 1) {
      throw TensorRuntimeException.of(sequence, splits);
    }
    Tensor interpolate = sequence.get(0);
    for (int index = 0; index < splits.length() - 2; ++index)
      interpolate = geodesicInterface.split(interpolate, sequence.get(index + 1), splits.Get(index));
    Tensor extrapolate = geodesicInterface.split(interpolate, sequence.get(sequence.length() - 2), splits.Get(splits.length() - 2));
    return geodesicInterface.split(extrapolate, Last.of(sequence), splits.Get(splits.length() - 1));
  }
}
