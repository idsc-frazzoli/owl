// code by jph
package ch.ethz.idsc.sophus.surf.subdiv;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.AffineQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class CatmullClarkRefinement implements SurfaceMeshRefinement, Serializable {
  /** @param biinvariantMean non-null
   * @return */
  public static SurfaceMeshRefinement of(BiinvariantMean biinvariantMean) {
    return new CatmullClarkRefinement(Objects.requireNonNull(biinvariantMean));
  }

  // ---
  private final BiinvariantMean biinvariantMean;
  private final LinearMeshRefinement linearMeshSubdivision;

  private CatmullClarkRefinement(BiinvariantMean biinvariantMean) {
    this.biinvariantMean = biinvariantMean;
    linearMeshSubdivision = new LinearMeshRefinement(biinvariantMean);
  }

  private static int position(Tensor vector, Scalar elem) {
    return IntStream.range(0, vector.length()) //
        .filter(index -> vector.get(index).equals(elem)) //
        .findFirst().getAsInt();
  }

  @Override
  public SurfaceMesh refine(SurfaceMesh surfaceMesh) {
    SurfaceMesh out = linearMeshSubdivision.refine(surfaceMesh);
    surfaceMesh = null;
    int vix = 0;
    Tensor cpy = out.vrt.copy();
    for (List<Integer> list : out.vertToFace()) {
      int n = list.size();
      if (2 < n) {
        Tensor sequence = Tensors.empty();
        Tensor weights = Tensors.empty();
        Scalar ga = RationalScalar.of(1, 4);
        Scalar al = RationalScalar.of(1, 4 * n);
        Scalar be = RationalScalar.of(1, 2 * n);
        for (int fix : list) {
          int pos = position(out.ind.get(fix), RealScalar.of(vix));
          int _p1 = Math.floorMod(pos + 1, 4);
          int _p2 = Math.floorMod(pos + 2, 4);
          int p1 = out.ind.Get(fix, _p1).number().intValue();
          int p2 = out.ind.Get(fix, _p2).number().intValue();
          sequence.append(out.vrt.get(p1));
          sequence.append(out.vrt.get(p2));
          weights.append(be);
          weights.append(al);
        }
        Tensor interp = out.vrt.get(vix);
        sequence.append(interp);
        weights.append(ga);
        AffineQ.require(weights);
        // System.out.println(Total.ofVector(weights));
        Tensor mean = biinvariantMean.mean(sequence, weights);
        // mean = interp;
        cpy.set(mean, vix);
      }
      ++vix;
    }
    out.vrt = cpy;
    return out;
  }
}
