import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Cliente {

    public static void main(String[] args) {

        final File[] fileToSend = new File[1];

        // Se crea el Frame principal que contendrá los paneles
        JFrame jFrame = new JFrame("Envío de archivos");
        jFrame.setSize(500, 500);
        jFrame.setLocationRelativeTo(null);
        //Se hace un box layout que permitirá poner los paneles uno sobre otro
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        //Cuando se cierra la Ventana, se cierra el programa.
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JLabel jlTitle = new JLabel("Envío de Archivos");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        // Se añade un borde alrededor del JLabel como espaciado.
        jlTitle.setBorder(new EmptyBorder(20,0,10,0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Se crea un label para indicar que tiene que escoger un archivo para enviar
        JLabel jlFileName = new JLabel("Seleccione un archivo para enviar:");
        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
        jlFileName.setBorder(new EmptyBorder(50, 0, 0, 0));
        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Se crea un panel que contiene los botones
        JPanel jpButton = new JPanel();
        // Se crea un borde de espaciado para el panel que contiene los botones
        jpButton.setBorder(new EmptyBorder(75, 0, 10, 0));
        // Se crea el boton para enviar archivo
        JButton jbSendFile = new JButton("Enviar");
        jbSendFile.setPreferredSize(new Dimension(150, 75));
        jbSendFile.setFont(new Font("Arial", Font.BOLD, 20));
        jbSendFile.setFocusable(false);
        jbSendFile.setBackground (new Color (-394241));
        // Se crea el segundo boton para elegir archivo
        JButton jbChooseFile = new JButton("Seleccionar");
        jbChooseFile.setPreferredSize(new Dimension(150, 75));
        jbChooseFile.setFont(new Font("Arial", Font.BOLD, 20));
        jbChooseFile.setFocusable(false);
        jbChooseFile.setBackground (new Color (-394241));

        // Se añaden los botones al panel
        jpButton.add(jbSendFile);
        jpButton.add(jbChooseFile);

        // Se crea un ActionListener
        // Se crea un botón para seleccionar el archivo que queremos enviar
        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Se crea la un JFileChooser para abrir el explorador de archivos
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Seleccione el archivo");
                // Si se selecciona un archivo desde el JFileChooser se ejecuta la acción
                if (jFileChooser.showOpenDialog(null)  == JFileChooser.APPROVE_OPTION) {
                    // Se obtiene el archivo seleccionado
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jlFileName.setText("El archivo que seleccionó para enviar es: " + fileToSend[0].getName());
                }
            }
        });

        // Se crea otro ActionListener
        // Se envía el archivo que seleccionamos cuando se presiona el botón
        jbSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Si no se seleccionó ningún archivo se enviará este mensaje de advertencia
                if (fileToSend[0] == null) {
                    jlFileName.setText("Seleccione un archivo antes de enviar.");
                    jlFileName.setForeground(Color.RED);
                    // Si se seleccionó un archivo se ejecuta lo siguiente
                } else {
                    try {
                        // Se crea un inputStream en el achivo que queremos enviar.
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                        // Se crea una conexión mediante un socket con el servidor
                        Socket socket = new Socket("localhost", 1234);
                        // Se crea un OutputStream para escribir al servidor mediante el socket.
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        // Se obtiene el nombre del archivo que queremos enviar y lo guardamos en fileName
                        String fileName = fileToSend[0].getName();
                        // Convertimos el nombre del archivo en un array de bytes para que se puedan enviar al servidor
                        byte[] fileNameBytes = fileName.getBytes();
                        // Se crea un array de bytes del tamaño del archivo
                        byte[] fileBytes = new byte[(int)fileToSend[0].length()];
                        // Se pone el contenido del archivo en el array de bytes creado para que sea enviado.
                        fileInputStream.read(fileBytes);
                        // Se envía el largo del nombre del archivo así el servidor sabe cuando dejar de leerlo
                        dataOutputStream.writeInt(fileNameBytes.length);
                        // Se envía el nombre del archivo.
                        dataOutputStream.write(fileNameBytes);
                        // Se envía el largo del array de bytes así el servidor sabe cuando dejar de leerlo
                        dataOutputStream.writeInt(fileBytes.length);
                        // Se envía el archivo
                        dataOutputStream.write(fileBytes);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Se agregan los paneles, labels y botones al Frame y se hacen visibles.
        jFrame.add(jlTitle);
        jFrame.add(jlFileName);
        jFrame.add(jpButton);
        jFrame.setVisible(true);
    }

}