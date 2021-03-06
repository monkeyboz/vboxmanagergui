/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import javax.swing.ImageIcon;
//import com.jcraft.jsch.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author monkeyboz
 */
public class CreateServerConnection extends javax.swing.JFrame {

    private VBox parent;
    private int[] mouseOrigin;
    private String keypairFile;
    private String commandString;
    private final String currentCommandString;
    private String outputString;
    
    //private JSch serverTest;
    /**mouseOrigin
     * Creates new form CreateServer
     */
    public CreateServerConnection() {
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        
        jLabel6.setIcon(new ImageIcon(getClass().getResource("/images/close_button.png")));
        currentCommandString = "";
        outputString = "";

        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(getClass().getResource("/images/vmicon.png").getFile());
        this.setIconImage(img);
        //this.setBounds(0,0,500,258);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        serverNameLabel = new javax.swing.JLabel();
        serverName = new javax.swing.JTextField();
        ipLabel = new javax.swing.JLabel();
        ipField = new javax.swing.JTextField();
        usernameLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        submitButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        portField = new javax.swing.JTextField();
        portLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        keypairField = new javax.swing.JPasswordField();
        passwordLabel1 = new javax.swing.JLabel();
        submitButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setUndecorated(true);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        jLayeredPane1.setBackground(new java.awt.Color(51, 51, 51));
        jLayeredPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jLayeredPane1.setForeground(new java.awt.Color(255, 255, 255));
        jLayeredPane1.setOpaque(true);

        serverNameLabel.setForeground(new java.awt.Color(255, 255, 255));
        serverNameLabel.setText("Server Name");

        serverName.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        ipLabel.setForeground(new java.awt.Color(255, 255, 255));
        ipLabel.setText("IP Address");

        ipField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        usernameLabel.setForeground(new java.awt.Color(255, 255, 255));
        usernameLabel.setText("Login");

        usernameField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        passwordLabel.setForeground(new java.awt.Color(255, 255, 255));
        passwordLabel.setText("Password");

        submitButton.setText("Submit Server");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("close");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });

        portField.setText("22");
        portField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        portLabel.setForeground(new java.awt.Color(255, 255, 255));
        portLabel.setText("Port");

        passwordField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        passwordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordFieldActionPerformed(evt);
            }
        });

        keypairField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        keypairField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keypairFieldActionPerformed(evt);
            }
        });

        passwordLabel1.setForeground(new java.awt.Color(255, 255, 255));
        passwordLabel1.setText("Public Key Pair File");

        submitButton1.setText("Search For Key File");
        submitButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButton1ActionPerformed(evt);
            }
        });

        jLayeredPane1.setLayer(serverNameLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(serverName, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(ipLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(ipField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(usernameLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(usernameField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(passwordLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(submitButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel6, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(portField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(portLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(passwordField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(keypairField, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(passwordLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(submitButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(passwordField)
                    .addComponent(serverName)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                .addComponent(ipLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                .addComponent(serverNameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                                .addComponent(portLabel)
                                .addGap(116, 116, 116))
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(usernameField)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(ipField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(usernameLabel)
                            .addComponent(passwordLabel)
                            .addComponent(passwordLabel1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(keypairField, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(submitButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(submitButton)))
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(serverNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(serverName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ipLabel)
                            .addComponent(portLabel))
                        .addGap(4, 4, 4)
                        .addComponent(ipField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(usernameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(submitButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(keypairField, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(submitButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        //serverTest = new JSch();
        commandString = "";
        
        /*UserInfo ui = new MyUserInfo(){
            @Override
            public void showMessage(String message){
              JOptionPane.showMessageDialog(null, message);
            }
            @Override
            public boolean promptYesNo(String message){
              Object[] options={ "yes", "no" };
              int foo=JOptionPane.showOptionDialog(null, 
                                                   message,
                                                   "Warning", 
                                                   JOptionPane.DEFAULT_OPTION, 
                                                   JOptionPane.WARNING_MESSAGE,
                                                   null, options, options[0]);
              return foo==0;
            }
            public String[] promptKeyboardInteractfaceive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo){
                return null;
            }

            @Override
            public String[] promptKeyboardInteractive(String string, String string1, String string2, String[] strings, boolean[] blns) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };*/
        
        int maxStringLength = 0;
        
        /*try{
            Session sshSession = serverTest.getSession(usernameField.getText(),ipField.getText(),Integer.parseInt(portField.getText()));
            char[] password = passwordField.getPassword();
            String passwordHolder = "";
            for(int i = 0; i < password.length; ++i){
                passwordHolder += password[i];
            }
            sshSession.setPassword(passwordHolder);
            sshSession.setUserInfo(ui);
            sshSession.connect();
            
            JFrame terminal = new JFrame("Shell for - "+ipField.getText());
            JPanel panel = new JPanel();
            panel.setPreferredSize(new Dimension(200, 200));
            JTextArea log = new JTextArea();
            JTextField cmd = new JTextField();
            cmd.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
            log.setBackground(java.awt.Color.BLACK);
            log.setForeground(java.awt.Color.WHITE);
            log.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));
            panel.setLayout(new BorderLayout());
            JScrollPane scrollPane = new JScrollPane(log);
            JComboBox cmdBox = new JComboBox();
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(cmd, BorderLayout.SOUTH);
            
            setupComboBox(cmdBox);
                
            terminal.setVisible(true);
            terminal.add(panel);
            terminal.setBounds(0,0,500,500);
            terminal.setLocationRelativeTo(null);
            
            Channel channel = sshSession.openChannel("shell");
            outputString = new String();
            
            InputStream in = new InputStream() {
                int pos = 0;
                @Override
                public int read() throws IOException {
                    while (null == cmd.getText() || cmd.getText().length() <= pos) {
                        pos = 0;
                    }
                    int ret = (int) cmd.getText().charAt(pos);
                    pos++;
                    return ret;
                }

                @Override
                public boolean markSupported() {
                    return false;
                }

                @Override
                public int read(byte[] b) throws IOException {
                    return read(b, 0, b.length); //To change body of generated methods, choose Tools | Templates.
                }
            };
            OutputStream out = new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                   outputString += (char)b;
                   log.setText(outputString);
                }
            };
            
            cmd.addKeyListener(new KeyListener(){
                @Override
                public void keyTyped(KeyEvent e) {
                    if('\n' == e.getKeyChar()){
                        try {
                            in.read(cmd.getText().getBytes());
                        } catch (IOException ex) {
                            java.util.logging.Logger.getLogger(CreateServerConnection.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                
                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }
            });
            
            channel.setInputStream(new CustomInputStream(cmd));
            channel.setOutputStream(new CustomOutputStream(log));
            channel.connect();
            
            //channel.disconnect();
        }catch(JSchException ex){
            System.out.println(ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(CreateServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        //this.parent.addServer(jTextField1.getText(),jTextField2.getText(),jTextField3.getText(),jPasswordField1.toString());
    }//GEN-LAST:event_submitButtonActionPerformed
    
    public void setupComboBox(JComboBox comboBox) throws FileNotFoundException, IOException{
        File cmdFile = new File(getClass().getResource("folder/server_commands").getFile());
        BufferedReader reader = new BufferedReader(new FileReader(cmdFile));
        
        String l = "";
    }
    
    public class CustomInputStream extends InputStream implements ActionListener {

        final JTextField field;
        final BlockingQueue<String> q;

        public CustomInputStream(JTextField field) {
            this.field = field;
            q = new LinkedBlockingQueue<>();
            field.addActionListener(this);
        }

        private String s;
        int pos;

        @Override
        public int read() throws IOException {
            while (null == s || s.length() <= pos) {
                try {
                    s = q.take();
                    pos = 0;
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
            int ret = (int) s.charAt(pos);
            pos++;
            return ret;
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int bytes_copied = 0;
            while (bytes_copied < 1) {
                while (null == s || s.length() <= pos) {
                    try {
                        s = q.take();
                        pos = 0;
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }
                }
                int bytes_to_copy = len < s.length()-pos ? len : s.length()-pos;
                System.arraycopy(s.getBytes(), pos, b, off, bytes_to_copy);
                pos += bytes_to_copy;
                bytes_copied += bytes_to_copy;
            }
            return bytes_copied;
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            q.add(field.getText() + "\r\n");
            field.setText("");
        }
    }

// This is the code for making textarea as output stream
    public class CustomOutputStream extends OutputStream {

        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            // redirects data to the text area
            textArea.append(String.valueOf((char) b));
            String textAreaString = textArea.getText();
            // scrolls the text area to the end of data
            textArea.setCaretPosition(textArea.getText().length());
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            String s = new String(b,off,len);
            textArea.append(s);
            textArea.setCaretPosition(textArea.getText().length());
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }
    }
    
    /*public static abstract class MyUserInfo
        implements UserInfo, UIKeyboardInteractive{
        public String getPassword(){ return null; }
        public boolean promptYesNo(String str){ return false; }
        public String getPassphrase(){ return null; }
        public boolean promptPassphrase(String message){ return false; }
        public boolean promptPassword(String message){ return false; }
        public void showMessage(String message){ }
        public String[] promptKeyboardInteractfaceive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo){
            return null;
        }
    }*/
    
    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        this.setVisible(false);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        this.setBounds(evt.getX()+this.getX()-mouseOrigin[0],evt.getY()+this.getY()-mouseOrigin[1],this.getWidth(),this.getHeight());
    }//GEN-LAST:event_formMouseDragged

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        mouseOrigin = new int[]{evt.getX(),evt.getY()};
    }//GEN-LAST:event_formMousePressed

    private void passwordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordFieldActionPerformed

    private void keypairFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keypairFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_keypairFieldActionPerformed

    private void submitButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButton1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        JFrame frame = new JFrame("Key File Search");
        frame.add(fileChooser);
        frame.setBounds(0,0,500,500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        fileChooser.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand() == "CancelSelection"){
                    frame.setVisible(false);
                } else {
                    keypairField.setText(fileChooser.getSelectedFile().toString());
                    frame.setVisible(false);
                }
            }
        });
    }//GEN-LAST:event_submitButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CreateServerConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CreateServerConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CreateServerConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CreateServerConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CreateServerConnection().setVisible(true);
            }
        });
    }
    
    public void setParent(VBox parent){
        this.parent = parent;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ipField;
    private javax.swing.JLabel ipLabel;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPasswordField keypairField;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel passwordLabel1;
    private javax.swing.JTextField portField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JTextField serverName;
    private javax.swing.JLabel serverNameLabel;
    private javax.swing.JButton submitButton;
    private javax.swing.JButton submitButton1;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}
