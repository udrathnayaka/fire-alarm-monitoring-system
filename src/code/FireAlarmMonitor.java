package code;

import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class FireAlarmMonitor extends UnicastRemoteObject implements FireAlarmMonitorInterface {
	
	
	static FireAlarmInterface interf =null;
	
			
	JFrame newframe = new JFrame("FireAlarmMonitor");
	JList<String> sList = new JList<String>(list);
	
	
	JPanel pan1 = new JPanel();
	JPanel pan2 = new JPanel();
	
	JButton button1 = new JButton("View Fire Status");
	
	static DefaultListModel<String> list = new DefaultListModel<String>();	
	JPanel cp = new JPanel();
	JPanel newcp = new JPanel();
	static JTextArea text = new JTextArea(25,80);
	
	JButton button2 = new JButton("View All Fire Status");		
	
	public FireAlarmMonitor() throws Exception
	{
		
	    newframe.getContentPane().setLayout(new BorderLayout());
	    
	    newframe.getContentPane().add(new JScrollPane(sList), "West");
	    
	    sList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	    
	    newcp.add(button2,"Center");
	    
	    sList.setSize(35, 40);	   
	    
	    newframe.getContentPane().add(new JScrollPane(text), "East");
	    
	    newcp.setLayout(new BorderLayout());
	    
	    newframe.getContentPane().add(newcp, "North");
	    
	    newcp.add(button1, "North");    
	    
	    newframe.getContentPane().add(cp, "South");	    
	   
	    
	    newframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	    
	    
	    fai= (FireAlarmInterface) Naming.lookup("//localhost/FireAlarmInterface");
	    
	    newframe.pack();
	 
	    
	    button1.addActionListener(new ActionListener()
	    {
            public void actionPerformed(ActionEvent e) 
            {
            	if(sList.isSelectionEmpty()) 
            	{
            		System.out.println("No Sensor Selected!");
            	}
            	else {
            		try
            		{
						getSensorStat(sList.getSelectedValue());
					} catch (RemoteException ex) 
            		{
						ex.printStackTrace();
					}
            	}
            }
        });
	    
	    button2.addActionListener(new ActionListener()
	    {
            public void actionPerformed(ActionEvent e) 
            {
            	try 
            	{
            		getAllSensorStats();
				} catch (RemoteException e2)
            	{
					e2.printStackTrace();
				}
            	
            }
        });    
	   	
	}	
	
	//alert the monitors
	public void warnMonitors(String sid,String sensorstatus) throws java.rmi.RemoteException
	{
		text.setText(text.getText()+"ALERT RECEIVED !"+ "\n"+"Identified Sensor:"+sid+"\n"+sensorstatus+"\n");
		
	}
	
	public static void getAllSensorStats() throws java.rmi.RemoteException
	{
	//
	}
	
	FireAlarmInterface fai=null;
	
	//updating the sensors
	public void updateSensors()throws java.rmi.RemoteException
	{
		try 
		{
        	HashSet<String> ids = interf.getSensors();
        	list.clear();
        	
        	for(String id: ids)
        	{
        		list.addElement(id);
    		}
    	}
    	catch(RemoteException e) 
		{
            e.printStackTrace();
    	}
	}
	
	//taking sensor status
	public static void getSensorStat(String Sensors) throws java.rmi.RemoteException
	{
		try {
        	String sstats = interf.getStatus(Sensors);
        	
			text.setText(text.getText()+"\n"+sstats+"\n");
    	}
    	catch(RemoteException e) 
		{
            e.printStackTrace();
    	}
	}	
	
	static FireAlarmMonitor alarmmon;
	
	public static void main(String[] args) 
	{	
		try {

			alarmmon = new FireAlarmMonitor();
			alarmmon.newframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			alarmmon.newframe.setVisible(true);
			interf.insertMonitorListener(alarmmon);
			((FireAlarmMonitor) interf).run();
        	try {
	        	HashSet<String> set = interf.getSensors();
	        	list.clear();
	        	for(String ids: set) {
	        		list.addElement(ids);
	    		}
	    	}
	    	catch(RemoteException re) {
	            re.printStackTrace();
	    	}
        	getAllSensorStats();
		}
		catch (MalformedURLException m)
		{
			m.printStackTrace();
			
		} 
		catch (RemoteException ex)
		{
			ex.printStackTrace();
		} 
		catch (NotBoundException b)
		{
			b.printStackTrace();
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	//Implementing Remote Interface
	public void run() throws RemoteException
	{
		if (System.getSecurityManager() ==  null)
            System.setSecurityManager (new RMISecurityManager());
		
		try
		{
            interf = (FireAlarmInterface)Naming.lookup("//localhost/FireAlarmInterface");
        } 
		catch (NotBoundException e) 
		{
            System.err.println(e.getMessage());
        }
		catch (MalformedURLException e) 
		{
            System.err.println(e.getMessage());
        } 
	}	
}

