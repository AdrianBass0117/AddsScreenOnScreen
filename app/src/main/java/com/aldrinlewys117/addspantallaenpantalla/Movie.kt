package com.aldrinlewys117.addspantallaenpantalla

data class Movie(val title: String, val imageUrl: String)

data class MovieRow(val title: String, val movies: List<Movie>)

object MovieData {
    // Imágenes de ejemplo; se pueden reemplazar por posters reales (p. ej. API de TMDB)
    private fun movie(title: String, seed: String) =
        Movie(title, "https://picsum.photos/seed/$seed/300/450")

    val rows = listOf(
        MovieRow("Tendencias", listOf(
            movie("Noche Eterna", "a1"), movie("Galaxia Perdida", "a2"),
            movie("El Último Viaje", "a3"), movie("Sombras", "a4"), movie("Horizonte", "a5"))),
        MovieRow("Acción", listOf(
            movie("Velocidad Máxima", "b1"), movie("Operación Alfa", "b2"),
            movie("Fuego Cruzado", "b3"), movie("Titanes", "b4"), movie("Rescate", "b5"))),
        MovieRow("Ciencia ficción", listOf(
            movie("Orbita 9", "c1"), movie("Neón", "c2"),
            movie("Mundo Digital", "c3"), movie("Androide", "c4"), movie("Cosmos", "c5"))),
        MovieRow("Comedia", listOf(
            movie("Vecinos Locos", "d1"), movie("Fin de Semana", "d2"),
            movie("La Boda", "d3"), movie("Oficina", "d4"), movie("Vacaciones", "d5")))
    )
}
