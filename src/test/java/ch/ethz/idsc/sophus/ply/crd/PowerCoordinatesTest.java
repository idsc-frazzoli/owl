// code by jph
package ch.ethz.idsc.sophus.ply.crd;

import java.io.IOException;

import ch.ethz.idsc.sophus.ply.crd.PowerCoordinates.Aux;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class PowerCoordinatesTest extends TestCase {
  public void testDij() {
    Scalar dij = PowerCoordinates.dij(Tensors.vector(1, 2), Tensors.vector(3, 4), RealScalar.of(.2), RealScalar.of(.4));
    Chop._10.requireClose(dij, RealScalar.of(1.3788582233137676));
  }

  public void testAux() {
    Aux aux = new PowerCoordinates.Aux(Tensors.vector(1, 2), Tensors.vector(3, 4), RealScalar.of(.2), RealScalar.of(.4));
    Tensor exp0 = Tensors.vector(1.975, 2.975);
    Tensor exp1 = Tensors.vector(-0.7071067811865475, 0.7071067811865475);
    Chop._10.requireClose(aux.pos, exp0);
    Chop._10.requireClose(aux.dir, exp1);
  }

  public void testGetDual() {
    PowerCoordinates powerCoordinates = new PowerCoordinates(Barycentric.WACHSPRESS);
    Tensor P = Tensors.fromString("{{1, 1}, {5, 1}, {3, 5}, {2, 5}}");
    Tensor weights = powerCoordinates.getDual(P, Tensors.vector(4, 2));
    Tensor exp = Tensors.fromString("{{40/11, 23/11}, {4, 1}, {6, 3}, {4, 7/3}}");
    Chop._12.requireClose(weights, exp);
  }

  public void testHDual() {
    PowerCoordinates powerCoordinates = new PowerCoordinates(Barycentric.WACHSPRESS);
    Tensor P = Tensors.fromString("{{1, 1}, {5, 1}, {3, 5}, {2, 5}}");
    Tensor weights = powerCoordinates.hDual(P, Tensors.vector(4, 2));
    Tensor exp = Tensors.fromString("{4/11, 2, 2/3, 4/33}");
    Chop._12.requireClose(weights, exp);
  }

  public void testWeights() throws ClassNotFoundException, IOException {
    PowerCoordinates powerCoordinates = Serialization.copy(new PowerCoordinates(Barycentric.WACHSPRESS));
    Tensor P = Tensors.fromString("{{1, 1}, {5, 1}, {3, 5}, {2, 5}}");
    Tensor weights = powerCoordinates.weights(P, Tensors.vector(4, 2));
    Tensor exp = Tensors.fromString("{3/26, 33/52, 11/52, 1/26}");
    Chop._12.requireClose(weights, exp);
  }

  public void testFailEmpty() throws ClassNotFoundException, IOException {
    PowerCoordinates powerCoordinates = Serialization.copy(new PowerCoordinates(Barycentric.MEAN_VALUE));
    try {
      powerCoordinates.weights(Tensors.empty(), Tensors.vector(4, 2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
