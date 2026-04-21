package dev.meow.drizzle.utils.math

import net.minecraft.util.Mth
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round

fun Double.step(step: Double): Double = round(this / step) * step
fun Double.decimals(n: Int): Double {
    val bigDecimal = BigDecimal(this)
    val rounded = bigDecimal.setScale(n, RoundingMode.HALF_UP)
    return rounded.toDouble()
}

fun Double.toRadians(): Double = this * (Math.PI / 180.0)
fun Double.toDegrees(): Double = this * (180.0 / Math.PI)
fun Double.floorToInt() = Mth.floor(this)
fun Double.ceilToInt() = Mth.ceil(this)

fun Float.toRadians(): Float = this * (Math.PI / 180f).toFloat()
fun Float.toDegrees(): Float = this * (180f / Math.PI).toFloat()
fun Float.step(step: Float): Float = round(this / step) * step
fun Float.decimals(n: Int): Float = this.toDouble().decimals(n).toFloat()
fun Float.floorToInt() = Mth.floor(this)
fun Float.ceilToInt() = Mth.ceil(this)

fun Int.step(step: Int): Int = (this + step / 2) / step * step
fun Long.step(step: Long): Long = (this + step / 2L) / step * step

