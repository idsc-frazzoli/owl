// code by jph
package ch.ethz.idsc.owl.subdiv.surf;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.owl.math.group.Se3Geodesic;
import ch.ethz.idsc.owl.math.group.Se3Utils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** elements of R3S2 are tensors of the form
 * {{px, py, pz}, {nx, ny, nz}} */
public enum R3S2Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  private static final Tensor ID3 = IdentityMatrix.of(3);

  @Override
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    Tensor pt = p.get(0);
    Tensor pn = p.get(1);
    Tensor qt = q.get(0);
    Tensor qn = q.get(1);
    Tensor rotation = RotationMatrix3D.of(pn, qn);
    Tensor pSe3 = Se3Utils.of(ID3, pt);
    Tensor qSe3 = Se3Utils.of(rotation, qt);
    Tensor split = Se3Geodesic.INSTANCE.split(pSe3, qSe3, scalar);
    Tensor r = Se3Utils.rotation(split);
    Tensor t = split.get(Tensor.ALL, 3).extract(0, 3);
    return Tensors.of(t, r.dot(pn));
  }
}
