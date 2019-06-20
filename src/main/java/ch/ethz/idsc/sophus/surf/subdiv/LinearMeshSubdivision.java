// code by jph
package ch.ethz.idsc.sophus.surf.subdiv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Primitives;

public class LinearMeshSubdivision {
  private final BiinvariantMean biinvariantMean;

  public LinearMeshSubdivision(BiinvariantMean biinvariantMean) {
    this.biinvariantMean = biinvariantMean;
  }

  public SurfaceMesh refine(SurfaceMesh surfaceMesh) {
    SurfaceMesh out = new SurfaceMesh();
    out.vrt = surfaceMesh.vrt.copy(); // interpolation
    int nV = surfaceMesh.vrt.length();
    for (Tensor face : surfaceMesh.ind) { // midpoint
      Tensor sequence = Tensor.of(IntStream.of(Primitives.toIntArray(face)).mapToObj(surfaceMesh.vrt::get));
      int n = sequence.length();
      Tensor weights = Array.of(l -> RationalScalar.of(1, n), n);
      out.addVert(biinvariantMean.mean(sequence, weights));
    }
    Map<Tensor, Integer> edges = new HashMap<>();
    int faceInd = 0;
    for (Tensor face : surfaceMesh.ind) {
      List<Integer> list = new ArrayList<>();
      for (int c0 = 0; c0 < face.length(); ++c0) {
        Scalar p0 = face.Get(c0);
        Scalar p1 = face.Get(Math.floorMod(c0 + 1, face.length()));
        Tensor key = Tensors.of(p0, p1);
        if (edges.containsKey(key)) {
          int index = edges.get(key);
          list.add(index);
        } else {
          Tensor sequence = Tensors.of( //
              surfaceMesh.vrt.get(p0.number().intValue()), //
              surfaceMesh.vrt.get(p1.number().intValue()));
          Tensor weights = Tensors.vector(0.5, 0.5);
          Tensor mid = biinvariantMean.mean(sequence, weights);
          int index = out.addVert(mid);
          edges.put(Tensors.of(p1, p0), index);
          list.add(index);
        }
      }
      for (int c0 = 0; c0 < face.length(); ++c0) {
        Scalar p0 = face.Get(c0);
        out.addFace(p0.number().intValue(), list.get(c0), nV + faceInd, list.get(Math.floorMod(c0 - 1, face.length())));
      }
      ++faceInd;
    }
    return out;
  }
}
