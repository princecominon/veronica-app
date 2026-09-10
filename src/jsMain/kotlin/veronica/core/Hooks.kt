package veronica.core

import js.coroutines.awaitCancellation
import react.CSSProperties
import react.RefObject
import react.useEffectOnce
import react.useRef
import react.useState
import web.cssom.ClassName
import web.dom.Element

/** Reference to the browser [window] as a dynamic value (keeps DOM event typing simple). */
private val jsWindow: dynamic
    get() = js("window")

fun styleOf(vararg props: Pair<String, Any?>): CSSProperties {
    val obj: dynamic = js("({})")
    props.forEach { (key, value) -> obj[key] = value }
    return obj as CSSProperties
}

fun cls(vararg parts: Any?): ClassName =
    ClassName(parts.filterNotNull().joinToString(" "))

fun <T : Element> useInViewOnce(earlyTriggerPx: Double = 80.0): Pair<RefObject<T>, Boolean> {
    val ref = useRef<T>(null)
    val (inView, setInView) = useState(false)
    useEffectOnce {
        val check = {
            val element = ref.current
            if (!inView && element != null) {
                val rect = element.getBoundingClientRect()
                // FIX: Cast window.innerHeight to Double to prevent ClassCastException in JS
                val viewportBottom = (jsWindow.innerHeight as Double) + earlyTriggerPx
                if (rect.top <= viewportBottom && rect.bottom >= -earlyTriggerPx) {
                    setInView(true)
                }
            }
        }
        check()
        jsWindow.addEventListener("scroll", check)
        awaitCancellation {
            jsWindow.removeEventListener("scroll", check)
        }
    }
    return ref to inView
}

fun currentScrollY(): Double = (jsWindow.scrollY as Double)

/** Current viewport height in px — used to approximate where a full-screen hero ends. */
fun viewportHeight(): Double = (jsWindow.innerHeight as Double)

fun addScrollListener(listener: () -> Unit) {
    jsWindow.addEventListener("scroll", listener)
}

fun removeScrollListener(listener: () -> Unit) {
    jsWindow.removeEventListener("scroll", listener)
}