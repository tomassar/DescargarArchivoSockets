import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class VentanaCliente extends JFrame {
    private JPanel panel1;
    private JTextField descargarTexto;
    private JButton descargarBoton;
    private JLabel avisoDescarga;
    private JRadioButton mayúsculaRadioButton;
    private JRadioButton minúsculaRadioButton;
    private ButtonGroup bg;
    private Socket socketCliente;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public VentanaCliente(String title) {
        super (title);
        this.setSize (800, 600);
        panel1.setSize (800, 600);
        bg = new ButtonGroup();
        bg.add(mayúsculaRadioButton);
        bg.add(minúsculaRadioButton);
        this.setLocationRelativeTo (null);
        this.setDefaultCloseOperation (EXIT_ON_CLOSE);
        this.setContentPane (panel1);
        this.pack ();
        this.setVisible (true);


        try {
            this.socketCliente  = new Socket ("localhost", 1234);
            dataOutputStream = new DataOutputStream (socketCliente.getOutputStream ());
            dataInputStream = new DataInputStream (socketCliente.getInputStream ());
            System.out.println ("Conexión establecida :)");
        } catch (IOException e) {
            e.printStackTrace ();
        }

        this.addWindowListener (new WindowAdapter () {
            public void windowClosing(WindowEvent e) {
                try {
                    dataOutputStream.writeUTF ("EXIT");
                    socketCliente.close ();
                } catch (IOException ioException) {
                    ioException.printStackTrace ();
                }
            }

        });

        descargarBoton.addActionListener (new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    String estilo;
                    try {
                        estilo = bg.getSelection ().getActionCommand ();
                    }catch(NullPointerException e){
                        estilo = "";
                    }
                    String mensaje = descargarTexto.getText ();

                    dataOutputStream.writeUTF (mensaje);
                    socketCliente.setKeepAlive (true);
                    // Lee el tamaño del archivo para saber cuando detener la lectura
                    int fileLength = dataInputStream.readInt ();

                    if (fileLength > 1) {
                        // Arreglo que contiene los bytes del archivo
                        byte[] fileContentBytes = new byte[fileLength];
                        // Lee desde el Inputstream en el arreglo fileContentBytes
                        dataInputStream.readFully (fileContentBytes, 0, fileContentBytes.length);
                        // Crea el archivo desde el arreglo de bytesd
                        String file = new String (fileContentBytes);

                        switch (estilo){
                            case "Mayuscula":
                                file = file.toUpperCase ();
                                System.out.println (file);
                                break;
                            case "Minuscula":
                                file = file.toLowerCase ();
                                break;
                            default:
                                System.err.println ("No existe tal estilo");
                        }

                        try {
                            File texto = new File ("C:/Users/tomas/IdeaProjects/Cliente/Descargas/" + mensaje + estilo+".txt");
                            if (texto.createNewFile ()) {
                                System.out.println ("Archivo creado: " + texto.getName ());
                                try {
                                    FileWriter myWriter = new FileWriter (texto.getAbsolutePath ());
                                    myWriter.write (file);
                                    myWriter.close ();
                                    System.out.println ("Escrito satisfactoriamente al archivo.");
                                    avisoDescarga.setForeground (Color.blue);
                                    avisoDescarga.setText ("Archivo satisfactoriamente descargado.");
                                } catch (IOException e) {
                                    System.out.println ("Ocurrió un error.");
                                    e.printStackTrace ();
                                }
                            } else {
                                avisoDescarga.setForeground (Color.red);
                                avisoDescarga.setText ("El archivo ya existe.");
                                System.out.println ("El archivo ya existe.");
                            }
                        } catch (IOException e) {
                            System.out.println ("Ocurrió un error.");
                            e.printStackTrace ();
                        }


                        System.out.println (file);
                    }
                    else{
                        avisoDescarga.setForeground (Color.red);
                        avisoDescarga.setText ("El archivo no existe en nuestra base de datos.");
                    }
                } catch (ConnectException e) {
                    System.err.println ("Error: Conexion rechazada");
                } catch (IOException e) {
                    e.printStackTrace ();
                }

                setTimeout (() -> {
                    avisoDescarga.setText ("");
                }, 5000);
            }
        });
    }

    public static void main(String[] args) {
        Scanner teclado = new Scanner (System.in);
        VentanaCliente ventana = new VentanaCliente ("Descargar Archivo");
    }

    //Función que ayuda a ejecutar una función lambda con retraso.
    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }
}
