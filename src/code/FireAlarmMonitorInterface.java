package code;

import java.net.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashSet;


public interface FireAlarmMonitorInterface  extends java.rmi.Remote
{
	//Fire Alarm Monitor services provided
	public void updateSensors()throws java.rmi.RemoteException;
	public void warnMonitors(String sid,String sensorstatus)throws java.rmi.RemoteException;
	
	
}
