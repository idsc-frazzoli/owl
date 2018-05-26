// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PolygonsTest extends TestCase {
  public void testInside() {
    Tensor polygon = Tensors.matrix(new Number[][] { //
        { 0, 0 }, //
        { 1, 0 }, //
        { 1, 1 }, //
        { 0, 1 } //
    });
    assertTrue(Polygons.isInside(polygon, Tensors.vector(.5, .5)));
    assertTrue(Polygons.isInside(polygon, Tensors.vector(.9, .9)));
    assertTrue(Polygons.isInside(polygon, Tensors.vector(.1, .1)));
    assertFalse(Polygons.isInside(polygon, Tensors.vector(.1, -.1)));
    assertFalse(Polygons.isInside(polygon, Tensors.vector(1, 1.1)));
  }

  public void testSome() {
    Tensor asd = Tensors.vector(2, 3, 4, 5);
    asd.set(RealScalar.of(8), 1);
    assertEquals(asd.Get(1), RealScalar.of(8));
    List<Integer> list = new ArrayList<>();
    list.add(6);
    list.add(2);
    list.add(3);
    list.add(9);
    list.get(1).longValue();
  }

  public void testCPointers() {
    {
      String wer = "asdf";
      String wer2 = wer;
      wer = "987345"; // does not change wer2
      assertFalse(wer.equals(wer2));
    }
    {
      Tensor wo = Tensors.vector(2, 3, 4, 5);
      Tensor wo2 = wo;
      wo = Tensors.vector(9, 9); // does not change wo2
      assertFalse(wo.equals(wo2));
    }
  }

  public void testAreaTriangle() {
    Tensor poly = Tensors.fromString("{{1,1},{2,1},{1,2}}");
    Scalar area = Polygons.area(poly);
    assertEquals(area, RationalScalar.HALF);
    assertTrue(ExactScalarQ.of(area));
  }

  public void testAreaCube() {
    Tensor poly = Tensors.fromString("{{1,1},{2,1},{2,2},{1,2}}");
    Scalar area = Polygons.area(poly);
    assertEquals(area, RealScalar.ONE);
    assertTrue(ExactScalarQ.of(area));
  }

  public void testAreaEmpty() {
    {
      Tensor poly = Tensors.fromString("{{1,1},{2,1}}");
      Scalar area = Polygons.area(poly);
      assertEquals(area, RealScalar.ZERO);
      assertTrue(ExactScalarQ.of(area));
    }
    {
      Tensor poly = Tensors.fromString("{{1,1}}");
      Scalar area = Polygons.area(poly);
      assertEquals(area, RealScalar.ZERO);
      assertTrue(ExactScalarQ.of(area));
    }
  }

  public void testAreaTriangleUnit() {
    Tensor poly = Tensors.fromString("{{1[m],1[m]},{2[m],1[m]},{1[m],2[m]}}");
    Scalar area = Polygons.area(poly);
    assertEquals(area, Scalars.fromString("1/2[m^2]"));
    assertTrue(ExactScalarQ.of(area));
  }

  public void testAreaCubeUnit() {
    Tensor poly = Tensors.fromString("{{1[cm],1[cm]},{2[cm],1[cm]}}");
    Scalar area = Polygons.area(poly);
    assertEquals(area, Scalars.fromString("0[cm^2]"));
    assertTrue(ExactScalarQ.of(area));
  }

  public void testAreaEmptyUnit() {
    {
      Tensor poly = Tensors.fromString("{{1[m],1[m]},{2[m],1[m]}}");
      Scalar area = Polygons.area(poly);
      assertEquals(area, Scalars.fromString("0[m^2]"));
      assertTrue(ExactScalarQ.of(area));
    }
    {
      Tensor poly = Tensors.fromString("{{1[m],1[m]}}");
      Scalar area = Polygons.area(poly);
      assertEquals(area, Scalars.fromString("0[m^2]"));
      assertTrue(ExactScalarQ.of(area));
    }
  }
}
