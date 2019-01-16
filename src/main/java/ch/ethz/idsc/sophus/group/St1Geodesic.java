// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Log;

public enum St1Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from TensorGeodesic
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    St1GroupElement p_act = new St1GroupElement(p);
    
//    Hier muss irgendwo ein Fehler sein
//    Scalar dlambda = Log.FUNCTION.apply(p.Get(0).divide(q.Get(0)));
//    Scalar dt = dlambda.divide((q.Get(1).subtract(p.Get(1))).divide(p.Get(0).subtract(q.Get(0))));
//    Tensor delta = Tensors.of(dlambda, dt);
    

    Tensor delta = p_act.inverse().combine(q);
    Tensor x = St1Exponential.INSTANCE.log(delta);
    return scalar -> p_act.combine(St1Exponential.INSTANCE.exp(x.multiply(scalar)));
  }

  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return curve(p, q).apply(scalar);
  }

  public static void main(String [ ] args) {
    Tensor p = Tensors.vector(3,4);
    Tensor q = Tensors.vector(4,10);
    for (float i = 0; i < 20; i++) {
      Tensor split = St1Geodesic.INSTANCE.split(p, q, RealScalar.of(i/19));
      System.out.println(split);
    }
  }
}
