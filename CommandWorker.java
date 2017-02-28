/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

/**
 *
 * @author monkeyboz
 */
public class CommandWorker extends SwingWorker<String,Void>{
    private JLabel jLabel2;
    private JLayeredPane jLayeredPane1;
    private String[] command;
    private Boolean showOutput;
    private String function;
    private final VBox parent;
    private Boolean showDirectlyAfter;
    private Boolean commandList;
    
    /**
     *
     * @param parent
     * @param jlabel2
     * @param jLayeredPane1
     * @param command
     * @param showOutput
     * @param function
     */
    public CommandWorker(VBox parent,JLabel jlabel2,JLayeredPane jLayeredPane1,String[] command,Boolean showOutput,String function){
        this.jLabel2 = jlabel2;
        this.jLayeredPane1 = jLayeredPane1;
        this.command = command;
        this.showOutput = showOutput;
        this.function = function;
        this.parent = parent;
        this.showDirectlyAfter = true;
    }
    
    public CommandWorker(VBox parent){
        this.parent = parent;
    }
    
    public CommandWorker(VBox parent,FileOutputStream writer){
        this.parent = parent;
    }
    
    public void setCommand(String[] command){
        this.command = command;
    }
    
    public CommandWorker(VBox parent,String[] command){
        this.command = command;
        this.parent = parent;
    }
    
    /**
     *
     * @param parent
     */
    public CommandWorker(VBox parent,Boolean list,String[] command){
       this.parent = parent;
       this.commandList = list;
       this.command = command;
       this.function = "runCommandsSync";
       this.showDirectlyAfter = false;
       this.showOutput = true;
    }
    
    public void showDirectlyAfter(Boolean showAfter){
        this.showDirectlyAfter = showAfter;
    }
    
    private void changeLoadingText(String function){
        switch(function){
            case "setupIPTable":
                jLabel2.setText("Setting Up IP Tables...");
                break;
            case "updateIPInformation":
                jLabel2.setText("Refreshing IP Information For Adapters...");
                break;
            case "createVM":
                jLabel2.setText("Creating Virtual Machine...");
                try{
                    parent.downloadFile("http://dlc-cdn.sun.com/virtualbox/5.0.0_BETA3/VBoxGuestAdditions_5.0.0_BETA3.iso","VBoxGuestAdditions 5.0.0 BETA3.iso");
                } catch(IOException ex){
                    System.out.println("testing");
                }
                break;
            case "addServerInfoToServerList":
                jLabel2.setText("Adding Server Info for List");
                break;
            case "startVM":
                jLabel2.setText("Starting VM...");
                break;
            default:
                jLabel2.setText(function);
                break;
        }
    }
    
    @Override
    public String doInBackground(){
        jLabel2.setVisible(true);
        jLayeredPane1.setVisible(false);
        String l = null;
        
        changeLoadingText(function);
        try{
            Process run = Runtime.getRuntime().exec(command);
            InputStreamReader v = new InputStreamReader(run.getInputStream());
            BufferedReader m = new BufferedReader(v);
                        
            ArrayList<String> commandOutput = new ArrayList<>();
            while((l = m.readLine()) != null){
                commandOutput.add(l);
            }
            parent.setCommandOutput(commandOutput);
            
            try{
                parent.getClass().getMethod(function).invoke(parent);
            } catch(NoSuchMethodException | SecurityException ex){
                changeLoadingText("Error With Function Call");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(CommandWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(showOutput){
                for(int i = 0; i < command.length; ++i){
                    System.out.print(command[i]+" ");
                }
                System.out.println();
                
                for(int i = 0; i < commandOutput.size(); ++i){
                    System.out.println(commandOutput.get(i));
                }
            }
        } catch(IOException ex){
            System.out.println(ex);
        }
        return l;
    }

    @Override
    public void done() {
        if(this.showDirectlyAfter){
            jLayeredPane1.setVisible(true);
            jLabel2.setVisible(false);
        }
    }
}
