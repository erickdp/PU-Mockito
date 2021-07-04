package org.ediaz.appmockito.services;

import org.ediaz.appmockito.models.Examen;

import java.util.Optional;

public interface ExamenService {
    Optional<Examen> findExamenPorNombre(String nombre);

    Examen findExamenPorNombreConPreguntas(String nombre);

    Examen guardarExamen(Examen examen);
}
