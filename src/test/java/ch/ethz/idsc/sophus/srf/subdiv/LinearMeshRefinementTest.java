// code by jph
package ch.ethz.idsc.sophus.srf.subdiv;

import java.io.IOException;

import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantMean;
import ch.ethz.idsc.sophus.srf.SurfaceMesh;
import ch.ethz.idsc.sophus.srf.SurfaceMeshExamples;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.red.FirstPosition;
import junit.framework.TestCase;

public class LinearMeshRefinementTest extends TestCase {
  public void testSe2CSimple() throws ClassNotFoundException, IOException {
    SurfaceMeshRefinement surfaceMeshRefinement = //
        Serialization.copy(LinearMeshRefinement.of(Se2CoveringBiinvariantMean.INSTANCE));
    SurfaceMesh surfaceMesh = surfaceMeshRefinement.refine(SurfaceMeshExamples.quads6());
    assertEquals(surfaceMesh.ind.length(), 24);
    assertEquals(surfaceMesh.vrt.length(), 35);
    ExactTensorQ.require(surfaceMesh.ind);
  }

  public void testRnSimple() throws ClassNotFoundException, IOException {
    SurfaceMeshRefinement surfaceMeshRefinement = //
        Serialization.copy(LinearMeshRefinement.of(RnBiinvariantMean.INSTANCE));
    SurfaceMesh surfaceMesh = surfaceMeshRefinement.refine(SurfaceMeshExamples.quads5());
    assertEquals(surfaceMesh.ind.length(), 20);
    assertEquals(surfaceMesh.vrt.length(), 31);
    ExactTensorQ.require(surfaceMesh.ind);
    ExactTensorQ.require(surfaceMesh.vrt);
  }

  public void testR3Simple() throws ClassNotFoundException, IOException {
    SurfaceMeshRefinement surfaceMeshRefinement = //
        Serialization.copy(LinearMeshRefinement.of(RnBiinvariantMean.INSTANCE));
    SurfaceMesh surfaceMesh = surfaceMeshRefinement.refine(SurfaceMeshExamples.unitQuad());
    assertEquals(surfaceMesh.ind.length(), 4);
    assertEquals(surfaceMesh.vrt.length(), 9);
    assertTrue(FirstPosition.of(surfaceMesh.vrt, Array.zeros(3)).isPresent());
    ExactTensorQ.require(surfaceMesh.ind);
    ExactTensorQ.require(surfaceMesh.vrt);
  }

  public void testFailNull() {
    try {
      LinearMeshRefinement.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
