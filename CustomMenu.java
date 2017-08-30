/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author monkeyboz
 */
public class CustomMenu extends JPanel{
    Color backgroundColor;
    Color selectColor;
    Color lightbackgroundColor;
    ArrayList<JPanel> menu;
    JPanel dropdown;
    int menuWidth;
    ArrayList<Integer> maxMenuWidth;
    ArrayList<ArrayList<Integer>> orders;
    int totalParentComponents;
    JPanel menuPanel;
    JLabel icon;
    Timer animationTimer;
    VBox parent;
    JPanel self;
    JPanel mainHolder;
    
    public CustomMenu(ArrayList<String[]> menuItems,Color backgroundColorHolder,Color lightBackgroundHolder, Color selectColorHolder,VBox parentHolder){
        //totalParentComponents = this.getParent().getParent().getComponentCount();
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        backgroundColor = backgroundColorHolder;
        lightbackgroundColor = lightBackgroundHolder;
        selectColor = selectColorHolder;
        
        self = this;
        parent = parentHolder;
        
        menuWidth = 0;
        maxMenuWidth = new ArrayList<>();
        int parent = 0;
        int count = 0;
        
        menu = new ArrayList<>();
        orders = new ArrayList<>();
        
        dropdown = new JPanel();
        menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainHolder = new JPanel();
        
        for(int j = 0; j < menuItems.size(); ++j){
            ArrayList<Integer> list = new ArrayList<>();
            Integer maxMenuWidthHolder = 0;
            JPanel drop = new JPanel();
            for(int i = 0; i < menuItems.get(j).length; ++i){
                list.add(0,count);
                boolean main = (i == 0);
                drop = createMenuItem(menuItems.get(j)[i],main,parent);
                if(drop.getPreferredSize().width > maxMenuWidthHolder){
                    maxMenuWidthHolder = drop.getPreferredSize().width;
                }
                menu.add(drop);
                ++count;
            }
            ++parent;
            maxMenuWidth.add(maxMenuWidthHolder);
            orders.add(list);
        }
        
        for(int i = 0; i < orders.size(); ++i){
            dropdown.add(menu.get(orders.get(i).get(orders.get(i).size()-1)));
        }
        
        this.setVisible(true);
        
        icon = new JLabel();
        icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/information/images/dropdown.png")));
        icon.setBounds(0,0,10,10);
        icon.setBackground(backgroundColor);
        icon.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent evt){
                dropdown.setVisible(true);
                if(animationTimer != null){
                    animationTimer.stop();
                    animationTimer = animate(true,dropdown.getY(),100);
                } else {
                    animationTimer = animate(true,dropdown.getY(),100);
                }
            }
        });
        
        mainHolder = new JPanel();
        mainHolder.add(new JLabel("testing"));
        mainHolder.setPreferredSize(new Dimension(dropdown.getWidth(),1));
        mainHolder.setBackground(backgroundColor);
        dropdown.setBackground(backgroundColor);
        dropdown.setVisible(false);
    }
    
    public void setParentComponentCount(){
        this.setBackground(backgroundColor);
        this.add(icon);
        this.add(mainHolder);
        this.add(dropdown);
        totalParentComponents = this.getParent().getParent().getComponentCount();
    }
    
    private JPanel createMenuItem(String text,boolean main,int parent){
        JPanel menuItem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel menuText = new JLabel(text);
        menuText.setText(text);
        menuText.setForeground(Color.WHITE);
        
        menuItem.setVisible(true);
        menuItem.add(menuText);
        
        if(main){
            menuItem.setBackground(backgroundColor);
        }else{
            menuItem.setBackground(lightbackgroundColor);
        }
        menuItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        menuItem.addMouseListener(new MouseAdapter(){
            int info = parent;
            boolean mainMenu = main;
            String textHolder = text;
            
            @Override
            public void mousePressed(MouseEvent evt){
                if(main){
                    setupMenu(mainMenu,info,new int[]{evt.getX(),evt.getY()},true);
                } else {
                    showOption(textHolder);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent evt){
                evt.getComponent().setBackground(selectColor);
            }
            
            public void mouseExited(MouseEvent evt){
                if(main){
                    evt.getComponent().setBackground(backgroundColor);
                }else{
                    evt.getComponent().setBackground(lightbackgroundColor);
                }
            }
        });
            
        menuText.addMouseListener(new MouseAdapter(){
            int info = parent;
            boolean mainMenu = main;
            String textHolder = text;
            
            @Override
            public void mousePressed(MouseEvent evt){
                if(main){
                    setupMenu(mainMenu,info,new int[]{evt.getX(),evt.getY()},true);
                }else{
                    showOption(textHolder);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent evt){
                evt.getComponent().getParent().setBackground(selectColor);
            }
            
            @Override
            public void mouseExited(MouseEvent evt){
                if(main){
                    evt.getComponent().getParent().setBackground(backgroundColor);
                }else{
                    evt.getComponent().getParent().setBackground(lightbackgroundColor);
                }
            }
        });
        
        if(main){
            menuWidth += menuItem.getWidth();
        }
        
        return menuItem;
    }
    
    private void showOption(String text){
        text = text.replace(" ","");
        try{
            parent.getClass().getMethod(text+"Menu").invoke(parent);
            menuPanel.setVisible(false);
        }catch(IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException ex){
            System.out.println(ex);
        }
    }
    
    public Timer animate(boolean first,int y,int height){
        if(first){
            int heightH = height;
            int yH = y;
            dropdown.setBounds(dropdown.getX(),10,dropdown.getWidth(),0);
            ActionListener test = new ActionListener() {
                int v = heightH;
                int step = 20;
                int m = yH;
                @Override
                public void actionPerformed(ActionEvent e) {
                    animate(v,m,--step);
                }
            };
            Timer testing = new Timer(10,test);
            testing.start();
            return testing;
        }
        return new Timer(0, (ActionEvent e) -> {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        });
    }
    
    public void animate(int height,int y, int currStep){
        if(currStep != 1){
            dropdown.setVisible(true);
            mainHolder.setVisible(false);
            dropdown.setBounds(dropdown.getX(),(int)((float)y/(float)currStep),dropdown.getWidth(),(int)((float)height/(float)currStep));
        } else {
            animationTimer.stop();
        }
    }
    
    private void setupMenu(boolean main,int info,int[] position,boolean show){
        if(main){
            menuPanel.removeAll();
            int maxWidth = 0;
            for(int i = 0; i < orders.get(info).size()-1; ++i){
                menu.get(orders.get(info).get(i)).setPreferredSize(new Dimension(maxMenuWidth.get(info),29));
                menuPanel.add(menu.get(orders.get(info).get(i)));
            }
            
            menuPanel.setOpaque(true);
            menuPanel.setBounds((int)(menu.get(orders.get(info).get(orders.get(info).size()-1)).getX()+this.getX()),
                    (int)(menu.get(orders.get(info).get(orders.get(info).size()-1)).getY()+this.getY()+29),
                    maxMenuWidth.get(info)+10,(orders.get(info).size()-1)*39);

            menuPanel.setBackground(lightbackgroundColor);
            menuPanel.setVisible(true);
            this.getParent().getParent().getParent().add(menuPanel,0);
        }
    }
}
