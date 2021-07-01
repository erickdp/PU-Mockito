package org.ediaz.appmockito.repositories;

import org.ediaz.appmockito.models.Examen;

import java.util.Arrays;
import java.util.List;

public class ExamenRepositoryImp implements ExamenRepository {
    @Override
    public List<Examen> findAll() {
        return Arrays.asList(
                new Examen(1L, "Análisis de Datos"),
                new Examen(2L, "Seguridad de las TI"),
                new Examen(3L, "Marcos de Desarrollo II")
        );
    }
}
