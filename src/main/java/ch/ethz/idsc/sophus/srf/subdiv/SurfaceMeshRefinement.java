// code by jph
package ch.ethz.idsc.sophus.srf.subdiv;

import ch.ethz.idsc.sophus.srf.SurfaceMesh;

@FunctionalInterface
public interface SurfaceMeshRefinement {
  /** @param surfaceMesh
   * @return */
  SurfaceMesh refine(SurfaceMesh surfaceMesh);
}
