package snap;

import java.awt.Color;
import base.*;
import testbed.*;
import java.util.*;

public class SnapArrangement {
  public SnapArrangement() {
  }

  public SnapArrangement(Segment[] segs) {
    for (int i = 0; i < segs.length; i++) {
      Segment s = segs[i];
      int pid = 0;
      for (int j = 0; j < s.nSnapPoints(); j++) {
        int id = addNode(s.getSnapPoint(j));
        if (pid != 0)
          addArc(pid, id);
        pid = id;
      }
    }
  }

  /**
   * Render the arrangement
   */
  public void render() {

//    final boolean arrows = true;


//    vp V = TestBed.view();

    V.pushColor(new Color(80, 20, 200));

    Grid g = Main.grid();
   boolean withDots =  Main.highDetail(); //(g.cellSize() > 3);
   

    DArray a = graph.getNodeList();
//    double len = V.getScale() * 12;
    for (int i = 0; i < a.size(); i++) {
      int id = a.getInt(i);
      IPoint2 pt = hotPixel(id);
      FPoint2 wpt = g.toView(pt);

//      Streams.out.println("node "+i+" id="+id+" pt="+pt+" wpt="+wpt);
      if (withDots)
        V.fillCircle(wpt, V.getScale() * .4);

      for (int j = 0; j < graph.nCount(id); j++) {
        IPoint2 pt2 = hotPixel(graph.neighbor(id, j));
        
        
//        Streams.out.println("snaparrangement, pt2="+pt2);
//        if (f)
//          pt2 = IPointUtils.flipIfNec(pt2);
        FPoint2 wpt2 = g.toView(pt2);
        V.drawLine(wpt, wpt2);
        //        if (arrows) {
        //          double t = FPoint2.distance(wpt, wpt2);
        //          if (t > len * 2) {
        //            double an = MyMath.polarAngle(wpt, wpt2);
        //            final double angle = Math.PI * .9;
        //            FPoint2 a0 = MyMath.ptOnCircle(wpt2, an - angle, len);
        //            FPoint2 a1 = MyMath.ptOnCircle(wpt2, an + angle, len);
        //            V.drawLine(a0, wpt2);
        //            V.drawLine(a1, wpt2);
        //          }
        //        }
      }

    }
    V.popColor();
  }

  public IPoint2 hotPixel(int id) {
    return (IPoint2) graph.nodeData(id);
  }

  public IPoint2 hotPixel(IPoint2 pixel) {
    return hotPixel(addNode(pixel));
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("SnapArrangement ");
    sb.append(graph.toString());

    return sb.toString();
  }

  /**
   * Add a node to the arrangement, if it doesn't already exist
   * 
   * @param pixel :
   *          location of node
   * @return id of node
   */
  public int addNode(IPoint2 pixel) {
    Integer id = (Integer) nodes.get(pixel);
    if (id == null) {
      IPoint2 f = new IPoint2(pixel);
      id = new Integer(graph.newNode(f));
      if (totalNodes == 0)
        idBase = id.intValue();
      totalNodes++;
      nodes.put(pixel, id);
    }
    return id.intValue();
  }

  public Graph getGraph() {
    return graph;
  }

  public int nNodes() {
    return totalNodes;
  }
  public int idBase() {
    return idBase;
  }

  private int idBase;
  private int totalNodes;

  //  public int[] nodeList() {
  //    return graph.getNodeList().toIntArray();
  //  }

  //	/**
  //	 * Remove arc from arrangement
  //	 * 
  //	 * @param src :
  //	 *          source id
  //	 * @param dest :
  //	 *          destination id
  //	 */
  //	private void removeArc(int src, int dest) {
  //		removeHalfArc(src, dest);
  //		removeHalfArc(dest, src);
  //	}
  //
  //	private void removeHalfArc(int src, int dest) {
  //		// Find arc in src list
  //		for (int pos = 0;; pos++) {
  //			int arc = graph.neighbor(src, pos);
  //			if (arc == dest) {
  //				graph.removeEdge(src, pos);
  //				break;
  //			}
  //		}
  //	}

  /**
   * Add an arc between two nodes. The arc is only added if the nodes are
   * distinct, and no arc already exists.
   * 
   * @param src :
   *          source node id
   * @param dest :
   *          dest node id
   */
  public void addArc(int src, int dest) {
    if (!addHalfArc(src, dest))
      return;
    addHalfArc(dest, src);
  }

  /**
   * Add a directed arc (half edge) between two nodes
   * @param src
   * @param dest
   * @return true if arc did not already exist
   */
  private boolean addHalfArc(int src, int dest) {
    boolean added = false;

    outer: do {
      // Find insertion point for arc
      int pos = 0;
      for (; pos < graph.nCount(src); pos++) {
        int arc = graph.neighbor(src, pos);
        if (arc == dest)
          break outer;
        if (arc > dest)
          break;
      }
      graph.addEdge(src, dest, null, pos, false);
      added = true;
    } while (false);
    return added;
  }

  //	/**
  //	 * Determine canonical version of this arrangement.
  //	 * 
  //	 * This is the arrangement where the nodes are sorted lexicographically, and
  //	 * arcs only exist from lower -> higher nodes.
  //	 * 
  //	 * @return
  //	 */
  //	public SnapArrangement canonical() {
  //		SnapArrangement canonical = new SnapArrangement();
  //		DArray nodeList = graph.getNodeList();
  //
  //		// Generate sorted list of hot pixels, and add each as a node to the
  //		// canonical set
  //		{
  //			SortedPixels hotPixelSet = new SortedPixels();
  //			for (int i = 0; i < nodeList.size(); i++)
  //				hotPixelSet.add(hotPixel(nodeList.getInt(i)));
  //			Iterator it = hotPixelSet.iterator();
  //			while (it.hasNext()) {
  //				canonical.addNode((IPoint2) it.next());
  //			}
  //		}
  //
  //		// For each arc in the original arrangement, add it to the canonical one,
  //		// so that arcs are sorted by destination node
  //		for (int i = 0; i < nodeList.size(); i++) {
  //			int src = nodeList.getInt(i);
  //			IPoint2 srcP = hotPixel(src); 
  //			int csrc = canonical.addNode(srcP);
  //
  //			for (int j = 0; j < graph.nCount(src); j++) {
  //				int dest = graph.neighbor(src, j);
  //				IPoint2 destP = hotPixel(dest); 
  //				int cdest = canonical.addNode(destP);
  //
  //				if (IPoint2.compare(srcP, destP) < 0) {
  //					canonical.addArc(csrc, cdest);
  //				} else {
  //					canonical.addArc(cdest, csrc);
  //				}
  //			}
  //		}
  //
  //		return canonical;
  //	}

  //	/**
  //	 * Filter out nodes with degree = 2 that are not endpoints.
  //	 */
  //	public void filterDegree2Nodes() {
  //
  //		final boolean db = false;
  //
  //		if (db && T.update())
  //			T.msg("filterDegree2Nodes: \n" + this);
  //		if (db && T.update())
  //			T.msg("graph:\n" + graph);
  //
  //		DArray nodeList = graph.getNodeList();
  //		if (nodeList.isEmpty())
  //			return;
  //
  //		for (int i = 0; i < nodeList.size(); i++) {
  //			int nodeId = nodeList.getInt(i);
  //			IPoint2 hp = hotPixel(nodeId);
  //
  //			if (db && T.update())
  //				T.msg("node " + nodeId + ":\n" + hp);
  //
  //			// Is this node to be filtered?
  //			if (hp.isEndpoint())
  //				continue;
  //			if (graph.nCount(nodeId) > 2)
  //				continue;
  //
  //			if (graph.nCount(nodeId) != 2)
  //				T.msg("node count for " + nodeId + " is " + graph.nCount(nodeId));
  //
  //			int dest0 = graph.neighbor(nodeId, 0), dest1 = graph.neighbor(nodeId, 1);
  //
  //			removeArc(nodeId, dest0);
  //			removeArc(nodeId, dest1);
  //			addArc(dest0, dest1);
  //
  //			if (db && T.update())
  //				T.msg("deleting pixel " + hp + ", id=" + nodeId);
  //			nodes.remove(hp); // .pixel());
  //			graph.delete(nodeId);
  //
  //		}
  //		if (db && T.update())
  //			T.msg("after filter:\n" + this);
  //	}

  //	/**
  //	 * Mark endpoints of segments as distinguished
  //	 * 
  //	 * @param segs
  //	 */
  //	public void markEndpoints(Segment[] segs) {
  //		for (int i = 0; i < segs.length; i++) {
  //			Segment s = segs[i];
  //			for (int j = 0; j < 2; j++) {
  //				hotPixel(s.pt (j)).setEndpoint();
  //			}
  //		}
  //	}

  // Map to determine what node, if any, is associated with a pixel
  private Map nodes = new HashMap();

  // The underlying graph we are using to store the arrangement within
  private Graph graph = new Graph();
}
