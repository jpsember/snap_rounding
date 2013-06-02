package snaptree;

import java.util.*;
import base.*;

public class Stats {
  /*! .enum  .public
  tree endpt isect hotpixel nsegs total
  */

  public static final int TREE = 0; //!
  public static final int ENDPT = 1; //!
  public static final int ISECT = 2; //!
  public static final int HOTPIXEL = 3; //!
  public static final int NSEGS = 4; //!
  public static final int TOTAL = 5; //!
  /* !*/

  public static void reset() {
    Arrays.fill(totals, 0);
  }

  public static void clearHistory() {
    trials = 0;
    kSum = 0;
    hSum = 0;
  }

  public static void event(int type, String desc) {
    totals[type]++;
  }
  public static void event(int type) {
    event(type, null);
  }

  public static int count(int type) {
    return totals[type];
  }

  //  private static String[] labels = { "Tree", "Endpt", "Isect", "Hotpix",
  //      "# Segments", };

  //  private static int[] sum = { NSEGS, ISECT, HOTPIXEL, TREE, };

  private static StringBuilder sb;

  public static String summary() {
    sb = new StringBuilder();

    pr("n", totals[NSEGS]);
    pr("h", totals[HOTPIXEL]);
    int hMax = totals[NSEGS] * 2 + totals[ISECT];

    pr("h*", hMax);
    int t = totals[TREE];
    pr("t", t);

    //    for (int j = 0; j < sum.length; j++) {
    //      int i = sum[j];
    //      pr( labels[i],totals[i]);
    ////      sb.append(Tools.f(labels[i] + ":", 10));
    ////      sb.append(Tools.f(totals[i]));
    ////      sb.append('\n');
    //    }
    //   
    int n = totals[NSEGS];
    int h = totals[HOTPIXEL];
    double lgn = Math.log(n) / Math.log(2); //))MyMath.lg(n);
    //    double actual = (n+h)*lgn;
    //    double worstcase = (n+hMax)*lgn;

    //   pr("(n+h )lg n", actual);
    // pr("(n+h*)lg n", worstcase);

    //  pr("t / (n+h*)lg n ",t / worstcase);

    double s = 1.824;
    double k = t / (s * lgn) - n;

    pr("k", k);
    pr("k/h", k / h);

    kSum += k / h;
    hSum += h;
    trials++;

    pr("trials",trials);
    pr("mean k/h", kSum / trials);
    pr("mean h", hSum / trials);

    return sb.toString();
  }
  private static void pr(String label, int val) {
    pr(label, Tools.f(val));
  }
  private static void pr(String label, double val) {
    pr(label, Tools.f(val));
  }

  private static void pr(String label, String val) {
    sb.append(Tools.f(label + ":", 15));
    sb.append(val);
    sb.append('\n');
  }

  private static double kSum;
  private static int hSum;
  private static int trials;

  private static int[] totals = new int[TOTAL];
//private static boolean active;
//
//  public static boolean active() {
//    return active;
//  }
}
