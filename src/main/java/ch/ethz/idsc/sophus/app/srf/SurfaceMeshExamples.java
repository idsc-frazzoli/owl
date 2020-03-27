// code by jph
package ch.ethz.idsc.sophus.app.srf;

import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.srf.SurfaceMesh;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

/** Hint:
 * implementation exists only for evaluation purposes
 * class may be removed in future releases */
/* package */ enum SurfaceMeshExamples {
  ;
  public static SurfaceMesh unitQuad() {
    SurfaceMesh surfaceMesh = new SurfaceMesh();
    CirclePoints.of(4).stream() //
        .map(xy -> xy.append(RealScalar.ZERO)) //
        .forEach(surfaceMesh::addVert);
    surfaceMesh.addFace(0, 1, 2, 3);
    return surfaceMesh;
  }

  public static SurfaceMesh quads2() {
    SurfaceMesh surfaceMesh = new SurfaceMesh();
    surfaceMesh.addVert(Tensors.vector(0, 0, 0));
    surfaceMesh.addVert(Tensors.vector(0, 2, 0));
    surfaceMesh.addVert(Tensors.vector(2, 0, 0));
    surfaceMesh.addVert(Tensors.vector(2, 2, 0));
    surfaceMesh.addVert(Tensors.vector(4, 0, 0));
    surfaceMesh.addVert(Tensors.vector(4, 2, 0));
    surfaceMesh.addFace(0, 2, 3, 1);
    surfaceMesh.addFace(2, 4, 5, 3);
    return surfaceMesh;
  }

  public static SurfaceMesh quads3() {
    SurfaceMesh surfaceMesh = quads2();
    surfaceMesh.addVert(Tensors.vector(0, 4, 0));
    surfaceMesh.addVert(Tensors.vector(2, 4, 0));
    surfaceMesh.addFace(1, 3, 7, 6);
    return surfaceMesh;
  }

  public static SurfaceMesh quads5() {
    SurfaceMesh surfaceMesh = quads3();
    surfaceMesh.addVert(Tensors.vector(3, 6, 0));
    surfaceMesh.addVert(Tensors.vector(4, 4, 0));
    surfaceMesh.addVert(Tensors.vector(6, 4, 0));
    surfaceMesh.addFace(3, 9, 8, 7);
    surfaceMesh.addFace(3, 5, 10, 9);
    return surfaceMesh;
  }

  public static SurfaceMesh quads6() {
    SurfaceMesh surfaceMesh = quads5();
    surfaceMesh.addVert(Tensors.vector(6, 6, 0));
    surfaceMesh.addFace(9, 10, 11, 8);
    return surfaceMesh;
  }
}
