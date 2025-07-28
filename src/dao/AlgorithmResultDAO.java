package dao;

import java.util.List;
import models.AlgorithmResult;

public interface AlgorithmResultDAO {
    void saveResult(AlgorithmResult result);
    List<AlgorithmResult> getAllResults();
    void clearResults();
}