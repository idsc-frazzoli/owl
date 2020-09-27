// code by jph
package ch.ethz.idsc.tensor.ref;

import java.awt.event.ActionListener;

import ch.ethz.idsc.tensor.Tensor;

/** Design rationale:
 * 
 * {@link ActionListener#actionPerformed(java.awt.event.ActionEvent)} */
public interface TensorListener {
  /** @param tensor */
  void tensorReceived(Tensor tensor);
}
