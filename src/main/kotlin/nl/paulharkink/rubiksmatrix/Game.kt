package nl.paulharkink.rubiksmatrix

class Game {

    val puzzles = listOf(
        InteractiveSolver(
            Matrix(
                arrayOf(
                    arrayOf(1, 1, 10, 10),
                    arrayOf(1, 1, 10, 10),
                    arrayOf(1, 1, 10, 10),
                    arrayOf(1, 1, 10, 10)
                )
            )
        ),
        InteractiveSolver(
            Matrix(
                arrayOf(
                    arrayOf(1, 2, 3, 4),
                    arrayOf(5, 6, 7, 8),
                    arrayOf(9, 10, 11, 12),
                    arrayOf(13, 14, 15, 16)
                )
            )
        ),
        InteractiveSolver(
            Matrix(
                arrayOf(
                    arrayOf(107, 54, 128, 15),
                    arrayOf(12, 75, 110, 138),
                    arrayOf(100, 96, 34, 85),
                    arrayOf(75, 15, 28, 112)
                )
            )
        ),
        InteractiveSolver(
            Matrix(
                arrayOf(
                    arrayOf(112, 42, 83, 119),
                    arrayOf(56, 125, 56, 49),
                    arrayOf(15, 78, 101, 43),
                    arrayOf(62, 98, 114, 108)
                )
            )
        )
    )

    fun playAll() {
        puzzles.takeWhile { it.interactiveLoop() }
    }
}

fun main() {

    Game().playAll()

    /*InteractiveSolver(
        Matrix(
            arrayOf(
                arrayOf(1, 1, 10, 10),
                arrayOf(1, 1, 10, 10),
                arrayOf(1, 1, 10, 10),
                arrayOf(1, 1, 10, 10)
            )
        )
    ).interactiveLoop()

    InteractiveSolver(
        Matrix(
            arrayOf(
                arrayOf(1, 2, 3, 4),
                arrayOf(5, 6, 7, 8),
                arrayOf(9, 10, 11, 12),
                arrayOf(13, 14, 15, 16)
            )
        )
    ).interactiveLoop()
    // 54

    InteractiveSolver(
        Matrix(
            arrayOf(
                arrayOf(107, 54, 128, 15),
                arrayOf(12, 75, 110, 138),
                arrayOf(100, 96, 34, 85),
                arrayOf(75, 15, 28, 112)
            )
        )
    ).interactiveLoop()
    // 436: (all permutations) -> c0 c1 c3 r0
    // 469: (all permutations halves) -> c0 c1 c3 r0 c0
    // 451: (halves halves)-> r1 r3 c3 r0
    // 488: (random 100)                  -> c3 r1 r2 c0 r0 r3
    // 488: (all permutations random 100) -> c0 c1 c3 r0 c3 r1 r3
    //                                       c2 r3

    InteractiveSolver(
        Matrix(
            arrayOf(
                arrayOf(112, 42, 83, 119),
                arrayOf(56, 125, 56, 49),
                arrayOf(15, 78, 101, 43),
                arrayOf(62, 98, 114, 108)
            )
        )
    ).interactiveLoop()
    // 414: c2 r0*/


}