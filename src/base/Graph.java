package base;

import java.util.*;

/**
 * Graph class
 *
 * Implements a directed graph.  Each node has a unique non-zero id
 * which can be used as an index into a storage array.
 * 
 */
public class Graph {

//  public int nNodes() {
//    return nodes.size();
//  }

  /**
   * Get list of nodes in a graph.
   * @param list : a list of node ids, sorted by id
   */
  public void getNodeList(DArray list) {
    list.clear();

    for (int i = 0; i < nodes.size(); i++) {
      if (!nodes.exists(i)) {
        continue;
      }
      list.addInt(nodeIdFromIndex(i));
    }
  }

  /**
   * Get list of nodes in a graph.
   * @return a list of node ids, sorted by id
   */
  public DArray getNodeList() {
    DArray a = new DArray();
    getNodeList(a);
    return a;
  }

  /**
   * Convert an index to a node id
   * @param index : index 0...n-1
   * @return Node ID
   */
  private int nodeIdFromIndex(int index) {
    return index + idBase;
  }

  /**
   * Convert a node id to an index
   * @param id : node id
   * @return index 0...n-1
   */
  private int indexFromNodeId(int id) {
    if (id < idBase) {
      throw new IllegalArgumentException("*!!indexFromNodeId, id=" + id);
    }
    return id - idBase;
  }

  /**
   * Add a new node to the graph
   * @return id of new node
   */
  public int newNode() {
    return newNode(null);
  }

  /**
   * Add a new node to the graph
   * @param userData   user data to store with node, or null
   * @return id of new node
   */
  public int newNode(Object userData) {
    Node node = new Node(userData);
    int index;
    if (rNodes.isEmpty()) {
      index = nodes.size();
    } else {
      index = rNodes.popInt();
    }
    nodes.growSet(index, node);
    int id = nodeIdFromIndex(index);
    return id;
  }

  public boolean nodeExists(int id) {
    int ind = indexFromNodeId(id);
    return nodes.exists(ind);
  }

  protected Node node(int id) {
    int ind = indexFromNodeId(id);
    if (true) {
      if (!nodes.exists(ind)) {
        throw new IllegalArgumentException("*Attempt to get non-existent node "
            + id + " in graph\n" + this);
      }
    } else {
      Tools.ASSERT(nodes.exists(ind));
    }
    return (Node) nodes.get(ind);
  }

  /**
   * Add a neighbor to a node by creating a directed edge to it
   * @param src : source node
   * @param dest : destination node
   * @param edgeData : data to store with edge, or null
   * @param pos : position in source's neighbor list to insert, or -1 for end
   * @param replaceFlag : if true, existing edge is replaced; else new inserted
   * @return neighbor index
   */
  public int addEdge(int src, int dest, Object edgeData, int pos,
      boolean replaceFlag) {
    Node sn = node(src);
    return sn.addNeighbor(dest, edgeData, pos, replaceFlag);
  }

  //  public void redirectEdge(int src, int 
  /**
   * Add edges between two nodes
   * @param a : id of first node
   * @param b : id of second node
   * @param edgeDataAB : data to store with edge from a->b, or null
   * @param edgeDataBA : data to store with edge from a->b, or null
   */
  public void addEdgesBetween(int a, int b, Object edgeDataAB, Object edgeDataBA) {
    addEdge(a, b, edgeDataAB);
    addEdge(b, a, edgeDataBA);
  }

  /**
   * Add a neighbor to a node by creating a directed edge to it
   * @param src : source node
   * @param dest : destination node
   */
  public void addEdge(int src, int dest) {
    addEdge(src, dest, null);
  }

  /**
   * Add a neighbor to a node by creating a directed edge to it
   * @param src : source node
   * @param dest : destination node
   * @param edgeData : data to store with edge, or null
   * @return neighbor index
   */
  public int addEdge(int src, int dest, Object edgeData) {
    return addEdge(src, dest, edgeData, -1, false);
  }

  /**
   * Determine if a node has a particular neighbor
   * @param src : id of source node
   * @param dest : id of destination node to look for
   * @return index of src neighbor leading to dest, or -1
   */
  public int hasNeighbor(int src, int dest) {
    int nIndex = -1;
    Node sn = node(src);
    for (int i = 0; i < sn.nTotal(); i++) {
      if (sn.neighbor(i) == dest) {
        nIndex = i;
        break;
      }
    }
    return nIndex;
  }

  /**
   * Remove a neighbor from a node
   * @param src : id of source node
   * @param index : index of neighbor to remove
   */
  public void removeEdge(int src, int index) {
    Node sn = node(src);
    sn.removeIndex(index);
  }

  /**
   * Remove an edge between two nodes, if it exists
   * @param src : id of source node
   * @param dest : id of dest node
   * @param all : if true, removes all such edges; if false,
   *  removes just the first one found
   * @return number of edges removed
   */
  public int findAndRemoveEdge(int src, int dest, boolean all) {
    int count = 0;
    for (int i = 0; i < nCount(src); i++) {
      if (neighbor(src, i) == dest) {
        node(src).removeIndex(i--);
        count++;
        if (!all) {
          break;
        }
      }
    }
    return count;
  }

  public void removeEdgesBetween(int n0, int n1) {
    findAndRemoveEdge(n0, n1, true);
    findAndRemoveEdge(n1, n0, true);
  }

  //  /**
  //   * @deprecated
  //   */
  //  public void removeLoops() {
  //    DArray nl = getNodeList();
  //    for (int i = 0; i < nl.size(); i++) {
  //      int n = nl.getInt(i);
  //      for (int j = nCount(n) - 1; j >= 0; j--) {
  //        if (neighbor(n, j) == n) {
  //          removeEdge(n, j);
  //        }
  //      }
  //    }
  //  }

  /**
   * Clear the graph
   */
  public void clear() {
    nodes.clear();
    //    edges.clear();
  }

  /**
   * Get number of neighbors for a node
   * @param id : id of node
   */
  public int nCount(int id) {
    return node(id).nTotal();
  }

  /**
   * Get neighbor for a node
   * @param id : id of node
   * @param nIndex : index of neighbor
   * @return id of neighbor
   */
  public int neighbor(int id, int nIndex) {
    return node(id).neighbor(nIndex);
  }

  /**
   * Get user data stored with node
   * @param nodeId id of node
   * @return user data, or null if none was stored
   */
  public Object nodeData(int nodeId) {
    return node(nodeId).userData();
  }

  /**
   * Set user data for a node
   * @param nodeId   id of node
   * @param data   user data to store with node, or null
   */
  public void setNodeData(int nodeId, Object data) {
    node(nodeId).setUserData(data);
  }

  /**
   * Get user data stored with edge
   * @param nodeId : id of node at start of edge
   * @param nIndex : index of edge in node's edge list
   * @return user data, or null if none was stored
   */
  public Object edgeData(int nodeId, int nIndex) {
    return node(nodeId).edgeData(nIndex);
  }

  /**
   * Delete a node.  Doesn't delete any edges that other nodes may
   * have to this node!
   * @param id : id of node to delete
   */
  public void delete(int id) {
    int ind = indexFromNodeId(id);
    nodes.set(ind, null);
    rNodes.push(id);
  }

  /**
   * Construct description
   * @return String
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Graph\n");
    for (int i = 0; i < nodes.size(); i++) {
      if (!nodes.exists(i)) {
        continue;
      }

      int id = nodeIdFromIndex(i);
      Node n = node(id);
      sb.append(" ");
      sb.append(id);
      sb.append(": ");
      sb.append(n.toString());
      sb.append('\n');
    }
    return sb.toString();
  }

  /**
   * Default constructor.
   * 
   * Sets id base to 30
   */
  public Graph() {
    this(30);
  }

  /**
   * Constructor
   * @param idBase : base id to use for nodes of this graph
   */
  public Graph(int idBase) {
    this.idBase = idBase;
  }

  /**
   * Sort the edges leaving a node
   * @param node
   * @param c
   */
  public void sortEdges(int node, Comparator c) {

    Object[] o1 = new Object[3];
    Object[] o2 = new Object[3];

    Node n = node(node);
    for (int i = 0; i < n.nTotal(); i++)
      for (int j = i + 1; j < n.nTotal(); j++) {
        o1[0] = this;
        o1[1] = new Integer(node);
        o1[2] = new Integer(i);

        o2[0] = this;
        o2[1] = o1[1]; //new Integer(node);
        o2[2] = new Integer(j);

        if (c.compare(o1, o2) > 0) {
          n.swap(i, j);
        }
      }
  }

  private DArray nodes = new DArray();
  private DQueue rNodes = new DQueue();
  private int idBase;

  /**
   * Node class for graph
   */
  private static class Node {

    public Node(Object userData) {
      this.data = userData;
    }

    /**
     * Construct description
     * @return String
     */
    public String toString() {
      StringBuilder sb = new StringBuilder();
      if (false) {
        Tools.warn("disabled");
        sb.append("disabled");
        return sb.toString();
      }

      boolean big = false;
      final String padding = "   --> ";
      for (int i = 0; i < nTotal(); i++) {
        if (big) {
          sb.append(padding);
        } else {
          Tools.addSp(sb);
        }
        sb.append(neighbor(i));
        if (true) {
          Tools.warn("not plotting nbr data");
        } else {
          Object eData = edgeData(i);
          if (eData != null) {
            sb.append(':');
            String ns = eData.toString();
            sb.append(ns);
            if ((i == 0 && ns.length() > 10)) {
              big = true;
              sb.insert(0, "\n" + padding);
            }
            if (big) {
              sb.append('\n');
            }
          }
        }
      }
      if (data != null) {
        Tools.tab(sb, 5 * 3);
        sb.append(">");
        sb.append(data);
      }
      return sb.toString();
    }

    /**
     * Get number of neighbors adjacent to node
     * @return int : # edges
     */
    public int nTotal() {
      return neighbors.size();
    }

    /**
     * Get neighbor node
     * @param nIndex : edge index (0..nTotal()-1)
     * @return id of neighbor node
     */
    public int neighbor(int nIndex) {
      GEdge e = getEdge(nIndex);
      return e.dest();
    }

    /**
     * Get GEdge object from neighbor array
     * @param nIndex : index of neighbor
     * @return GEdge read from array
     */
    private GEdge getEdge(int nIndex) {
      if (!neighbors.exists(nIndex))
        throw new IllegalArgumentException("node has no edge " + nIndex + ":\n"
            + this);
      return (GEdge) neighbors.get(nIndex);
    }

    public void setUserData(Object data) {
      this.data = data;
    }

    /**
     * Get user data from edge
     * @param nIndex : edge index (0..nTotal()-1)
     * @return user data
     */
    public Object edgeData(int nIndex) {
      return getEdge(nIndex).data();
    }

    /**
     * Add a neighbor to this node
     * @param id : id of neighbor
     * @param edgeData : data to store with edge, or null
     * @param insertPos : position in neighbor list to insert at, or -1 to
     *   append to the end
     * @param replaceFlag : if true, neighbor replaces old instead of being inserted
     * @return neighbor index
     */
    public int addNeighbor(int neighborId, Object edgeData, int insertPos,
        boolean replaceFlag) {
      //System.out.println("addNeighbor id="+neighborId+" insertPos="+insertPos+" rep="+replaceFlag);
      if (replaceFlag) {
        neighbors.set(insertPos, new GEdge(neighborId, edgeData));
      } else {
        if (insertPos < 0)
          insertPos = neighbors.size();
        neighbors.add(insertPos, new GEdge(neighborId, edgeData));
      }
      return insertPos;
    }

    /**
     * Remove a neighbor from a particular position in the list.
     * @param loc : location to delete
     */
    public void removeIndex(int loc) {
      neighbors.remove(loc);
      //      neighbors.delete(loc, 1);
    }

    /**
     * Get user data stored with node
     * @return Object, or null if no data is stored here
     */
    public Object userData() {
      return data;
    }

    //    private static class GEdgeComparator implements Comparator {
    //      public GEdgeComparator(Graph g, int srcNode, Comparator c) {
    //        this.c = c;
    //        this.srcNode = srcNode;
    //        this.g = g;
    //      }
    //      private Comparator c;
    //      private Graph g;
    //      private int srcNode;
    //
    //      public int compare(Object o1, Object o2) {
    //        Object[] n1 = new Object[3];
    //        n1[0] = g;
    //        n1[1] = new Integer(srcNode);
    //        n1[2] = new Integer(((GEdge) o1).dest());
    //        Object[] n2 = new Object[3];
    //        n2[0] = g;
    //        n2[1] = n1[1];
    //        n2[2] = new Integer(((GEdge) o2).dest());
    //        return c.compare(n1, n2);
    //      }
    //    }

    //    public void sortEdges(Graph g, int srcNode, Comparator c) {
    //      neighbors.sort(new GEdgeComparator(g, srcNode, c));
    //    }

    public void swap(int ni, int nj) {
      neighbors.swap(ni, nj);
    }

    // neighbor list; stores GEdge objects
    private DArray neighbors = new DArray();

    // user data stored with node
    private Object data;
  }

  /**
   * Edge class for graphs
   */
  private static class GEdge {
    public GEdge(int destNode, Object data) {
      this.destNode = destNode;
      this.data = data;
    }

    public int dest() {
      return destNode;
    }

    public Object data() {
      return data;
    }

    // destination of edge
    private int destNode;

    // user data stored with edge
    private Object data;
  }
  public String printRootedTree(int root) {
    StringBuilder sb = new StringBuilder();
    printRootedTree(root, sb, 0);
    return sb.toString();
  }

  private void printRootedTree(int root, StringBuilder sb, int depth) {
    int nKids = nCount(root);

    //    public Object getPrintObject(Forest forest, int nodeId) {
    Object obj = nodeData(root);
    //    if (printTreeNode != null) {
    //      Object o2 = printTreeNode.getNodeDescription(obj);
    //      obj = o2;
    //    }
    if (obj != null) {
      for (int i = 0; i < depth; i++)
        sb.append("    ");
      //      sb.append(Tools.tv(obj) );
      sb.append(obj);
      sb.append('\n');
    }
    for (int i = 0; i < nKids; i++) {
      printRootedTree(neighbor(root, i), sb, depth + 1);
    }
  }

}