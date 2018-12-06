// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public interface TensorGeodesic {
  ScalarTensorFunction curve(Tensor p, Tensor q);
}
