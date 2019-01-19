// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PolygonAreaTest extends TestCase {
  public void testAreaTriangle() {
    Tensor poly = Tensors.fromString("{{1,1},{2,1},{1,2}}");
    Scalar area = PolygonArea.FUNCTION.apply(poly);
    assertEquals(area, RationalScalar.HALF);
    ExactScalarQ.require(area);
  }

  public void testAreaCube() {
    Tensor poly = Tensors.fromString("{{1,1},{2,1},{2,2},{1,2}}");
    Scalar area = PolygonArea.FUNCTION.apply(poly);
    assertEquals(area, RealScalar.ONE);
    ExactScalarQ.require(area);
  }

  public void testAreaEmpty() {
    Scalar area = PolygonArea.FUNCTION.apply(Tensors.empty());
    assertEquals(area, RealScalar.ZERO);
  }

  public void testAreaLine() {
    Tensor poly = Tensors.fromString("{{1,1},{2,1}}");
    Scalar area = PolygonArea.FUNCTION.apply(poly);
    assertEquals(area, RealScalar.ZERO);
    ExactScalarQ.require(area);
  }

  public void testAreaPoint() {
    Tensor poly = Tensors.fromString("{{1,1}}");
    Scalar area = PolygonArea.FUNCTION.apply(poly);
    assertEquals(area, RealScalar.ZERO);
    ExactScalarQ.require(area);
  }

  public void testAreaTriangleUnit() {
    Tensor poly = Tensors.fromString("{{1[m],1[m]},{2[m],1[m]},{1[m],2[m]}}");
    Scalar area = PolygonArea.FUNCTION.apply(poly);
    assertEquals(area, Scalars.fromString("1/2[m^2]"));
    ExactScalarQ.require(area);
  }

  public void testAreaCubeUnit() {
    Tensor poly = Tensors.fromString("{{1[cm],1[cm]},{2[cm],1[cm]}}");
    Scalar area = PolygonArea.FUNCTION.apply(poly);
    assertEquals(area, Scalars.fromString("0[cm^2]"));
    ExactScalarQ.require(area);
  }

  public void testAreaEmptyUnit() {
    {
      Tensor poly = Tensors.fromString("{{1[m],1[m]},{2[m],1[m]}}");
      Scalar area = PolygonArea.FUNCTION.apply(poly);
      assertEquals(area, Scalars.fromString("0[m^2]"));
      ExactScalarQ.require(area);
    }
    {
      Tensor poly = Tensors.fromString("{{1[m],1[m]}}");
      Scalar area = PolygonArea.FUNCTION.apply(poly);
      assertEquals(area, Scalars.fromString("0[m^2]"));
      ExactScalarQ.require(area);
    }
  }

  public void testFailScalar() {
    try {
      PolygonArea.FUNCTION.apply(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
