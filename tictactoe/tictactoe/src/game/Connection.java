package game;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection implements Runnable{
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    
    private boolean running;
    
    private Game game;
    
    public Connection(Game game, Socket socket){
        this.game = game;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        new Thread (this).start();
    }
    
    @Override
    public void run(){
        running = true;
        while(running){
            try {
                Object object = inputStream.readObject();
                game.packetReceived(object);
                
                
            } catch(EOFException | SocketException e) {
            	running = false;
            }
                catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        } 
    }
    
    public void sendPacket(Object object){
        try {
        	outputStream.reset();
            outputStream.writeObject(object);
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void close() throws IOException{
        running = false;
        inputStream.close();
        outputStream.close();
    }
}
