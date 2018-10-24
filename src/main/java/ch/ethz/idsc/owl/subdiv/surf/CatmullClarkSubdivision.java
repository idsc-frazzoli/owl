// code by jph
package ch.ethz.idsc.owl.subdiv.surf;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;

public class CatmullClarkSubdivision {
  private final GeodesicInterface geodesicInterface;

  public CatmullClarkSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  public Tensor quad(Tensor p00, Tensor p01, Tensor p10, Tensor p11) {
    Tensor d1 = geodesicInterface.split(p00, p11, RationalScalar.HALF);
    Tensor d2 = geodesicInterface.split(p01, p10, RationalScalar.HALF);
    return geodesicInterface.split(d1, d2, RationalScalar.HALF);
  }

  public Tensor refine(Tensor grid) {
    int rows = grid.length();
    int cols = Unprotect.dimension1(grid);
    int outr = 2 * rows - 1;
    int outc = 2 * cols - 1;
    Tensor array = Array.zeros(outr, outc);
    for (int pix = 0; pix < rows; ++pix)
      for (int piy = 0; piy < cols; ++piy)
        array.set(grid.get(pix, piy), 2 * pix, 2 * piy);
    for (int pix = 1; pix < rows; ++pix)
      for (int piy = 1; piy < cols; ++piy) {
        Tensor mid = quad(grid.get(pix - 1, piy - 1), grid.get(pix - 1, piy), grid.get(pix, piy - 1), grid.get(pix, piy));
        array.set(mid, 2 * pix - 1, 2 * piy - 1);
      }
    for (int pix = 1; pix < rows; ++pix)
      for (int piy = 0; piy < cols; ++piy) {
        Tensor mid = geodesicInterface.split(grid.get(pix - 1, piy), grid.get(pix, piy), RationalScalar.HALF);
        array.set(mid, 2 * pix - 1, 2 * piy);
      }
    for (int pix = 0; pix < rows; ++pix)
      for (int piy = 1; piy < cols; ++piy) {
        Tensor mid = geodesicInterface.split(grid.get(pix, piy - 1), grid.get(pix, piy), RationalScalar.HALF);
        array.set(mid, 2 * pix, 2 * piy - 1);
      }
    for (int pix = 1; pix < rows; ++pix)
      for (int piy = 1; piy < cols - 1; ++piy) {
        Tensor mid = array.get(2 * pix - 1, 2 * piy);
        Tensor c1 = array.get(2 * pix - 1, 2 * piy - 1);
        Tensor c2 = array.get(2 * pix - 1, 2 * piy + 1);
        Tensor s1 = geodesicInterface.split(c1, c2, RationalScalar.HALF);
        Tensor res = geodesicInterface.split(s1, mid, RationalScalar.HALF);
        array.set(res, 2 * pix - 1, 2 * piy);
      }
    for (int pix = 1; pix < rows - 1; ++pix)
      for (int piy = 1; piy < cols; ++piy) {
        Tensor mid = array.get(2 * pix, 2 * piy - 1);
        Tensor c1 = array.get(2 * pix - 1, 2 * piy - 1);
        Tensor c2 = array.get(2 * pix + 1, 2 * piy - 1);
        Tensor s1 = geodesicInterface.split(c1, c2, RationalScalar.HALF);
        Tensor res = geodesicInterface.split(s1, mid, RationalScalar.HALF);
        array.set(res, 2 * pix, 2 * piy - 1);
      }
    return array;
  }
}
