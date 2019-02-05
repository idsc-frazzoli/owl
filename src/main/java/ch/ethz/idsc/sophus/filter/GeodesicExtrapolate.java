// code by jph
package ch.ethz.idsc.sophus.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.IntegerTensorFunction;
import ch.ethz.idsc.sophus.math.WindowCenterSampler;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;



//  TODO OB: Arbeitsversion. Ungetestet!
/** GeodesicExtrapolate projects a sequence of points to their next (expected) point
 * with each point weighted as provided by an external function. */
public class GeodesicExtrapolate implements TensorUnaryOperator {
  /** @param geodesicInterface
   * @param function that maps an extent to a weight mask of length "sequence.length - 2"
   * @return operator that maps a sequence of number of points to their next (expected) point
   * @throws Exception if either input parameters is null */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, IntegerTensorFunction function) {
    return new GeodesicExtrapolate(geodesicInterface, Objects.requireNonNull(function));
  }

  /** @param geodesicInterface
   * @param windowFunction
   * @return */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface, ScalarUnaryOperator windowFunction) {
    return new GeodesicExtrapolate(geodesicInterface, new WindowCenterSampler(windowFunction));
  }

  // ---
  private final GeodesicInterface geodesicInterface;
  private final IntegerTensorFunction function;
  private final List<Tensor> weights = new ArrayList<>();

  private GeodesicExtrapolate(GeodesicInterface geodesicInterface, IntegerTensorFunction function) {
    this.geodesicInterface = Objects.requireNonNull(geodesicInterface);
    this.function = function;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    if(tensor.length() < 2)
      return tensor.get(tensor.length()-1);
    int filterLength = tensor.length()-2;
    while (weights.size() <= filterLength)
      weights.add(splits(function.apply(weights.size())));
    Tensor splits = weights.get(filterLength);
    Tensor result = tensor.get(0);
    for (int index = 0; index <= filterLength;) {
      Scalar scalar = splits.Get(index++);
      result = geodesicInterface.split(result, tensor.get(index), scalar);
    }
    return result;
  }
  /** @param causal mask
   * @return Tensor [i1, ..., in, e] with i being interpolatory weights and e the extrapolation weight
   * @throws Exception if mask is not affine */
  /* package */ static Tensor splits(Tensor mask) {
    Tensor splits = StaticHelperCausal.splits(mask);
    Scalar temp = RealScalar.ONE;
    splits = Reverse.of(splits);
    for (int index = 0; index < splits.length(); ++index)
      temp = temp.multiply(RealScalar.ONE.subtract(splits.Get(index))).add(RealScalar.ONE);
    splits.append(temp.add(RealScalar.ONE).divide(temp));
    return splits;
  }
  
//  //Zum Testen
//  public static void main(String[] args) {
//    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + "0w/20180702T133612_2" + ".csv").stream() //
//        .limit(5) //
//        .map(row -> row.extract(1, 4)));
//    GeodesicInterface geodesicInterface = Se2Geodesic.INSTANCE;
//    WindowSideSampler windowSideSampler = new WindowSideSampler(SmoothingKernel.GAUSSIAN);
//    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolate.of(geodesicInterface, windowSideSampler);
//    Tensor refined = tensorUnaryOperator.apply(control);
//  }
}
