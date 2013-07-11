/*
 * Created on 9/10/2003
 * YAWLEditor v1.0 
 *
 * @author Lindsay Bradford
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.yawlfoundation.yawl.editor.ui.actions.datatypedialog;

import org.yawlfoundation.yawl.editor.ui.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.editor.ui.swing.menu.DataTypeDialogToolBarMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FindTextDataTypeDialogAction extends YAWLBaseAction {

    private DataTypeDialogToolBarMenu toolbar;

    {
        putValue(Action.SHORT_DESCRIPTION, " Find text");
        putValue(Action.NAME, "FindText");
        putValue(Action.LONG_DESCRIPTION, "Find Text");
        putValue(Action.SMALL_ICON, getPNGIcon("find"));
        putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_F));
    }

    public FindTextDataTypeDialogAction(DataTypeDialogToolBarMenu bar) {
        toolbar = bar;
    }

    public void actionPerformed(ActionEvent event) {
        DataTypeDialogToolBarMenu.getEditorPane().findText(toolbar.getFindText());
    }
}