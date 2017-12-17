// code by jph
package ch.ethz.idsc.owl.data;

// placement of class not final
@DontModify
public enum GlobalAssert {
  ;
  /** throws an exception if valid == false
   * 
   * @param valid */
  // DO NOT MODIFY THIS FUNCTION BUT ADD ANOTHER FUNCTION IF CHANGE IS REQUIRED
  public static void that(boolean valid) {
    if (!valid)
      throw new RuntimeException();
  }
}
