package veronica.components

import react.FC
import react.Key
import react.Props
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.br
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.footer
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.section
import react.dom.svg.ReactSVG.polyline
import react.dom.svg.ReactSVG.svg
import react.dom.svg.StrokeLinecap
import react.dom.svg.StrokeLinejoin
import veronica.core.cls
import veronica.core.styleOf
import veronica.core.useInViewOnce
import web.html.HTMLElement
import web.cssom.ClassName

// ---------------------------------------------------------------------------
// Sparkline mini chart
// ---------------------------------------------------------------------------

external interface SparklineProps : Props {
    var values: Array<Double>?
    var color: String?
}

val Sparkline = FC<SparklineProps> { props ->
    val values = props.values ?: arrayOf(40.0, 55.0, 45.0, 70.0, 60.0, 80.0, 75.0)
    val color = props.color ?: "#3b5bfe"
    val max = values.maxOrNull() ?: 1.0
    val min = values.minOrNull() ?: 0.0
    val range = max - min
    val h = 28.0
    val w = 80.0
    val pts = values.mapIndexed { i, v ->
        val x = (i / (values.size - 1.0)) * w
        val y = h - ((v - min) / range) * h
        "$x,$y"
    }.joinToString(" ")

    svg {
        width = w; height = h; viewBox = "0 0 $w $h"; fill = "none"
        polyline {
            points = pts
            stroke = color
            strokeWidth = 1.5
            strokeLinecap = StrokeLinecap.round
            strokeLinejoin = StrokeLinejoin.round
        }
    }
}

// ---------------------------------------------------------------------------
// Mini bar chart (decision time trend)
// ---------------------------------------------------------------------------

private val MiniBarChart = FC<Props> {
    val bars = listOf(65, 82, 71, 90, 78, 95, 88, 100)
    val max = (bars.maxOrNull() ?: 100)

    div {
        className = ClassName("flex items-end gap-1 h-10")
        bars.forEachIndexed { i, bar ->
            div {
                key = Key(i)
                className = ClassName("w-4 bg-[#3b5bfe]/20 rounded-sm overflow-hidden")
                div {
                    className = ClassName("w-full bg-[#3b5bfe] rounded-sm transition-all duration-500")
                    style = styleOf("height" to "${(bar / max.toDouble()) * 100}%")
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Status chip
// ---------------------------------------------------------------------------

external interface StatusChipProps : Props {
    var label: String?
    var variant: String?
}

private val StatusChip = FC<StatusChipProps> { props ->
    val styles = mapOf(
        "escalated" to "text-red-500 bg-red-500/10 border-red-500/20",
        "resolved" to "text-emerald-500 bg-emerald-500/10 border-emerald-500/20",
        "monitoring" to "text-[#3b5bfe] bg-[#3b5bfe]/10 border-[#3b5bfe]/20",
        "default" to "text-[#4b4f66] bg-black/5 border-black/5",
    )
    span {
        className = cls(
            "text-[10px] font-semibold tracking-wider px-2 py-0.5 rounded-full border",
            styles[props.variant ?: "default"] ?: styles.getValue("default"),
        )
        +(props.label ?: "")
    }
}

// ---------------------------------------------------------------------------
// Data used by the digest card
// ---------------------------------------------------------------------------

private data class DigestItem(
    val title: String,
    val status: String,
    val statusVariant: String,
    val detail: String,
    val time: String,
)

private val DIGEST_ITEMS = listOf(
    DigestItem(
        title = "Delayed berth allocation — Port of Chennai",
        status = "Escalated",
        statusVariant = "escalated",
        detail = "Berth 7 slot pushed to +48h. Auto-escalated to 3 vendors. Rerouting simulation running.",
        time = "2h ago",
    ),
    DigestItem(
        title = "Freight rate spike — IHC W. Africa route",
        status = "Monitoring",
        statusVariant = "monitoring",
        detail = "Rate up 18% in 72h. Benchmark alert fired. Procurement team notified.",
        time = "5h ago",
    ),
    DigestItem(
        title = "Vendor compliance updated — TransOcean Lines",
        status = "Resolved",
        statusVariant = "resolved",
        detail = "Flag cleared after doc resubmission. Risk score revised: 87 → 94.",
        time = "1d ago",
    ),
)

private val DECISION_CATEGORIES = listOf("Vessel Selection", "Rate Lock", "Vendor Approval", "Route Approval")
private val DECISION_HOURS = listOf("3.1h", "5.4h", "2.8h", "5.5h")

// ---------------------------------------------------------------------------
// Section 3 — digest dashboard + CTA + footer
// ---------------------------------------------------------------------------

val Dashboard = FC {
    val (sectionRef, isInView) = useInViewOnce<HTMLElement>(80.0)

    section {
        ref = sectionRef
        className = ClassName("relative bg-white py-24 lg:py-32 overflow-hidden")
        div {
            className = ClassName("max-w-7xl mx-auto px-6")

            // Section header
            div {
                className = ClassName("mb-16 lg:mb-20")
                h2 {
                    className = ClassName("text-4xl sm:text-5xl lg:text-6xl font-bold leading-[1.08] tracking-tight mb-6")
                    span {
                        className = cls("text-[#12142b] block blur-in", if (isInView) "revealed" else "")
                        +"Your team already knows"
                    }
                    span {
                        className = cls("gradient-text block mt-1 blur-in", if (isInView) "revealed" else "")
                        +"which shipment is at risk"
                    }
                }
                p {
                    className = cls("text-[#4b4f66] text-lg sm:text-xl max-w-2xl mb-6 blur-in", if (isInView) "revealed" else "")
                    style = styleOf("transitionDelay" to "0.2s")
                    +"Veronica turns that instinct into a decision — automatically, the moment the data shifts."
                }
                a {
                    href = "#"
                    className = cls("inline-flex items-center gap-1.5 text-[#3b5bfe] font-medium text-sm group fade-in", if (isInView) "revealed" else "")
                    style = styleOf("transitionDelay" to "0.35s")
                    +"Signal received"
                    ArrowRight {
                        size = 14.0
                        className = cls("group-hover:translate-x-1 transition-transform duration-200")
                    }
                }
            }

            // Cards layout — 5 column grid
            div {
                className = ClassName("grid grid-cols-1 lg:grid-cols-5 gap-6 lg:gap-5")

                // Main digest card (3 cols)
                div {
                    className = cls(
                        "lg:col-span-3 card-light rounded-2xl p-6 lg:p-8 shadow-lg reveal-card",
                        if (isInView) "revealed" else "",
                    )
                    style = styleOf("transitionDelay" to "0s")

                    div {
                        className = ClassName("flex items-center justify-between mb-6")
                        div {
                            h3 {
                                className = ClassName("text-base font-semibold text-[#12142b]")
                                +"Weekly Procurement Digest"
                            }
                            p {
                                className = ClassName("text-[#4b4f66] text-xs mt-0.5")
                                +"Sep 1 – Sep 7, 2026"
                            }
                        }
                        div {
                            className = ClassName("text-[10px] font-semibold tracking-wider text-[#4b4f66] bg-[#eef1fb] px-2.5 py-1 rounded-full")
                            +"WEEK 36"
                        }
                    }

                    div {
                        className = ClassName("space-y-4")
                        DIGEST_ITEMS.forEachIndexed { i, item ->
                            div {
                                key = Key(i)
                                className = ClassName("flex items-start gap-4 p-4 rounded-xl bg-[#f8f9fc] border border-black/5")
                                div {
                                    className = ClassName("mt-0.5")
                                    StatusChip {
                                        label = item.status
                                        variant = item.statusVariant
                                    }
                                }
                                div {
                                    className = ClassName("flex-1 min-w-0")
                                    div {
                                        className = ClassName("flex items-center gap-2")
                                        h4 {
                                            className = ClassName("text-sm font-medium text-[#12142b] truncate")
                                            +item.title
                                        }
                                        span {
                                            className = ClassName("text-[10px] text-[#4b4f66]/70 flex-shrink-0")
                                            +item.time
                                        }
                                    }
                                    p {
                                        className = ClassName("text-xs text-[#4b4f66] mt-1 leading-relaxed")
                                        +item.detail
                                    }
                                }
                                AlertTriangle {
                                    size = 14.0
                                    className = cls("text-[#3b5bfe]/40 flex-shrink-0 mt-1")
                                }
                            }
                        }
                    }
                }

                // Right column — 2 smaller cards
                div {
                    className = ClassName("lg:col-span-2 flex flex-col gap-5")

                    // Decision time card
                    div {
                        className = cls("card-light rounded-2xl p-6 shadow-md reveal-card", if (isInView) "revealed" else "")
                        style = styleOf("transitionDelay" to "0.15s")

                        div {
                            className = ClassName("flex items-center justify-between mb-4")
                            div {
                                h3 {
                                    className = ClassName("text-sm font-semibold text-[#12142b]")
                                    +"Decision time by category"
                                }
                                p {
                                    className = ClassName("text-[10px] text-[#4b4f66] mt-0.5")
                                    +"Avg. hours to procurement decision"
                                }
                            }
                            TrendingUp {
                                size = 16.0
                                className = cls("text-[#3b5bfe]")
                            }
                        }

                        div {
                            className = ClassName("flex items-end gap-3 mb-4")
                            span {
                                className = ClassName("text-3xl font-bold text-[#12142b]")
                                +"4.2"
                            }
                            span {
                                className = ClassName("text-xs text-[#4b4f66] mb-1")
                                +"hrs avg"
                            }
                            div {
                                className = ClassName("flex items-center gap-1 text-emerald-500 text-xs font-medium mb-1 ml-auto")
                                ChevronUp { size = 12.0 }
                                +"18%"
                            }
                        }

                        MiniBarChart()

                        div {
                            className = ClassName("mt-3 space-y-2")
                            DECISION_CATEGORIES.forEachIndexed { i, label ->
                                div {
                                    key = Key(label)
                                    className = ClassName("flex items-center justify-between text-xs")
                                    span {
                                        className = ClassName("text-[#4b4f66]")
                                        +label
                                    }
                                    span {
                                        className = ClassName("text-[#12142b] font-medium")
                                        +DECISION_HOURS[i]
                                    }
                                }
                            }
                        }
                    }

                    // Cost report card
                    div {
                        className = cls("card-light rounded-2xl p-6 shadow-md reveal-card", if (isInView) "revealed" else "")
                        style = styleOf("transitionDelay" to "0.3s")

                        div {
                            className = ClassName("flex items-center justify-between mb-5")
                            div {
                                h3 {
                                    className = ClassName("text-sm font-semibold text-[#12142b]")
                                    +"Cost Report"
                                }
                                p {
                                    className = ClassName("text-[10px] text-[#4b4f66] mt-0.5")
                                    +"This month vs. last month"
                                }
                            }
                            Clock3 {
                                size = 16.0
                                className = cls("text-[#3b5bfe]")
                            }
                        }

                        div {
                            className = ClassName("grid grid-cols-2 gap-4")
                            div {
                                className = ClassName("bg-red-50/60 rounded-xl p-4 border border-red-100")
                                div {
                                    className = ClassName("text-[10px] font-semibold tracking-wider text-red-400 uppercase mb-1")
                                    +"Escalated"
                                }
                                div {
                                    className = ClassName("text-2xl font-bold text-[#12142b]")
                                    +"$2.4M"
                                }
                                div {
                                    className = ClassName("flex items-center gap-1 mt-1 text-red-400 text-[10px]")
                                    TrendingUp { size = 10.0 }
                                    +"+12%"
                                }
                            }
                            div {
                                className = ClassName("bg-emerald-50/60 rounded-xl p-4 border border-emerald-100")
                                div {
                                    className = ClassName("text-[10px] font-semibold tracking-wider text-emerald-500 uppercase mb-1")
                                    +"Contained"
                                }
                                div {
                                    className = ClassName("text-2xl font-bold text-[#12142b]")
                                    +"$8.1M"
                                }
                                div {
                                    className = ClassName("flex items-center gap-1 mt-1 text-emerald-500 text-[10px]")
                                    CheckCircle { size = 10.0 }
                                    +"+6%"
                                }
                            }
                        }

                        div {
                            className = ClassName("mt-5 pt-4 border-t border-black/5")
                            div {
                                className = ClassName("flex items-center justify-between")
                                span {
                                    className = ClassName("text-xs text-[#4b4f66]")
                                    +"Auto-savings this month"
                                }
                                span {
                                    className = ClassName("text-sm font-semibold text-[#3b5bfe]")
                                    +"$340K"
                                }
                            }
                            div {
                                className = ClassName("mt-2 flex items-center gap-2")
                                Sparkline {
                                    values = arrayOf(30.0, 45.0, 38.0, 55.0, 50.0, 65.0, 60.0, 72.0)
                                    color = "#3b5bfe"
                                }
                                span {
                                    className = ClassName("text-[10px] text-[#4b4f66]")
                                    +"vs. avg."
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom CTA strip
        div {
            className = cls("mt-24 lg:mt-32 py-16 px-6 fade-in", if (isInView) "revealed" else "")
            style = styleOf("transitionDelay" to "0.8s")
            div {
                className = ClassName("max-w-4xl mx-auto text-center")
                h2 {
                    className = ClassName("text-3xl sm:text-4xl lg:text-5xl font-bold text-[#12142b] mb-5 leading-tight")
                    +"Ready to clear the noise"
                    br()
                    span {
                        className = ClassName("gradient-text")
                        +"before the next shipment hits?"
                    }
                }
                p {
                    className = ClassName("text-[#4b4f66] text-lg mb-8 max-w-xl mx-auto")
                    +"Join SAIL's procurement team and get early access to Veronica — built for the decisions that move the world."
                }
                div {
                    className = ClassName("flex flex-col sm:flex-row gap-4 justify-center")
                    button {
                        className = ClassName("btn-primary text-white font-semibold px-8 py-3.5 rounded-xl flex items-center gap-2 text-base group mx-auto sm:mx-0")
                        +"Request Access"
                        ArrowRight {
                            size = 16.0
                            className = cls("group-hover:translate-x-1 transition-transform duration-200")
                        }
                    }
                    button {
                        className = ClassName("px-8 py-3.5 rounded-xl border border-[#3b5bfe]/30 text-[#3b5bfe] font-semibold text-base hover:bg-[#3b5bfe]/5 transition-colors duration-200 mx-auto sm:mx-0")
                        +"Talk to Sales"
                    }
                }
            }
        }

        // Footer
        footer {
            className = ClassName("mt-16 pt-8 border-t border-black/5 text-center text-xs text-[#4b4f66]/60")
            div {
                className = ClassName("flex items-center justify-center gap-2 mb-2")
                ShipLogo { size = 18.0 }
                span {
                    className = ClassName("text-[#3b5bfe] font-medium")
                    +"Veronica"
                }
            }
            p {
                +"© 2026 SAIL Shipping & Cargo. All rights reserved."
            }
            div {
                className = ClassName("flex justify-center gap-4 mt-3")
                listOf("Privacy", "Terms", "Security", "Contact").forEach { label ->
                    a {
                        key = Key(label)
                        href = "#"
                        className = ClassName("hover:text-[#3b5bfe] transition-colors")
                        +label
                    }
                }
            }
        }
    }
}