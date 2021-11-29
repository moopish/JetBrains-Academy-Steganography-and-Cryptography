package cryptography

import java.awt.image.BufferedImage

operator fun BufferedImage.get(x: Int, y: Int): Int = getRGB(x, y)
operator fun BufferedImage.set(x: Int, y: Int, rgb: Int) = setRGB(x, y, rgb)

inline fun BufferedImage.forEach(action: (x: Int, y: Int) -> Unit) {
    for (y in 0 until height) {
        for (x in 0 until width) {
            action(x, y)
        }
    }
}