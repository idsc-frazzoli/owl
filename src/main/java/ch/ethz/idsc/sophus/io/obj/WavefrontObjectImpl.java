// code by jph
package ch.ethz.idsc.sophus.io.obj;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class WavefrontObjectImpl implements WavefrontObject, Serializable {
  private final String string;
  private final Tensor faces = Tensors.empty();
  private final Tensor normals = Tensors.empty();

  /** @param string may be null */
  public WavefrontObjectImpl(String string) {
    this.string = string;
  }

  @Override // from WavefrontObject
  public String name() {
    return string;
  }

  @Override // from WavefrontObject
  public Tensor faces() {
    return faces.unmodifiable();
  }

  @Override // from WavefrontObject
  public Tensor normals() {
    return normals.unmodifiable();
  }

  void append_f(String line) {
    String[] nodes = line.split(" +");
    Tensor iv = Tensors.empty();
    Tensor in = Tensors.empty();
    for (int index = 0; index < nodes.length; ++index) {
      String[] node = StaticHelper.slash(nodes[index]);
      iv.append(RealScalar.of(Integer.parseInt(node[0]) - 1));
      if (!node[2].isEmpty())
        in.append(RealScalar.of(Integer.parseInt(node[2]) - 1));
    }
    faces.append(iv);
    if (0 < in.length())
      normals.append(in);
  }
}
