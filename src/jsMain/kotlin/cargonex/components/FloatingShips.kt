package cargonex.components

import js.coroutines.awaitCancellation
import react.FC
import react.Key
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.style as styleTag
import react.useEffectOnce
import react.useState
import cargonex.core.addScrollListener
import cargonex.core.currentScrollY
import cargonex.core.removeScrollListener
import cargonex.core.styleOf
import cargonex.core.viewportHeight
import web.cssom.ClassName

private data class Sail(
    val topPercent: Double,
    val sizePx: Double,
    val durationS: Double,
    val delayS: Double,
    val opacity: Double,
)

// Sizes are doubled (except the 40px one). 
// Delays are ordered so the largest ship (96px) moves first, down to the smallest.
private val FLEET = listOf(
    Sail(topPercent = 25.0, sizePx = 96.0, durationS = 31.0, delayS = 0.0, opacity = 0.18),
    Sail(topPercent = 45.0, sizePx = 80.0, durationS = 40.0, delayS = 3.0, opacity = 0.15),
    Sail(topPercent = 65.0, sizePx = 64.0, durationS = 24.0, delayS = 7.0, opacity = 0.16),
    Sail(topPercent = 15.0, sizePx = 60.0, durationS = 27.0, delayS = 11.0, opacity = 0.13),
    Sail(topPercent = 80.0, sizePx = 52.0, durationS = 21.0, delayS = 14.0, opacity = 0.14),
    Sail(topPercent = 35.0, sizePx = 40.0, durationS = 34.0, delayS = 18.0, opacity = 0.24)
)

val FloatingShips = FC {
    val (pastHero, setPastHero) = useState(false)

    useEffectOnce {
        val onScroll = {
            // Check if we've scrolled past 85% of the viewport (out of the dark hero)
            setPastHero(currentScrollY() > viewportHeight() * 0.85)
        }
        
        onScroll()
        addScrollListener(onScroll)
        awaitCancellation { removeScrollListener(onScroll) }
    }

    // Zig-zag animation moving all over the screen vertically while crossing horizontally
    styleTag {
        +"""
            @keyframes veronica-sail {
              0%   { transform: translate(-20vw, 0vh) rotate(0deg); }
              25%  { transform: translate(15vw, -15vh) rotate(-4deg); }
              50%  { transform: translate(50vw, 15vh) rotate(4deg); }
              75%  { transform: translate(85vw, -10vh) rotate(-2deg); }
              100% { transform: translate(120vw, 5vh) rotate(0deg); }
            }
        """.trimIndent()
    }

    div {
        className = ClassName("fixed inset-0 z-[4] overflow-hidden pointer-events-none")
        
        FLEET.forEachIndexed { i, sail ->
            div {
                key = Key(i.toString())
                className = ClassName("absolute left-0 will-change-transform")
                style = styleOf(
                    "top" to "${sail.topPercent}%",
                    "opacity" to sail.opacity,
                    "animation" to "veronica-sail ${sail.durationS}s linear ${sail.delayS}s infinite",
                )
                
                ShipLogo {
                    size = sail.sizePx
                    tone = if (pastHero) "accent" else "brand"
                }
            }
        }
    }
}