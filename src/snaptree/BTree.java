package snaptree;

import base.*;
import java.util.*;
import testbed.*;
public class BTree {

	/**
	 * Compare two keys.
	 * 
	 * @param a :
	 *          first object, or null for smallest possible item
	 * @param b :
	 *          second object, or null for smallest possible item
	 * @return int : the standard Comparable interface code (i.e. a - b)
	 */
	int compareItems(Object /* Item */a, Item b) {
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}
		return comparator.compare(a, b);
	}

	/**
	 * Get maximium pointers stored in an index page.
	 * 
	 * @return int
	 */
	final int maxKeysPerIndexPage() {
		return 1 + maxKeysPerLeafPage();
	}

	/**
	 * Get maximum number of values stored in a leaf page
	 * 
	 * @return int
	 */
	int maxKeysPerLeafPage() {
		return 4;
	}

	/**
	 * Output string if tracing active
	 * 
	 * @param s
	 *          String
	 */
	public void trace(String s) {
		if (trace) {
			Streams.out.println(s);
			if (T.update()) {
				T.msg(s);
			}
		}
	}

	/**
	 * Constructor
	 */
	public BTree(Comparator c) {
		createRoot();
		setComparator(c);
		// inf = new Inf("BTree",300000);
	}

	// private Inf inf;
	//
	// void infTest() {
	// if (inf != null) {
	// inf.update();
	// }
	// }

	public BTree() {
		this(null);
	}

	protected void setComparator(Comparator c) {
		this.comparator = c;
		// handleId = 20;
	}

	/**
	 * Set tracing status
	 * 
	 * @param f :
	 *          true to turn on tracing
	 */
	public void setTrace(boolean f) {
		// trace = f;
	}

	/**
	 * Construct a BPage (i.e. the BPage factory)
	 * 
	 * @param id :
	 *          id to assign to page
	 * @param isLeaf :
	 *          true if it's to be a leaf page (vs. an interior/index page)
	 * @return BPage
	 */
	protected BPage constructPage(int id, boolean isLeaf) {
		return new BPage(this, id, isLeaf);
	}

	/**
	 * Create a root node for the tree. Allocates a new page, increments the tree
	 * height.
	 * 
	 * @return BPage
	 */
	private BPage createRoot() {
		int rootId = getNewPageId();
		rootPage = constructPage(rootId, treeHeight == 0);
		treeHeight++;
		return rootPage;
	}

	public BPage getRootPage() {
		return rootPage;
	}

	public void remove(Item item) {
		if (trace) {
			trace("\n\nBTree.remove, item=" + item);
			testIntegrity("before remove " + item);
		}

		// construct path to leaf node that would contain the key
		if (trace) {
			BPath path = findKey(item, false);
			trace("Path without changes:\n" + path);
		}

		BPath path = findKey(item, true);
		if (trace) {
			trace("Path with changes:   \n" + path);
		}

		// if key not found, error.
		PathNode tail = path.tail();

		int pos = -1;
		if (tail.position() < tail.page().nKeys()) {
			KeyEntry ent = tail.page().getKey(tail.position());
			if (compareItems(item, ent.item()) == 0) {
				pos = tail.position();
			}
		}
		if (pos < 0) {
			T.msg("key not found: " + item + "\nin page:\n"
					+ tail.page() + "\nin tree:\n" + this+"\n"+Tools.stackTrace(1,10));
		}

		BPage page = tail.page();

		page.deleteEntries(pos, 1);

		{
			// wherever the removed item appears as an interior key, we must
			// replace it with its successor.

			BPage pg = page;
			Item succ = null;
			if (pos == pg.nKeys) {
				pg = pg.getSiblingPage(true);
				pos = 0;
			}
			if (pg != null) {
				succ = pg.getKey(pos).item();
			}
			if (succ != null) {
				path = findKey(item, false);

				while (path.height() > 0) {
					tail = path.removeTail();
					pg = tail.page();
					for (int i = 0; i < pg.nKeys() - 1; i++) {
						KeyEntry ent = pg.getKey(i);
						if (compareItems(item, ent.item()) == 0) {
							if (false) {
								Streams.out.println("replacing internal key #" + i + " :\n"
										+ ent.item() + "\nwith successor:\n" + succ);
							}
							pg.setKey(i, ent.modify(succ) );
							pg.touch();
							break;
						}
					}
				}
			}
		}
		item.setPage(0);
		changed();
		if (trace) {
			testIntegrity("after remove " + item);
		}
	}

	/**
	 * Add item to tree. Throws exception if item already exists.
	 * 
	 * @return leaf BPage containing added item
	 */
	public BPage add(Item item) {
		// construct a KeyEntry.
		if (trace) {
			trace("\n\nBTree.add, item=" + item);
			testIntegrity("before add " + item);
		}

		// construct path to leaf node that would contain the key
		BPath path = findKey(item, true);
		if (trace) {
			trace("" + path);
		}

		// if key found, error.
		PathNode tail = path.tail();

		if (tail.position() < tail.page().nKeys()) {
			KeyEntry ent = tail.page().getKey(tail.position());
			if (compareItems(item, ent.item()) == 0) {
				throw new BTreeException("duplicate key:\n " + item + "\n "
						+ ent.item());
			}
		}

		BPage page = tail.page();

		// insert key into tail page.
		page.insert(tail.position(), new KeyEntry(null, item));

		// page.flush();
		if (trace) {
			testIntegrity("after add " + item);
		}
		changed();

		return page;
	}

	/**
	 * Allocate a new page
	 * 
	 * @return page id
	 */
	private int getNewPageId() {
		int out = 0;
		if (!recycleBin.isEmpty()) {
			out = recycleBin.popInt();
		} else {
			out = PAGE_ID_BASE + pages.size();
		}
		return out;
	}

	// /**
	// * Get root page's id
	// * @return int
	// */
	// protected int rootId() {
	// return rootPage.id();
	// }

	/**
	 * Get the height of the tree. A tree with only a root node has height 1.
	 * 
	 * @return int
	 */
	public int getHeight() {
		return treeHeight;
	}

	private void freePage(BPage page) {
		changed();
		recycleBin.pushInt(page.getId());
	}

	public BTreeIterator iterator() {
		return iterator(null);
	}

	public BTreeIterator iterator(Object seekItem) {
		BPath path = findKey(seekItem, false);
		return new BTreeIterator(this, path, changeCounter);
	}

	public Object first() {
		Iterator it = iterator();
		Object out = null;
		if (it.hasNext())
			out = it.next();
		return out;
	}

	public Object last() {
		final boolean db = false;

		BPage page = rootPage;
		if (db && T.update())
			T.msg("tree.last(), treeHeight=" + treeHeight);
		int depth = 0;
		while (true) {

			if (db && T.update())
				T.msg(" curr ht = " + depth);
			// if we've reached the leaf depth, we're done
			if (depth == treeHeight - 1) {
				break;
			}

			if (db && T.update())
				T.msg(" page=" + page.toString(true));
			page = page.lastEntry().ptr();
			depth++;
		}
		Object out = null;
		if (page.nKeys() > 0) {
			out = page.lastEntry().item();
		}
		return out;
	}

	/**
	 * Construct a path to key in leaf page. If full pages are encountered along
	 * the way, they are split. If underfull pages are encountered, they are
	 * adjusted or merged. This may percolate underfull pages upward. Thus this
	 * will return a path containing a leaf page that is guaranteed to not be full
	 * and not be underfull.
	 * 
	 * @param item :
	 *          item to find, or null to find the smallest leaf item
	 * @param makeChanges :
	 *          true to make changes to tree to ensure nodes are not full or
	 *          underfull; should be set true for add/delete operations, false for
	 *          iteration or debug purposes; touches every page encountered during
	 *          path construction; if auxilliary data is stored with nodes (as
	 *          with SegTree), the aux data should be marked invalid as a result
	 *          of this touching
	 * @return BPath : path to leaf that will contain node if it exists; if it
	 *         doesn't exist, path to the item following the sought item, or eof
	 *         if no such follower exists
	 */
	protected BPath findKey(Object item0, boolean makeChanges) {

		boolean db = false && !makeChanges;
		if (db && T.update())
			T.msg("BTree.findKey " + item0);

		BPath path = new BPath(item0);

		BPage page = rootPage;

		while (true) {
			if (makeChanges) {
				page.touch();
			}

			if (db && T.update())
				T.msg(" findKey, page=" + page);

			if (makeChanges) {
				// is page underfull?
				if (page.underFull()) {
					if (db) {
						trace(" underfull, height=" + path.height());
					}

					// if this is the root, delete it if it is an index page
					// with only one key;
					// otherwise, do nothing.
					if (path.height() == 0) {
						if (db) {
							trace(" root...");
						}
						if (!page.isLeaf() && page.nKeys() == 1) {
							if (trace) {
								testIntegrity("before underfull changes");
							}
							// delete root.
							rootPage = page.firstEntry().ptr();
							treeHeight--;

							freePage(page);
							page = rootPage;
							if (trace) {
								testIntegrity("after underfull changes: delete root");
							}
							continue;
						}
					} else {
						// find sibling.
						PathNode node = path.tail();
						BPage parent = node.page();

						// choose sibling to right, unless none exists.
						boolean sibToLeft = (node.position() == parent.nKeys() - 1);

						if (db) {
							trace(" node=" + node);
							trace(" parent=" + parent);
							trace(" sibToLeft=" + sibToLeft);
						}

						BPage p1, p2;
						p1 = page;
						if (!sibToLeft) {
							if (db) {
								trace(" node.position=" + node.position() + ", parent.nkeys="
										+ parent.nKeys());
							}

							p2 = parent.getKey(node.position() + 1).ptr();
						} else {
							p2 = parent.getKey(node.position() - 1).ptr();
						}

						if (db) {
							trace(" p1=" + p1 + "\n p2=" + p2);
						}

						if (p2.moreThanHalfFull()) {
							// rotate an item from p2 to p1.
							if (!sibToLeft) {
								rotateLeft(parent, node.position(), p1, p2);
							} else {
								rotateRight(parent, node.position() - 1, p2, p1);
							}
						} else {
							if (!sibToLeft) {
								mergeLeft(parent, node.position(), p1, p2);
							} else {
								mergeRight(parent, node.position() - 1, p1, p2);
							}
						}
						// repeat with modified p1
						continue;
					}
				}

				// is page full?
				if (page.full()) {
					if (db) {
						trace(" page is full...");
					}

					// create a new page to copy half the items to; it will
					// be the right sibling of the old page (page)
					BPage pnew = constructPage(getNewPageId(), page.isLeaf());
					BPage newPage = page.split(pnew);

					// get key to propagate up to parent.
					KeyEntry propKey = null;
					if (page.isLeaf()) {
						KeyEntry k = newPage.minKey();
						propKey = k.modify(page);
					} else {
						KeyEntry last = page.lastEntry();
						propKey = new KeyEntry(page, last.handle()); // item());
						// clear key of last entry in original page to zero, since
						// it is now unused.
						page.clearLastKey();
					}
					if (trace) {
						Streams.out.println("propagating key: " + propKey);
					}

					// if we have no parent, create new root and grow tree.

					if (path.height() == 0) {
						BPage newRoot = createRoot();

						// the 'right side' entry has no key, with value of old page id.
						newRoot.insert(0, new KeyEntry(page, (Handle) null));

						// add new root to path, so we can immediately pop it off...
						path.addPage(newRoot);
					}

					PathNode tail = path.removeTail();

					BPage parent = tail.page();

					KeyEntry oldChildPtr = parent.getKey(tail.position());
					if (db) {
						trace("parent's oldChildPtr: " + oldChildPtr);
					}

					KeyEntry oldChild = oldChildPtr;
					
					parent.setKey(tail.position(), oldChild.modify(newPage) );

					// insert new entry into parent, now that we are sure there is room
					parent.insert(tail.position(), propKey);
					page = parent;
					if (trace) {
						testIntegrity("after full changes: inserted into parent");
					}
				}
			}

			// add this page to the path
			path.addPage(page);

			// if we've reached the leaf depth, we're done
			if (path.height() == treeHeight) {
				break;
			}

			// read appropriate child page (this was determined by
			// path.addPage(...) )
			page = path.getTailPtr();
		}

		if (db && T.update())
			T.msg(" path=" + path);

		return path;
	}

	/**
	 * Read a page
	 * 
	 * @param id :
	 *          id of page
	 * @return BPage
	 */
	public BPage read(int id) {
		// infTest();
		return (BPage) pages.get(id - PAGE_ID_BASE);
	}

	void store(BPage pg) {
		// infTest();
		changed();
		pages.growSet(pg.getId() - PAGE_ID_BASE, pg);
	}

	private void changed() {
		changeCounter++;
	}

	// int getChangeCounter() {
	// return changeCounter;
	// }

	void verifyUnchanged(int counter) {
		if (counter != changeCounter) {
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Determine if tracing is active
	 * 
	 * @return boolean
	 */
	public boolean trace() {
		return trace;
	}

	/**
	 * Merge entries from p2 into p1, where p2 is to the right of p1
	 * 
	 * @param parent
	 *          BPage
	 * @param pos
	 *          int
	 * @param p1
	 *          BPage
	 * @param p2
	 *          BPage
	 */
	private void mergeLeft(BPage parent, int pos, BPage p1, BPage p2) {
		if (trace) {
			testIntegrity("before mergeLeft");
		}

		if (trace) {
			trace(p1.isLeaf() ? "mergeLeft, leaf page" : "mergeLeft, index page");
		}

		if (!p1.isLeaf()) {
			// create a new key entry using parent's key and p1's right ptr
			KeyEntry midEnt = parent.getKey(pos).modify(p1.rightEntryPage());
			// midEnt.setPtr(p1.rightEntryValue());

			p1.popLast();
			p1.append(midEnt);
			// insert(midEnt, p1.nKeys());
		}

		p1.insertEntries(p2, 0, p2.nKeys(), p1.nKeys());
		parent.deleteEntries(pos, 1);
		parent.change(pos, p1);
		parent.clearLastKey();

		if (p1.isLeaf()) {
			p1.setSibling(true, null);
			BPage rsib = p2.getSiblingPage(true);
			if (rsib != null) {
				BPage.connectLeafNodes(p1, rsib);
			}
		}
		freePage(p2);
		if (trace) {
			testIntegrity("after mergeLeft");
		}
	}

	/**
	 * Merge entries from p2 into p1, where p2 is to the left of p1
	 * 
	 * @param parent
	 *          BPage
	 * @param pos
	 *          int
	 * @param p1
	 *          BPage
	 * @param p2
	 *          BPage
	 */
	private void mergeRight(BPage parent, int pos, BPage p1, BPage p2) {
		if (trace) {
			testIntegrity("before mergeRight");
		}

		if (trace) {
			trace(p1.isLeaf() ? "mergeRight, leaf page" : "mergeRight, index page");
		}

		if (!p1.isLeaf()) {
			// create a new key entry using parent's key and p2's right ptr
			KeyEntry midEnt = parent.getKey(pos).modify(p2.rightEntryPage());
			p1.insert(0, midEnt);
			p1.insertEntries(p2, 0, p2.nKeys() - 1, 0);
		} else {
			p1.insertEntries(p2, 0, p2.nKeys(), 0);
		}

		// delete second entry from parent, adjust first so it points to p1
		parent.deleteEntries(pos + 1, 1);
		parent.change(pos, p1);
		parent.clearLastKey();

		if (p1.isLeaf()) {
			p1.setSibling(false, null);
			BPage lsib = p2.getSiblingPage(false);
			if (lsib != null) {
				BPage.connectLeafNodes(lsib, p1);
			}
		}
		freePage(p2);
		if (trace) {
			testIntegrity("after mergeRight");
		}
	}

	private void rotateRight(BPage parent, int pos, BPage p1, BPage p2) {
		if (trace) {
			testIntegrity("before rotateRight");
		}

		KeyEntry parentKey = parent.getKey(pos);
		if (p1.isLeaf()) {
			KeyEntry move = p1.popLast();
			p2.insert(0, move);
			parent.change(pos, move.handle());
		} else {
			KeyEntry move = parentKey.modify(p1.rightEntryPage());
			p2.insert(0, move);
			p1.popLast();
			parent.change(pos, p1.lastEntry().item());
			p1.clearLastKey();
		}
		changed();
		parent.touch();
		if (trace) {
			testIntegrity("after rotateRight");
		}
	}

	private void rotateLeft(BPage parent, int pos, BPage p1, BPage p2) {
		if (trace) {
			testIntegrity("before rotateLeft");
		}
		KeyEntry parentKey = parent.getKey(pos);
		if (p1.isLeaf()) {
			KeyEntry move = new KeyEntry(p2.firstEntry());
			p2.popFirst();
			p1.append(move); // .makeInterior());
			parent.change(pos, p2.firstEntry().item());
		} else {
			KeyEntry p2First = p2.popFirst();
			KeyEntry p1Last = p1.popLast();
			p1.append(p1Last.modify(parentKey.handle()));
			// )new KeyEntry(p1Last.ptr(), parentKey.item()));
			p1.append(p2First);
			p1.clearLastKey();
			parent.change(pos, p2First.handle());
		}
		changed();
		parent.touch();
		if (trace) {
			testIntegrity("after rotateLeft");
		}
	}

	/**
	 * For debugging purposes: Test integrity of tree
	 */
	protected void testIntegrity(String msg) {
		testSubtree(msg, 1, rootPage, null, null);
	}

	private void testSubtree(String where, int skip, BPage pg, KeyEntry minKey,
			KeyEntry maxKey) {

		// test that each key in this page doesn't violate the key ordering,
		// and test subtrees recursively if it's not a leaf page

		for (int j = 0; j < pg.nKeys(); j++) {
			KeyEntry ent = pg.getKey(j);

			if (pg.isLeaf() || j < pg.nKeys() - 1) {
				if (!verifyKeyWithinBounds(ent, minKey, maxKey)) {
					String msg = Tools.stackTrace(1 + skip, 1) + ", " + where + ": ";
					throw new BTreeException(msg
							+ "BTree integrity failure, key not within bounds;\n" + "min="
							+ minKey + "\n" + "ent=" + ent + "\nmax=" + maxKey);

				}
			}

			if (!pg.isLeaf()) {

				if (false && pg.nKeys() > 1) {
					// verify that last item is equal to the rightmost subtree's minimum
					// item.
					// The last item is stored in the second to last key, since the item
					// for
					// the last key is always null (in interior nodes).
					Object lastItem = pg.getKey(pg.nKeys() - 2).item();

					BPage cp = pg.lastEntry().ptr();
					while (true) {
						if (cp.isLeaf()) {
							break;
						}
						cp = cp.getPtr(0);
					}
					Object minItem = cp.getKey(0).item();
					if (lastItem != minItem) {
						String msg = Tools.stackTrace(1 + skip, 1) + ", " + where + ": ";
						Streams.out.print(this);
						throw new BTreeException(
								msg
										+ "BTree integrity failure, index page last item not equal to subtree min item:\n"
										+ lastItem + "\n" + minItem);
					}

				}

				KeyEntry prevEnt = (j == 0) ? minKey : pg.getKey(j - 1);
				KeyEntry nextEnt = (j + 1 < pg.nKeys()) ? ent : maxKey;

				if (ent.ptr() == null) {
					String msg = Tools.stackTrace(1 + skip, 1) + ", " + where + ": ";
					throw new BTreeException(msg
							+ "BTree integrity failure, interior node "
							+ "ptr undefined, page " + pg + ", j=" + j + ", pg=\n" + pg
							+ "\nentry=" + ent);
				}

				testSubtree(where, 1 + skip, ent.ptr(), prevEnt, nextEnt);
			}
		}
	}

	public static boolean DEB_comparator;

	/**
	 * Verify that min <= key < max
	 * 
	 * @param ent
	 *          KeyEntry
	 * @param min
	 *          KeyEntry
	 * @param max
	 *          KeyEntry
	 * @return boolean
	 */
	private boolean verifyKeyWithinBounds(KeyEntry ent, KeyEntry min, KeyEntry max) {
		boolean legal = true;
		if (min != null) {
			int c = compareItems(min.item(), ent.item());
			if (c > 0) {
				DEB_comparator = true;
				try {
					compareItems(min.item(), ent.item());
				} finally {
					DEB_comparator = false;
				}

				legal = false;
				Streams.out.println("*** min > key:\n min=" + min + "\n key=" + ent);
			}
		}
		if (max != null) {
			int c = compareItems(max.item(), ent.item());
			if (c <= 0) {
				DEB_comparator = true;
				try {
					compareItems(max.item(), ent.item());
				} finally {
					DEB_comparator = false;
				}
				legal = false;
				Streams.out.println("*** key >= max:\n key=" + ent + "\n max=" + max);
			}
		}
		return legal;
	}

	private void bfAdd(DArray a, BPage p, DQueue q, BitSet set) {
		if (set.get(p.getId() - PAGE_ID_BASE)) {
			throw new BTreeException("page already in list");
		}
		set.set(p.getId() - PAGE_ID_BASE);

		a.addInt(p.getId());

		BPage pg = p;
		if (!pg.isLeaf()) {
			for (int i = 0; i < pg.nKeys(); i++) {
				q.push(pg.getKey(i).ptr());
			}
		}

		if (!q.isEmpty()) {
			bfAdd(a, (BPage) q.pop(), q, set);
		}
	}

	private int[] getBreadthFirstPages() {

		DArray a = new DArray();
		bfAdd(a, rootPage, new DQueue(), new BitSet());

		return a.toIntArray();
	}

	/**
	 * Get string describing object
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("BTree");
		sb.append(" root=" + rootPage);
		sb.append(" ht=" + getHeight());
		sb.append(" kpi=" + maxKeysPerIndexPage());
		sb.append(" kpl=" + maxKeysPerLeafPage());
		sb.append("\n");

		int[] bf = getBreadthFirstPages();
		BPage prev = null;
		KeyEntry prevKey = null;
		for (int i = 0; i < bf.length; i++) {
			BPage p = read(bf[i]);
			if (prev != null && p.isLeaf() != prev.isLeaf()) {
				sb.append(" === Leaf nodes start ===\n");
			}
			prev = p;
			sb.append(p.toString(true) + "\n");

			if (p.isLeaf()) {
				for (int j = 0; j < p.nKeys(); j++) {
					KeyEntry e = p.getKey(j);
					if (prevKey != null) {
						int c = compareItems(prevKey.item(), e.item());
						if (c > 0) {
							sb.append("*** Keys are not in order: " + prevKey + " " + e
									+ "\n");
							if (false) {
								throw new BTreeException("*** Keys are not in order: "
										+ prevKey + " " + e);
							}
							break;
						}
					}
					prevKey = e;
				}
			}
		}

		sb.append("----------------------------------------\n");
		// sb.append("printing items in order:\n");
		Iterator it = iterator();
		// sb.append("iterator="+it);
		int item = 0;
		int sc = sb.length();
		while (it.hasNext()) {
			if (item > 0) {
				sb.append(' ');
			}
			String is = getItemString((Item) it.next());

			// insert cr's to break up line? Many characters are not printed, but
			// are interpreted as tags; don't bother.

			if (false) {
				if (sb.length() + is.length() - sc > 80 && is.length() < 75) {
					sb.append('\n');
					sc = sb.length();
				}
			}
			sb.append(is);
			// sb.append(Tools.f(item, 4) + ": " + getItemString(it.next()) + "\n");
			item++;
			if (item == 1000) {
				break;
			}
		}
		if (item > 0) {
			sb.append('\n');
		}
		return sb.toString();
	}

	protected String getItemString(Item item) {
		return item.toString();
	}

//	public void dump() {
//		Streams.out.print(this);
//	}

	protected Comparator comparator() {
		// infTest();
		return comparator;
	}

	private static final int PAGE_ID_BASE = 1;

	private Comparator comparator;

	private DArray pages = new DArray();

	private static final boolean trace = false;

	// private int rootId;

	private BPage rootPage;

	private int treeHeight;

	private DArray recycleBin = new DArray();

	private int changeCounter;
}
