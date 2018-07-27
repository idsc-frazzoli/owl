// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.map.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CoveringGeodesicTest extends TestCase {
  public void testArticle() {
    Tensor tensor = Se2CoveringGeodesic.INSTANCE.split( //
        Tensors.vector(1, 2, 3), Tensors.vector(4, 5, 6), RealScalar.of(0.7));
    // System.out.println(tensor);
    // {4.483830852817113, 3.2143505344919467, 5.1} // p.exp(x)
    // {4.483830852817112, 3.2143505344919463, 5.1} // spin(p,x)
    assertTrue(Chop._14.close(tensor, Tensors.fromString("{4.483830852817113, 3.2143505344919467, 5.1}")));
  }

  public void testCenter() {
    // mathematica gives: {2.26033, -0.00147288, 0.981748}
    Tensor p = Tensors.vector(2.017191762967754, -0.08474511292102775, 0.9817477042468103);
    Tensor q = Tensors.vector(2.503476971090440, +0.08179934782700435, 0.9817477042468102);
    Scalar scalar = RealScalar.of(0.5);
    // this used to be the result with the former implementation of Se2CoveringIntegrator
    // {2.017191762967754, -0.08474511292102775, 0.9817477042468103}
    // System.out.println(tensor);
    // Tensor p_inv = ;
    Tensor delta = new Se2CoveringGroupAction(p).inverse().combine(q);
    Tensor x = Se2CoveringExponential.INSTANCE.log(delta).multiply(scalar);
    x.get();
    // x= {0.20432112230000457, -0.1559021143001622, -5.551115123125783E-17}
    // mathematica gives
    // x= {0.204321, .......... -0.155902, ......... -5.55112*10^-17}
    // Tensor exp_x = Se2CoveringIntegrator.INSTANCE.spin(Tensors.vector(0, 0, 0), x);
    // exp_x == {0.20432112230000457, -0.1559021143001622, -5.551115123125783E-17}
    Tensor tensor = Se2CoveringGeodesic.INSTANCE.split(p, q, scalar);
    // {2.260334367029097, -0.0014728825470118057, 0.9817477042468103}
    assertTrue(Chop._14.close(tensor, //
        Tensors.fromString("{2.260334367029097, -0.0014728825470118057, 0.9817477042468103}")));
    // System.out.println(tensor);
    // System.out.println(x);
    // return Se2CoveringIntegrator.INSTANCE.spin(p, x);
    // assertEquals(tensor, Tensors.fromString("{4.483830852817112, 3.2143505344919463, 5.1}"));
  }
}
