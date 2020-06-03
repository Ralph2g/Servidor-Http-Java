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
import java.util.Date;
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
            // Si "?" nunca aparece Muestra la página de inicio
            if (line.indexOf("?") == -1 && line.toUpperCase().startsWith("GET")){
                getArch(line);
                if(FileName.compareTo("") == 0){//Si la solicitud no tiene nada
                    SendA("index.htm");
                }else{//Obtiene el nombre del archivo a  enviar
                    SendA(FileName);
                }
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
            }else if (line.toUpperCase().startsWith("POST")){
                System.out.println("Peticion POST");
                //Extraemos Toda la cabecera
                int bodyLength = 0;
                do{
                    line = br.readLine();
                    System.out.println(line);
                    if(line.startsWith("Content-Length: "))
                        bodyLength = Integer.parseInt(line.substring(16));
                }while(line.length() != 0);
                //Obtenemos los parametros
                line = "";
                if(bodyLength > 0){
                        char body[] = new char[bodyLength];
                        br.read(body, 0, bodyLength);
                        line = new String(body);
                }
                System.out.println(line);
                pw.print("HTTP/1.0 200 Okay\n");
                pw.flush();
                pw.print("Server: Rafael/1.0 \n");
                pw.flush();
                pw.print("Date: " + new Date()+" \n");
                pw.flush();
                pw.print("Content-Type: text/html \n");
                pw.flush();
                pw.println();
                pw.flush();
                pw.print("<html><head><title>SERVIDOR WEB");
                pw.flush();
                pw.print("</title></head><body bgcolor=\"#AACCFF\"><center><h1><br>Parametros Obtenidos..</br></h1>");
                pw.flush();
                pw.print("<h3><b>Datos Procesados con su solicitud post</b></h3>");
                pw.flush();
                pw.print("</center></body></html>");
                pw.flush();
            }else if (line.toUpperCase().startsWith("HEAD")){
                System.out.println("Peticion HEAD");
                 //Extraemos Toda la cabecera
                int bodyLength = 0;
                    line = br.readLine();
                    System.out.println(line);
                    if(line.startsWith("Content-Length: "))
                        bodyLength = Integer.parseInt(line.substring(16));
                }while(line.length() != 0);
                                System.out.println(line);
                pw.print("HTTP/1.0 200 Okay\n");
                pw.flush();
                pw.print("Server: Rafael/1.0 \n");
                pw.flush();
                pw.print("Date: " + new Date()+" \n");
                pw.flush();
                pw.print("Content-Type: text/html \n");
                pw.flush();
                pw.println();
                pw.flush();
            }else{
                String sb = "";
                sb = sb = sb + "HTTP/1.0 501 Not Implemented\n";
                sb = sb + "Server: Rafael Server\1.0 \n";
                sb = sb + "Date: " + new Date() + "\n";
                sb = sb + "Content-Length: 0 \n";
                sb = sb + "\n";
                pw.println(sb);
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
    //Obtiene el nombre de los archivos solitidos
    private void getArch(String line) {
        int i;
        int f;
        //Obtiene los archivos de una solicitud GET
        if(line.toUpperCase().startsWith("GET")){
            i=line.indexOf("/");
            f=line.indexOf(" ",i);
            FileName = line.substring(i+1,f);
            /* ERROR 404: File not found 
            Este se despliega en caso de que no se encuentre el archivo solicitado
            */
            if (!(new File(FileName).exists()) && !(FileName.compareTo("") == 0) ){
                String body = "<!DOCTYPE html>";
                body += "<head><title>Error 404</title></head>";
                body += "<body>";
                body += "<h1>Error 404</h1>";
                body += "<h4>"+FileName+" No encontrado</h4>";
                body += "</body>";
                
                String sb = "";
                sb = sb + "HTTP/1.0 404  Not found\n";
                sb = sb + "Server: Rafael Server/1.0 \n";
                sb = sb + "Date: " + new Date()+" \n";
                sb = sb + "Content-Length: " + body.length()+" \n";
                sb = sb + "Content-Type: text/html \n";
                sb = sb + "\n";
                pw.println(sb + body);
            }
        }else{
                i=line.indexOf("/");
                f=line.indexOf(" ",i);
                FileName = line.substring(i+1,f);
                /* ERROR 404: File not found 
                Este se despliega en caso de que no se encuentre el archivo solicitado
                */
                if (!(new File(FileName).exists())){
                    String sb = "";
                    sb = sb + "HTTP/1.0 404  Not found\n";
                    sb = sb + "Server: Rafael Server/1.0 \n";
                    sb = sb + "Date: " + new Date()+" \n";
                    sb = sb + "Content-Length: 0 \n";
                    sb = sb + "Content-Type: text/html \n";
                    sb = sb + "\n";
                    pw.println(sb);
            }
                pw.flush();
        }
    }
    public void SendA(String fileName,Socket sc) {
        int fSize = 0;
        byte[] buffer = new byte[4096];
        try{
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            FileInputStream f = new FileInputStream(fileName);
            int x = 0;
            while((x = f.read(buffer))>0){
                //System.out.println(x);
                out.write(buffer,0,x);
            }
            out.flush();
            f.close();        
        
        }catch(FileNotFoundException e){
            //msg.printErr("Transaction::sendResponse():1", "El archivo no existe: " + fileName);
        }catch(IOException e){
            //System.out.println(e.getMessage());
            //msg.printErr("Transaction::sendResponse():2", "Error en la lectura del archivo: " + fileName);}
        }
    }
    //Envía un archivo como salida del servidor
    public void SendA(String arg){
        try{
            int b_leidos = 0;
            BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(arg));
            byte[] buf = new byte[1024];
            int tam_bloque = 0;
            if(bis2.available()>=1024){
                tam_bloque = 1024;
            }else{
                bis2.available();
            }
            int tam_archivo = bis2.available();
            //Escribe el encabezado de la petición http
                /**************************************************/
                String sb = "";
                sb = sb+"HTTP/1.0 200 ok\n";
                sb = sb+"Server: Rafael Server/1.0 \n";
                sb = sb+"Date:"+ new Date()+"\n";
                sb = sb+"Content-Type: text/html \n";
                sb = sb+"Content-Lenght"+tam_archivo+" \n";
                sb = sb + "\n";
                bos.write(sb.getBytes());
                bos.flush();
                //out.println("HTTP/1.0 200 ok");
                //out.println("Server: Axel Server/1.0");
                //out.println("Date: " + new Date());
                //out.println("Content-Type: text/html");
                //out.println("Content-Length: " + mifichero.length());
                //out.println("\n");
                /******************************************/
            while((b_leidos = bis2.read(buf, 0, buf.length))!=-1){
                bos.write(buf, 0, b_leidos);
            }
            bos.flush();
            bis2.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
				
    }
    
    
}

//Manejo de errores al obtener 
