package snaptree;

import base.*;
import testbed.*;

/**
 */
class BPath {

	public PathNode removeTail() {
		return (PathNode) path.pop();
	}

	/**
	 * Get string describing object
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("BPath nodeIds: ");
		for (int i = 0; i < height(); i++) {
			PathNode n = (PathNode) path.get(i);
			if (i > 0) {
				sb.append("-->");
			}
			sb.append(n.toString());
		}
		sb.append("\n");

		sb.append(" pages: ");
		for (int i = 0; i < height(); i++) {
			PathNode n = (PathNode) path.get(i);
			sb.append("...");
			sb.append(n.page().toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * Get height of path
	 * 
	 * @return int
	 */
	public int height() {
		return path.size();
	}

	/**
	 * Construct a path
	 * 
	 * @param value :
	 *          array containing value
	 * @param offset :
	 *          offset to value within array
	 */
	public BPath(Object item) {
		this.entry = item;
	}

	public void addPage(BPage page) {
		final boolean db = false;
		
		
		int insPos = page.findInsertionPoint(entry, false);
		
		if (db && T.update())
			T.msg("BPath.addPage page="+page+"\n insPos= "+insPos+"\n entry="+entry);
		path.add(new PathNode(page, insPos));
	}

	public BPage tailPage() {
		return tail().page();
	}

	public BPage getTailPtr() {
		PathNode t = tail();
		return t.page().getPtr(t.position());
	}

	public PathNode tail() {
		return (PathNode) path.last();
	}

	private DArray path = new DArray();

	private Object entry;
}
