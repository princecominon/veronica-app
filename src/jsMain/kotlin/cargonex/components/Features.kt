package cargonex.components

import react.FC
import react.Key
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.section
import web.html.HTMLElement
import react.dom.svg.ReactSVG.circle
import react.dom.svg.ReactSVG.path
import react.dom.svg.ReactSVG.rect
import react.dom.svg.ReactSVG.svg
import react.dom.svg.StrokeLinecap
import react.dom.svg.StrokeLinejoin
import cargonex.core.cls
import cargonex.core.styleOf
import cargonex.core.useInViewOnce
import web.cssom.ClassName

// ---------------------------------------------------------------------------
// Geometric line-art feature icons (48x48, matching the spec's aesthetic)
// ---------------------------------------------------------------------------

val IconVessel = FC<Props> {
    svg {
        width = 48.0; height = 48.0; viewBox = "0 0 48 48"; fill = "none"
        rect {
            x = 8.0; y = 20.0; width = 32.0; height = 4.0; rx = 1.0
            stroke = "#3b5bfe"; strokeWidth = 1.5
        }
        path {
            d = "M12 20 L14 14 L34 14 L36 20"
            stroke = "#3b5bfe"; strokeWidth = 1.5; strokeLinejoin = StrokeLinejoin.round
        }
        path { d = "M24 14 L24 8"; stroke = "#3b5bfe"; strokeWidth = 1.5 }
        path {
            d = "M24 8 L28 12 L24 10"
            stroke = "#3b5bfe"; strokeWidth = 1.5; strokeLinejoin = StrokeLinejoin.round
        }
        path {
            d = "M6 28 Q24 25 42 28 L38 34 Q24 31 10 34 Z"
            stroke = "#3b5bfe"; strokeWidth = 1.5; fill = "none"
        }
    }
}

val IconRate = FC<Props> {
    svg {
        width = 48.0; height = 48.0; viewBox = "0 0 48 48"; fill = "none"
        path {
            d = "M8 32 L16 22 L24 28 L32 16 L40 20"
            stroke = "#3b5bfe"; strokeWidth = 1.5
            strokeLinecap = StrokeLinecap.round; strokeLinejoin = StrokeLinejoin.round; fill = "none"
        }
        path {
            d = "M8 38 L40 38"
            stroke = "#3b5bfe"; strokeWidth = 1.5; strokeLinecap = StrokeLinecap.round
            opacity = 0.4
        }
        circle { cx = 16.0; cy = 22.0; r = 2.5; fill = "#3b5bfe" }
        circle { cx = 24.0; cy = 28.0; r = 2.5; fill = "#3b5bfe" }
        circle { cx = 32.0; cy = 16.0; r = 2.5; fill = "#6d8bff" }
        circle { cx = 40.0; cy = 20.0; r = 2.5; fill = "#6d8bff" }
    }
}

val IconRisk = FC<Props> {
    svg {
        width = 48.0; height = 48.0; viewBox = "0 0 48 48"; fill = "none"
        rect { x = 10.0; y = 10.0; width = 12.0; height = 12.0; rx = 2.0; stroke = "#3b5bfe"; strokeWidth = 1.5 }
        rect {
            x = 26.0; y = 10.0; width = 12.0; height = 12.0; rx = 2.0
            stroke = "#3b5bfe"; strokeWidth = 1.5; fill = "rgba(61,91,254,0.1)"
        }
        rect {
            x = 10.0; y = 26.0; width = 12.0; height = 12.0; rx = 2.0
            stroke = "#6d8bff"; strokeWidth = 1.5; fill = "rgba(61,91,254,0.1)"
        }
        rect { x = 26.0; y = 26.0; width = 12.0; height = 12.0; rx = 2.0; stroke = "#3b5bfe"; strokeWidth = 1.5 }
        path {
            d = "M32 10 L32 16 L38 16"
            stroke = "#3b5bfe"; strokeWidth = 1.5; strokeLinecap = StrokeLinecap.round
        }
        circle {
            cx = 40.0; cy = 9.0; r = 4.0
            fill = "rgba(61,91,254,0.15)"; stroke = "#3b5bfe"; strokeWidth = 1.5
        }
        path {
            d = "M38.5 8 L39.5 10 L41 9"
            stroke = "#3b5bfe"; strokeWidth = 1.0; strokeLinecap = StrokeLinecap.round
        }
    }
}

val IconRoute = FC<Props> {
    svg {
        width = 48.0; height = 48.0; viewBox = "0 0 48 48"; fill = "none"
        circle { cx = 24.0; cy = 24.0; r = 14.0; stroke = "#3b5bfe"; strokeWidth = 1.5; strokeDasharray = "3 3" }
        circle {
            cx = 24.0; cy = 24.0; r = 5.0
            stroke = "#3b5bfe"; strokeWidth = 1.5; fill = "rgba(61,91,254,0.08)"
        }
        circle { cx = 24.0; cy = 10.0; r = 2.5; fill = "#3b5bfe" }
        circle { cx = 38.0; cy = 24.0; r = 2.5; fill = "#6d8bff" }
        circle { cx = 24.0; cy = 38.0; r = 2.5; fill = "#3b5bfe" }
        circle { cx = 10.0; cy = 24.0; r = 2.5; fill = "#6d8bff" }
        path { d = "M24 14 L24 19"; stroke = "#3b5bfe"; strokeWidth = 1.5 }
        path { d = "M29 24 L34 24"; stroke = "#3b5bfe"; strokeWidth = 1.5 }
    }
}

// ---------------------------------------------------------------------------
// Feature data + card
// ---------------------------------------------------------------------------

private data class Feature(
    val overline: String,
    val icon: FC<Props>,
    val title: String,
    val desc: String,
)

private val FEATURES = listOf(
    Feature("LOG 01", IconVessel, "Vessel Matching",
        "Instantly compare available vessels against your cargo specs and delivery window — no more manual spreadsheet rounds."),
    Feature("LOG 02", IconRate, "Rate Intelligence",
        "Track freight rate shifts across routes in real time. Catch the window before the market moves."),
    Feature("LOG 03", IconRisk, "Vendor Risk",
        "Surface vendor reliability and compliance flags before you commit. Flag issues, not after the fact."),
    Feature("LOG 04", IconRoute, "Route Optimization",
        "Simulate port congestion and transit time to pick the safest route — every time, not just when things go wrong."),
)

external interface FeatureCardProps : Props {
    var overline: String?
    var icon: FC<Props>?
    var title: String?
    var desc: String?
    var index: Int?
}

private val FeatureCard = FC<FeatureCardProps> { props ->
    val (cardRef, inView) = useInViewOnce<HTMLElement>(60.0)
    val icon = props.icon ?: IconVessel

    div {
        ref = cardRef
        className = cls(
            "card-light rounded-2xl p-6 lg:p-8 group hover:shadow-xl hover:shadow-[#3b5bfe]/5 transition-all duration-300 hover:-translate-y-1 reveal-card-sm",
            if (inView) "revealed" else "",
        )
        style = styleOf("transitionDelay" to "${(props.index ?: 0) * 0.12}s")

        div {
            className = ClassName("mb-5")
            span {
                className = ClassName("text-[10px] font-semibold tracking-[0.15em] text-[#3b5bfe] uppercase")
                +(props.overline ?: "")
            }
        }
        div {
            className = ClassName("mb-5 opacity-80 group-hover:opacity-100 transition-opacity duration-300")
            icon()
        }
        h3 {
            className = ClassName("text-lg font-semibold text-[#12142b] mb-2.5")
            +(props.title ?: "")
        }
        p {
            className = ClassName("text-sm text-[#4b4f66] leading-relaxed")
            +(props.desc ?: "")
        }
    }
}

// ---------------------------------------------------------------------------
// Section 2 — "Built for the decisions that actually move cargo"
// ---------------------------------------------------------------------------

val Features = FC {
    val (headlineRef, headlineInView) = useInViewOnce<HTMLElement>(60.0)

    section {
        className = ClassName("relative bg-[#eef1fb] py-24 lg:py-32")
        div {
            className = ClassName("max-w-7xl mx-auto px-6")

            // Section header
            div {
                ref = headlineRef
                className = ClassName("text-center mb-16 lg:mb-20")
                h2 {
                    className = ClassName("text-4xl sm:text-5xl lg:text-6xl font-bold leading-[1.08] tracking-tight mb-6")
                    span {
                        className = cls("text-[#12142b] block blur-in", if (headlineInView) "revealed" else "")
                        +"Built for the decisions"
                    }
                    span {
                        className = cls("gradient-text block mt-1 blur-in", if (headlineInView) "revealed" else "")
                        +"that actually move cargo"
                    }
                }
                p {
                    className = cls("text-[#4b4f66] text-lg sm:text-xl max-w-2xl mx-auto blur-in", if (headlineInView) "revealed" else "")
                    style = styleOf("transitionDelay" to "0.2s")
                    +"Veronica clears the noise so your procurement team can focus on the shipments that matter."
                }
            }

            // Feature grid
            div {
                className = ClassName("grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5")
                FEATURES.forEachIndexed { i, feature ->
                    div {
                        key = Key(i)
                        FeatureCard {
                            overline = feature.overline
                            icon = feature.icon
                            title = feature.title
                            desc = feature.desc
                            index = i
                        }
                    }
                }
            }
        }

        // Bottom gradient into white
        div {
            className = ClassName("absolute bottom-0 left-0 right-0 h-24 bg-gradient-to-b from-[#eef1fb] to-white pointer-events-none")
        }
    }
}
