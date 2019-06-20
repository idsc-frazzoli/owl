// code by jph
package ch.ethz.idsc.sophus.surf.subdiv;

import ch.ethz.idsc.tensor.Tensors;

public enum SurfaceMeshes {
  ;
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
    SurfaceMesh surfaceMesh = new SurfaceMesh();
    surfaceMesh.addVert(Tensors.vector(0, 0, 0));
    surfaceMesh.addVert(Tensors.vector(0, 2, 0));
    surfaceMesh.addVert(Tensors.vector(2, 0, 0));
    surfaceMesh.addVert(Tensors.vector(2, 2, 0));
    surfaceMesh.addVert(Tensors.vector(4, 0, 0));
    surfaceMesh.addVert(Tensors.vector(4, 2, 0));
    surfaceMesh.addVert(Tensors.vector(0, 4, 0));
    surfaceMesh.addVert(Tensors.vector(2, 4, 0));
    surfaceMesh.addFace(0, 2, 3, 1);
    surfaceMesh.addFace(2, 4, 5, 3);
    surfaceMesh.addFace(1, 3, 7, 6);
    return surfaceMesh;
  }

  public static SurfaceMesh quads5() {
    SurfaceMesh surfaceMesh = new SurfaceMesh();
    surfaceMesh.addVert(Tensors.vector(0, 0, 0));
    surfaceMesh.addVert(Tensors.vector(0, 2, 0));
    surfaceMesh.addVert(Tensors.vector(2, 0, 0));
    surfaceMesh.addVert(Tensors.vector(2, 2, 0));
    surfaceMesh.addVert(Tensors.vector(4, 0, 0));
    surfaceMesh.addVert(Tensors.vector(4, 2, 0));
    surfaceMesh.addVert(Tensors.vector(0, 4, 0));
    surfaceMesh.addVert(Tensors.vector(2, 4, 0));
    surfaceMesh.addVert(Tensors.vector(3, 6, 0));
    surfaceMesh.addVert(Tensors.vector(4, 4, 0));
    surfaceMesh.addVert(Tensors.vector(6, 4, 0));
    surfaceMesh.addFace(0, 2, 3, 1);
    surfaceMesh.addFace(2, 4, 5, 3);
    surfaceMesh.addFace(1, 3, 7, 6);
    surfaceMesh.addFace(3, 9, 8, 7);
    surfaceMesh.addFace(3, 5, 10, 9);
    return surfaceMesh;
  }
}
