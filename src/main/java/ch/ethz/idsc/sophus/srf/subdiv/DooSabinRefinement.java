// code by jph
package ch.ethz.idsc.sophus.srf.subdiv;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.srf.SurfaceMesh;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.RotateLeft;
import ch.ethz.idsc.tensor.io.Primitives;

/** Reference:
 * "Behaviour of recursive division surfaces near extraordinary points"
 * by D. Doo, M. Sabin, Computer-Aided Design 10(6), 1978 */
public class DooSabinRefinement implements SurfaceMeshRefinement, Serializable {
  /** @param biinvariantMean non-null
   * @return */
  public static SurfaceMeshRefinement of(BiinvariantMean biinvariantMean) {
    return new DooSabinRefinement(Objects.requireNonNull(biinvariantMean));
  }

  // ---
  private final BiinvariantMean biinvariantMean;

  private DooSabinRefinement(BiinvariantMean biinvariantMean) {
    this.biinvariantMean = biinvariantMean;
  }

  @Override // from SurfaceMeshRefinement
  public SurfaceMesh refine(SurfaceMesh surfaceMesh) {
    SurfaceMesh out = new SurfaceMesh();
    for (Tensor face : surfaceMesh.ind) {
      Tensor sequence = Tensor.of(IntStream.of(Primitives.toIntArray(face)).mapToObj(surfaceMesh.vrt::get));
      int n = sequence.length();
      Tensor weights = DooSabinWeights.instance().apply(n);
      int ofs = out.vrt.length();
      for (int offset = 0; offset < n; ++offset)
        out.addVert(biinvariantMean.mean(sequence, RotateLeft.of(weights, offset)));
      out.ind.append(Range.of(ofs, ofs + n));
    }
    // TODO JPH add quads at vertices and edges
    return out;
  }
}
