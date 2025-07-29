package dao;

import java.util.List;
import models.AlgorithmResult;

/**
 * Define el contrato (las reglas) para cualquier clase que gestione la persistencia
 * de los resultados de los algoritmos.
 * * Este patrón (Data Access Object - DAO) separa la lógica de negocio de la lógica
 * de acceso a datos (por ejemplo, guardar en un archivo CSV, una base de datos, etc.).
 */
public interface AlgorithmResultDAO {

    /**
     * Guarda el resultado de la ejecución de un único algoritmo.
     * * @param result El objeto AlgorithmResult que contiene los datos a guardar.
     */
    void saveResult(AlgorithmResult result);

    /**
     * Recupera todos los resultados de los algoritmos que han sido guardados previamente.
     * * @return Una lista (List) de objetos AlgorithmResult con los datos recuperados.
     */
    List<AlgorithmResult> getAllResults();

    /**
     * Borra todos los resultados almacenados en la fuente de datos.
     */
    void clearResults();
}