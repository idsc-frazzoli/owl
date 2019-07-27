// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.util.Iterator;

import ch.ethz.idsc.sophus.crv.clothoid.ClothoidCurvature;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;

/** class exists for */
/* package */ enum ClothoidCurvatures {
  ;
  /** @param tensor
   * @return vector of length of given tensor with curvature estimates */
  public static Tensor of(Tensor tensor) {
    Tensor vector = Unprotect.empty(tensor.length());
    Iterator<Tensor> iterator = tensor.iterator();
    if (iterator.hasNext()) {
      ClothoidCurvature clothoidCurvature = null;
      Tensor p = iterator.next();
      while (iterator.hasNext()) {
        clothoidCurvature = new ClothoidCurvature(p, p = iterator.next());
        vector.append(clothoidCurvature.head());
      }
      vector.append(clothoidCurvature.tail());
    }
    return vector;
  }
}
