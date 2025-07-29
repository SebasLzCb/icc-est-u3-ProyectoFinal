package dao.daoImpl;

import dao.AlgorithmResultDAO;
import models.AlgorithmResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementación del DAO (Data Access Object) para manejar la persistencia de los
 * resultados de los algoritmos en un archivo CSV.
 * Esta clase se encarga de todas las operaciones de lectura y escritura en el archivo.
 */
public class AlgorithmResultDAOFile implements AlgorithmResultDAO {
    // La ruta del archivo CSV donde se guardarán los resultados.
    private final String filePath = "results.csv";
    // La cabecera estándar para el archivo CSV. Ahora incluye PathLength y Found
    private static final String HEADER = "Algorithm,ExecutionTime(ns),PathLength,Found";

    // Objeto para sincronizar el acceso al archivo
    private final Object fileLock = new Object();

    /**
     * Constructor de la clase.
     * Verifica si el archivo "results.csv" existe. Si no existe, lo crea y le
     * añade la cabecera (HEADER).
     */
    public AlgorithmResultDAOFile() {
        System.out.println("DEBUG: Constructor de AlgorithmResultDAOFile llamado.");
        synchronized (fileLock) { // Sincronizar el bloque para asegurar que solo una instancia acceda al archivo al inicio
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("DEBUG: results.csv no existe. Intentando crearlo.");
                try (PrintWriter writer = new PrintWriter(new FileWriter(file, false))) { // Usar 'file' en lugar de 'filePath'
                    writer.println(HEADER);
                    writer.flush(); // Asegurarse de que el contenido se escriba inmediatamente
                    System.out.println("DEBUG: results.csv creado y cabecera escrita.");
                } catch (IOException e) {
                    System.err.println("ERROR: Fallo al crear results.csv: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("DEBUG: results.csv ya existe.");
            }
        }
    }

    /**
     * Guarda el resultado de la ejecución de un algoritmo en el archivo CSV.
     * Añade una nueva línea al final del archivo sin sobreescribir los datos existentes.
     * @param result El objeto AlgorithmResult que contiene los datos a guardar.
     */
    @Override
    public void saveResult(AlgorithmResult result) {
        synchronized (fileLock) { // Sincronizar el acceso para escribir
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
                String line = String.format("%s,%d,%d,%b",
                        result.getAlgorithmName(),
                        result.getExecutionTime(),
                        result.getPathLength(),
                        !result.getPath().isEmpty());
                writer.println(line);
                writer.flush(); // Asegurarse de que el contenido se escriba inmediatamente
                System.out.println("DEBUG: Resultado guardado: " + line);
            } catch (IOException e) {
                System.err.println("ERROR: Fallo al guardar resultado en results.csv: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Lee todos los resultados guardados desde el archivo CSV.
     * @return Una lista de objetos AlgorithmResult con los datos leídos.
     */
    @Override
    public List<AlgorithmResult> getAllResults() {
        List<AlgorithmResult> results = new ArrayList<>();
        synchronized (fileLock) { // Sincronizar el acceso para leer
            // Verificar si el archivo existe antes de intentar leerlo
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("DEBUG: results.csv no encontrado al intentar leer. Retornando lista vacía.");
                return Collections.emptyList();
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line = reader.readLine(); // Saltar la línea de la cabecera.
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 3) {
                        String name = data[0];
                        long time = Long.parseLong(data[1]);
                        int length = Integer.parseInt(data[2]);
                        results.add(new AlgorithmResult(name, time, length));
                    }
                }
                System.out.println("DEBUG: " + results.size() + " resultados leídos de results.csv.");
            } catch (IOException | NumberFormatException e) {
                System.err.println("ERROR: Fallo al leer results.csv: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return results;
    }

    /**
     * Borra todos los resultados del archivo CSV, pero mantiene la cabecera.
     */
    @Override
    public void clearResults() {
        synchronized (fileLock) { // Sincronizar el acceso para limpiar
            System.out.println("DEBUG: Intentando limpiar results.csv.");
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) { // false para sobrescribir
                writer.println(HEADER); // Trunca el archivo y escribe solo la cabecera.
                writer.flush(); // Asegurarse de que el contenido se escriba inmediatamente
                System.out.println("DEBUG: results.csv limpiado exitosamente.");
            } catch (IOException e) {
                System.err.println("ERROR: Fallo al limpiar results.csv: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}