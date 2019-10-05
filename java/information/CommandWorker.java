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
import java.util.Arrays;
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
    private Boolean isoInstall;
    private String commandText;
    private String[] functionParameters;
    
    /**
     *
     * @param parent
     * @param jlabel2
     * @param jLayeredPane1
     * @param command
     * @param showOutput
     * @param function
     * @param commandText
     */
    public CommandWorker(VBox parent,JLabel jlabel2,JLayeredPane jLayeredPane1,String[] command,Boolean showOutput,String function,String commandText){
        this.jLabel2 = jlabel2;
        this.jLayeredPane1 = jLayeredPane1;
        this.command = command;
        this.showOutput = showOutput;
        String[] functionArray = function.split(",");
        this.function = (functionArray.length > 1)?functionArray[0]:function;
        this.functionParameters = functionArray;
        this.parent = parent;
        this.showDirectlyAfter = true;
        this.commandText = commandText;
        jLabel2.setText(commandText);
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
     * @param list
     * @param command
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
                jLabel2.setText("Updating IP Tables ...");
                break;
            case "updateIPInformation":
                jLabel2.setText("Refreshing IP Information For Adapters ...");
                break;
            case "createVM":
                jLabel2.setText("Creating Virtual Machine ...");
                try{
                    if(!isoInstall){
                        parent.downloadFile("http://dlc-cdn.sun.com/virtualbox/5.0.0_BETA3/VBoxGuestAdditions_5.0.0_BETA3.iso","VBoxGuestAdditions 5.0.0 BETA3.iso");
                    }
                } catch(IOException ex){
                    System.out.println(ex);
                }
                break;
            case "addServerInfoToServerList":
                jLabel2.setText("Adding Server Information to Server List ...");
                break;
            case "startVM":
                jLabel2.setText("Starting VM ...");
                break;
            case "ipIPTable":
                jLabel2.setText("Creating new hostonly adapter ...");
                break;
            default:
                if("".equals(commandText)){
                    jLabel2.setText(function);
                } else {
                    jLabel2.setText(commandText);
                }
                break;
        }
    }
    
    @Override
    public String doInBackground(){
        String l = null;
        isoInstall = (parent.getISOFile()!=null);
        changeLoadingText(function);
        try{
            Process run = Runtime.getRuntime().exec(command);
            
            for(int i = 0; i < command.length; ++i){
                System.out.print(command[i]+" ");
            }
            System.out.println();
            
            InputStreamReader v = new InputStreamReader(run.getInputStream());
            BufferedReader m = new BufferedReader(v);
            
            ArrayList<String> commandOutput = new ArrayList<>();
            while((l = m.readLine()) != null){
                commandOutput.add(l);
                System.out.println(l);
            }
            parent.setCommandOutput(commandOutput);
            
            if(showOutput){                
                if("createvm".equals(command[1])){
                    String o = commandOutput.get(commandOutput.size()-1).replace("Settings file: '","").replace("\'","").trim();
                    parent.setVirtualboxdir(o.substring(0,o.lastIndexOf("\\")).trim());
                }
            }
        } catch(IOException ex){
            System.out.println(ex);
        }
        return l;
    }
    
    @Override
    public void done() {
        try{
            if(this.functionParameters.length == 1){
                parent.getClass().getMethod(function).invoke(parent);
                jLabel2.setText("Done ...");
            } else {
                Class[] argTypes = new Class[] { String[].class };
                System.out.println(function);
                System.out.println(functionParameters);
                parent.getClass().getDeclaredMethod(function, argTypes).invoke(parent,(Object)functionParameters);
                jLabel2.setText("Done ...");
            }
        } catch(NoSuchMethodException | SecurityException ex){
            changeLoadingText("Error With Function Call");
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            System.out.println(ex);
        }
    }
}
