package org.ediaz.appmockito.services;

import org.ediaz.appmockito.models.Examen;

import java.util.Arrays;
import java.util.List;

public class Datos {
    public static final List<Examen> EXAMENES = Arrays.asList(
            new Examen(1L, "Analisis de datos"),
            new Examen(2L, "Seguridad de ti"),
            new Examen(3L, "Marcos de desarrollo"));

    public static final List<Examen> EXAMENES_ID_NEGATIVO = Arrays.asList(
            new Examen(-1L, "Analisis de datos"),
            new Examen(-2L, "Seguridad de ti"),
            new Examen(-3L, "Marcos de desarrollo"));

    public static final List<String> PREGUNTAS = Arrays.asList(
            "aritmetica", "probabilidades",
            "integrales", "derivadas", "ecuaciones"
    );

    public static final Examen EXAMEN = new Examen(4L, "Desarrollo de sistemas");
}
