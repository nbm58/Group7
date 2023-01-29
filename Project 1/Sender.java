package utils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Sender extends Thread implements MessageTypes{

	//sender processes user input, translates to messages
	//and sends to chat server
	Socket serverConnection = null;
	Scanner userInput = new Scanner(System.in);
	String inputLine = null;
	boolean hasJoined;
	
	
	/*
	 * Constructor
	*/
	public Sender()
	{
	 userInput = new Scanner(System.in);
	 hasJoined = false;
	}
	
	@Override
	public void run()
	{

	 ObjectOutputStream writeToNet;
	 ObjectInputStream readFromNet;
	 
	 /*while(true)
	 {
	  //get user input
	  inputLine = userInput.nextLine(); 
	  
	  // case statement
	  	//if JOIN
	  	//if LEAVE
	  	//etc
	  	 * 
	 }*/
	 
	}   
}
