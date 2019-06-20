// code by jph
package ch.ethz.idsc.sophus.surf.subdiv;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

public class CatmullClarkRefinement {
  private final BiinvariantMean biinvariantMean;

  public CatmullClarkRefinement(BiinvariantMean biinvariantMean) {
    this.biinvariantMean = biinvariantMean;
  }

  public SurfaceMesh refine(SurfaceMesh surfaceMesh) {
    SurfaceMesh out = new SurfaceMesh();
    for (Tensor sequence : surfaceMesh.polygons()) {
      int n = sequence.length();
      Tensor weights = Array.of(l -> RationalScalar.of(1, n), n);
      out.addVert(biinvariantMean.mean(sequence, weights));
    }
    return out;
  }
}
