package cryptography

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

const val CHECK_BYTE: Byte = 3

fun main() {
    var active = true
    do {
        println("Task (hide, show, exit):")
        when (val answer = readLine()!!) {
            "exit" -> {
                active = false
                println("Bye!")
            }
            "hide" -> hide()
            "show" -> show()
            else -> println("Wrong task: $answer")
        }
    } while (active)
}

private fun hide() {
    println("Input image file:")
    val inputFile: String = readLine() ?: run {
        println("No input image file read!")
        return
    }

    println("Output image file:")
    val outputFile: String = readLine() ?: run {
        println("No output image file read!")
        return
    }

    val image: BufferedImage
    try {
        image = ImageIO.read(File(inputFile))
    } catch (e: Exception) {
        println("Can't read input file!")
        return
    }

    println("Message to hide:")
    val message = readLine()!!.encodeToByteArray() + byteArrayOf(0.toByte(), 0.toByte(), CHECK_BYTE)

    println("Password:")
    val password = readLine()!!.encodeToByteArray()

    if (message.size > (image.width * image.height) / 8) {
        println("The input image is not large enough to hold this message.")
    } else {
        for (index in 0 until message.size - CHECK_BYTE) {
            message[index] = (message[index].toInt() xor password[index % password.size].toInt()).toByte()
        }

        for ((index, byte) in message.withIndex()) {
            for (bit in 0 until Byte.SIZE_BITS) {
                val offsetIndex = (index * Byte.SIZE_BITS + bit)
                val setOrClear = (byte.toInt() shr (Byte.SIZE_BITS - 1 - bit)) and 1
                val x = offsetIndex % image.width
                val y = offsetIndex / image.width
                image[x, y] = image[x, y] and 1.inv() or setOrClear
            }
        }

        ImageIO.write(image, "png", File(outputFile))
        println("Message saved in $outputFile image.")
    }
}

fun show() {
    println("Input image file:")
    val inputFile: String = readLine() ?: run {
        println("No input image file read!")
        return
    }

    val image: BufferedImage
    try {
        image = ImageIO.read(File(inputFile))
    } catch (e: Exception) {
        println("Can't read input file!")
        return
    }

    println("Password:")
    val password = readLine()!!.encodeToByteArray()

    val byteList = mutableListOf<Byte>()
    var currByte = 0
    var currBit = 0

    imageLoop@for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            currByte = currByte shl 1 or (image[x, y] and 1)
            ++currBit

            if (currBit == Byte.SIZE_BITS) {
                val toAdd = currByte.toByte()

                if (toAdd == CHECK_BYTE) {
                    if (byteList.size >= CHECK_BYTE) {
                        if (byteList[byteList.lastIndex - 1] == 0.toByte()
                            && byteList[byteList.lastIndex] == 0.toByte()) {
                            byteList.removeLast()
                            byteList.removeLast()
                            break@imageLoop
                        }
                    }
                }

                byteList.add(toAdd)
                currByte = 0
                currBit = 0
            }
        }
    }

    println("Message:")
    println(byteList.mapIndexed { index, byte -> (byte.toInt() xor password[index % password.size].toInt()).toByte() } .toByteArray().toString(Charsets.UTF_8))
}

