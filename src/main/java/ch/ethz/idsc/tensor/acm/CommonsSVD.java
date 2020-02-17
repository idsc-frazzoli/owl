// code by jph
package ch.ethz.idsc.tensor.acm;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.mat.SingularValueDecomposition;

public class CommonsSVD implements SingularValueDecomposition {
  /** @param matrix of arbitrary dimensions
   * @return */
  public static SingularValueDecomposition of(Tensor matrix) {
    return new CommonsSVD(matrix);
  }

  /***************************************************/
  private final org.apache.commons.math3.linear.SingularValueDecomposition svd;

  private CommonsSVD(Tensor matrix) {
    RealMatrix realMatrix = new Array2DRowRealMatrix(Primitives.toDoubleArray2D(matrix));
    svd = new org.apache.commons.math3.linear.SingularValueDecomposition(realMatrix);
  }

  @Override // from SingularValueDecomposition
  public Tensor getU() {
    return Tensors.matrixDouble(svd.getU().getData());
  }

  @Override // from SingularValueDecomposition
  public Tensor values() {
    return Tensors.vectorDouble(svd.getSingularValues());
  }

  @Override // from SingularValueDecomposition
  public Tensor getV() {
    return Tensors.matrixDouble(svd.getV().getData());
  }
}
