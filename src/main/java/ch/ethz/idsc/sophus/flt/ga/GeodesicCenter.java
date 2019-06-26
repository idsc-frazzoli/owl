// code by jph
package ch.ethz.idsc.sophus.flt.ga;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.sophus.math.SymmetricVectorQ;
import ch.ethz.idsc.sophus.math.win.UniformWindowSampler;
import ch.ethz.idsc.sophus.util.MemoFunction;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** GeodesicCenter projects a sequence of points to their geodesic center
 * with each point weighted as provided by an external function.
 * 
 * <p>Careful: the implementation only supports sequences with ODD number of elements!
 * When a sequence of even length is provided an Exception is thrown. */
public class GeodesicCenter implements TensorUnaryOperator {
  /* package */ static class Splits implements Function<Integer, Tensor>, Serializable {
    private static final Scalar TWO = RealScalar.of(2);
    // ---
    private final Function<Integer, Tensor> function;

    public Splits(Function<Integer, Tensor> function) {
      this.function = function;
    }

    @Override
    public Tensor apply(Integer t) {
      return of(function.apply(t));
    }

    /** @param mask symmetric vector of odd length
     * @return weights of Kalman-style iterative moving average
     * @throws Exception if mask is not symmetric or has even number of elements */
    /* package */ static Tensor of(Tensor mask) {
      if (mask.length() % 2 == 0)
        throw TensorRuntimeException.of(mask);
      SymmetricVectorQ.require(mask);
      int radius = (mask.length() - 1) / 2;
      Tensor halfmask = Tensors.vector(i -> i == 0 //
          ? mask.Get(radius + i)
          : mask.Get(radius + i).multiply(TWO), radius);
      Scalar factor = RealScalar.ONE;
      Tensor splits = Tensors.empty();
      for (int index = 0; index < radius; ++index) {
        Scalar lambda = halfmask.Get(index).divide(factor);
        splits.append(lambda);
        factor = factor.multiply(RealScalar.ONE.subtract(lambda));
      }
      return Reverse.of(splits);
    }
  }

  /** @param splitInterface
   * @param function that maps an extent to a weight mask of length == 2 * extent + 1
   * @return operator that maps a sequence of odd number of points to their geodesic center
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(SplitInterface splitInterface, Function<Integer, Tensor> function) {
    return new GeodesicCenter(splitInterface, Objects.requireNonNull(function));
  }

  /** @param splitInterface
   * @param windowFunction
   * @return
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(SplitInterface splitInterface, ScalarUnaryOperator windowFunction) {
    return new GeodesicCenter(splitInterface, UniformWindowSampler.of(windowFunction));
  }

  // ---
  private final SplitInterface splitInterface;
  private final Function<Integer, Tensor> function;

  private GeodesicCenter(SplitInterface splitInterface, Function<Integer, Tensor> function) {
    this.splitInterface = Objects.requireNonNull(splitInterface);
    this.function = MemoFunction.wrap(new Splits(function));
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    if (tensor.length() % 2 != 1)
      throw TensorRuntimeException.of(tensor);
    // spatial neighborhood we want to consider for centering
    int radius = (tensor.length() - 1) / 2;
    Tensor splits = function.apply(tensor.length());
    Tensor pL = tensor.get(0);
    Tensor pR = tensor.get(2 * radius);
    for (int index = 0; index < radius;) {
      Scalar scalar = splits.Get(index++);
      pL = splitInterface.split(pL, tensor.get(index), scalar);
      Tensor lR = tensor.get(2 * radius - index);
      pR = splitInterface.split(lR, pR, RealScalar.ONE.subtract(scalar));
    }
    return splitInterface.split(pL, pR, RationalScalar.HALF);
  }
}
