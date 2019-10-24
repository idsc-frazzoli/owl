// code by jph
package ch.ethz.idsc.owl.data.nd;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class SpecialContent implements Serializable {
  Tensor handled = Tensors.vector(99, 100);
  Tensor value;

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeObject(handled);
    out.writeObject(Tensors.vector(1, 2, 3));
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    handled = (Tensor) in.readObject();
    value = (Tensor) in.readObject();
  }

  @SuppressWarnings({ "unused", "static-method" })
  private void readObjectNoData() throws ObjectStreamException {
    System.out.println("no data");
  }
}