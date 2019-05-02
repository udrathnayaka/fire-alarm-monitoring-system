package code;

import java.net.*;
import java.io.*;
import java.net.*;
import java.net.*;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Handler;

public class FireAlarmServer extends UnicastRemoteObject implements FireAlarmInterface{
	
		public FireAlarmServer() throws RemoteException
			{
				super();
			}
	
	//Port of the server
	public static final int PORT= 7001;
			
	
	
	public static void main(String[] args) throws Exception {
        System.out.println("The FireAlarmServer is running.");
        ServerSocket myserver = new ServerSocket(PORT);
        
        if  (System.getSecurityManager() ==  null)
            System.setSecurityManager (new RMISecurityManager());
        try 
        {
        	LocateRegistry.createRegistry(7001); 
        	
            FireAlarmServer sever =new FireAlarmServer();
            Naming.bind("FireAlarmInterface", sever);
          
         while (true) 
                   {
                new Handler(myserver.accept()).start();
                   }
        }
          
        	catch(AlreadyBoundException a)
        		{
        			System.err.println(a.getMessage());
        		}
        catch(RemoteException r)
        		{
        			System.err.println(r.getMessage());
        		}        
         finally 
         		{
        	 		myserver.close();
         		}
    }
	
	private  static   ArrayList<FireAlarmMonitor>   monitors=   new ArrayList<FireAlarmMonitor>();
	
	public  static   HashMap<String,PrintWriter>   sensors =   new HashMap<String,PrintWriter>();
	
	public  static  HashMap<String,String>   statusOfSensors =   new HashMap<String,String>();
	
	
		public String getStatus(String Sensor)throws java.rmi.RemoteException
		{
		
		Iterator ite = statusOfSensors.entrySet().iterator();
		
			while(ite.hasNext())
			{
				Map.Entry<String,String> m=(Map.Entry)ite.next();
			
				if(m.getKey().equals(Sensor))
				{
					String status_Sen =m.getKey()+":"+m.getValue();
					return status_Sen;
				}
			}
			
			return "Status unavailable";
		
		}
	
		public HashSet<String> getSensors()throws java.rmi.RemoteException
		{
	    	HashSet<String> SID = new HashSet<String>();
	    	
	    	for (String key : sensors.keySet()) 
	    	{
	    	    System.out.println(key);
	    	    
	    	    SID.add(key);
	    	}
	    	
	    	return SID;
	    }
		
		public void removeFireAlarmMonitors(FireAlarmInterface listener)throws java.rmi.RemoteException 
		{
			monitors.remove(listener);
						
		}
		
		public void pushFireAlarmMonitors(FireAlarmInterface listener)throws java.rmi.RemoteException 
		{
			monitors.add((FireAlarmMonitor) listener);
						
		}
		
		public static void updMonitor()
		{
			try
			{
				for(FireAlarmMonitorInterface fami:monitors)
				{
					fami.updateSensors();
				}
			
			}
			catch(Exception e)
				{
					System.out.println(e);
				}
		}
		
		public static void pushMonitor()
		{
			try
			{
				for(FireAlarmMonitorInterface fami:monitors)
				{
					fami.pushMonitor();
				}
			
			}
			catch(Exception e)
				{
					System.out.println(e);
				}
		}
		
		private static void warnListen(String sid, String status) {
			try{
				for(FireAlarmMonitorInterface fami :monitors){
					fami.warnMonitors(sid,status);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
		private static class Handler extends Thread
		{
			
			public BufferedReader input;
			public String sID; 
			public PrintWriter output;
			public Socket socket_s;
	        
	        	        
	        public Handler(Socket socket) 
	        {
	            this.socket_s = socket_s;
	        }
	            
	        public void run() 
	        {
	        	try 
	        	{
	        		output = new PrintWriter(socket_s.getOutputStream(), true);
	        		input= new BufferedReader(new InputStreamReader(socket_s.getInputStream()));
	       
	        		        		
	                
	while(true)
	{
						sID = input.readLine();
	                	output.println("INSERTNEWSENSOR");
	                    
	                    if (sID == null)
	                    {
	                        return;
	                    }
	                    synchronized (sensors) 
	                    {
	                        if (!sensors.containsKey(sID)) 
	                        {
	                        	sensors.put(sID, output);                           
	                            break;
	                        }
	                        
	                    }
	}
	                
	                output.println("SENSORINSERTED");
	                sensors.put(sID,output);
	                
	                while(true)
	                {
	                	String insert = input.readLine();
	                	
	                    if (insert == null) 
	                    {
	                        return;
	                    }
	                    
	                    else if(insert.startsWith("STATUS"))
	                    {
	                    	
	                    	String stringout=insert.substring(12);
	                    	
	                    	System.out.println(stringout);
	                    	
	                    	                  
	                    		                    	
	                    	StringTokenizer st= new StringTokenizer(stringout , "|");
	                    	String stringstatus=st.nextToken();
	                    	StringTokenizer st1= new StringTokenizer( stringstatus , ":");
	                    	String ssid = st.nextToken();
	                    	
	                    	int smokelevel = Integer.parseInt(st1.nextToken());
	                    	
	                    	int carbonlevel = Integer.parseInt(st1.nextToken());
	                    	
	                    	int temperaturelevel = Integer.parseInt(st1.nextToken());
	                    	
	                    	int batterylevel = Integer.parseInt(st1.nextToken());
	                    	
	                    	
	                    	
	                    	String fireStatus = "CarbonDioxide Level = "+ carbonlevel +"\tTemperature Level : "+temperaturelevel+	"\tBattery Level :"+batterylevel+"\tSmoke Level : "+smokelevel+"\n";
	                    	
	                    	if( statusOfSensors.containsKey(fireStatus))
	                    	{
	                    		 statusOfSensors.remove(fireStatus);
	                    	}
	                    	else 
	                    	{
	                    		 statusOfSensors.put(ssid, fireStatus);
	                    	}
	                    	
	                    	if(temperaturelevel>50 || smokelevel>7) 
	                    	{
	                    		warnListen(ssid, fireStatus);
	                    	}
	                    			
	                    }
	                }
	                                   
	        	}
	        	catch(IOException e) 
	        	{
	        		System.out.println(e);
	        	}
	        	finally
	        	{
	        			        		
	        			if (sID != null)
	        			{
	        			sensors.remove(sID);
	        			
	        			updMonitor();   
	                    
	                    System.out.println(sID+ " is Removed.");
	                    
	                    System.out.println("No.of Sensors active: "+ sensors.size());
	                    
	                    if (output!= null)
	                    {
	                    	sensors.remove(output);
	                    }
	                    	                   
	                    try {
							socket_s.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    
	                    }
	        	}
	        }
		}            	
	                
	        	
}
	        
