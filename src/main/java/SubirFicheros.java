import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class SubirFicheros {
    //INICIAMOS SESION CON EL CLIENTE
    public static FTPClient cliente = new FTPClient();
    public static boolean login;
    public static int decision1;
    public static int decision2;


    public static void main(String[] arg) throws Exception{
        iniciarSesion();
        if(login){
            System.out.println("¿Qué quieres hacer?");
            System.out.println("1.- Subir fichero");
            System.out.println("2.- Descargar fichero");
            Scanner sc = new Scanner(System.in);
            decision1= Integer.parseInt(sc.nextLine());

            switch(decision1){
                case 1:
                    System.out.println("Pulsa 1 para subir fichero pasandole el nombre");
                    System.out.println("Pulsa 2 para subir fichero con file chooser");
                    System.out.println("Pulsa 3 para subir toda la carpeta pasando ruta");
                    System.out.println("Pulsa 4 para subir fichero con un nombre diferente");
                    sc = new Scanner(System.in);
                    decision2=Integer.parseInt(sc.nextLine());
                    switch (decision2){
                        case 1:
                            System.out.println("¿Cuántos archivos quieres subir?");
                            int numveces = Integer.parseInt(sc.nextLine());
                            int contador = 0;
                            while (contador < numveces) {
                                System.out.println("Dime el nombre del archivo que se suba");
                                String nombre = sc.nextLine();
                                SubirFicheroNombre(nombre);
                                contador++;

                            }
                            break;
                        case 2:
                            SubirFicheroFileChooser();
                            break;
                    }

            }
        }

    }

    public static void iniciarSesion() throws IOException {
        //La clase FTPClient esta integrada en java

        String servidor = "localhost";//Tambien puede ser localhost
        String user = "admin";
        String pasw = "1";


        System.out.println("Conectandose a " + servidor);

        //CONECTAMOS CON EL SERVIDOR
        cliente.connect(servidor);
        login = cliente.login(user, pasw);

        //Fijo conexión binaria y pasiva. Declaro direc que va a ser el directorio del FTP al que subo el archivo
        cliente.setFileType(FTP.BINARY_FILE_TYPE);
        //Comprobamos si se ha hecho login o no
        if(login){
            System.out.println("Login correcto");
            cliente.enterLocalPassiveMode(); //La conexion es en modo pasivo, que es más segura
        }else{
            System.out.println("Login incorrecto");
        }
    }

    public static void crearDirectorio(String direc) throws IOException{//Para crear el directorio en la carpeta del FileZilla
        //Si no puede cambiarse al directorio nuevodirec
        if (!cliente.changeWorkingDirectory(direc)) {
            String directorio = "NUEVODIREC";
            //Aquí lo crea
            if (cliente.makeDirectory(directorio)) {
                System.out.println("Directorio :  " +
                        directorio + " creado ...");
                cliente.changeWorkingDirectory(directorio);//Aquí como ya está creado se cambia
            } else {
                System.out.println("No se ha podido crear el Directorio");
                System.exit(0);
            }

        }
    }
    public static void SubirFicheroNombre(String nombreArchivo) throws IOException{
        String direc = "/NUEVODIREC"; //Se indica directorio destino

        crearDirectorio(direc);

        System.out.println("Directorio actual: " +
                cliente.printWorkingDirectory());

        String archivo ="C:\\Users\\llago\\Desktop\\Prueba_PSP\\"+nombreArchivo;
        BufferedInputStream in = new BufferedInputStream
                (new FileInputStream(archivo));

        if (cliente.storeFile(nombreArchivo, in))
            System.out.println("Subido correctamente... ");
        else
            System.out.println("No se ha podido subir el fichero... ");

        in.close(); // Cerrar flujo
        cliente.logout();
        cliente.disconnect();
    }

    public static void SubirFicheroFileChooser() throws IOException{
        String direc = "/NUEVODIREC";

        crearDirectorio(direc);

        //Muestro el directorio actual del FTP
        System.out.println("Directorio actual: " +  cliente.printWorkingDirectory());

        //Creo y abro un file chooser para leer el archivo de mi ordenador que quiero subir al FTP
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(fileChooser);
        String rutaynombre="";

        //Guardo la ruta completa + el nombre del archivo que ha seleccionado el usuario
        rutaynombre = fileChooser.getSelectedFile().getAbsolutePath();

        //Guardo el nombre sin ruta del archivo que ha seleccionado el usuario
        String nombresinruta=fileChooser.getSelectedFile().getName();//Esta linea es para coger solo el nombre

        //En la rutaynombre cambio las contrabarras por dobles contrabarras
        rutaynombre=rutaynombre.replace("\\", "\\\\");
        System.out.println(rutaynombre);

        //Guardo el contenido del archivo rutaynombre en un buffer
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(rutaynombre));
        
        //Estas dos lineas comentadas son lo mismo que la liena de arriba para coger solo el nombre

        /*String[] arrayarchivo=archivo.split("\\\\");
        String archivoDestino=arrayarchivo[arrayarchivo.length-1];*/

        //Almaceno el contenido del buffer en el servidor FTP
        if (cliente.storeFile(nombresinruta, bis))
            System.out.println("Subido correctamente... ");
        else
            System.out.println("No se ha podido subir el fichero... ");
        bis.close(); // Cerrar flujo
        cliente.logout();
        cliente.disconnect();

    }
}