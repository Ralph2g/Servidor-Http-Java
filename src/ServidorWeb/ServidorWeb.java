package ServidorWeb;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
class Handler implements Runnable {
   private final Socket socket;
   Handler(Socket socket) { this.socket = socket; }
   public void run() {
     // read and service request on socket
   }
 }
public class ServidorWeb implements Runnable 
{
    private final ExecutorService pool;
    public static final int PUERTO=8000;
    ServerSocket ss;
    public ServidorWeb() throws Exception{
        pool = Executors.newFixedThreadPool(100);
        System.out.println("Iniciando Servidor.......");
        this.ss=new ServerSocket(PUERTO);
    }
    public void run() {
        System.out.println("Servidor iniciado:---OK");
        System.out.println("Esperando por Cliente....");
        try {
            for(;;){
                Socket accept=ss.accept();
                pool.execute(new Handler(accept));
                new Manejador(accept).start();
            }
        }catch (IOException ex) {
            pool.shutdown();
        }
        
    }
    public static void main(String[] args) throws Exception{
        ServidorWeb sWEB=new ServidorWeb();
        sWEB.run();
    }
}