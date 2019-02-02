// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.List;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;

public class NonuniformFilter {
  /** @param time stamp of control sequence
   * @return affine combination used to generate mask
   * @throws Exception if mask is not a vector or empty */
  // TODO OB: under construction!
  // TODO OB: Tests
  private BoundedLinkedList<StateTime> boundedLinkedList = new BoundedLinkedList<>(10);
  private GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;

  // Function that takes tensor of ALL data in CSV (tensor) and a duckiebot number (scalar) and returns the StateTime list of this bot;
  public static List<StateTime> dataParser(Tensor tensor, Scalar scalar) {
    // TODO OB
    List<StateTime> list = null;
    return list;
  }

  public Tensor SplitsGenerator(Tensor mask) {
    Tensor splits = StaticHelperCausal.splits(mask.extract(0, mask.length() - 1));
    Scalar temp = RealScalar.ONE;
    for (int index = 0; index < splits.length(); index++) {
      temp = temp.multiply(RealScalar.ONE.subtract(splits.Get(index))).add(RealScalar.ONE);
    }
    splits.append(temp.add(RealScalar.ONE).divide(temp));
    splits.append(mask.Get(mask.length() - 1));
    return splits;
  }

  // TODO OB: Analog zu IIRn auteilen in mehrere Funktionen sobald es funktionier!
  public StateTime apply(StateTime stateTime) {
    boundedLinkedList.add(stateTime);
    if (boundedLinkedList.size() == 1) {
      return stateTime;
    }
    // Use NonuniformMaskGenerator to get a mask;
    NonuniformMaskGenerator nonuniformMaskGenerator = new NonuniformMaskGenerator();
    Tensor mask = nonuniformMaskGenerator.fixedLength(boundedLinkedList, RealScalar.of(5));
    Scalar alpha = RationalScalar.HALF;
    mask.append(alpha);
    // Convert affine mask into geodesic mask;
    Tensor splits = SplitsGenerator(mask);
    // Convert affine mask into geodesic mask;
    // User IIRn filter analogos
    Tensor interpolate = boundedLinkedList.getFirst().state();
    for (int index = 0; index < splits.length() - 2; index++) {
      interpolate = geodesicInterface.split(interpolate, boundedLinkedList.get(index + 1).state(), splits.Get(index));
    }
    Tensor extrapolate = geodesicInterface.split(interpolate, boundedLinkedList.get(boundedLinkedList.size() - 2).state(), splits.Get(splits.length() - 2));
    Tensor update = geodesicInterface.split(extrapolate, boundedLinkedList.getLast().state(), splits.Get(splits.length() - 1));
    StateTime stateTimeUpdate = new StateTime(update, boundedLinkedList.getLast().time());
    // The following line is only valid for IIR, for FIR comment it
    boundedLinkedList.set(boundedLinkedList.size() - 1, stateTimeUpdate);
    return stateTimeUpdate;
  }

  public static void main(String[] args) {
    Tensor data = Tensor.of(ResourceData.of("/dubilab/app/pose/0w/20180702T133612_1.csv").stream().map(row -> row.extract(0, 4)));
    // TODO OB: correct
    // TODO duckietown data is now in ephemeral, see DuckietownDataDemo
    // Tensor data2 = Tensor.of(ResourceData.of("C:/Users/Oliver/Desktop/MA/duckietown/duckie20180713175124.csv").stream().map(row -> row.extract(0, 4)));
    Scalar Length = RealScalar.of(5);
    List<StateTime> list = dataParser(data, Length);
    // Apply fixedLength to the list which returns the causally filtered list
  }
}
