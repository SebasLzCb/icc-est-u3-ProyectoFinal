package dao.daoImpl;

import dao.AlgorithmResultDAO;
import models.AlgorithmResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlgorithmResultDAOFile implements AlgorithmResultDAO {
    private final String filePath = "results.csv";
    private static final String HEADER = "Algorithm,ExecutionTime(ns),PathLength,Found";

    public AlgorithmResultDAOFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                writer.println(HEADER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveResult(AlgorithmResult result) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            String line = String.format("%s,%d,%d,%b",
                    result.getAlgorithmName(),
                    result.getExecutionTime(),
                    result.getPathLength(),
                    !result.getPath().isEmpty());
            writer.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<AlgorithmResult> getAllResults() {
        List<AlgorithmResult> results = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Saltar la cabecera
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    String name = data[0];
                    long time = Long.parseLong(data[1]);
                    int length = Integer.parseInt(data[2]);
                    // Creamos un path vacío porque no guardamos las celdas, solo los datos
                    results.add(new AlgorithmResult(name, time, Collections.emptyList()));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public void clearResults() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            writer.println(HEADER); // Trunca el archivo y escribe solo la cabecera
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}