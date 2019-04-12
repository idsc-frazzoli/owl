// code by ob, jph
package ch.ethz.idsc.sophus.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;

/** input to the operator are the individual elements of the sequence */
public class NonuniformFilter implements TensorUnaryOperator {
  /** @param geodesicExtrapolation
   * @param geodesicInterface
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of( //
      GeodesicInterface geodesicInterface, Scalar length, Scalar alpha) {
    return new NonuniformFilter( //
        Objects.requireNonNull(geodesicInterface), //
        length, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final BoundedLinkedList<StateTime> boundedLinkedList;
  private final GeodesicInterface geodesicInterface;
  private final Scalar alpha;
  private final List<Tensor> weights = new ArrayList<>();
  private final Scalar length;

  /* package */ NonuniformFilter(GeodesicInterface geodesicInterface, Scalar length, Scalar alpha) {
    this.geodesicInterface = geodesicInterface;
    this.alpha = alpha;
    // TODO OB: might be not possible with boundedlinked list => not always same filterlength...
    this.boundedLinkedList = new BoundedLinkedList<>(Scalars.intValueExact(length));
    this.length = length;
  }

  // Create nonuniformly sampled mask from StateTime bounded linked list using fixed interval method;
  public Tensor createAffineMask(BoundedLinkedList<StateTime> boundedLinkedList, Scalar interval) {
    while (Scalars.lessEquals(boundedLinkedList.getFirst().time(), boundedLinkedList.getLast().time().subtract(interval)))
      boundedLinkedList.remove();
    if (boundedLinkedList.size() == 1)
      return Tensors.of(RealScalar.ONE);
    Tensor weight = Tensors.empty();
    Scalar delta = interval;
    for (int index = 0; index < boundedLinkedList.size(); ++index) {
      Scalar conversion = boundedLinkedList.get(index).time().subtract(interval).divide(delta.add(delta)).subtract(RationalScalar.HALF);
      weight.append(SmoothingKernel.GAUSSIAN.apply(conversion));
    }
    return weight;
  }

  static Tensor splits(Tensor mask) {
    // check for affinity
    Chop._12.requireClose(Total.of(mask), RealScalar.ONE);
    // no extrapolation possible
    if (mask.length() == 1)
      return Tensors.vector(1);
    Tensor splits = Tensors.empty();
    Scalar factor = mask.Get(0);
    // Calculate interpolation splits
    for (int index = 1; index < mask.length() - 1; ++index) {
      factor = factor.add(mask.get(index));
      Scalar lambda = mask.Get(index).divide(factor);
      splits.append(lambda);
    }
    // Calculate extrapolation splits
    Scalar temp = RealScalar.ONE;
    for (int index = 0; index < splits.length(); ++index)
      temp = temp.multiply(RealScalar.ONE.subtract(splits.Get(index))).add(RealScalar.ONE);
    splits.append(RealScalar.ONE.add(temp.reciprocal()));
    return splits;
  }

  public Tensor process(Tensor tensor, Tensor splits) {
    Tensor result = tensor.get(0);
    for (int index = 0; index < splits.length(); ++index)
      result = geodesicInterface.split(result, tensor.get(index + 1), splits.Get(index));
    return result;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    System.out.println(tensor);
    Tensor splits = Tensors.empty();
    if (!boundedLinkedList.isEmpty())
      splits = splits(createAffineMask(boundedLinkedList, length));
    Tensor value = boundedLinkedList.size() < 2 //
        ? tensor.copy()
        : geodesicInterface.split(process(Tensor.of(boundedLinkedList.stream().map(StateTime::state)), splits), tensor, alpha);
    System.out.println(value);
    StateTime stateTime = new StateTime(value, tensor.Get(0));
    boundedLinkedList.add(stateTime);
    return value;
  }

  // TODO OB convert main() to test, remove main()
  public static void main(String[] args) {
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/2r/20180820T165637_1.csv").stream() //
        .map(row -> row.extract(0, 4)));
    NonuniformFilter nonuniformFilter = new NonuniformFilter(Se2Geodesic.INSTANCE, RealScalar.of(4), RealScalar.of(0.4));
    nonuniformFilter.apply(control);
  }
}
