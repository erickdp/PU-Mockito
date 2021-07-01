package org.ediaz.appmockito.services;

import org.ediaz.appmockito.models.Examen;

public interface ExamenService {
    Examen findExamenPorNombre(String nombre);
}
