import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Servidor {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            Socket cliente = serverSocket.accept();

            System.out.println("Se ha conectado un CLiente :)");
            DataInputStream dataInputStream = new DataInputStream(cliente.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(cliente.getOutputStream());
            String mensajeCliente = "";

            while (!mensajeCliente.equals("EXIT")){

                mensajeCliente = dataInputStream.readUTF();
                System.out.println(mensajeCliente);

                try {
                    File file = new File ("C:/Users/tomas/IdeaProjects/Servidor/Archivos/"+mensajeCliente+".txt");
                    // Crea un inputstream en el archivo que se quiere enviar
                    FileInputStream fileInputStream = new FileInputStream (file.getAbsolutePath ());
                    byte[] fileBytes = new byte[(int) file.length ()];
                    // Poner el contenido del archivo en el arreglo de bytes para enviar estos bytes al cliente
                    fileInputStream.read(fileBytes);
                    // Enviar el largo del archivo de tal forma que el cliente sepa cuando detener la lectura
                    dataOutputStream.writeInt (fileBytes.length);
                    // Enviar el archivo.
                    dataOutputStream.write (fileBytes);
                } catch (FileNotFoundException fileNotFoundException) {
                    System.out.println ("El archivo no se encuentra");
                    dataOutputStream.writeInt(0);
                } catch (IOException ioException) {
                    ioException.printStackTrace ();
                }
            }
            dataInputStream.close();
            dataOutputStream.close();
            cliente.close();



        } catch (IOException e) {

            e.printStackTrace();
        }
    }



}