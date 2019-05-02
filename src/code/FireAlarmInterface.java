package code;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.net.*;

public interface FireAlarmInterface extends Remote 
{
	public String getStatus(String Sensors)throws  java.rmi.RemoteException;
	public HashSet<String> getSensors()throws java.rmi.RemoteException;
	public void removeFireAlarmMonitors(FireAlarmInterface listener)throws java.rmi.RemoteException ;
	public void pushFireAlarmMonitors(FireAlarmInterface listener)throws java.rmi.RemoteException ;
	public void insertMonitorListener(FireAlarmMonitorInterface listener)throws java.rmi.RemoteException;

}
