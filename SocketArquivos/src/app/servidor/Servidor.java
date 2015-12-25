package app.servidor;

import app.bean.FileMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author rodrigo
 */
public class Servidor {
    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> streamMap = new HashMap<String, ObjectOutputStream>();

    public Servidor() {
        try {
            serverSocket = new ServerSocket(3000);
            
            JOptionPane.showMessageDialog(null, "Servidor iniciado! \nAguardando conexões...");
            
            while(true){
                socket = serverSocket.accept();
                new Thread(new ListenerSocket(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private class ListenerSocket implements Runnable{
       
       private ObjectOutputStream envio; //Envio das mensagens
       private ObjectInputStream recebe; //Recebe a mensagem
       
       public ListenerSocket(Socket socket) throws IOException {
           this.envio = new ObjectOutputStream(socket.getOutputStream());
           this.recebe = new ObjectInputStream(socket.getInputStream());
        }

        public void run() {
            FileMessage mensagem = null;
           try {
               while((mensagem = (FileMessage) recebe.readObject()) != null){
                   streamMap.put(mensagem.getCliente(), envio);
                   if(mensagem.getFile() != null){
                       for(Map.Entry<String, ObjectOutputStream> kv : streamMap.entrySet()){
                           if(!mensagem.getCliente().equals(kv.getKey())){
                               kv.getValue().writeObject(mensagem);
                           }
                       }
                   }
               }
           } catch (IOException e) {
               streamMap.remove(mensagem.getCliente());
               JOptionPane.showMessageDialog(null, mensagem.getCliente()+" Desconectou");
           } catch (ClassNotFoundException e) {
               e.printStackTrace();
           }
        }
    }
    
    public static void main(String [] args){
        new Servidor();
    }
}
   