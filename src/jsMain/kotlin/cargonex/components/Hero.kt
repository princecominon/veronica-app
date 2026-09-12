package cargonex.components

import js.coroutines.awaitCancellation
import react.ChildrenBuilder
import react.FC
import react.Key
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.section
import react.dom.html.ReactHTML.span
import react.dom.svg.ReactSVG.animate
import react.dom.svg.ReactSVG.circle
import react.dom.svg.ReactSVG.ellipse
import react.dom.svg.ReactSVG.g
import react.dom.svg.ReactSVG.line
import react.dom.svg.ReactSVG.path
import react.dom.svg.ReactSVG.polygon
import react.dom.svg.ReactSVG.rect
import react.dom.svg.ReactSVG.svg
import react.dom.svg.StrokeLinejoin
import react.useEffectOnce
import react.useRef
import react.useState
import cargonex.core.addScrollListener
import cargonex.core.cls
import cargonex.core.removeScrollListener
import cargonex.core.styleOf
import cargonex.core.useInViewOnce
import web.cssom.ClassName
import web.html.HTMLElement
import web.html.HTMLSpanElement
import web.svg.RepeatCount
import web.svg.indefinite

external interface HeroProps : Props {
    var onNavigate: ((String) -> Unit)?
}

external interface CharacterRevealProps : Props {
    var text: String?
    var baseDelay: Double?
    var stagger: Double?
    var className: ClassName?
}

val CharacterReveal = FC<CharacterRevealProps> { props ->
    val text = props.text ?: ""
    val baseDelay = props.baseDelay ?: 0.0
    val stagger = props.stagger ?: 0.03
    val (revealRef, inView) = useInViewOnce<HTMLSpanElement>(80.0)

    val words = text.split(" ")
    var globalIndex = 0

    val classString = props.className?.toString() ?: ""
    val isGradient = classString.contains("gradient-text")
    val parentClassName = ClassName(classString.replace("gradient-text", "").trim())

    span {
        ref = revealRef
        className = parentClassName

        words.forEachIndexed { wordIndex, word ->
            span {
                key = Key(wordIndex.toString())
                className = ClassName("inline-block whitespace-nowrap")
                style = styleOf("marginRight" to if (wordIndex == words.lastIndex) "0" else "0.28em")

                word.forEach { char ->
                    val delay = baseDelay + globalIndex * stagger
                    globalIndex += 1

                    span {
                        key = Key(globalIndex.toString())

                        val animClass = if (inView) "char-reveal revealed" else "char-reveal"
                        className = ClassName(if (isGradient) "$animClass gradient-text" else animClass)
                        style = styleOf("transitionDelay" to "${delay}s")

                        +char.toString()
                    }
                }
            }
        }
    }
}

private data class DataLabel(
    val label: String,
    val value: String,
    val x: Double,
    val y: Double,
    val icon: FC<IconProps>,
)

private fun ChildrenBuilder.containerStack(x: Double, y: Double, w: Double, h: Double) {
    val dx = 14.0
    val dy = -10.5
    g {
        path {
            d = "M $x,$y L ${x + w},$y L ${x + w},${y - h} L $x,${y - h} Z"
            fill = "rgba(61,91,254,0.15)"
            stroke = "#6d8bff"
            strokeWidth = 0.8
            strokeLinejoin = StrokeLinejoin.round
        }
        path {
            d = "M $x,${y - h} L ${x + w},${y - h} L ${x + w + dx},${y - h + dy} L ${x + dx},${y - h + dy} Z"
            fill = "rgba(61,91,254,0.08)"
            stroke = "#6d8bff"
            strokeWidth = 0.8
            strokeLinejoin = StrokeLinejoin.round
        }
        path {
            d = "M ${x + w},$y L ${x + w},${y - h} L ${x + w + dx},${y - h + dy} L ${x + w + dx},${y + dy} Z"
            fill = "rgba(61,91,254,0.25)"
            stroke = "#6d8bff"
            strokeWidth = 0.8
            strokeLinejoin = StrokeLinejoin.round
        }
        listOf(0.25, 0.5, 0.75).forEach { f ->
            line {
                x1 = x + w * f; y1 = y
                x2 = x + w * f; y2 = y - h
                stroke = "#6d8bff"
                strokeWidth = 0.4
                opacity = 0.6
            }
        }
    }
}

private fun ChildrenBuilder.bridgeBlock(x: Double, y: Double, w: Double, h: Double) {
    val dx = 14.0
    val dy = -10.5
    g {
        path {
            d = "M $x,$y L ${x + w},$y L ${x + w},${y - h} L $x,${y - h} Z"
            fill = "rgba(109,139,255,0.2)"
            stroke = "#93c5fd"
            strokeWidth = 1.0
            strokeLinejoin = StrokeLinejoin.round
        }
        path {
            d = "M $x,${y - h} L ${x + w},${y - h} L ${x + w + dx},${y - h + dy} L ${x + dx},${y - h + dy} Z"
            fill = "rgba(109,139,255,0.1)"
            stroke = "#93c5fd"
            strokeWidth = 1.0
            strokeLinejoin = StrokeLinejoin.round
        }
        path {
            d = "M ${x + w},$y L ${x + w},${y - h} L ${x + w + dx},${y - h + dy} L ${x + w + dx},${y + dy} Z"
            fill = "rgba(109,139,255,0.3)"
            stroke = "#93c5fd"
            strokeWidth = 1.0
            strokeLinejoin = StrokeLinejoin.round
        }
    }
}

val IsometricFreightShip = FC {
    val dataLabels = listOf(
        DataLabel("FREIGHT FORECAST", "$17.8 / MT", -10.0, 45.0, TrendingUp),
        DataLabel("VESSEL FIT", "PANAMAX • 94%", 250.0, 55.0, Ship),
        DataLabel("PORT CONGESTION", "LOW • 18%", -10.0, 280.0, Anchor),
        DataLabel("CHARTER WINDOW", "7-12 DAYS", 245.0, 275.0, Clock3),
    )

    val glowingPoints = listOf(
        -80.0 to -85.0,
        140.0 to -17.0,
        -100.0 to 10.0,
        130.0 to -2.0,
        -35.0 to -40.0,
        15.0 to -44.0,
        65.0 to -26.0,
    )

    div {
        className = ClassName("relative w-[380px] h-[380px] flex items-center justify-center float-bob")

        svg {
            width = 380.0
            height = 380.0
            viewBox = "0 0 380 380"
            fill = "none"
            xmlns = "http://www.w3.org/2000/svg"
            className = ClassName("overflow-visible")

            g {
                opacity = 0.55
                ellipse {
                    cx = 190.0; cy = 208.0; rx = 148.0; ry = 58.0
                    stroke = "#3b5bfe"
                    strokeWidth = 1.0
                    strokeDasharray = "4 6"
                    animate {
                        attributeName = "stroke-dashoffset"
                        values = "0;-80"
                        dur = "9s"
                        repeatCount = RepeatCount.indefinite
                    }
                }
                ellipse {
                    cx = 190.0; cy = 208.0; rx = 124.0; ry = 43.0
                    stroke = "#6d8bff"
                    strokeWidth = 0.8
                    strokeDasharray = "2 7"
                    opacity = 0.6
                    animate {
                        attributeName = "stroke-dashoffset"
                        values = "0;100"
                        dur = "11s"
                        repeatCount = RepeatCount.indefinite
                    }
                }
            }

            ellipse {
                cx = 190.0; cy = 258.0; rx = 120.0; ry = 28.0
                stroke = "#3b5bfe"
                strokeWidth = 1.0
                fill = "rgba(61,91,254,0.05)"
                opacity = 0.65
            }
            ellipse {
                cx = 190.0; cy = 264.0; rx = 90.0; ry = 17.0
                stroke = "#6d8bff"
                strokeWidth = 0.8
                fill = "none"
                strokeDasharray = "3 5"
                opacity = 0.5
            }

            g {
                transform = "translate(190 205)"

                path {
                    d = "M -100,10 C -130,15 -160,20 -190,20"
                    stroke = "#3b5bfe"
                    strokeWidth = 1.5
                    fill = "none"
                    strokeDasharray = "8 8"
                    opacity = 0.5
                    animate {
                        attributeName = "stroke-dashoffset"
                        values = "16;0"
                        dur = "1s"
                        repeatCount = RepeatCount.indefinite
                    }
                }
                path {
                    d = "M -86,-0.5 C -116,4 -146,9 -176,9"
                    stroke = "#6d8bff"
                    strokeWidth = 1.0
                    fill = "none"
                    strokeDasharray = "6 6"
                    opacity = 0.3
                    animate {
                        attributeName = "stroke-dashoffset"
                        values = "12;0"
                        dur = "1.2s"
                        repeatCount = RepeatCount.indefinite
                    }
                }

                path {
                    d = "M 100,10 L 125,4 C 135,6 135,14 125,14 L 100,10 Z"
                    fill = "rgba(61,91,254,0.3)"
                    stroke = "#3b5bfe"
                    strokeWidth = 1.0
                    strokeLinejoin = StrokeLinejoin.round
                }
                path {
                    d = "M -100,10 L -100,-10 L -86,-20.5 L -86, -0.5 Z"
                    fill = "rgba(61,91,254,0.3)"
                    stroke = "#3b5bfe"
                    strokeWidth = 1.6
                    strokeLinejoin = StrokeLinejoin.round
                }
                path {
                    d = "M -100,-10 L 100,-10 L 140,-17 L 130,-2 L 100,10 L -100,10 Z"
                    fill = "rgba(61,91,254,0.15)"
                    stroke = "#3b5bfe"
                    strokeWidth = 1.6
                    strokeLinejoin = StrokeLinejoin.round
                }
                path {
                    d = "M -100,-10 L -86,-20.5 L 114,-20.5 L 140,-17 L 100,-10 Z"
                    fill = "rgba(61,91,254,0.08)"
                    stroke = "#3b5bfe"
                    strokeWidth = 1.6
                    strokeLinejoin = StrokeLinejoin.round
                }

                path {
                    d = "M -100,2 L 100,2 L 132,-6"
                    stroke = "#6d8bff"
                    strokeWidth = 0.8
                    fill = "none"
                    opacity = 0.6
                }
                ellipse {
                    cx = 120.0; cy = -14.0; rx = 8.0; ry = 3.0
                    stroke = "#6d8bff"
                    strokeWidth = 0.8
                    fill = "none"
                    opacity = 0.7
                }
                line {
                    x1 = 118.0; y1 = -15.0; x2 = 118.0; y2 = -13.0
                    stroke = "#6d8bff"
                    strokeWidth = 0.6
                }
                line {
                    x1 = 122.0; y1 = -15.0; x2 = 122.0; y2 = -13.0
                    stroke = "#6d8bff"
                    strokeWidth = 0.6
                }
                line {
                    x1 = 118.0; y1 = -14.0; x2 = 122.0; y2 = -14.0
                    stroke = "#6d8bff"
                    strokeWidth = 0.6
                }

                containerStack(-60.0, -10.0, 22.0, 22.0)
                containerStack(-35.0, -10.0, 22.0, 30.0)
                containerStack(-10.0, -10.0, 22.0, 26.0)
                containerStack(15.0, -10.0, 22.0, 34.0)
                containerStack(40.0, -10.0, 22.0, 20.0)
                containerStack(65.0, -10.0, 22.0, 16.0)
                containerStack(90.0, -10.0, 15.0, 10.0)

                bridgeBlock(-95.0, -10.0, 32.0, 18.0)
                bridgeBlock(-92.0, -28.0, 26.0, 14.0)
                bridgeBlock(-89.0, -42.0, 20.0, 12.0)

                rect { x = -86.0; y = -50.0; width = 3.0; height = 4.0; fill = "#93c5fd"; opacity = 0.9 }
                rect { x = -81.0; y = -50.0; width = 3.0; height = 4.0; fill = "#93c5fd"; opacity = 0.9 }
                rect { x = -76.0; y = -50.0; width = 3.0; height = 4.0; fill = "#93c5fd"; opacity = 0.9 }

                line {
                    x1 = -80.0; y1 = -54.0; x2 = -80.0; y2 = -80.0
                    stroke = "#93c5fd"
                    strokeWidth = 1.2
                }
                line {
                    x1 = -85.0; y1 = -70.0; x2 = -75.0; y2 = -70.0
                    stroke = "#93c5fd"
                    strokeWidth = 0.8
                }
                polygon {
                    points = "-80,-85 -77,-80 -83,-80"
                    fill = "#93c5fd"
                    opacity = 0.8
                }
                ellipse {
                    cx = -80.0; cy = -80.0; rx = 6.0; ry = 1.5
                    stroke = "#93c5fd"
                    fill = "none"
                    strokeWidth = 0.8
                    animate {
                        attributeName = "rx"
                        values = "6;0;6"
                        dur = "1.2s"
                        repeatCount = RepeatCount.indefinite
                    }
                }

                glowingPoints.forEachIndexed { index, (gx, gy) ->
                    circle {
                        key = Key("glow-$index")
                        cx = gx; cy = gy
                        r = 2.6
                        fill = "#3b5bfe"
                        opacity = 0.9
                        animate {
                            attributeName = "opacity"
                            values = "0.9;0.3;0.9"
                            dur = "${2 + index * 0.4}s"
                            repeatCount = RepeatCount.indefinite
                        }
                    }
                }
            }

            line {
                x1 = 72.0; y1 = 180.0; x2 = 28.0; y2 = 88.0
                stroke = "#3b5bfe"; strokeWidth = 0.8
                strokeDasharray = "3 3"; opacity = 0.55
            }
            line {
                x1 = 320.0; y1 = 190.0; x2 = 350.0; y2 = 100.0
                stroke = "#3b5bfe"; strokeWidth = 0.8
                strokeDasharray = "3 3"; opacity = 0.55
            }
            line {
                x1 = 76.0; y1 = 220.0; x2 = 34.0; y2 = 302.0
                stroke = "#3b5bfe"; strokeWidth = 0.8
                strokeDasharray = "3 3"; opacity = 0.5
            }
            line {
                x1 = 300.0; y1 = 228.0; x2 = 344.0; y2 = 300.0
                stroke = "#3b5bfe"; strokeWidth = 0.8
                strokeDasharray = "3 3"; opacity = 0.5
            }

            path {
                d = "M 55 315 C 105 296, 130 322, 180 308 C 230 294, 262 315, 325 292"
                stroke = "#3b5bfe"
                strokeWidth = 1.0
                strokeDasharray = "4 5"
                opacity = 0.45
                fill = "none"
                animate {
                    attributeName = "stroke-dashoffset"
                    values = "0;-70"
                    dur = "5s"
                    repeatCount = RepeatCount.indefinite
                }
            }
            circle { cx = 55.0; cy = 315.0; r = 3.0; fill = "#3b5bfe" }
            circle { cx = 180.0; cy = 308.0; r = 3.0; fill = "#7fa8ff" }
            circle { cx = 325.0; cy = 292.0; r = 3.0; fill = "#3b5bfe" }
        }

        dataLabels.forEachIndexed { index, item ->
            div {
                key = Key(item.label)
                className = ClassName("absolute min-w-[116px] text-[8px] font-medium tracking-[0.12em] text-[#7fa8ff] border border-[#3b5bfe]/30 bg-[#0c1130]/90 backdrop-blur-sm px-2.5 py-2 rounded-lg shadow-[0_0_20px_rgba(59,91,254,0.08)] card-bob")
                style = styleOf(
                    "left" to item.x,
                    "top" to item.y,
                    "animationDuration" to "${3 + index * 0.5}s",
                    "animationDelay" to "${index * 0.25}s",
                )

                div {
                    className = ClassName("flex items-center gap-1.5 mb-1")
                    item.icon {
                        size = 10.0
                        className = ClassName("text-[#6d8bff]")
                    }
                    span {
                        +item.label
                    }
                }
                div {
                    className = ClassName("text-[10px] text-[#c7cbe0] tracking-[0.06em]")
                    +item.value
                }
            }
        }

        div {
            className = ClassName("absolute inset-0 flex items-center justify-center pointer-events-none")
            div {
                className = ClassName("w-4 h-4 rounded-full bg-[#3b5bfe]")
                style = styleOf("boxShadow" to "0 0 20px 6px rgba(61,91,254,0.45)")
                div {
                    className = ClassName("w-full h-full rounded-full bg-[#9bb2ff] animate-ping")
                    style = styleOf("animationDuration" to "2s")
                }
            }
        }
    }
}

val Hero = FC<HeroProps> { props ->
    val heroRef = useRef<HTMLElement>(null)
    val (dissolved, setDissolved) = useState(false)

    useEffectOnce {
        val handleScroll: () -> Unit = {
            heroRef.current?.let { element ->
                if (element.getBoundingClientRect().bottom < -200.0) {
                    setDissolved(true)
                } else {
                    setDissolved(false)
                }
            }
        }

        handleScroll()
        addScrollListener(handleScroll)
        awaitCancellation { removeScrollListener(handleScroll) }
    }

    section {
        ref = heroRef
        className = cls(
            "relative min-h-screen flex items-center overflow-hidden transition-all duration-500",
            if (dissolved) "pixel-dissolve" else "",
        )
        style = styleOf(
            "background" to "radial-gradient(ellipse at 80% 120%, #1d2bd8 0%, #3b5bfe 22%, #0c1130 60%, #070914 100%)",
        )

        div {
            className = ClassName("absolute inset-0 opacity-[0.035]")
            style = styleOf(
                "backgroundImage" to "linear-gradient(#7fa8ff 1px, transparent 1px), linear-gradient(90deg, #7fa8ff 1px, transparent 1px)",
                "backgroundSize" to "60px 60px",
            )
        }

        div {
            className = ClassName("absolute inset-0 pointer-events-none")
            style = styleOf(
                "background" to "radial-gradient(circle at 72% 50%, rgba(59,91,254,0.12), transparent 34%), radial-gradient(circle at 18% 80%, rgba(109,139,255,0.05), transparent 30%)",
            )
        }

        div {
            className = ClassName("relative max-w-7xl mx-auto px-6 pt-20 pb-20 w-full")

            div {
                className = ClassName("flex flex-col lg:flex-row items-center gap-12 lg:gap-10")

                div {
                    className = ClassName("flex-1 text-center lg:text-left")

                    h1 {
                        className = ClassName("text-5xl sm:text-6xl lg:text-7xl xl:text-[80px] font-bold leading-[1.05] tracking-tight mb-7")

                        div {
                            className = ClassName("block")
                            CharacterReveal {
                                text = "Forecast freight."
                                baseDelay = 0.15
                                stagger = 0.03
                                className = ClassName("text-[#c7cbe0]")
                            }
                        }

                        div {
                            className = ClassName("block mt-1")
                            CharacterReveal {
                                text = "Charter smarter."
                                baseDelay = 0.80
                                stagger = 0.03
                                className = ClassName("text-[#6d8bff]")
                            }
                        }
                    }

                    p {
                        className = ClassName("text-[#c7cbe0]/70 text-lg sm:text-xl max-w-xl mx-auto lg:mx-0 mb-10 leading-relaxed anim-in-y10")
                        style = styleOf("animationDelay" to "1.4s")
                        +"Predict freight rates, compare vessel options, evaluate port constraints, and identify the optimal charter window before market conditions change."
                    }

                    div {
                        className = ClassName("flex items-center gap-4 justify-center lg:justify-start anim-in-y16")
                        style = styleOf("animationDelay" to "1.55s")

                        button {
                            className = ClassName("btn-primary text-white font-semibold px-7 py-3.5 rounded-xl flex items-center gap-2 text-base group")
                            onClick = { props.onNavigate?.invoke("forecast_dashboard") }

                            +"Explore Forecast"
                            ArrowRight {
                                size = 16.0
                                className = ClassName("group-hover:translate-x-1 transition-transform duration-200")
                            }
                        }

                        button {
                            className = ClassName("w-12 h-12 rounded-xl border border-[#3b5bfe]/40 bg-[#1d2bd8]/20 flex items-center justify-center hover:bg-[#1d2bd8]/40 transition-colors duration-200 group")
                            Play {
                                size = 18.0
                                className = ClassName("text-[#7fa8ff] group-hover:scale-110 transition-transform duration-200 ml-0.5")
                            }
                        }

                        span {
                            className = ClassName("text-[#c7cbe0]/50 text-sm hidden sm:block")
                            +"See how it works"
                        }
                    }

                    div {
                        className = ClassName("flex flex-wrap gap-8 mt-10 justify-center lg:justify-start anim-in-y12")
                        style = styleOf("animationDelay" to "1.7s")

                        div {
                            div { className = ClassName("text-[#c7cbe0] text-lg font-semibold"); +"7-90D" }
                            div { className = ClassName("text-[#c7cbe0]/40 text-[10px] tracking-[0.14em] mt-1"); +"FORECAST HORIZON" }
                        }
                        div {
                            div { className = ClassName("text-[#c7cbe0] text-lg font-semibold"); +"4+" }
                            div { className = ClassName("text-[#c7cbe0]/40 text-[10px] tracking-[0.14em] mt-1"); +"VESSEL CLASSES" }
                        }
                        div {
                            div { className = ClassName("text-[#c7cbe0] text-lg font-semibold"); +"AI" }
                            div { className = ClassName("text-[#c7cbe0]/40 text-[10px] tracking-[0.14em] mt-1"); +"DECISION ENGINE" }
                        }
                    }
                }

                div {
                    className = ClassName("flex-shrink-0 flex items-center justify-center anim-pop")
                    style = styleOf("animationDelay" to "0.3s")
                    IsometricFreightShip()
                }
            }
        }

        div {
            className = ClassName("absolute bottom-0 left-0 right-0 h-32 bg-gradient-to-t from-[#eef1fb] to-transparent pointer-events-none")
        }
    }
}
