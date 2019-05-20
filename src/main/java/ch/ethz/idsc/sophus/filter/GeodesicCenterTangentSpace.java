// code by jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.IntegerTensorFunction;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowCenterSampler;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.SymmetricVectorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** GeodesicCenterTangentSpace projects a sequence of point to the tangent space,
 * takes the weighted average and reprojects this average to the group
 * 
 * <p>Careful: the implementation only supports sequences with ODD number of elements!
 * When a sequence of even length is provided an Exception is thrown. */
public class GeodesicCenterTangentSpace implements TensorUnaryOperator {
  private static final Scalar TWO = RealScalar.of(2);

  /** @param geodesicInterface
   * @param function that maps an extent to a weight mask of length == 2 * extent + 1
   * @return operator that maps a sequence of odd number of points to their geodesic center
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(LieExponential lieExponential, IntegerTensorFunction function) {
    return new GeodesicCenterTangentSpace(lieExponential, Objects.requireNonNull(function));
  }

  /** @param geodesicInterface
   * @param windowFunction
   * @return
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(LieExponential lieExponential, ScalarUnaryOperator windowFunction) {
    return new GeodesicCenterTangentSpace(lieExponential, new WindowCenterSampler(windowFunction));
  }

  // ---
  private final LieExponential lieExponential;
  private final IntegerTensorFunction function;

  private GeodesicCenterTangentSpace(LieExponential lieExponential, IntegerTensorFunction function) {
    this.lieExponential = Objects.requireNonNull(lieExponential);
    this.function = function;
  }

  // {1.4522630629264224, 1.222442793259794, 1.3434064786307582}
  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    int radius = (tensor.length() - 1) / 2;
    Tensor mask = SymmetricVectorQ.require(function.apply(radius));
    Tensor tangentTensor = Tensor.of(tensor.stream().map(xya -> lieExponential.log(xya)));
    Tensor mean = Tensors.vector(0, 0, 0);
    for (int index = 0; index < tensor.length(); ++index) {
      mean = mean.add(tangentTensor.get(index).multiply(mask.Get(index)));
    }
    return lieExponential.exp(mean);
  }

  public static void main(String[] args) {
    Tensor p = Tensors.vector(1, 1, 1);
    Tensor test = Tensors.of(p, p, p.add(p), p, p);
    LieExponential lieExponential = Se2CoveringExponential.INSTANCE;
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenterTangentSpace.of(lieExponential, SmoothingKernel.GAUSSIAN);
    System.out.println(tensorUnaryOperator.apply(test));
  }
}
