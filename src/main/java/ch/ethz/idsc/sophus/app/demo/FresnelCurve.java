// code by jph
package ch.ethz.idsc.sophus.app.demo;

import ch.ethz.idsc.owl.math.FresnelC;
import ch.ethz.idsc.owl.math.FresnelS;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;

public enum FresnelCurve {
  ;
  public static Tensor of(int n) {
    return Subdivide.of(-5, 5, 1000) //
        .map(t -> Tensors.of(FresnelC.FUNCTION.apply(t), FresnelS.FUNCTION.apply(t)));
  }
}
