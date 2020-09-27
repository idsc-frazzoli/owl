// code by jph
package ch.ethz.idsc.tensor.ref;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.Scalar;

/** denotes that a field of type {@link Scalar} should satisfy {@link IntegerQ} */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface FieldIntegerQ {
  // ---
}
