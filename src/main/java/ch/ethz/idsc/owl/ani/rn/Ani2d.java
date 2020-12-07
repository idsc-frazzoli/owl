// code by jph
package ch.ethz.idsc.owl.ani.rn;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Ani2d {
  private static final Tensor SKEW = Tensors.fromString("{{0, 1}, {-1, 0}}");
  private final Scalar mas;
  private final Scalar ine;
  private Tensor pos;
  private Tensor rot;
  private Tensor vel;
  private Scalar ome;

  public Ani2d(Scalar mas, Scalar ine) {
    this.mas = mas;
    this.ine = ine;
  }

  public void setPos(Tensor pos, Tensor rot) {
    this.pos = pos;
    this.rot = rot;
  }

  public void setVel(Tensor vel, Scalar ome) {
    this.vel = vel;
    this.ome = ome;
  }

  public void integrate() {
    // Tensor tau = LinearSolve.of(ine, Ome.dot(ine).dot(Ome));
    // System.out.println(tau);
  }
}
