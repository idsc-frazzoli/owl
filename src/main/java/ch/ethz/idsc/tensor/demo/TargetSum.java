// code by jph
package ch.ethz.idsc.tensor.demo;

/* package */ enum TargetSum {
  ;
  /** @param array
   * @param target sum
   * @return whether there exists a subset of array
   * elements that sums up to given target sum */
  public static boolean check(int[] array, int target) {
    return check(array, target, 0);
  }

  private static boolean check(int[] array, int target, int index) {
    if (array.length == index)
      return target == 0;
    return check(array, target - array[index], index + 1) //
        || check(array, target, index + 1);
  }
}
