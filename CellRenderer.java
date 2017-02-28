/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author monkeyboz
 */
public class CellRenderer implements TreeCellRenderer{
    private final JLabel label;

    CellRenderer() {
        label = new JLabel();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        String treeText = value.toString().trim().replace("\"","");
        ImageIcon baseIcon = null;
        
        if (treeText.toLowerCase().contains("centos")) {
            baseIcon = new ImageIcon(getClass().getResource("centos.png"));
        } else if(treeText.toLowerCase().contains("ubuntu")) {
            baseIcon = new ImageIcon(getClass().getResource("ubuntu.png"));
        } else if(treeText.toLowerCase().contains("windows")) {
            baseIcon = new ImageIcon(getClass().getResource("windows.png"));
        } else {
            baseIcon = null;
        }
        
        if(baseIcon != null){
            label.setIcon(baseIcon);
            label.setText(treeText.trim().replaceAll(" \\{(.*)\\}","")+" (click to start)");
        } else {
            label.setIcon(baseIcon);
            label.setText(treeText.trim());
        }
        return label;
    }
}