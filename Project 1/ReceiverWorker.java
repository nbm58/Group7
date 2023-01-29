package utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ReceiverWorker extends Thread
{
 ObjectInputStream readFromNet;
 ObjectOutputStream writeToNet;
 Message message = null;
 public ReceiverWorker(Socket serverConnection)
 {
  try
  {
   readFromNet = new ObjectInputStream(serverConnection.getInputStream());
   writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
  }
  catch(IOException ex)
  {
   //Log Failure to open object streams.
  }
 }

 @Override
 public void run()
 {
  try
  {
   //read message
   message = (Message)readFromNet.readObject();
  }
  catch (IOException | ClassNotFoundException ex)
  {
   //Log Failure to read message
   System.exit(1);
  }
 
  //decide what to do depending on the type of message received.
  switch(message.getType())
  {
   	 
  }
 }
}
