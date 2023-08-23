package nl.paulharkink.rubiksmatrix

class InteractiveSolver (private val original: Matrix){

    fun interactiveLoop(): Boolean {
        var current = original;
        val mutationCommandPattern = "([cr])[\\s:]?([0-9]+)[\\s,]*".toRegex()
        val randomCommandPattern = "random(?:\\s+(?:threads (\\d+))?(set))?(?:\\s(\\d+))?".toRegex()

        while (true) {
            println("$current")

            val input = readLine()?.trim()?.toLowerCase()
                ?: return false
            val mutationCommands = mutationCommandPattern.findAll(input)
            if (mutationCommands.count() == 0) {
                when (input) {

                    "reset" -> {
                        println("Resetting to initial state")
                        current = original
                    }

                    "clear" -> {
                        repeat(80) { println() }
                    }

                    "cancel", "abort", "bye" -> {
                        println("Bye")
                        return false
                    }

                    "limit", "max" -> {
                        println("Upper limit: ${current.theoreticalLimit()}")
                    }

                    "skip", "next" -> {
                        return true
                    }

                    "all permutations" -> {
                        current = current.maximizeByAllPermutations()
                    }

                    "halves" -> {
                        current = current.maximizeHalves()
                    }

                    "rows" -> {
                        current = current.maximizeRows()
                    }

                    "cols" -> {
                        current = current.maximizeCols()
                    }

                    else -> {
                        if (input.startsWith("debug")) {
                            Matrix.parseDebugCommand(input)
                        } else {
                            val randomCommand = randomCommandPattern.find(input);
                            if (randomCommand != null) {
                                val groups = randomCommand.groups
                                val threads = groups[1]?.value?.toInt() ?: 1
                                val isSet = groups[2]?.value?.isNotEmpty() == true
                                val times = groups[3]?.value?.toInt() ?: 1

                                if (threads > 1) {

//                                val tasks = List(threads) { taskNumber ->
//                                    async(Dispatchers.Default) {
//                                        current = randomMutations(current, isSet, times)
//                                    }
//                                }
                                } else {
                                    current = randomMutations(current, isSet, times)
                                }
                            } else {
                                println("Unrecognized input")
                            }
                        }
                    }
                }
            } else {
                current = mutationCommands.fold(current) { currentState, match ->
                    processPermutationCommand(match, currentState)
                }
            }
        }
    }

    private fun randomMutations(current: Matrix, isSet: Boolean, times: Int): Matrix {
        var current1 = current
        current1 = if (isSet)
            current1.randomPermutationSets(times)
        else current1.randomPermutations(times)
        return current1
    }

    private fun processPermutationCommand(match: MatchResult, currentMatrix: Matrix): Matrix {
        val index = match.groups[2]?.value?.toInt() ?: -1
        return if (index >= currentMatrix.size() || index < 0) {
            println("Invalid index: $index")
            currentMatrix
        } else {
            if (match.groups[1]?.value == "c") {
                currentMatrix.flipColumn(index)
            } else {
                currentMatrix.flipRow(index)
            }
        }
    }

}