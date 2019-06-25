// code by jph
package ch.ethz.idsc.sophus.srf.subdiv;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.red.FirstPosition;

/** Reference:
 * "Recursively generated B-spline surfaces on arbitrary topological meshes"
 * by Catmull, Clark; Computer-Aided Design 16(6), 1978 */
public class CatmullClarkRefinement implements SurfaceMeshRefinement, Serializable {
  /** @param biinvariantMean non-null
   * @return */
  public static SurfaceMeshRefinement of(BiinvariantMean biinvariantMean) {
    return new CatmullClarkRefinement(Objects.requireNonNull(biinvariantMean));
  }

  // ---
  private final BiinvariantMean biinvariantMean;
  private final SurfaceMeshRefinement surfaceMeshRefinement;

  private CatmullClarkRefinement(BiinvariantMean biinvariantMean) {
    this.biinvariantMean = biinvariantMean;
    surfaceMeshRefinement = new LinearMeshRefinement(biinvariantMean);
  }

  @Override // from SurfaceMeshRefinement
  public SurfaceMesh refine(SurfaceMesh surfaceMesh) {
    SurfaceMesh out = surfaceMeshRefinement.refine(surfaceMesh);
    int vix = 0;
    Tensor cpy = out.vrt.copy();
    for (List<Integer> list : out.vertToFace()) {
      int n = list.size();
      if (2 < n) {
        // TODO identify boundary
        Tensor sequence = Unprotect.empty(2 * n + 1);
        Tensor weights = Unprotect.empty(2 * n + 1);
        Scalar ga = RationalScalar.of(1, 4);
        Scalar al = RationalScalar.of(1, 4 * n);
        Scalar be = RationalScalar.of(1, 2 * n);
        Scalar elem = RealScalar.of(vix);
        for (int fix : list) { // edges from vertex ring (unordered)
          int pos = FirstPosition.of(out.ind.get(fix), elem).getAsInt();
          int p1 = out.ind.Get(fix, (pos + 1) % 4).number().intValue();
          int p2 = out.ind.Get(fix, (pos + 2) % 4).number().intValue();
          sequence.append(out.vrt.get(p1));
          sequence.append(out.vrt.get(p2));
          weights.append(be);
          weights.append(al);
        }
        Tensor interp = out.vrt.get(vix);
        sequence.append(interp);
        weights.append(ga);
        cpy.set(biinvariantMean.mean(sequence, weights), vix);
      }
      ++vix;
    }
    out.vrt = cpy;
    return out;
  }
}
