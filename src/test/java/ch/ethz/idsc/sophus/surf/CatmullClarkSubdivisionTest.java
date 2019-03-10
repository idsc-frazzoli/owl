// code by jph
package ch.ethz.idsc.sophus.surf;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class CatmullClarkSubdivisionTest extends TestCase {
  public void testQuad() {
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(RnGeodesic.INSTANCE);
    Tensor quad = CirclePoints.of(4);
    Tensor center = catmullClarkSubdivision.quad(quad.get(0), quad.get(1), quad.get(3), quad.get(2));
    assertTrue(Chop._10.allZero(center));
  }

  public void testEdgeCorner() {
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(RnGeodesic.INSTANCE);
    Tensor quad = Tensors.vector(1, 0, 0, 0, 0, 0);
    Tensor mid1 = catmullClarkSubdivision.quad(quad.get(0), quad.get(1), quad.get(2), quad.get(3));
    Tensor mid2 = catmullClarkSubdivision.quad(quad.get(2), quad.get(3), quad.get(4), quad.get(5));
    assertEquals(mid1, RationalScalar.of(1, 4));
    assertEquals(mid2, RationalScalar.of(0, 4));
    Tensor edge = catmullClarkSubdivision.quad(mid1, mid2, quad.get(2), quad.get(3));
    assertEquals(edge, RationalScalar.of(1, 16));
  }

  public void testEdgeCentral1() {
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(RnGeodesic.INSTANCE);
    Tensor quad = Tensors.vector(0, 0, 1, 0, 0, 0);
    Tensor mid1 = catmullClarkSubdivision.quad(quad.get(0), quad.get(1), quad.get(2), quad.get(3));
    Tensor mid2 = catmullClarkSubdivision.quad(quad.get(2), quad.get(3), quad.get(4), quad.get(5));
    assertEquals(mid1, RationalScalar.of(1, 4));
    assertEquals(mid2, RationalScalar.of(1, 4));
    Tensor edge = catmullClarkSubdivision.quad(mid1, mid2, quad.get(2), quad.get(3));
    assertEquals(edge, RationalScalar.of(3, 8));
  }

  public void testEdgeCentral2() {
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(RnGeodesic.INSTANCE);
    Tensor quad = Tensors.vector(0, 0, 0, 1, 0, 0);
    Tensor mid1 = catmullClarkSubdivision.quad(quad.get(0), quad.get(1), quad.get(2), quad.get(3));
    Tensor mid2 = catmullClarkSubdivision.quad(quad.get(2), quad.get(3), quad.get(4), quad.get(5));
    assertEquals(mid1, RationalScalar.of(1, 4));
    assertEquals(mid2, RationalScalar.of(1, 4));
    Tensor edge = catmullClarkSubdivision.quad(mid1, quad.get(2), quad.get(3), mid2);
    assertEquals(edge, RationalScalar.of(3, 8));
  }

  public void testCenterCorner() {
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(RnGeodesic.INSTANCE);
    Tensor quad = Tensors.vector(1, 0, 0, 0, 0, 0, 0, 0, 0);
    Tensor mid00 = catmullClarkSubdivision.quad(quad.get(0), quad.get(1), quad.get(3), quad.get(4));
    Tensor mid01 = catmullClarkSubdivision.quad(quad.get(1), quad.get(2), quad.get(4), quad.get(5));
    Tensor mid10 = catmullClarkSubdivision.quad(quad.get(3), quad.get(4), quad.get(6), quad.get(7));
    Tensor mid11 = catmullClarkSubdivision.quad(quad.get(4), quad.get(5), quad.get(7), quad.get(8));
    assertEquals(mid00, RationalScalar.of(1, 4));
    assertEquals(mid01, RealScalar.of(0));
    assertEquals(mid10, RealScalar.of(0));
    assertEquals(mid11, RealScalar.of(0));
    Tensor edg0 = catmullClarkSubdivision.quad(mid00, quad.get(1), quad.get(4), mid01);
    assertEquals(edg0, RationalScalar.of(1, 16));
    Tensor edg1 = catmullClarkSubdivision.quad(mid00, quad.get(3), quad.get(4), mid10);
    assertEquals(edg1, RationalScalar.of(1, 16));
  }

  public void testRefine() {
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(RnGeodesic.INSTANCE);
    Tensor grid = Tensors.fromString("{{0,0,0,0},{0,1,0,0},{0,0,0,1}}");
    Tensor refine = catmullClarkSubdivision.refine(grid);
    String string = "{{0, 0, 0, 0, 0, 0, 0}, {0, 1/4, 3/8, 1/4, 1/16, 0, 0}, {0, 3/8, 9/16, 3/8, 7/64, 1/16, 1/8}, {0, 1/4, 3/8, 1/4, 1/8, 1/4, 1/2}, {0, 0, 0, 0, 1/8, 1/2, 1}}";
    assertEquals(refine, Tensors.fromString(string));
  }

  public void testRefineX() {
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(RnGeodesic.INSTANCE);
    Tensor grid = Tensors.fromString("{{0,1},{0,1},{0,1}}");
    Tensor refine = Nest.of(catmullClarkSubdivision::refine, grid, 2);
    assertEquals(refine, Tensors.vector(i -> Subdivide.of(0, 1, 4), 9));
    ExactTensorQ.require(refine);
  }

  public void testRefineY() {
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(RnGeodesic.INSTANCE);
    Tensor grid = Tensors.fromString("{{0,0},{1,1},{2,2}}");
    Tensor refine = Nest.of(catmullClarkSubdivision::refine, grid, 2);
    assertEquals(Transpose.of(refine), Tensors.vector(i -> Subdivide.of(0, 2, 8), 5));
    ExactTensorQ.require(refine);
  }
}
