package nl.paulharkink.rubiksmatrix

import kotlin.random.Random

class Matrix(
    private val rows: List<List<Int>>,
    private val trail: String
) : Comparable<Matrix> {

    companion object {
        var logFlips = false
        var logAllPermutations = false

        private val regex = "debug\\s+(flips|permutations)\\s+(on|off|true|false|0|1)".toRegex()

        fun parseDebugCommand(command: String) {
            val match = regex.find(command)
            val value = match?.groups?.get(2)?.value ?: ""
            when (match?.groups?.get(1)?.value ?: "") {
                "flips" -> logFlips = parseBool(value)
                "permutations" -> logAllPermutations = parseBool(value)
            }
        }

        private fun parseBool(booly: String): Boolean {
            @Suppress("MemberVisibilityCanBePrivate")
            return when (booly) {
                "on", "true", "1" -> true
                else -> false
            }
        }
    }

    constructor(rowsArr: Array<Array<Int>>) :
            this(rowsArr.map { it.toList() }, "")


    fun size(): Int {
        return rows.size
    }

    fun flipRow(row: Int): Matrix {
        val res = if (row < rows.size)
            Matrix(
                rows.mapIndexed { index, originalRow ->
                    if (index == row) originalRow.reversed()
                    else originalRow
                },
                "$trail r$row"
            )
        else
            this;
        if (logFlips) {
            println("Flipping row $row: $res")
        }
        return res;
    }

    fun flipColumn(col: Int): Matrix {
        val res = if (col < rows.size)
            Matrix(
                rows.mapIndexed { index, originalRow ->
                    (
                            originalRow.slice(0 until col) +
                                    rows[rows.size - (index + 1)][col] +
                                    originalRow.slice(col + 1 until originalRow.size)
                            )
                },
                "$trail c$col"
            )
        else this;
        if (logFlips) {
            println("Flipping column $col: $res")
        }
        return res;
    }

    override fun compareTo(other: Matrix): Int {
        val valueDiff = value().compareTo(other.value())
        return if (valueDiff != 0)
            valueDiff
        else other.trail.length.compareTo(trail.length)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun value(): Int {
        return topLeft().sum()
    }

    private fun topLeft(): Matrix {
        return Matrix(
            rows.slice(0 until rows.size / 2)
                .map { row -> row.slice(0 until row.size / 2) },
            trail
        )
    }

    private fun sum(): Int {
        return rows.fold(0) { res, value -> res + value.sum() }
    }

    private fun max(): Int {
        return rows.fold(0) { res, value -> res.coerceAtLeast(value.max()) }
    }

    private fun List<Int>.max(): Int {
        return fold(0) { res, value -> res.coerceAtLeast(value) }
    }

    private fun List<Int>.formattedRow(
        padLength: Int,
        divideVertical: String = "",
        underline: Boolean,
        sum: Int? = null
    ): String {
        return this
            .withIndex()
            .fold("") { res, (col, value) ->
                "$res${if (col == size / 2) divideVertical else ""}${value.toString().padEnd(padLength, ' ')} "
            } + if (underline) "\n" + "-".repeat((padLength + 1) * (size / 2)) + "+" + (if (sum != null) "($sum)" else "") else ""
    }

    override fun toString(): String {
        val padLength = max().toString().length;
        return rows.mapIndexed { index, row ->
            row.formattedRow(
                padLength = padLength,
                divideVertical = (if (index < size() / 2) "| " else "  "),
                underline = (index == size() / 2 - 1),
                sum = value()
            )
        }
            .fold(if (trail.isNotBlank()) "trail: $trail\n" else "") { res, row -> res + "\n" + row }
    }

    fun theoreticalLimit(): Int {
        return rows.flatMap { it.sortedDescending() }
            .sortedDescending()
            .slice(0 until size())
            .sum()
    }

    fun randomPermutationSets(times: Int): Matrix {
        var state = this;

        return (0 until times)
            .map { time ->
                state =
                    if (time == 0) state else
                        state.flipFromBitmap(Random.nextInt(possiblePermutations()))
                time to state
            }
            .onEach { (time, matrix) -> println("After random step $time, the state was: $matrix") }
            .map { (_, matrix) -> matrix }
            .max() ?: this
    }

    fun randomPermutations(times: Int): Matrix {
        var state = this;
        return (0 until times)
            .map { time ->
                state =
                    if (time == 0) state else {
                        val i = Random.nextInt(size())
                        if (Random.nextBoolean()) {
                            state.flipRow(i)
                        } else {
                            state.flipColumn(i)
                        }
                    }
                time to state
            }
            .onEach { (time, matrix) -> println("After random step $time, the state was: $matrix") }
            .map { (_, matrix) -> matrix }
            .max() ?: this
    }

    fun maximizeByAllPermutations(): Matrix {
        val allPermutations = (0 until possiblePermutations())
            .map { flipFromBitmap(it) }
        return if (logAllPermutations) {
            allPermutations
                .asSequence()
                .sorted()
                .withIndex()
                .onEach { (index, result) -> println("On place $index with sum ${result.topLeft().sum()}: $result") }
                .map { (_, result) -> result }
                .last()
        } else {
            allPermutations.max() ?: this
        }
    }

    private fun possiblePermutations() = (1 shl (2 * size()))

    private fun flipFromBitmap(state: Int): Matrix {
        val bitmask = (1 shl size()) - 1
        val rowFlipBitmap = (state shr size()) and bitmask
        val colFlipsBitmap = state and bitmask

//        println(
//            "Determining state for $state - " +
//                    "rows ${rowFlipBitmap.toString(2).padStart(size(), '0')}," +
//                    "cols ${colFlipsBitmap.toString(2).padStart(size(), '0')}: $this"
//        )

        val colsFlipped = (0 until size())
            .fold(this) { matrix, col -> if (colFlipsBitmap and (1 shl col) != 0) matrix.flipColumn(col) else matrix }

        val rowsFlipped = (0 until size())
            .fold(colsFlipped) { matrix, row -> if (rowFlipBitmap and (1 shl row) != 0) matrix.flipRow(row) else matrix }


        if (logFlips) {
            println(
                "Transformed state; corner = ${rowsFlipped.topLeft()}\n (${
                    rowsFlipped.topLeft().sum()
                }):$rowsFlipped"
            )
        }
        return rowsFlipped;
    }

    fun maximizeHalves(): Matrix {
        return maximizeCols()
            .maximizeRows()
    }

    fun maximizeRows() = (0 until size())
        .fold(this) { matrix, rowNo ->
            val row = row(rowNo)
            return@fold if (row.firstHalve() < row.secondHalve())
                matrix.flipRow(rowNo)
            else matrix
        }

    fun maximizeCols() = (0 until size())
        .fold(this) { matrix, colNo ->
            val column = column(colNo)
            return@fold if (column.firstHalve() < column.secondHalve())
                matrix.flipColumn(colNo)
            else matrix
        }

    private fun column(col: Int): List<Int> {
        return Array(size()) { rows[it][col] }.asList()
    }

    private fun row(row: Int): List<Int> {
        return rows[row]
    }

    private fun List<Int>.firstHalve(): Int {
        return slice(0 until (size / 2)).sum()
    }

    private fun List<Int>.secondHalve(): Int {
        return slice((size / 2) until size).sum()
    }
}