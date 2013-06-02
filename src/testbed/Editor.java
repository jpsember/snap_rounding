package testbed;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import base.*;

/**
 * Object editor.
 * 
 * This will be moved into the TestBed package when it is working cleanly.
 */
public class Editor implements Globals, IEditorScript {
  /*! .enum  .public .prefix TYPE_ 1
    point segment disc polygon
  */

  public static final int TYPE_POINT = 1; //!
  public static final int TYPE_SEGMENT = 2; //!
  public static final int TYPE_DISC = 3; //!
  public static final int TYPE_POLYGON = 4; //!
  /* !*/

  /*! .enum  .private .prefix G_ 9300
       file new open save saveas
       edit undo redo cut copy paste all none dup filepath
       revert opennext deletept
       backward forward back front
       errorset toggleactive withlabels labelverts
       scaleup scaledn addanother rotate scale
       saveasnext
       additems  50 
  */

  private static final int G_FILE = 9300;//!
  private static final int G_NEW = 9301;//!
  private static final int G_OPEN = 9302;//!
  private static final int G_SAVE = 9303;//!
  private static final int G_SAVEAS = 9304;//!
  private static final int G_EDIT = 9305;//!
  private static final int G_UNDO = 9306;//!
  private static final int G_REDO = 9307;//!
  private static final int G_CUT = 9308;//!
  private static final int G_COPY = 9309;//!
  private static final int G_PASTE = 9310;//!
  private static final int G_ALL = 9311;//!
  private static final int G_NONE = 9312;//!
  private static final int G_DUP = 9313;//!
  private static final int G_FILEPATH = 9314;//!
  private static final int G_REVERT = 9315;//!
  private static final int G_OPENNEXT = 9316;//!
  private static final int G_DELETEPT = 9317;//!
  private static final int G_BACKWARD = 9318;//!
  private static final int G_FORWARD = 9319;//!
  private static final int G_BACK = 9320;//!
  private static final int G_FRONT = 9321;//!
  private static final int G_ERRORSET = 9322;//!
  private static final int G_TOGGLEACTIVE = 9323;//!
  private static final int G_WITHLABELS = 9324;//!
  private static final int G_LABELVERTS = 9325;//!
  private static final int G_SCALEUP = 9326;//!
  private static final int G_SCALEDN = 9327;//!
  private static final int G_ADDANOTHER = 9328;//!
  private static final int G_ROTATE = 9329;//!
  private static final int G_SCALE = 9330;//!
  private static final int G_SAVEASNEXT = 9331;//!
  private static final int G_ADDITEMS = 9332;//!
  /* !*/

  /**
   * Editor state: ready (no mode)
   */
  private static final int ES_READY = 0;

  /**
   * Editor state: adding a new item
   */
  private static final int ES_INSERTINGPT = 1;

  /**
   * Editor state: adjusting existing point
   */
  private static final int ES_EDITINGPT = 2;

  /**
   * Editor state: about to move objects
   */
  private static final int ES_STARTMOVING = 3;
  /**
   * Editor state: moving selected items
   */
  private static final int ES_MOVING = 4;

  /**
   * Editor state:  dragging box
   */
  private static final int ES_BOX = 5;

  private static final int ES_ROTWAIT = 6, ES_ROTATING = 7;
  private static final int ES_SCALEWAIT = 8, ES_SCALING = 9;

  /**
   * Editor state: waiting for mouse to move slightly before choosing next
   * point to edit
   */
  private static final int ES_EDITWAIT = 10;

  /**
   * Determine if editor is included and has been initialized
   * @return true if editor is included and initialized
   */
  static boolean initialized() {
    return items != null;
  }

  private static final boolean db = false;

  /**
   * Add a type of object to the editor, so user can manipulate
   * objects of this type.  Adds an appropriate item to the edit menu
   * to allow creating the objects.
   * @param f object type
   */
  public static void addObjectType(EdObjectFactory f) {
    objTypesMap.put(f.getTag(), new Integer(objectFactories.size()));
    objectFactories.add(f);
  }

  /**
   * Add editor controls to script
   */
  static void addControls() {
    fileStats = new FileStats();
    TestBed.setFileStats(fileStats);

    fileStats.persistPath(G_FILEPATH);
    //    fileStats.setPathGadget(G_FILEPATH);
    //    C.sHide();
    //    C.sTextField(G_FILEPATH, null, null, 200, false, null);
    C.sOpen();
    C.sCheckBox(G_WITHLABELS, "labels", "label items in editor", false);
    C.sNewColumn();
    C.sCheckBox(G_LABELVERTS, "verts", "label item vertices", false);
    C.sClose();
  }

  static boolean menuAdded;

  /**
   * Construct a menu for the editor.  Should be balanced by a call 
   * to closeMenu().  Before doing so, user can extend this menu by 
   * adding more items.  For example, within the application's addMenus() method,
   * <pre>
   *    Editor.openMenu();
   *    // add a menu item to set selected discs' radii to zero
   *    C.sMenuItem(G_ZERODISCRADII, "Zero radii", "!^t");
   *    Editor.closeMenu();
   * </pre>
   */
  public static void openMenu() {
    menuAdded = true;

    C.sOpenMenu(G_FILE, "File");
    C.sMenuItem(G_NEW, "New", null);
    C.sMenuItem(G_OPEN, "Open", "^o");
    //if (!TestBed.isApplet())
    C.sMenuItem(G_OPENNEXT, "Open next", "^O");
    C.sMenuItem(G_SAVE, "Save", "^s");
    C.sMenuItem(G_SAVEAS, "Save as", null);
    C.sMenuItem(G_SAVEASNEXT, "Save as next", "^A");
    C.sMenuItem(G_REVERT, "Revert", null);
    if (!TestBed.isApplet())
      C.sMenuItem(G_ERRORSET, "Get error set", "^E");
    C.sCloseMenu();
    C.sOpenMenu(G_EDIT, "Edit");
    C.sMenuItem(G_UNDO, "Undo", "^z");
    C.sMenuItem(G_REDO, "Redo", "^y");
    C.sMenuSep();
    C.sMenuItem(G_CUT, "Cut", "^x");
    C.sMenuItem(G_COPY, "Copy", "^c");
    C.sMenuItem(G_PASTE, "Paste", "^v");
    C.sMenuItem(G_ALL, "Select All", "^a");
    C.sMenuItem(G_NONE, "Select None", "#" + KeyEvent.VK_ESCAPE);
    C.sMenuItem(G_DUP, "Duplicate", "^d");
    C.sMenuItem(G_TOGGLEACTIVE, "Toggle active", "^w");
    C.sMenuSep();
    C.sMenuItem(G_BACKWARD, "Backward", "[");
    C.sMenuItem(G_FORWARD, "Forward", "]");
    C.sMenuItem(G_BACK, "Back", "^[");
    C.sMenuItem(G_FRONT, "Front", "^]");
    C.sMenuItem(G_DELETEPT, "Delete point", "#" + KeyEvent.VK_BACK_SPACE);
    C.sMenuItem(G_SCALEDN, "Scale down", "^-");
    C.sMenuItem(G_SCALEUP, "Scale up", "^=");
    C.sMenuItem(G_SCALE, "Scale", "^S");
    C.sMenuItem(G_ROTATE, "Rotate", "^R");

    addObjectMenuItems();
  }

  /**
   * Close menu previously opened by openMenu(), add it to the menu bar.
   * @see #openMenu()
   */
  public static void closeMenu() {
    C.sCloseMenu();
  }

  /**
   * Get width of grid in cells
   * @return width of grid, in cells
   */
  public static int gridSize() {
    return C.vi(TBGlobals.GRIDSIZE);
  }

  /**
   * Initialize editor
   */
  static void init() {

    Tools.ASSERT(TBGlobals.EDITORPARMS == 9300);

    undoStack = new DArray();
    objTypesMap = new HashMap();
    objectFactories = new DArray();
    //    fileModified = false;
    resetDupOffset();
    clipboard = null;
    lastMouseDown = null;
    editField = 0;

    editState = ES_READY;
    setEditObject(-1);
    items = new ObjArray();
    clearUndo(true);
    clipboard = new ObjArray();

  }

  static void init2() {

    String filePath = fileStats.getPath();

    if (filePath == null && TestBed.isApplet()) {
      DArray fl = Streams.getFileList(null, TestBed.parms.fileExt);
      if (!fl.isEmpty())
        filePath = fl.getString(0);
    }

    if (filePath != null) {
      try {
        doOpen(filePath);
      } catch (Throwable t) {
        Tools.warn("problem opening " + filePath + ": " + t + "\n"
            + Tools.stackTrace(0, 10, t));
      }
    }
  }

  private final static boolean dbDUP = false;

  private static boolean dupPrepare(int oper) {
    boolean ret = false;
    if (oper != dupOper) {
      resetDupOffset();
      dupOper = oper;
      ret = true;
    }
    if (dbDUP)
      Streams.out.println("dupPrepare " + oper + " ret " + ret + ": off="
          + dupOffset + " acc=" + dupAccum);
    return ret;
  }

  private static void resetDupOffset() {
    final boolean db = dbDUP;
    if (db) {
      if (dupOffset.x != 2 || dupOffset.y != 2) {
        Streams.out.println("reset dup: " + Tools.stackTrace(1, 5));
      }
    }
    dupOffset.setLocation(2, 0);
    dupAccum.clear();
    dupOper = 0;
  }

  private static void adjustDupOffset(FPoint2 amt) {
    dupOffset.add(amt);
    dupAccum.add(amt);

    if (dupAccum.length() > V.viewRect.width * .3)
      resetDupOffset();

    final boolean db = dbDUP;
    if (db) {
      Streams.out.println("adjust dup: " + Tools.stackTrace() + "\n off="
          + dupOffset + "\n acc=" + dupAccum);
    }
  }

  /**
   * Get EdObjects, prior to performing some editing action upon them.
   * Saves them in the undo buffers.
   * Skips objects that are not complete, active, and selected.
   * @param objType  type of object, or null for any
   * @return DArray of matching objects
   */
  public static DArray editObjects(EdObjectFactory objType) {
    return editObjects(objType, true, true);
  }

  /**
   * Get EdObjects, prior to performing some editing action upon them.
   * Saves them in the undo buffers.
   * Skips objects that are not complete.
   * @param objType  type of object, or null for any
   * @param selectedOnly  if true, includes only selected objects
   * @param skipInactive  if true, skips inactive objects
   * @return DArray of matching objects
   */
  public static DArray editObjects(EdObjectFactory objType,
      boolean selectedOnly, boolean skipInactive) {
    ChangeItemsOper oper = new ChangeItemsOper(objType, selectedOnly,
        skipInactive);
    perform(oper);

    ObjArray ret = new ObjArray(Editor.items, oper.itemSlots(), false);
    return ret;
  }

  /**
   * Make a copy of the current items, and store in 'error' buffer.
   * Called by algorithm tracing (T.runAlgorithm) to save the items
   * that produced a problem, as the user may be clicking on a button
   * that generates random items and tests them quickly
   */
  static void storeErrorItems() {
    errorItems = new ObjArray(items);
  }

  /**
   * Replace existing objects with a new set, saving old in undo 
   * buffers beforehand
   * @param newObjects
   */
  public static void replaceAllObjects(Collection newObjects) {
    ChangeItemsOper oper = new ChangeItemsOper(null, false, false);
    perform(oper);
    items.clear();
    items.addAll(newObjects);
  }

  /**
   * Replace selected objects with a new set, saving old in 
   * undo buffers beforehand
   * @param newObjects
   */
  public static void replaceSelectedObjects(Collection newObjects) {
    ChangeItemsOper oper = new ChangeItemsOper(null, false, false);
    perform(oper);
    ObjArray ni = new ObjArray();
    for (int i = 0; i < items.size(); i++) {
      EdObject obj = items.obj(i);
      if (!obj.isSelected())
        ni.add(obj);
    }
    for (Iterator it = newObjects.iterator(); it.hasNext();) {
      EdObject obj = (EdObject) it.next();
      obj.setSelected(true);
      ni.add(obj);
    }
    items = ni;
  }

  private static int lastTypeAdded = -1;
  private static final boolean dba = false; //Tools.dbWarn(true);

  private static void updateCoordDisplay(FPoint2 loc) {
    if (TestBed.parms.includeGrid) {
      String s = Tools.f(loc.x, 4, 1);
      s = s + Tools.f(loc.y, 4, 1);
      C.sets(TBGlobals.MOUSELOC, s);
    }

  }

  private static final boolean NEWEDIT = true;
  /**
   * Process a testbed action
   * @param a : action 
   */
  static void processAction(TBAction a) {
    final boolean db = dba;

    if (db) {
      if (a.code != TBAction.HOVER && a.code != TBAction.DRAG)
        Streams.out.println("editState=" + Tools.f(editState) + " " + a
            + " editObj=" + editObj + " editField=" + editField);
    }

    switch (a.code) {
    case TBAction.DOWN2:
      switch (editState) {
      case ES_INSERTINGPT:
        editObj.deletePoint(editField);
        stopEdit();
        break;
      case ES_EDITINGPT:
        editObj.deletePoint(editField);
        stopEdit();
        break;
      case ES_EDITWAIT:
        stopEdit();
        break;
      }
      break;

    case TBAction.DOWN1:
      {
        lastMouseDown = new FPoint2(a.loc);
        switch (editState) {
        case ES_ROTWAIT:
          if (editBox.contains(a.loc)) {

            rotStartPt = lastMouseDown;
            rotThetaOrig = rotThetaNow;
            setState(ES_ROTATING);
          } else {
            setState(ES_READY);
          }
          break;
        case ES_SCALEWAIT:
          {
            rotStartPt = lastMouseDown;
            scaleUniformly = a.altPressed();

            setState(ES_SCALING);
            editBoxOrig = new FRect(editBox);
          }
          break;

        case ES_READY:
          {
            int i = findObjAt(a.loc);
            if (i < 0) {
              resetDupOffset();
              if (!a.ctrlPressed())
                unselectAll();
              otherBoxCorner = new FPoint2(lastMouseDown);
              setState(ES_BOX);
              break;
            }

            EdObject obj = obj(i);
            if (db)
              Streams.out.println("ES_READY, mouse down on object " + obj);

            if (a.ctrlPressed()) {
              resetDupOffset();
              obj.setSelected(!obj.isSelected());
              break;
            }
            int particularPt = -1;
            boolean wasSelected = obj.isSelected();
            if (!wasSelected) {
              resetDupOffset();
              unselectAll();
              obj.setSelected(true);
            } else {

              if (!a.altPressed()) {
                // determine if we have selected a particular point of an object;
                // if so, move just that point
                int j = 0;
                double nearestDist = 0;

                double maxDist = nearPointDist();

                for (;; j++) {
                  double dist = obj.distFrom(j, a.loc);
                  if (dist < 0)
                    break;
                  if (dist > maxDist)
                    continue;
                  if (particularPt < 0 || nearestDist < dist) {
                    nearestDist = dist;
                    particularPt = j;
                  }
                }
              }
            }
            if (dp)
              Streams.out.println("clearing dpoffset to null in DOWN/READY");
            adjustPositionOffset = null;
            if (particularPt >= 0) {
              if (db)
                Streams.out.println(" chose particular point " + particularPt
                    + ", setting ES_EDITINGPT");

              resetDupOffset();
              editField = particularPt;

              // determine difference between mouse location and 
              // point's location, so when we drag it it doesn't jump.
              adjustPositionOffset = FPoint2.difference(a.loc,
                  obj.getPoint(particularPt), null);
              if (dp)
                Streams.out.println(" dpoffset set to a.loc=" + a.loc
                    + " - pt=" + obj.getPoint(particularPt) + " = "
                    + adjustPositionOffset);

              setEditObject(i);
              setState(ES_EDITINGPT);

              if (true) {
                Undoable oper = new ChangeItemsOper(null, true, false);
                perform(oper);
                touch();
              }

            } else {
              setState(ES_STARTMOVING);
              if (db)
                Streams.out.println(" setting ES_STARTMOVING");
              pendingUndo = new ChangeItemsOper(null, true, false);
            }
          }
          break;

        case ES_EDITINGPT:
        case ES_INSERTINGPT:
          resetDupOffset();
          if (db)
            Streams.out.println(" INSERTINGPT, setting point editField="
                + editField + " to " + a.loc);
          editObj.setPoint(editField, a.loc, true, a);
          touch();
          break;
        }

      }
      break;

    case TBAction.UP1:
      {
        switch (editState) {
        case ES_ROTATING:
        case ES_SCALING:
          setState(ES_READY);
          break;

        case ES_BOX:
          {
            FRect r = new FRect(lastMouseDown, otherBoxCorner);

            if (r.width == 0 && r.height == 0) {
              // he didn't move the mouse away from the original point
              // find object at this point, and toggle its selection state
              int i = findObjAt(r.start());
              if (i >= 0) {
                EdObject obj = obj(i);
                obj.setSelected(!obj.isSelected());
              }
            } else {
              for (int i = 0; i < items.size(); i++) {
                EdObject obj = items.obj(i);
                if (obj.isSelected())
                  continue;
                if (r.contains(obj.getBounds())) {
                  obj.setSelected(true);
                }
              }
            }
            setState(ES_READY);
          }
          break;

        case ES_INSERTINGPT:
        case ES_EDITINGPT:
          touch();

          if (NEWEDIT) {
            mouseUpLoc = a.loc;
            procDrift(a);
          } else {
            // get next point to insert
            int efNew = editObj.getNextPointToInsert(a, editField, null);
            // if we're done all the points, stop editing this object
            if (efNew < 0) {
              setState(ES_READY);
            } else {
              editField = efNew;
              setState(ES_INSERTINGPT);
            }
          }
          break;

        case ES_MOVING:
          adjustDupOffset(FPoint2.difference(a.loc, lastMouseDown, null));
          setState(ES_READY);
          break;
        case ES_STARTMOVING:
          setState(ES_READY);
          break;
        }
      }
      break;

    case TBAction.HOVER:
      {
        updateCoordDisplay(a.loc);
        switch (editState) {
        default:
          a.code = 0;
          break;
        case ES_INSERTINGPT:
        case ES_EDITINGPT:
          touch();
          editObj.setPoint(editField, a.loc, true, a);
          break;
        case ES_EDITWAIT:
          procDrift(a);
          break;
        }
      }
      break;

    case TBAction.DRAG:
      {
        updateCoordDisplay(a.loc);
        switch (editState) {
        default:
          a.code = 0;
          break;
        case ES_SCALING:
          {
            if (pendingUndo != null) {
              perform(pendingUndo);
              touch();
              pendingUndo = null;
            }
            FPoint2 rotOrigin = editBoxOrig.midPoint();
            FPoint2 origDiff = FPoint2.difference(rotStartPt, rotOrigin, null);
            double origDist = origDiff.length();
            if (origDist == 0)
              break;
            //            double origDist = origDiff.length();
            //            if (origDist == 0)
            //              break;

            FPoint2 newDiff = FPoint2.difference(a.loc, rotOrigin, null);
            if (Math.signum(origDiff.x) != Math.signum(newDiff.x))
              newDiff.x = 0;
            if (Math.signum(origDiff.y) != Math.signum(newDiff.y))
              newDiff.y = 0;
            double newDist = newDiff.length();
            if (newDist == 0)
              break;

            double sclx = 1.0;
            double scly = 1.0;
            if (scaleUniformly) {
              if (origDist > 0 && newDist > 0)
                sclx = scly = newDist / origDist;
            } else {
              if (origDiff.x != 0)
                sclx = newDiff.x / origDiff.x;
              if (origDiff.y != 0)
                scly = newDiff.y / origDiff.y;
            }

            //            double scl = newDist / origDist;
            {
              ObjArray origItems = lastUndoItems();
              DArray origItemSlots = lastUndoItemSlots();

              for (int i = 0; i < origItemSlots.size(); i++) {
                EdObject obj = items.obj(origItemSlots.getInt(i));
                EdObject origObj = origItems.obj(i);

                for (int j = 0; j < obj.nPoints(); j++) {
                  FPoint2 pt = origObj.getPoint(j);
                  obj.setTransformedPoint(j, new FPoint2(rotOrigin.x
                      + (pt.x - rotOrigin.x) * sclx, rotOrigin.y
                      + (pt.y - rotOrigin.y) * scly));
                }
              }
            }
            double sw = editBoxOrig.width * .5 * sclx;
            double sh = editBoxOrig.height * .5 * scly;
            editBox = new FRect(rotOrigin.x - sw, rotOrigin.y - sh, sw * 2,
                sh * 2);
          }
          break;

        case ES_ROTATING:
          {
            if (pendingUndo != null) {
              perform(pendingUndo);
              touch();
              pendingUndo = null;
            }
            FPoint2 rotOrigin = editBox.midPoint();
            double theta0 = MyMath.polarAngle(rotOrigin, rotStartPt);
            double theta1 = MyMath.polarAngle(rotOrigin, a.loc);
            double thAdd = MyMath.normalizeAngle(theta1 - theta0);
            rotThetaNow = rotThetaOrig + thAdd;

            {
              ObjArray origItems = lastUndoItems();
              DArray origItemSlots = lastUndoItemSlots();

              for (int i = 0; i < origItemSlots.size(); i++) {
                EdObject obj = items.obj(origItemSlots.getInt(i));
                EdObject origObj = origItems.obj(i);

                for (int j = 0; j < obj.nPoints(); j++) {
                  FPoint2 pt = origObj.getPoint(j);
                  double theta = MyMath.polarAngle(rotOrigin, pt);
                  //  public void setTransformedPoint(int ptIndex, FPoint2 point) {
                  double radius = FPoint2.distance(pt, rotOrigin);
                  obj.setTransformedPoint(j,
                      MyMath.ptOnCircle(rotOrigin, theta + rotThetaNow, radius));
                }
              }
            }
          }
          break;
        case ES_INSERTINGPT:
        case ES_EDITINGPT:
          touch();
          {
            FPoint2 adj = a.loc;
            if (adjustPositionOffset != null)
              adj = new FPoint2(a.loc.x - adjustPositionOffset.x, a.loc.y
                  - adjustPositionOffset.y);
            if (dp)
              Streams.out.println("setting pt " + editField + " to a=" + a.loc
                  + " minus dp=" + adjustPositionOffset + " = " + adj);
            editObj.setPoint(editField, adj, true, a);
          }
          break;

        case ES_MOVING:
        case ES_STARTMOVING:
          {
            FPoint2 delta = FPoint2.difference(a.loc, lastMouseDown, null);
            if (pendingUndo != null) {
              setState(ES_MOVING);
              perform(pendingUndo);
              pendingUndo = null;
              touch();
            }
            ObjArray origItems = lastUndoItems();
            DArray origItemSlots = lastUndoItemSlots();
            for (int i = 0; i < origItemSlots.size(); i++) {
              EdObject obj = items.obj(origItemSlots.getInt(i));
              EdObject origObj = origItems.obj(i);
              obj.moveBy(origObj, delta);
            }
          }
          break;
        case ES_BOX:
          otherBoxCorner = new FPoint2(a.loc);
          break;
        }
      }
      break;

    case TBAction.CTRLVALUE:
      switch (a.ctrlId) {
      case G_NEW:
        selectAll();
        perform(new CutOper());
        clearUndo(true);
        fileStats.setPath(null);
        break;
      case G_OPEN:
        doOpen(null);
        break;
      case G_DELETEPT:
        if (editState == ES_INSERTINGPT) {
          editObj.deletePoint(editField);
          stopEdit();
        }
        break;
      case G_OPENNEXT:
        doOpenNext();
        break;
      case G_SAVE:
      case G_SAVEAS:
      case G_SAVEASNEXT:
        doSave(a.ctrlId);
        break;
      case G_REVERT:
        {
          if (fileStats.getPath() != null) {
            doOpen(fileStats.getPath());
          }
        }
        break;
      case G_ERRORSET:
        {
          if (errorItems != null) {
            restoreErrorSet();
          }
        }
        break;
      case G_UNDO:
        if (canUndo()) {
          unselectAll();
          doUndo();
        }
        break;
      case G_REDO:
        if (canRedo()) {
          unselectAll();
          doRedo();
          touch();
        }
        break;
      case G_BACKWARD:
      case G_FORWARD:
      case G_BACK:
      case G_FRONT:
        doAdjustSlot(a.ctrlId);
        break;
      case G_CUT:
        perform(new CutOper());
        break;
      case G_DUP:
        perform(new DupOper());
        break;
      case G_TOGGLEACTIVE:
        {
          DArray m = editObjects(null, true, false);
          for (int i = 0; i < m.size(); i++) {
            EdObject e = (EdObject) m.get(i);
            e.setActive(!e.isActive());
          }
        }
        break;
      case G_COPY:
        perform(new CopyOper());
        break;
      case G_PASTE:
        perform(new PasteOper(true));
        break;
      case G_ALL:
        for (int i = 0; i < items.size(); i++)
          items.obj(i).setSelected(true);
        break;
      case G_NONE:
        {
          switch (editState) {
          default:
            stopEdit();
            break;
          case ES_ROTWAIT:
          case ES_ROTATING:
            setState(ES_READY);
            break;
          }
        }
        break;
      case G_SCALEUP:
        scaleObjects(1.2);
        break;
      case G_SCALEDN:
        scaleObjects(1 / 1.2);
        break;
      case G_ROTATE:
        stopEdit();
        startRotate();
        break;
      case G_SCALE:
        stopEdit();
        startScale();
        break;
      default:
        if (a.ctrlId >= G_ADDANOTHER && a.ctrlId < G_ADDITEMS + nObjTypes()) {
          int type = lastTypeAdded;
          if (a.ctrlId != G_ADDANOTHER)
            type = a.ctrlId - G_ADDITEMS;
          if (type < 0)
            break;
          lastTypeAdded = type;
          EdObjectFactory f = getType(type);
          add(f.construct());
          touch();
          break;
        }
        break;
      }
      break;
    }

    if (displayedModified != fileStats.modified())
      updateTitle();

  }
  private static void scaleObjects(double factor) {
    ArrayList m = editObjects(null, true, false);
    for (int i = 0; i < m.size(); i++) {
      EdObject e = (EdObject) m.get(i);
      e.scale(factor);
    }
  }
  private static void doOpenNext() {
    final boolean db = false;
    do {
      if (TestBed.isApplet()) {
        String s = fileStats.getPath();
        DArray fl = Streams.getFileList(s, TestBed.parms.fileExt);
        String fNext = Path.getNextFile(fl, s, true);
        if (db)
          Streams.out.println("nextfile to " + s + " is " + fNext);
        if (fNext == null)
          break;
        doOpen(fNext);
        break;
      }
      String s = fileStats.getPath();
      if (s == null)
        break;
      if (db)
        Streams.out.println("OPENNEXT, s=" + s);

      File f = new File(s);
      if (!f.exists())
        break;
      File f2 = f.getParentFile();
      if (db)
        Streams.out.println("parentfile=" + f2);
      if (!f2.isDirectory())
        break;
      DArray fl = Streams.getFileList(f2.toString(), TestBed.parms.fileExt);
      File fNext = Path.getNextFile(fl, f, true);
      if (db)
        Streams.out.println("nextfile to " + f + " is " + fNext);
      if (fNext == null)
        break;
      doOpen(fNext.getPath());
    } while (false);
  }

  private static void restoreErrorSet() {
    stopEdit();
    unselectAll();
    clearUndo(true);
    items = new ObjArray(errorItems);
  }

  private static void doAdjustSlot(int ctrlId) {
    int dir;
    switch (ctrlId) {
    default: //case G_BACK:
      dir = Integer.MIN_VALUE;
      break;
    case G_FRONT:
      dir = Integer.MAX_VALUE;
      break;
    case G_BACKWARD:
      dir = -1;
      break;
    case G_FORWARD:
      dir = +1;
      break;
    }
    perform(new AdjustOrderOper(dir));
  }

  private static void procDrift(TBAction a) {
    // get next point to insert
    FPoint2 drift = FPoint2.difference(a.loc, mouseUpLoc, null);
    int efNew = editObj.getNextPointToInsert(a, editField, drift);
    // if we're done all the points, stop editing this object
    if (efNew == -2) {
      setState(ES_EDITWAIT);
    } else if (efNew < 0) {
      setState(ES_READY);
    } else {
      editField = efNew;
      setState(ES_INSERTINGPT);
    }
  }

  static void doPaste(boolean applyOffsets) {
    if (applyOffsets) {
      dupPrepare(G_PASTE);
      // add offset to accumulator, since we are pasting original clipboard items
      dupAccum.add(dupOffset);
    }
    dupItems(clipboard, applyOffsets);
  }
  static void performDup(DArray itemInds) {
    //    DArray itemInds = getSelectedItemInd();
    if (dupPrepare(G_DUP))
      dupAccum.setTo(dupOffset);
    dupItems(new ObjArray(items, itemInds, true), true);
  }

  private static int nObjTypes() {
    return objectFactories.size();
  }

  private static boolean prepareScaleRot() {
    boolean valid = false;
    do {
      DArray sel = getSelectedItemInd();
      if (sel.isEmpty())
        break;

      Undoable oper = new ChangeItemsOper(null, true, false);
      FRect r = null;
      if (true) {
        //        ObjArray origItems = lastUndoItems();
        //        DArray origItemSlots = lastUndoItemSlots();
        for (Iterator it = sel.iterator(); it.hasNext();) {
          int objPos = ((Integer) it.next()).intValue();
          EdObject obj = items.obj(objPos);
          for (int j = 0; j < obj.nPoints(); j++) {
            r = FRect.add(r, obj.getPoint(j));
          }
        }
      } else {
        //        Tools.warn("old way");
        //        scaleRotOriginal = new DArray();
        //        for (Iterator it = sel.iterator(); it.hasNext();) {
        //          int objPos = ((Integer) it.next()).intValue();
        //          scaleRotOriginal.addInt(objPos);
        //          EdObject obj = items.obj(objPos);
        //          scaleRotOriginal.add(obj.clone());
        //          for (int i = 0; i < obj.nPoints(); i++)
        //            r = FRect.add(r, obj.getPoint(i));
        //        }
      }
      editBox = r;

      pendingUndo = oper;
      //   scaleRotUndoPerformed = false;
      valid = true;
    } while (false);
    return valid;
  }
  private static void startScale() {
    do {
      if (!prepareScaleRot())
        break;
      setState(ES_SCALEWAIT);
    } while (false);
  }

  private static void startRotate() {

    do {
      if (!prepareScaleRot())
        break;

      rotThetaOrig = 0;
      rotThetaNow = 0;

      setState(ES_ROTWAIT);
    } while (false);
  }
  private static EdObjectFactory getType(int index) {
    return (EdObjectFactory) objectFactories.get(index);
  }

  private static void dupItems(ObjArray dup, boolean applyOffsets) {
    unselectAll();
    for (int i = 0; i < dup.size(); i++) {
      EdObject obj = (EdObject) dup.obj(i).clone();

      if (applyOffsets) {
        if (dbDUP) {
          Streams.out.println("dup/paste, acc=" + dupAccum);
        }

        obj.moveBy(obj, dupAccum);
      }

      obj.setSelected(true);
      items.add(obj);
    }
  }

  /**
   * Get a list of the selected items' indices
   * @return DArray of Integers
   */
  static DArray getSelectedItemInd() {
    return ObjArrayUtil.getItemSlots(null, items, true, false);
  }

  /**
   * Cancel editing operation, if one is occurring
   */
  private static void stopEdit() {
    final boolean db = dba;

    if (editObj != null) {
      if (db)
        Streams.out.println("stopEdit; editObj complete=" + editObj.complete());

      editObj.cleanUp();
      if (!editObj.complete()) {
        // perform an undo, which will remove the incomplete object.
        if (canUndo()) {
          unselectAll();
          doUndo();
          // delete any old 'redo' records that may be on the stack, as they
          // are no longer valid.
          // We don't want a redo restoring the incomplete object.
          while (undoStack.size() > stackPtr)
            undoStack.pop();
        }
      }
    }
    setEditObject(-1);
    setState(ES_READY);
  }

  private static void setEditObject(int index) {
    editObjInd = index;
    if (editObjInd < 0) {
      editObj = null;
    } else
      editObj = items.obj(editObjInd);
  }

  /**
   * Add a new object
   * @param obj
   * @return index of added object
   */
  private static int add(EdObject obj) {
    stopEdit();
    unselectAll();

    Undoable proc = new AddObjectsOper(obj);
    perform(proc);
    int ret = items.size() - 1;
    setEditObject(ret);

    editObj.setSelected(true);
    editField = 0;
    setState(ES_INSERTINGPT);
    if (dba)
      Streams.out.println("add(" + obj.getFactory().getTag()
          + "), setting ES_INSERTINGPT");
    return ret;
  }

  /**
   * Set editor state
   * @param s : new state
   */
  private static void setState(int s) {
    editState = s;
  }

  /**
   * Get EditObj
   * @param index : index of object 
   * @return
   */
  private static EdObject obj(int index) {
    return (EdObject) items.get(index);
  }

  /**
   * Perform rendering related to the editor.
   * Plots the objects, and if necessary, the 'rubber band' selection box.
   * Some applications may not want the objects plotted.  Otherwise, user 
   * should just call render().
   * @param allObjects if true, includes all objects
   * @param selectedObjects if true, includes selected objects 
   * @param activeObjects if true, includes active objects 
   */
  public static void render(boolean allObjects, boolean selectedObjects,
      boolean activeObjects) {

    Iterator it = items.iterator();
    while (it.hasNext()) {
      EdObject e = (EdObject) it.next();
      if (allObjects || (selectedObjects && e.isSelected())
          || (activeObjects && e.isActive()))
        e.render();
    }

    FRect r = null;
    double rRot = 0;
    int handleBits = 0x1;

    switch (editState) {
    case ES_BOX:
      r = new FRect(lastMouseDown, otherBoxCorner);
      break;
    case ES_ROTATING:
    case ES_ROTWAIT:
      r = editBox;
      rRot = rotThetaNow;
      break;
    case ES_SCALING:
    case ES_SCALEWAIT:
      r = editBox;
      //  handleBits = 0x1ff;
      break;
    }
    if (r != null) {
      FPoint2[] pts = calcBoxPoints(r, rRot);
      V.pushColor(Color.RED);
      V.pushStroke(TestBed.STRK_RUBBERBAND);
      V.drawLine(pts[1], pts[2]);
      V.drawLine(pts[2], pts[3]);
      V.drawLine(pts[3], pts[4]);
      V.drawLine(pts[4], pts[1]);
      V.popStroke();
      V.pushStroke(STRK_THIN);
      for (int i = 0; i < 9; i++) {
        if ((handleBits & (1 << 1)) != 0) {
          V.mark(pts[i], i == 0 ? MARK_X : MARK_CIRCLE);
        }
      }
      V.popStroke();
      V.popColor();
    }

  }

  private static int[] boxHandleOffsets = { 0, 0, -1, -1, 1, -1, 1, 1, -1, 1,
      0, -1, 1, 0, 0, 1, -1, 0, };

  /**
   * Calculate the nine handle points for the (possibly rotated) box.
   * Has the format:
   *  4 7 3
   *  8 0 6
   *  1 5 2
   * @param r box
   * @param rRot angle of rotation
   * @return array of nine points
   */
  private static FPoint2[] calcBoxPoints(FRect r, double rRot) {
    FPoint2[] ret = new FPoint2[9];

    FPoint2 cent = r.midPoint();
    Matrix mCenterOfBox = Matrix.getTranslate(cent, true);
    Matrix mRotate = Matrix.getRotate(rRot);
    Matrix mToWorld = Matrix.getTranslate(r.midPoint(), false);
    Matrix m = Matrix.mult(mToWorld, mRotate, null);
    Matrix.mult(m, mCenterOfBox, m);

    for (int i = 0; i < ret.length; i++) {
      FPoint2 boxPt = new FPoint2(cent.x + r.width * .5
          * boxHandleOffsets[i * 2 + 0], cent.y + r.height * .5
          * boxHandleOffsets[i * 2 + 1]);
      ret[i] = m.apply(boxPt, null);
    }
    return ret;
  }
  /**
   * Perform rendering related to the editor.
   * Plots the objects, and if necessary, the 'rubber band' selection box
   */
  public static void render() {
    render(true, true, true);
  }

  private static double nearPointDist() {
    return V.getScale() * 1;
  }

  /**
   * Find which object, if any, is at a point.
   * Highlighted items have precedence; otherwise, topmost items have precedence.
   * @param pt : point in view space
   * @return index of object, or -1 if none
   */
  private static int findObjAt(FPoint2 pt) {

    final boolean db = false;

    if (db)
      Streams.out.println("findObjAt " + pt);

    int ret = -1;
    EdObject retObj = null;

    double maxDist = nearPointDist();
    if (db)
      Streams.out
          .println("view scale=" + V.getScale() + ", maxDist=" + maxDist);

    for (int i = items.size() - 1; i >= 0; i--) {
      EdObject obj = items.obj(i);
      Tools.ASSERT(obj.complete(), "findObjAt, wasn't complete");

      if (retObj != null && !obj.isSelected())
        continue;

      double distFromObj = obj.distFrom(pt);
      Tools.ASSERT(distFromObj >= 0);

      if (db)
        Streams.out.println(" dist from #" + i + " is " + distFromObj);

      if (distFromObj > maxDist)
        continue;

      retObj = obj;
      ret = i;
      if (retObj.isSelected())
        break;
    }
    if (db)
      Streams.out.println(" returning #" + ret);

    return ret;
  }

  /**
   * Unselect all objects
   */
  public static void unselectAll() {
    Iterator it = items.iterator();
    while (it.hasNext()) {
      EdObject e = (EdObject) it.next();
      e.setSelected(false);
    }
    setState(ES_READY);
  }

  private static void selectAll() {
    Iterator it = items.iterator();
    while (it.hasNext()) {
      EdObject e = (EdObject) it.next();
      e.setSelected(true);
    }
  }

  /**
   * Perform enable/disable of a menu's items in preparation for
   * it being shown.
   * @param menu : menu containing item
   * @param item : the item to enable/disable
   * @return new enabled state of item
   */
  static boolean processMenuEnable(int menu, int item) {
    boolean en = true;
    switch (menu) {
    case G_FILE:
      switch (item) {
      case G_OPENNEXT:
        en = fileStats.getPath() != null;
        break;
      case G_ERRORSET:
        en = errorItems != null;
        break;
      case G_REVERT:
        en = fileStats.getPath() != null;
        break;
      // allow saving even if file not modified, to allow saving of control settings
      //      case G_SAVE:
      //        en = fileModified;
      //        break;
      case G_NEW:
        en = !items.isEmpty() && fileStats.getPath() != null;
        break;
      }
      break;
    case G_EDIT:
      switch (item) {
      case G_UNDO:
        en = canUndo();
        break;
      case G_REDO:
        en = canRedo();
        break;
      case G_CUT:
      case G_COPY:
      case G_DUP:
      case G_BACK:
      case G_FRONT:
      case G_BACKWARD:
      case G_FORWARD:
      case G_DELETEPT:
      case G_ROTATE:
        en = !getSelectedItemInd().isEmpty();
        break;
      case G_PASTE:
        en = !clipboard.isEmpty();
        break;
      case G_ALL:
        en = !items.isEmpty();
        break;
      case G_ADDANOTHER:
        en = lastTypeAdded >= 0;
        break;
      }
      break;
    }
    return en;
  }

  /**
   * Add segment types to editor menu
   */
  private static void addObjectMenuItems() {
    boolean sep = false;
    for (int i = 0; i < nObjTypes(); i++) {
      EdObjectFactory f = getType(i);
      String lbl = f.getMenuLabel();
      if (lbl == null)
        continue;
      String keyEquiv = f.getKeyEquivalent();
      if (keyEquiv != null)
        keyEquiv = "!" + keyEquiv;
      if (!sep) {
        sep = true;
        C.sMenuSep();
      }
      C.sMenuItem(G_ADDITEMS + i, lbl, keyEquiv);
    }
    C.sMenuItem(G_ADDANOTHER, "Add another", "!z");
  }

  //  private static String filePath() {
  //    
  //    String filePath = C.vs(G_FILEPATH);
  //    if (filePath.length() == 0)
  //      filePath = null;
  //    return filePath;
  //  }

  private static class ObjIterator implements Iterator {
    private static final boolean db = false;

    public ObjIterator(Iterator iter, EdObjectFactory type,
        boolean selectedOnly, boolean skipInactive) {

      if (db)
        Streams.out.println("ObjIterator, type="
            + (type == null ? "<any>" : type.getTag()) + " selectedOnly="
            + selectedOnly + " skipInactive=" + skipInactive);

      this.iter = iter;
      this.objType = type;
      this.selectedOnly = selectedOnly;
      this.skipInactive = skipInactive;
      prepareNext();
    }

    private void prepareNext() {
      next = null;
      while (true) {
        if (!iter.hasNext())
          break;
        EdObject obj = (EdObject) iter.next();
        if (db)
          Streams.out.println("prepareNext, obj=" + obj + " fact="
              + obj.getFactory());

        if (!obj.complete())
          continue;
        if (selectedOnly && !obj.isSelected())
          continue;
        if (skipInactive && !obj.isActive())
          continue;
        if (objType != null && obj.getFactory() != objType) {
          if (db)
            Streams.out.println(" type disagrees: obj.fact=" + obj.getFactory()
                + " != " + objType);
          continue;
        }
        next = obj;
        break;
      }
      if (db)
        Streams.out.println("prepareNext: " + next);
    }
    public boolean hasNext() {
      return next != null;
    }

    public Object next() {
      Object r = next;
      if (r == null)
        throw new NoSuchElementException();
      prepareNext();
      if (db)
        Streams.out.println("next(), returning " + r);

      return r;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
    private Object next;
    private Iterator iter;
    private EdObjectFactory objType;
    private boolean selectedOnly;
    private boolean skipInactive;
  }

  /**
   * Get array of EdObjects that match certain criteria.
   * Skips objects that are not complete (i.e., are in the process of being
   * created).
   * <br>
   * This should be called only if the resulting objects are 'read only'; 
   * if they are to be edited, use editObjects() instead.
   * 
   * @param objType  type of object, or null for any
   * @param selectedOnly  if true, includes objects only if they are 
   *   currently selected
   * @param skipInactive  if true, skips inactive objects
   * @return DArray of matching objects
   */
  public static DArray readObjects(EdObjectFactory objType,
      boolean selectedOnly, boolean skipInactive) {
    DArray a = new DArray();
    Iterator it = iterator(objType, selectedOnly, skipInactive);
    while (it.hasNext()) {
      a.add(it.next());
    }
    return a;
  }

  /**
   * Get an iterator over EditObjects.  Objects must be complete.
   * @param objType  type of object, or null for any
   * @param selectedOnly  if true, includes only selected objects
   * @param skipInactive  if true, skips inactive objects
   * @return Iterator
   */
  private static Iterator iterator(EdObjectFactory objType,
      final boolean selectedOnly, boolean skipInactive) {
    return new ObjIterator(items.iterator(), objType, selectedOnly,
        skipInactive);
  }

  /**
   * Save file
   * @param type G_SAVExxx
   */
  private static void doSave(int type) {

    final boolean db = false; //Tools.dbWarn();

    String filePath = fileStats.getPath();

    stopEdit();
    unselectAll();

    if (db)
      Streams.out.println("doSave type=" + type + ", filePath=" + filePath);

    if (filePath == null || new File(filePath).isDirectory()) {
      type = G_SAVEAS;
      if (db)
        Streams.out.println(" change to SAVEAS");

    }

    do {
      if (type != G_SAVEASNEXT)
        break;
      type = G_SAVEAS;
      int ns = -1;
      for (int i = 0; i < filePath.length(); i++) {
        if (Character.isDigit(filePath.charAt(i))) {
          ns = i;
          break;
        }
      }
      if (ns < 0)
        break;

      int j = ns;
      while (j < filePath.length() && Character.isDigit(filePath.charAt(j)))
        j++;
      try {
        if (db)
          Streams.out.println(" ns=" + ns + " j=" + j);

        int fn = 1 + Integer.parseInt(filePath.substring(ns, j));
        String fns = Integer.toString(fn);
        while (fns.length() < 2)
          fns = "0" + fns;
        filePath = filePath.substring(0, ns) + fns + filePath.substring(j);
        fileStats.setPath(filePath);
        if (db)
          Streams.out.println(" fileStats path=" + fileStats.getPath());

      } catch (NumberFormatException e) {
        break;
      }
    } while (false);

    //    switch (type) {
    //    default:
    //      if (filePath == null || new File(filePath).isDirectory())
    //        type = G_SAVEAS;
    //      break;
    //    case G_SAVEASNEXT:
    //    {
    //      if (filePath == null || new File(filePath).isDirectory())
    //
    //    }
    //      break;
    //    }

    //    if (!saveAs) {
    //      if (filePath == null || new File(filePath).isDirectory())
    //        saveAs = true;
    //    }
    String f = (type == G_SAVEAS) ? getSavePath() : filePath;

    if (f != null) {
      try {
        PrintWriter pw = new PrintWriter(Streams.writer(f));

        try {
          fileStats.setPath(f);

          C.printGadgets(pw, false);

          StringBuilder sb = new StringBuilder();
          // sb.setLength(0);
          for (int i = 0; i < items.size(); i++) {
            EdObject obj = items.obj(i);
            write(obj, sb);
          }
          pw.write(sb.toString());
        } finally {
          pw.close();
        }
      } catch (IOException e) {
        throw new TBError(e.getMessage());
      }
      clearUndo(true);
      justSaved = true;
      updateTitle();
      TestBed.writeConfigFile();
    }
  }
  /**
   * Output current file to string
   * @return string containing file
   */
  public static String saveToString() {

    StringWriter sw = new StringWriter();

    PrintWriter pw = new PrintWriter(sw);
    C.printGadgets(pw, false);

    StringBuilder sb = new StringBuilder(sw.getBuffer());
    // sb.setLength(0);
    for (int i = 0; i < items.size(); i++) {
      EdObject obj = items.obj(i);
      write(obj, sb);
    }
    return sb.toString();
  }

  public static StringBuilder write(EdObject obj, StringBuilder sb) {

    if (false) {
      Tools.warn("db");
      Streams.out.println("writing poly:\n" + Tools.stackTrace(0, 5));
    }

    if (sb == null)
      sb = new StringBuilder();
    EdObjectFactory fa = obj.getFactory();
    sb.append(fa.getTag());
    sb.append(' ');
    TextScanner.toHex(sb, obj.flags());
    sb.append(' ');
    fa.write(sb, obj);
    sb.append('\n');
    return sb;
  }

  private static void doOpen(String f) {
    final boolean db = false;
    if (db)
      Tools.warn("db is on");
    try {

      stopEdit();
      unselectAll();
      Tokenizer tk = null;

      if (f == null) {
        String s = fileStats.getPath();
        if (s == null) {
          s = Path.getUserDir();
        }
        IFileChooser ch = Streams.fileChooser();
        String ns = ch.doOpen("Open file:", s, new PathFilter(
            TestBed.parms.fileExt));
        if (ns != null) {
          f = Path.changeExtension(ns, TestBed.parms.fileExt);
        }
      }
      if (f != null) {

        clearUndo(true);

        // read the new file
        tk = new Tokenizer(f);
        if (db) {
          tk.setEcho(true);
          tk.setTrace(true);
        }

        // create a new array to read file into
        ObjArray dp = new ObjArray();

        C.parseGadgets(tk);
          
        // read items
        while (!tk.eof()) {
          String tag = tk.read(T_WORD).text();
          Integer iv = (Integer) objTypesMap.get(tag);
          if (iv == null)
            throw new TBError("Unrecognized tag: " + tag);

          EdObjectFactory fa = getType(iv.intValue());
          if (db)
            Streams.out.println("factory for " + iv + " is " + fa);
          String wd = tk.read().text();
          int flags = TextScanner.parseHex(wd, true);

          EdObject eo = fa.parse(tk, flags);
          if (db)
            Streams.out.println(" parsed object " + eo + ", type="
                + eo.getFactory());

          dp.add(eo);
        }

        // now that read was ok, replace original
        items = dp;
        fileStats.setPath(f);

        updateTitle();
        TestBed.writeConfigFile();

      }
      if (tk != null)
        tk.close();

    } catch (Throwable e) {
      if (TestBed.parms.debug)
        Streams.out.println(Tools.d(e));
      throw new TBError(e);
    }
  }
  //  private static void setFilePath(String f) {
  //    fileStats.setPath(f);
  //  }

  /**
   * Request name of text file to write
   * @param fc FCData
   * @param autoMode boolean
   * @return File
   */
  private static String getSavePath() {
    String filePath = fileStats.getPath();
    //    if (filePath == null)
    //      filePath = Path.getUserDir();

    String s = filePath;
    String f = null;
    IFileChooser ch = Streams.fileChooser();

    f = ch.doWrite("Save file:", s, new PathFilter(TestBed.parms.fileExt));
    if (f != null) {
      f = Path.changeExtension(f, TestBed.parms.fileExt);
    }
    return f;
  }

  private static void updateTitle() {
    StringBuilder sb = new StringBuilder();
    TestBed app = TestBed.app;
    String title = app.title();

    sb.append(title);

    String s = fileStats.getPath();
    if (s != null) {
      sb.append(" : ");
      if (!TestBed.isApplet())
        s = Path.relativeToUserHome(new File(s));

      sb.append(s);
      if (fileStats.modified())
        sb.append("   (changes not written)");
      else if (justSaved) {
        justSaved = false;
        sb.append("   (saved)");
      }
    }
    String t = sb.toString();
    app.setExtendedTitle(t);
    app.updateTitle();

    displayedModified = fileStats.modified();
  }
  private static boolean justSaved;

  static ObjArray getItems() {
    return items;
  }

  static void touch() {
    fileStats.setModified(true);
  }

  static ObjArray getClipboard() {
    return clipboard;
  }

  static void setClipboard(ObjArray c) {
    clipboard = c;
  }
  static boolean getFileModified() {
    return fileStats.modified();
  }

  /**
   * Select specific items
   * @param slots an array of Integers indicating which objects
   *  should be selected
   */
  static void setSelectedItems(DArray slots) {
    unselectAll();
    for (int i = 0; i < slots.size(); i++)
      items.obj(slots.getInt(i)).setSelected(true);
  }

  //  /**
  //   * @deprecated add vert flag
  //   * Determine if if label objects checkbox is selected
  //   * @return true if label objects checkbox is selected
  //   */
  //  public static boolean withLabels() {
  //    return C.vb(G_WITHLABELS);
  //  }

  /**
   * Determine if if label vertices checkbox is selected
   * @return true if label vertices checkbox is selected
   */
  public static boolean withLabels(boolean vertices) {
    return C.vb(vertices ? G_LABELVERTS : G_WITHLABELS);
  }

  /**
   * @param label
   * @param position
   * @param box
   * @param c
   */
  public static void plotLabel(String label, FPoint2 position, boolean box,
      Color c) {
    plotLabel(label, position.x, position.y, box, c);
  }
  /**
   * @param label
   * @param x
   * @param y
   * @param box
   */
  public static void plotLabel(String label, double x, double y, boolean box) {
    plotLabel(label, x, y, box, null);
  }

  /**
   * @param vert true if vertex label vs object label
   * @param label
   * @param x
   * @param y
   * @param box
   * @param c
   */
  public static void plotLabel(String label, double x, double y, boolean box,
      Color c) {
    {
      if (c == null)
        c = MyColor.cDARKGREEN;
      V.pushColor(c);
      V.pushScale(.7);
      V.draw(label, x, y, box ? TX_BGND | TX_FRAME : 0);
      V.popScale();
      V.popColor();
    }
  }

  static void preparePaint() {
    if (initialized()) {
      int siteNumber = 0;

      StringBuilder sb = new StringBuilder();
      for (Iterator it = iterator(null, false, true); it.hasNext();) {
        EdObject ed = (EdObject) it.next();
        sb.setLength(0);
        int set = 1 + siteNumber / 52;
        int i = siteNumber % 52;
        if (i < 26)
          sb.append((char) (i + 'A'));
        else
          sb.append((char) (i - 26 + 'a'));

        if (set > 1)
          sb.append(set);
        String lbl = sb.toString();

        siteNumber++;
        ed.setLabel(lbl);
      }
    }
  }

  // --------------- undo/redo -----------------------------
  //
  //

  /**
   * Clear the undo stack
   */
  private static void clearUndo(boolean makeFileUnmodified) {
    undoStack.clear();
    stackPtr = 0;
    if (makeFileUnmodified)
      fileStats.setModified(false);
    undoFileWasModified = fileStats.modified();
  }

  /**
   * Determine if undo operation is available
   */
  private static boolean canUndo() {
    return stackPtr != 0;
  }

  /**
   * Determine if redo operation is available
   * @return
   */
  private static boolean canRedo() {
    return stackPtr < undoStack.size();
  }

  /**
   * Perform an undo
   */
  private static void doUndo() {
    if (db)
      Streams.out.println("Undo.doUndo()\n" + Tools.stackTrace(0, 4));

    Undoable rec = lastUndo();
    Undoable redo = rec.getUndo();
    rec.perform();

    undoStack.set(stackPtr - 1, redo);
    stackPtr--;

    if (stackPtr == 0)
      fileStats.setModified(undoFileWasModified);

  }

  /**
   * Perform a redo
   */
  private static void doRedo() {
    if (db)
      Streams.out.println("Undo.doRedo");

    Undoable rec = getUndo(stackPtr);
    Undoable undo = rec.getUndo();

    if (stackPtr == 0) {
      undoFileWasModified = fileStats.modified();
    }

    rec.perform();
    touch();
    undoStack.set(stackPtr, undo);
    stackPtr++;
  }

  /**
   * Read last operation from undo stack
   * @return
   */
  private static Undoable lastUndo() {
    return getUndo(stackPtr - 1);
  }

  /**
   * Read an operation from the undo stack
   * @param index
   * @return IUndo
   */
  private static Undoable getUndo(int index) {
    return (Undoable) undoStack.get(index);
  }

  // stack of IUndo objects
  private static DArray undoStack;
  // true if file will be in a modified state if every stacked operation
  // is undone
  private static boolean undoFileWasModified;

  // index of next item available for redoing (if >= stack size, none)
  private static int stackPtr;

  private static final int MAX_UNDO = 20;

  private static void perform(Undoable proc) {

    final boolean db = false;

    if (proc.valid()) {
      // delete any old 'redo' records that may be on the stack, as they
      // are no longer valid.
      while (undoStack.size() > stackPtr)
        undoStack.pop();

      Undoable undoOper = proc.getUndo();

      if (db)
        Streams.out.println("Undo.perform: " + Undoable.toString(proc)
            + " (saving undo: " + Undoable.toString(undoOper) + ")");

      undoStack.add(undoOper);
      stackPtr++;

      while (undoStack.size() > MAX_UNDO) {
        stackPtr--;
        undoStack.remove(0);
      }
      proc.perform();
    }

  }

  /**
   * Get most recent list of selected items stored on undo stack
   * @return ObjArray
   */
  private static ObjArray lastUndoItems() {
    return ((ChangeItemsOper) lastUndo()).items();
  }

  /**
   * Get most recent selected item indices stored on undo stack
   * @return DArray of Integers indicating position of selected items
   */
  private static DArray lastUndoItemSlots() {
    return ((ChangeItemsOper) lastUndo()).itemSlots();
  }

  //
  //
  // --------------- undo/redo -----------------------------

  private static DArray objectFactories;
  private static Map objTypesMap;

  private static boolean displayedModified;

  // edit state: ES_xxx
  private static int editState;

  // object being edited, or null
  private static EdObject editObj;

  // index of editObj, or -1
  private static int editObjInd;

  // collection of EditObj'ects
  private static ObjArray items;

  // items in effect when tracing error occurred
  private static ObjArray errorItems;

  // edit object field number being edited
  private static int editField;

  // point at which mouse down last occurred
  private static FPoint2 lastMouseDown;

  // debug dragging logic?
  private final static boolean dp = false;

  // offset between mouse down position and precise location of object 
  // being moved
  private static FPoint2 adjustPositionOffset;

  private static ObjArray clipboard;

  private static FPoint2 dupOffset = new FPoint2();

  private static FPoint2 dupAccum = new FPoint2();

  // G_xxx: operation last used dup vars
  private static int dupOper;

  private static FileStats fileStats;
  //  private static boolean fileModified;

  // pending undo for drag-related operation
  private static Undoable pendingUndo;

  // ES_BOX:
  // first corner of box 
  private static FPoint2 otherBoxCorner;

  // ES_ROTWAIT, ES_ROTATING:

  // bounding box of selected items
  private static FRect editBox;
  // original bounding box of selected items
  private static FRect editBoxOrig;
  // theta at initial mouse down
  private static double rotThetaOrig;
  // theta currently
  private static double rotThetaNow;
  // initial mouse down position
  private static FPoint2 rotStartPt;
  // scale both x and y dimensions uniformly (i.e., shift pressed?)
  private static boolean scaleUniformly;

  // location where last mouse up occured (drift waiting in poly edit)
  private static FPoint2 mouseUpLoc;
}
