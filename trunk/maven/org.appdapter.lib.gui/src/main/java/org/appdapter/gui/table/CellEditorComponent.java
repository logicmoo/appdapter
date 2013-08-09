package org.appdapter.gui.table;

import java.awt.Component;

import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;

public interface CellEditorComponent {
	public TableCellEditor getCellEditor();
	public TreeCellEditor getTreeCellEditor();

	public Component getCustomEditor();
}