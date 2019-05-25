// code by gjoel
package ch.ethz.idsc.tensor.crd;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Round;
import junit.framework.TestCase;

public class Rotation2DTest extends TestCase {
  public void testSimple() {
    Coordinates coordsA = Coordinates.of(Tensors.vector(1, 0), "A");
    Coordinates coordsB = Coordinates.of(Tensors.vector(0, 1), "B");
    CoordinateTransform transform = Rotation2D.of(Pi.HALF, CoordinateSystem.of("A"), CoordinateSystem.of("B"));
    assertEquals(coordsB, transform.apply(coordsA).map(Round._9));
  }

  public void testInverse() {
    Coordinates coordsA = Coordinates.of(Tensors.vector(1, 0), "A");
    Coordinates coordsB = Coordinates.of(Tensors.vector(0, 1), "B");
    CoordinateTransform transform = Rotation2D.of(Pi.HALF, CoordinateSystem.of("A"), CoordinateSystem.of("B"));
    assertEquals(coordsA, transform.inverse().apply(coordsB).map(Round._9));
  }

  public void testMultiply() {
    Coordinates coordsA = Coordinates.of(Tensors.vector(1, 0), "A");
    Coordinates coordsC = Coordinates.of(Tensors.vector(0, -1), "C");
    CoordinateTransform transformAB = Rotation2D.of(Pi.HALF, CoordinateSystem.of("A"), CoordinateSystem.of("B"));
    CoordinateTransform transformBC = Rotation2D.of(Pi.VALUE, CoordinateSystem.of("B"), CoordinateSystem.of("C"));
    // ---
    CoordinateTransform transformAC = transformAB.rightMultiply(transformBC);
    assertEquals(coordsC, transformAC.apply(coordsA).map(Round._9));
    // ---
    transformAC = transformBC.leftMultiply(transformAB);
    assertEquals(coordsC, transformAC.apply(coordsA).map(Round._9));
  }
}
