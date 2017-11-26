// code by jph
package ch.ethz.idsc.owl.data;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** the annotation or a class or method means that neither the API nor
 * the functionality should be altered.
 * 
 * modifications that are not visible from the outside such as enhancements
 * in the implementations are permitted. */
@Documented
@Retention(CLASS)
@Target({ TYPE, METHOD, CONSTRUCTOR })
public @interface DontModify {
}
