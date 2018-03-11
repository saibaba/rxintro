package intro
// https://www.infoq.com/articles/rxjava2-by-example

import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
import java.util.*
import java.util.concurrent.TimeUnit

fun just() {
    val hello:Observable<String>  = Observable.just("intro")
    hello.subscribeBy( onNext = {println(it)})
}

fun words():List<String> {
    return Arrays.asList( "the",
            "quick",
            "brown",
            "fox",
            "jumped",
            "over",
            "the",
            "lazy",
            "dogs")
}

fun hlist():List<Any> {
    return listOf("One", 2, "Three", "Four", 4.5, "Five", 6.0f)

}

fun l1() {
    // type safety gone bonkers due to implicit typing : it is List<> not individual items and you do not know until you see at runtime
    Observable.just(words()).subscribe( { it  ->  println(it) })

    // better way would be try this and it will show the error
    //    Observable.just(words).subscribe( { it: String  ->  println(it) })
}

fun l2() {
    Observable.fromIterable(words()).subscribe( { it -> println(it) })
}

fun lz() {
    val zipper = { s:String, c: Int -> String.format("%2d. %s", c, s) }
    val counter = Observable.range(1, Integer.MAX_VALUE)
    val observable:Observable<String> = Observable.fromIterable(words())

    observable.zipWith(counter, zipper).subscribe({ it:String -> println(it) })

}

fun split(w: String):Observable<String> {
    return Observable.fromIterable(w.split(""))
}

fun lc() {
    val zipper:(String, Int)->String = { s:String, c: Int -> String.format("%2d. %s", c, s) }
    val counter = Observable.range(1, Integer.MAX_VALUE)

    val observable:Observable<String> = Observable.fromIterable(words()).flatMap ( { word:String -> split(word) })

    // where are the empty strings coming from in the output
    observable.zipWith(counter, zipper).subscribe({ it:String -> println(it) })
}

fun lu() {
    val zipper:(String, Int)->String = { s:String, c: Int -> String.format("%2d. %s", c, s) }
    val counter = Observable.range(1, Integer.MAX_VALUE)

    val observable:Observable<String> = Observable.fromIterable(words()).flatMap ( { word:String -> split(word) }).distinct()

    // where are the empty strings coming from in the output
    observable.zipWith(counter, zipper).subscribe({ it:String -> println(it) })
}

fun lus() {
    val zipper:(String, Int)->String = { s:String, c: Int -> String.format("%2d. %s", c, s) }
    val counter = Observable.range(1, Integer.MAX_VALUE)

    val observable:Observable<String> = Observable.fromIterable(words()).flatMap ( { word:String -> split(word) }).distinct().sorted()

    // where are the empty strings coming from in the output
    observable.zipWith(counter, zipper).subscribe({ it:String -> println(it) })
}

object Global {
    val start = System.currentTimeMillis()
}

fun isSlowTickTime():Boolean {
    return (System.currentTimeMillis() - Global.start) % 30_000 >= 15_000
}

fun ticktock() {
    val fast = Observable.interval(1, TimeUnit.SECONDS)
    val slow = Observable.interval(3, TimeUnit.SECONDS)

    val clock = Observable.merge(slow.filter( { _ -> isSlowTickTime() }),
            fast.filter( { _ -> !isSlowTickTime()}))

    clock.subscribe( { _ -> println(Date()) })
}

fun doit() {
  val observable : io.reactivex.Observable<Any> = hlist().toObservable()
  observable.subscribeBy(
    onNext = { println (it) },
    onError = { it.printStackTrace() },
    onComplete = { println("Done!"); })
}

fun main(args: Array<String>) {
    just()
    doit()
    l1()
    l2()
    lz()
    lc()
    lu()
    lus()
    ticktock()

    Thread.sleep(60_000)
}
