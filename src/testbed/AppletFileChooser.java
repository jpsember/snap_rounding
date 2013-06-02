package testbed;

import base.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;

class AppletFileChooser implements IFileChooser {
	
	/**
	 * Get name of file to open
	 * 
	 * @param path :
	 *          previously selected filename
	 * @param filter :
	 *          if not null, FileFilter to apply to directory
	 * @return String : if not null, path of file to open
	 */
	public String doOpen(String prompt, String path, FilenameFilter filter) {
		DArray a = getFilteredList(filter);
		String out = myListDialog.showDialog(null, null, prompt, a, findString(
				path, a), false);

		return out;
	}

	private DArray getFilteredList(FilenameFilter filter) {
		DArray a = new DArray();
		SimFile[] ls = ((AppletFileAccess)Streams.getFileAccess()).appletFileList().getFiles();
		
		for (int i = 0; i < ls.length; i++) {
			String name = ls[i].name();
			
			if (filter != null && !filter.accept(null,name))
				continue;
			a.add(name);
		}
		return a;
	}

	private static int findString(String str, DArray objects) {
		int out = -1;
		if (str != null)
			for (int i = 0; i < objects.size(); i++) {
				if (str.equals(objects.get(i).toString())) {
					out = i;
					break;
				}
			}
		return out;
	}

	/**
	 * Get name of file to write
	 * 
	 * @param path :
	 *          previously selected filename
	 * @param filter :
	 *          if not null, FileFilter to apply to directory
	 * @return String : if not null, path of file to write
	 */
	public String doWrite(String prompt, String path, FilenameFilter filter) {
		DArray a = getFilteredList(filter);
		String out = myListDialog.showDialog(null, null, prompt, a, findString(
				path, a), true);

		return out;
	}

}

class myListDialog extends JDialog implements ActionListener,
		ListSelectionListener, KeyListener {

	public void valueChanged(ListSelectionEvent e) {
		// System.out.println("valueChanged, "+e.getFirstIndex()+" to
		// "+e.getLastIndex());
		for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
			if (list.isSelectedIndex(i)) {
				editPath.setText(items.get(i).toString());
			}
		}
	}

	/**
	 * Set up and show the dialog. The first Component argument determines which
	 * frame the dialog depends on; it should be a component in the dialog's
	 * controlling frame. The second Component argument should be null if you want
	 * the dialog to come up with its left corner in the center of the screen;
	 * otherwise, it should be the component on top of which the dialog should
	 * appear.
	 */
	public static String showDialog(Component frameComp, Component locationComp,
			String title, DArray items, int initialItem, boolean saveMode) {
		Frame frame = null;
		if (frameComp != null)
			frame = JOptionPane.getFrameForComponent(frameComp);
		myListDialog dialog = new myListDialog(frame, locationComp, title, items,
				initialItem, saveMode);
		dialog.setVisible(true);

		return dialog.value;
	}

	private void setValue(String item) {
		value = item;
		list.setSelectedValue(value, true);
	}

	private DArray items;

	private myListDialog(Frame frame, Component locationComp, String title,
			DArray items, int initialItem, boolean saveMode) {
		super(frame, title, true);
		this.items = items;

		// Create and initialize the buttons.
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		openButton = new JButton(saveMode ? "Save" : "Open");
		// openButton.setActionCommand("Open");
		openButton.addActionListener(this);
		getRootPane().setDefaultButton(openButton);

		editPath = new JTextField();
		if (initialItem >= 0) {
			editPath.setText(items.get(initialItem).toString());
		}
		editPath.setEditable(saveMode);
		if (saveMode) {
			// editPath.addCaretListener(this);
			editPath.addKeyListener(this);
		}

		// main part of the dialog
		list = new JList(items.toArray());
		list.setFont(SwingTools.monoFont());
		list.addListSelectionListener(this);
		if (initialItem >= 0)
			list.setSelectedIndex(initialItem);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					openButton.doClick(); // emulate button click
				}
			}
		});

		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(300, 400));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);

		JPanel bp = new JPanel(new GridBagLayout());
		bp.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		bp.add(editPath, SwingTools.setGBC(0, 0, 1, 1, 0, 0));

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(cancelButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(openButton);
		bp.add(buttonPane, SwingTools.setGBC(0, 1, 1, 1, 100, 100));

		// Put everything together, using the content pane's BorderLayout.
		Container contentPane = getContentPane();
		contentPane.add(listScroller, BorderLayout.CENTER);
		contentPane.add(bp, BorderLayout.PAGE_END);

		// Initialize values.
		if (initialItem >= 0)
			setValue(items.get(initialItem).toString());
		pack();
		setLocationRelativeTo(locationComp);
	}

	// Handle clicks on the Set and Cancel buttons.
	public void actionPerformed(ActionEvent e) {
		// System.out.println("actionPerformed, e="+e);
		if (e.getSource() == openButton) {
			// System.out.println("actionPerformed, setting to
			// "+list.getSelectedValue());
			if (list.getSelectedIndex() >= 0) {
				value = (String) list.getSelectedValue();
			} else {
				String s = editPath.getText();
				if (s.length() > 0)
					value = s;
			}
			setVisible(false);
		} else {
			value = null;
			setVisible(false);
		}
	}

	public void keyTyped(KeyEvent keyEvent) {
		list.clearSelection();
	}

	public void keyPressed(KeyEvent keyEvent) {
	}

	public void keyReleased(KeyEvent keyEvent) {
	}

	private JButton openButton, cancelButton;

	private JTextField editPath;

	private JList list;

	private String value;
}

