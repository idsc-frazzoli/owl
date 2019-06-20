// code by jph
package ch.ethz.idsc.sophus.surf.subdiv;

import java.io.IOException;

import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantMean;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class CatmullClarkRefinementTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    SurfaceMeshRefinement surfaceMeshRefinement = //
        Serialization.copy(CatmullClarkRefinement.of(Se2CoveringBiinvariantMean.INSTANCE));
    SurfaceMesh surfaceMesh = surfaceMeshRefinement.refine(SurfaceMeshExamples.quads6());
    assertEquals(surfaceMesh.ind.length(), 24);
    assertEquals(surfaceMesh.vrt.length(), 35);
    ExactTensorQ.require(surfaceMesh.ind);
  }

  public void testFailNull() {
    try {
      CatmullClarkRefinement.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
