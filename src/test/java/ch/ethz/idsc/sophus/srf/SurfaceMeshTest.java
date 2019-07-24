// code by jph
package ch.ethz.idsc.sophus.srf;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SurfaceMeshTest extends TestCase {
  public void testEmpty() {
    SurfaceMesh surfaceMesh = new SurfaceMesh();
    assertTrue(Tensors.isEmpty(surfaceMesh.polygons()));
    assertTrue(surfaceMesh.vertToFace().isEmpty());
  }

  public void testNullFail() {
    SurfaceMesh surfaceMesh = new SurfaceMesh();
    try {
      surfaceMesh.addVert(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
