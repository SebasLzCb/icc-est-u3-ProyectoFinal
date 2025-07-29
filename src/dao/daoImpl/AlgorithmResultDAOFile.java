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
    // La cabecera estándar para el archivo CSV.
    private static final String HEADER = "Algorithm,ExecutionTime(ns),PathLength,Found";

    /**
     * Constructor de la clase.
     * Verifica si el archivo "results.csv" existe. Si no existe, lo crea y le
     * añade la cabecera (HEADER).
     */
    public AlgorithmResultDAOFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            // Usamos try-with-resources para asegurar que el writer se cierre automáticamente.
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                writer.println(HEADER);
            } catch (IOException e) {
                // Imprime la traza del error si ocurre un problema al crear el archivo.
                e.printStackTrace();
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
        // Usamos try-with-resources con 'true' para abrir el archivo en modo "append" (añadir).
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            // Formatea los datos del resultado en una cadena separada por comas.
            String line = String.format("%s,%d,%d,%b",
                    result.getAlgorithmName(),
                    result.getExecutionTime(),
                    result.getPathLength(),
                    !result.getPath().isEmpty());
            // Escribe la nueva línea en el archivo.
            writer.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lee todos los resultados guardados desde el archivo CSV.
     * @return Una lista de objetos AlgorithmResult con los datos leídos.
     */
    @Override
    public List<AlgorithmResult> getAllResults() {
        List<AlgorithmResult> results = new ArrayList<>();
        // Usamos try-with-resources para que el reader se cierre automáticamente.
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Saltar la línea de la cabecera.
            
            // Lee el archivo línea por línea hasta el final.
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(","); // Divide la línea por las comas.
                if (data.length >= 3) {
                    String name = data[0];
                    long time = Long.parseLong(data[1]);
                    int length = Integer.parseInt(data[2]);
                    // Creamos un objeto AlgorithmResult con los datos.
                    // Se usa un path vacío porque no guardamos la ruta completa, solo los datos métricos.
                    results.add(new AlgorithmResult(name, time, Collections.emptyList()));
                }
            }
        } catch (IOException | NumberFormatException e) {
            // Captura errores de lectura de archivo o de conversión de números.
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Borra todos los resultados del archivo CSV, pero mantiene la cabecera.
     */
    @Override
    public void clearResults() {
        // Usamos try-with-resources con 'false' para sobreescribir (truncar) el archivo.
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            writer.println(HEADER); // Trunca el archivo y escribe solo la cabecera.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}