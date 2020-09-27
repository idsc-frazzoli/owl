// code by jph
package ch.ethz.idsc.tensor.ref;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface FieldClip {
  /** @return permitted lower bound */
  String min();

  /** @return permitted upper bound */
  String max();
}
