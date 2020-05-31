/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServidorWeb;

import java.io.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 *
 * @author Ralph
 */
public class Manejador extends Thread{
    protected Socket socket;
    protected PrintWriter pw;
    protected BufferedOutputStream bos;
    protected BufferedReader br;
    protected String FileName;
    
    public Manejador(Socket _socket){
        this.socket = _socket;
    }
    
    public void run(){
        try{
            //Se instancian las variables
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bos = new BufferedOutputStream(socket.getOutputStream());
            pw = new PrintWriter(new OutputStreamWriter(bos));
            String line = br.readLine();
            //En caso de que la solicitus tenga todo vacio
            if(line == null){
                pw.print("<html><head><title>Servidor WEB");
                pw.print("</title><body bgcolor=\"#AACCFF\"<br>Linea Vacia</br>");
                pw.print("</body></html>");
                socket.close();
                return;
            }
            System.out.println("\nCliente Conectado desde: "+socket.getInetAddress());
            System.out.println("Por el puerto: "+socket.getPort());
            System.out.println("Datos: "+line+"\r\n\r\n");
            // Si "?" nunca aparece
            if (line.indexOf("?") == -1){
                getArch(line);
                if(FileName.compareTo("") == 0){
                    SendA("index.htm");
                }else{
                    SendA(FileName);
                }
                System.out.println(FileName);
            }else if (line.toUpperCase().startsWith("GET")){
                StringTokenizer tokens = new StringTokenizer(line,"?");
                String req_a = tokens.nextToken();
                String req = tokens.nextToken();
                System.out.println("Token1: "+req_a+"\r\n\r\n");
                System.out.println("Token2: "+req+"\r\n\r\n");
                pw.println("HTTP/1.0 200 Okay");
                pw.flush();
                pw.println();
                pw.flush();
                pw.print("<html><head><title>SERVIDOR WEB");
                pw.flush();
                pw.print("</title></head><body bgcolor=\"#AACCFF\"><center><h1><br>Parametros Obtenidos..</br></h1>");
                pw.flush();
                pw.print("<h3><b>"+req+"</b></h3>");
                pw.flush();
                pw.print("</center></body></html>");
                pw.flush();
            }else{
                pw.println("HTTP/1.0 501 Not Implemented");
                pw.println();
            }
            pw.flush();
            bos.flush();
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        try{
            socket.close();;
        }catch(Exception e){
            e.printStackTrace();
        }
    
    }

    private void getArch(String line) {
        int i;
        int f;
        if(line.toUpperCase().startsWith("GET")){
            i=line.indexOf("/");
            f=line.indexOf(" ",i);
            FileName = line.substring(i+1,f);
        
        }
    }

    public void SendA(String fileName,Socket sc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void SendA(String arg){
    
    
    }
    
    
}
