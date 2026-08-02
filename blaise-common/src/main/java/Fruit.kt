import java.util.*

object Fruit {
    val list =  listOf("")
    val scanner = Scanner(System.`in`)

    @JvmStatic
    fun main(args: Array<String>) {
        var k = ' '
        while (k != 'q') {
            val compare = fruitCompare()
            println("A ${compare.first} or B ${compare.second}?")
            k = scanner.next().single()
        }
    }

    fun fruitCompare(): Pair<String, String> {
        val first = list.random()
        val second = (list - first).random()
        return first to second
    }
}