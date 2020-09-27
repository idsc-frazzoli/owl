// code by jph
package ch.ethz.idsc.tensor.ref;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public enum TensorReflection {
  ;
  /** @param fieldSubdivide
   * @return Optional.empty() if given fieldSubdivide is null,
   * or fields specified by given fieldSubdivide are invalid */
  public static Optional<Tensor> of(FieldSubdivide fieldSubdivide) {
    if (Objects.nonNull(fieldSubdivide))
      try {
        return Optional.of(strict(fieldSubdivide));
      } catch (Exception exception) {
        // ---
      }
    return Optional.empty();
  }

  /** @param fieldSubdivide non-null
   * @return
   * @throws Exception if parsing of strings to tensors fails */
  public static Tensor strict(FieldSubdivide fieldSubdivide) {
    return Subdivide.of( //
        Tensors.fromString(fieldSubdivide.start()), //
        Tensors.fromString(fieldSubdivide.end()), //
        fieldSubdivide.intervals());
  }

  /** @param fieldSubdivide
   * @return
   * @throws Exception if parsing of strings to scalars fails */
  public static Clip clip(FieldSubdivide fieldSubdivide) {
    return Clips.interval( //
        Scalars.fromString(fieldSubdivide.start()), //
        Scalars.fromString(fieldSubdivide.end()));
  }

  /***************************************************/
  /** @param fieldClip
   * @return Optional.empty() if given fieldClip is null,
   * or fields specified by given fieldClip are invalid */
  public static Optional<Clip> of(FieldClip fieldClip) {
    if (Objects.nonNull(fieldClip))
      try {
        return Optional.of(clip(fieldClip));
      } catch (Exception exception) {
        // ---
      }
    return Optional.empty();
  }

  /** @param fieldClip
   * @return
   * @throws Exception if parsing of strings to scalars fails */
  public static Clip clip(FieldClip fieldClip) {
    return Clips.interval( //
        Scalars.fromString(fieldClip.min()), //
        Scalars.fromString(fieldClip.max()));
  }
}
