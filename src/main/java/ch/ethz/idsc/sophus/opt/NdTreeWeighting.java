// code by jph
package ch.ethz.idsc.sophus.opt;

import java.io.Serializable;
import java.util.Collection;

import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.nrm.NormalizeTotal;
import ch.ethz.idsc.tensor.opt.nd.EuclideanNdCenter;
import ch.ethz.idsc.tensor.opt.nd.NdMatch;
import ch.ethz.idsc.tensor.opt.nd.NdTreeMap;
import ch.ethz.idsc.tensor.red.Entrywise;

public class NdTreeWeighting implements LogWeighting, Serializable {
  private final int limit;

  public NdTreeWeighting(int limit) {
    this.limit = limit;
  }

  @Override
  public TensorUnaryOperator operator(Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
    throw new UnsupportedOperationException();
  }

  @Override
  public TensorScalarFunction function(Biinvariant biinvariant, VectorLogManifold vectorLogManifold, //
      ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
    Tensor lbounds = Entrywise.min().of(sequence);
    Tensor ubounds = Entrywise.max().of(sequence);
    NdTreeMap<Scalar> ndTreeMap = new NdTreeMap<>(lbounds, ubounds, 2, 4);
    for (int index = 0; index < values.length(); ++index)
      ndTreeMap.add(sequence.get(index), values.Get(index));
    return new Inner(ndTreeMap, variogram);
  }

  @Override
  public String toString() {
    return String.format("%s[%d]", NdTreeWeighting.class.getSimpleName(), limit);
  }

  private class Inner implements TensorScalarFunction {
    private final NdTreeMap<Scalar> ndTreeMap;
    private final ScalarUnaryOperator variogram;

    public Inner(NdTreeMap<Scalar> ndTreeMap, ScalarUnaryOperator variogram) {
      this.ndTreeMap = ndTreeMap;
      this.variogram = variogram;
    }

    @Override
    public Scalar apply(Tensor center) {
      Collection<NdMatch<Scalar>> collection = ndTreeMap.cluster(EuclideanNdCenter.of(center), limit);
      if (collection.isEmpty())
        return DoubleScalar.INDETERMINATE;
      Tensor weights = NormalizeTotal.FUNCTION.apply( //
          Tensor.of(collection.stream().map(NdMatch::distance).map(variogram)));
      return (Scalar) weights.dot(Tensor.of(collection.stream().map(NdMatch::value)));
    }
  }
}
