package code;

import java.net.*;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.io.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;

public class FireAlarmSensor {
	
	double smokelevel;
	double temperature;
	double battery;
	double co2level;	
	
	static Timer time;
	PrintWriter out;
	BufferedReader in;
	String SID;
	
	DefaultListModel<String> listmodel = new DefaultListModel<String>();
	JFrame frame1 = new JFrame("FireAlarmSensor");
	
	
	public FireAlarmSensor(){
		//ui of the fire alarm sensor
		frame1.setLayout(new BorderLayout());
		
	}
	
	private String getSensor() 
	{	
		//to take the floor number and the sensor ID
		
		return 	JOptionPane.showInputDialog(frame1,"floor ID:",JOptionPane.PLAIN_MESSAGE)+"-"+
				JOptionPane.showInputDialog(frame1,"sensor ID:",JOptionPane.PLAIN_MESSAGE);
	}
	
	//to return the Server Address
	private String getServerAdd()
	{
		   
		 return JOptionPane.showInputDialog(frame1,"Please enter IP address of the server",JOptionPane.PLAIN_MESSAGE);
	}
    
	//for periodic updates
	public void runTimer() 
	{
		String status1 = fireSensorStatus();
		listmodel.addElement("Temp : "+temperature);
		listmodel.addElement("Battery : "+battery);
		listmodel.addElement("Smoke : "+smokelevel);
		listmodel.addElement("CO2 : "+co2level);
        System.out.println("STATUS"+SID+"|"+status1);
        
        time = new Timer(60000, new ActionListener()
        {
						
			public void actionPerformed(ActionEvent e)
			{
				
				listmodel.clear();
                String firestatus = fireSensorStatus();
                listmodel.addElement("Temperature = "+temperature);
                listmodel.addElement("Battery Level : "+battery);
                listmodel.addElement("Smoke Level : "+smokelevel);
                listmodel.addElement("CO2 Level: "+co2level);
                System.out.println("STATUS"+SID+"/"+status1);
			}
		});
        time.start();
    }
	
	//to run the server app
		public static void main(String[] args) throws IOException {
	        
			FireAlarmSensor sensorClient = new FireAlarmSensor();
	        sensorClient.frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        sensorClient.frame1.setVisible(true);
	        sensorClient.run();
	    }
		
		//Randomly change the readings 
		public String fireSensorStatus()
		{
			Random random = new Random();
			smokelevel = random.nextInt(11);
			
			temperature = random.nextInt(80);
			
			co2level = random.nextInt(300) ;
			
			battery = random.nextInt(100);
			
			
			String firestatus = smokelevel+"/"+temperature+"/"+co2level+"/"+battery;
			return firestatus;
		}			
	
	//connects to the Server
		private void run() throws IOException{
			
			//Create connection 
	        String serverAddress = getServerAdd();
	        
	        Socket socket = new Socket(serverAddress, 7001);
	        
	        in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        
	        out =new PrintWriter(socket.getOutputStream(), true);

	        while (true)
	        {
	            String msg = in.readLine();
	            
	            if(msg.startsWith("INSERTNEWSENSOR"))
	            {
	            	String newsensor = getSensor();
	            	System.out.println(newsensor);
	            }
	            else if(msg.startsWith("SENSORINSERTED"))
	            {
	            	SID = msg.substring(12);
	            	frame1.setTitle("Sensor (ID : "+SID+")");
	            	runTimer();
	            }
	            else if(msg.startsWith("STATUS"))
	            {
	            	System.out.println("STATUS"+SID+":"+fireSensorStatus());
	            }
	            
	        }
	    }
}


	
	
	
	
	
	
	
	
	









