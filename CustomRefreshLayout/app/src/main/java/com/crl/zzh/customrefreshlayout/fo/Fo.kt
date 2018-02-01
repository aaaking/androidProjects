package com.crl.zzh.customrefreshlayout.fo

/**
 * Created by 周智慧 on 31/01/2018.
 */
fun main(vararg args: String) {
    var set123 = setOf(6, 7, 8)
    var powerset = powerset(set123)
    println(powerset)
    println("------")
    println(set123.extensionPowerset())
    println("------")
    println(set123.powerset())
    println("------")
    println(set123.findPowersetByBinary())
    println("------")
}

fun <T> powerset(set: Set<T>): Set<Set<T>> {
    return if (set.isEmpty()) setOf(setOf())
    else {
        powerset(set.drop(1).toSet())
                .let { it + it.map { it + set.first() } }
    }
}

fun <T> Collection<T>.extensionPowerset(): Set<Set<T>> =
        if (isEmpty()) setOf(setOf())
        else drop(1)
                .extensionPowerset()
                .let { it + it.map { it + first() } }

fun <T> Collection<T>.powerset(): Set<Set<T>> =
        powerset(this, setOf(setOf()))

private tailrec fun <T> powerset(left: Collection<T>, acc: Set<Set<T>>): Set<Set<T>> =
        if (left.isEmpty()) acc
        else powerset(left.drop(1), acc + acc.map { it + left.first() })

fun <T> Collection<T>.findPowersetByBinary(): Set<Set<T>> =
        mutableSetOf(setOf<T>()).apply {
            for (i in 0 until Math.pow(2.0, this@findPowersetByBinary.size.toDouble()).toInt()) {
                mutableSetOf<T>().run {
                    var temp = i
                    var index = 0
                    do {
                        takeIf { temp and 1 == 1 }?.run {
                            add(this@findPowersetByBinary.elementAt(index))
                        }
                        temp = temp shr 1
                        index++
                    } while (temp > 0)
                    this@apply.add(this)
                }
            }
        }

private tailrec fun <T> powersetK(src: Collection<T>, k: Int, result: MutableSet<Set<T>>): Set<Set<T>> =
        if (src.isEmpty()) {
            result
        } else if (k == 1) {
            src.forEach {
                mutableSetOf<T>().apply {
                    add(it)
                    result.add(this)
                }
            }
            result
        } else {
            for (i in 0 until k) {
            }
            powersetK(src, k - 1, (result + result.map { it + src.first() }).toMutableSet())
        }