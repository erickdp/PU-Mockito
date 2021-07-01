package org.ediaz.appmockito.repositories;

import org.ediaz.appmockito.models.Examen;

import java.util.List;

public interface ExamenRepository {
    List<Examen> findAll();
}
