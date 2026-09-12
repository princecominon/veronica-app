package cargonex.components

import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.fetch.RequestInit
import react.FC
import react.Key
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.input
import web.html.InputType
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.style
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.th
import react.dom.html.ReactHTML.thead
import react.dom.html.ReactHTML.tr
import react.useEffect
import react.useState
import react.dom.svg.ReactSVG.path
import react.dom.svg.ReactSVG.svg
import cargonex.core.styleOf
import web.cssom.ClassName
import kotlin.js.JSON
import kotlin.js.json

external interface FreightAuctionProps : Props {
    var onNavigate: ((String) -> Unit)?
}

val FreightAuctionScreen = FC<FreightAuctionProps> { props ->
    val (isLoading, setIsLoading) = useState(true)
    val (auctionData, setAuctionData) = useState<Any?>(null)
    val (accepted, setAccepted) = useState(false)
    val (showNotification, setShowNotification) = useState(false)
    val (cargoType, setCargoType) = useState("Dry Bulk")
    val (quantityMt, setQuantityMt) = useState("75000")
    val (origin, setOrigin) = useState("Australia")
    val (destination, setDestination) = useState("Paradip")

    fun loadAuction() {
        MainScope().launch {
            setIsLoading(true)
            setAccepted(false)
            try {
                val quantity =
                    quantityMt
                        .toIntOrNull()
                        ?.coerceIn(1000, 250000)
                        ?: 75000

                val url =
                    "http://localhost:8080/api/auction/bids" +
                        "?cargoType=$cargoType" +
                        "&quantityMt=$quantity" +
                        "&origin=$origin" +
                        "&destination=$destination"

                val requestOptions =
                    json("method" to "GET").unsafeCast<RequestInit>()

                val response =
                    window
                        .fetch(url, requestOptions)
                        .await()

                if (response.ok) {
                    val textResponse = response.text().await()
                    setAuctionData(JSON.parse<dynamic>(textResponse).unsafeCast<Any?>())
                } else {
                    console.log("Failed to fetch auction data")
                }
            } catch (e: Throwable) {
                console.log("Failed to fetch auction data")
            } finally {
                setIsLoading(false)
            }
        }
    }

    useEffect(emptyList<Any?>()) {
        loadAuction()
    }

    style {
        +"""
            @keyframes veronica-ai-glow {
                0%, 100% {
                    box-shadow:
                        0 0 20px rgba(16, 185, 129, 0.15),
                        inset 0 0 10px rgba(16, 185, 129, 0.05);
                }

                50% {
                    box-shadow:
                        0 0 40px rgba(16, 185, 129, 0.3),
                        inset 0 0 20px rgba(16, 185, 129, 0.1);
                }
            }

            @keyframes veronica-sheen-sweep {
                0% {
                    transform: translateX(-150%) skewX(-25deg);
                    opacity: 0;
                }

                20% {
                    opacity: 0.15;
                }

                50% {
                    opacity: 0;
                    transform: translateX(250%) skewX(-25deg);
                }

                100% {
                    transform: translateX(250%) skewX(-25deg);
                    opacity: 0;
                }
            }

            @keyframes veronica-toast-slide {
                0% {
                    transform: translateX(120%);
                    opacity: 0;
                }

                10% {
                    transform: translateX(0);
                    opacity: 1;
                }

                90% {
                    transform: translateX(0);
                    opacity: 1;
                }

                100% {
                    transform: translateX(120%);
                    opacity: 0;
                }
            }
        """.trimIndent()
    }

    div {
        className =
            ClassName(
                "min-h-screen bg-[#070b20] pt-24 px-6 font-sans text-white relative"
            )

        style =
            styleOf(
                "background" to
                    "radial-gradient(circle at 50% 38%, rgba(59,91,254,0.15), transparent 50%), linear-gradient(180deg, #080d28 0%, #070b20 100%)"
            )

        button {
            className =
                ClassName(
                    "absolute top-24 left-6 z-[100] sm:left-12 flex items-center gap-2 px-4 py-2 rounded-xl bg-[#0c1130]/80 hover:bg-[#3b5bfe]/40 text-[#8fa6ff] hover:text-white backdrop-blur-md transition-all text-sm font-medium border border-[#3b5bfe]/30 shadow-lg cursor-pointer"
                )

            onClick = {
                props.onNavigate?.invoke("home")
            }

            +"Back to Home"
        }

        div {
            className = ClassName("max-w-5xl mx-auto pt-16")

            div {
                className = ClassName("mb-10 text-center")

                h2 {
                    className =
                        ClassName(
                            "text-4xl font-bold tracking-tight mb-4"
                        )

                    +"Live Freight Auction"
                }

                p {
                    className = ClassName("text-[#8fa6ff] max-w-3xl mx-auto text-lg")

                    if (auctionData != null) {
                        val auction = auctionData.unsafeCast<dynamic>()
                        +"Cargo: ${auction.cargoRequirement} | Route: ${auction.route}"
                    } else {
                        +"Configure cargo and route to get a new AI-ranked auction recommendation."
                    }
                }
            }

            div {
                className =
                    ClassName(
                        "mb-10 p-6 rounded-2xl border border-white/10 bg-[#0c1130]/70 backdrop-blur-md"
                    )

                div {
                    className = ClassName("grid grid-cols-1 md:grid-cols-2 gap-5")

                    div {
                        className = ClassName("space-y-2")

                        span {
                            className = ClassName("text-xs font-semibold uppercase tracking-wider text-[#8fa6ff]")
                            +"Cargo Type"
                        }

                        input {
                            value = cargoType
                            onChange = { event -> setCargoType(event.target.value) }
                            className =
                                ClassName(
                                    "w-full px-4 py-3 rounded-xl bg-[#070b20] border border-white/10 focus:border-emerald-400/60 outline-none text-white placeholder:text-[#626b8f]"
                                )
                            placeholder = "Dry Bulk"
                        }
                    }

                    div {
                        className = ClassName("space-y-2")

                        span {
                            className = ClassName("text-xs font-semibold uppercase tracking-wider text-[#8fa6ff]")
                            +"Cargo Limit (MT)"
                        }

                        input {
                            value = quantityMt
                            onChange = { event -> setQuantityMt(event.target.value) }
                            className =
                                ClassName(
                                    "w-full px-4 py-3 rounded-xl bg-[#070b20] border border-white/10 focus:border-emerald-400/60 outline-none text-white"
                                )
                            placeholder = "75000"
                            type = "number".unsafeCast<InputType>()
                            min = 1000.0
                            max = 250000.0
                        }
                    }

                    div {
                        className = ClassName("space-y-2")

                        span {
                            className = ClassName("text-xs font-semibold uppercase tracking-wider text-[#8fa6ff]")
                            +"Origin"
                        }

                        input {
                            value = origin
                            onChange = { event -> setOrigin(event.target.value) }
                            className =
                                ClassName(
                                    "w-full px-4 py-3 rounded-xl bg-[#070b20] border border-white/10 focus:border-emerald-400/60 outline-none text-white placeholder:text-[#626b8f]"
                                )
                            placeholder = "Australia"
                        }
                    }

                    div {
                        className = ClassName("space-y-2")

                        span {
                            className = ClassName("text-xs font-semibold uppercase tracking-wider text-[#8fa6ff]")
                            +"Destination"
                        }

                        input {
                            value = destination
                            onChange = { event -> setDestination(event.target.value) }
                            className =
                                ClassName(
                                    "w-full px-4 py-3 rounded-xl bg-[#070b20] border border-white/10 focus:border-emerald-400/60 outline-none text-white placeholder:text-[#626b8f]"
                                )
                            placeholder = "Paradip"
                        }
                    }
                }

                div {
                    className = ClassName("mt-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4")

                    p {
                        className = ClassName("text-xs text-[#626b8f]")
                        +"Change the cargo limit or route, then apply to recalculate vessel fit, pricing and AI recommendation."
                    }

                    button {
                        className =
                            ClassName(
                                "px-6 py-3 rounded-xl bg-emerald-500 text-[#070b20] font-bold hover:bg-emerald-400 hover:shadow-[0_0_20px_rgba(16,185,129,0.35)] transition-all cursor-pointer"
                            )

                        onClick = {
                            loadAuction()
                        }

                        +"Apply Changes & Recalculate"
                    }
                }
            }

            if (isLoading) {
                div {
                    className =
                        ClassName(
                            "text-center text-[#8fa6ff] py-20 animate-pulse"
                        )

                    +"Recalculating AI-ranked bids..."
                }
            } else if (auctionData != null) {
                val auction = auctionData.unsafeCast<dynamic>()
                val bidsArray = auction.bids as Array<dynamic>

                if (bidsArray.isNotEmpty()) {
                    val bestBid = bidsArray.first()

                    div {
                        className =
                            ClassName(
                                "relative mb-10 p-6 rounded-2xl border border-emerald-500/50 bg-emerald-900/10 backdrop-blur-md flex flex-col md:flex-row items-center justify-between overflow-hidden"
                            )

                        style =
                            styleOf(
                                "animation" to
                                    "veronica-ai-glow 4s ease-in-out infinite"
                            )

                        div {
                            className =
                                ClassName(
                                    "absolute top-0 bottom-0 w-1/3 bg-gradient-to-r from-transparent via-emerald-400/20 to-transparent pointer-events-none"
                                )

                            style =
                                styleOf(
                                    "animation" to
                                        "veronica-sheen-sweep 6s ease-in-out infinite"
                                )
                        }

                        div {
                            className = ClassName("relative z-10 md:max-w-3xl")

                            h3 {
                                className =
                                    ClassName(
                                        "text-emerald-400 font-bold text-xl mb-2 flex items-center gap-3"
                                    )

                                span {
                                    className =
                                        ClassName(
                                            "w-2 h-2 rounded-full bg-emerald-400 animate-ping shadow-[0_0_10px_#34d399]"
                                        )
                                }

                                +"Recommendation" // Modified here
                            }

                            p {
                                className = ClassName("text-[#c7cbe0] leading-7")
                                +(auction.recommendation as String)
                            }

                            p {
                                className = ClassName("text-[#8fa6ff] text-sm mt-3")
                                +(auction.routeInsight as String)
                            }
                        }

                        div {
                            className =
                                ClassName(
                                    "relative z-10 mt-5 md:mt-0 md:ml-8 flex flex-col items-end gap-4 shrink-0"
                                )

                            div {
                                className = ClassName("text-right")

                                div {
                                    className =
                                        ClassName(
                                            "text-4xl font-extrabold tracking-tight bg-gradient-to-br from-white to-emerald-200 bg-clip-text text-transparent"
                                        )

                                    +"$${bestBid.rate}/MT"
                                }

                                div {
                                    className =
                                        ClassName(
                                            "text-emerald-400/80 text-sm font-medium mt-1 uppercase tracking-wider"
                                        )

                                    +"Best Match: ${bestBid.vessel}"
                                }
                            }

                            button {
                                className =
                                    ClassName(
                                        "px-6 py-2.5 rounded-xl font-bold text-sm transition-all duration-300 flex items-center justify-center gap-2 w-full sm:w-auto " +
                                            if (accepted) {
                                                "bg-emerald-500/20 text-emerald-300 border border-emerald-500/50 cursor-default shadow-[inset_0_0_15px_rgba(16,185,129,0.2)]"
                                            } else {
                                                "bg-emerald-500 text-[#070b20] hover:bg-emerald-400 hover:shadow-[0_0_20px_rgba(16,185,129,0.4)] cursor-pointer"
                                            }
                                    )

                                disabled = accepted

                                onClick = {
                                    setAccepted(true)
                                    setShowNotification(true)

                                    js("window").setTimeout({
                                        setShowNotification(false)
                                    }, 4000)
                                }

                                if (accepted) {
                                    svg {
                                        className = ClassName("w-4 h-4")
                                        viewBox = "0 0 24 24"
                                        fill = "currentColor"

                                        path {
                                            d =
                                                "M12 2a10 10 0 100 20 10 10 0 000-20zm4.03 7.03l-4.5 4.5a.75.75 0 01-1.06 0l-2.5-2.5a.75.75 0 111.06-1.06l1.97 1.97 3.97-3.97a.75.75 0 111.06 1.06z"
                                        }
                                    }

                                    +"Charter Secured"
                                } else {
                                    +"Accept Recommendation"
                                }
                            }
                        }
                    }

                    div {
                        className =
                            ClassName(
                                "overflow-hidden rounded-xl border border-white/10 bg-[#0c1130]/50 backdrop-blur-sm"
                            )

                        table {
                            className =
                                ClassName(
                                    "w-full text-left border-collapse"
                                )

                            thead {
                                className =
                                    ClassName(
                                        "bg-white/5 border-b border-white/10"
                                    )

                                tr {
                                    listOf(
                                        "Vendor",
                                        "Vessel",
                                        "Rate (USD/MT)",
                                        "ETA",
                                        "Risk Score"
                                    ).forEach { header ->
                                        th {
                                            className =
                                                ClassName(
                                                    "p-4 text-xs font-semibold text-[#8fa6ff] uppercase tracking-wider"
                                                )

                                            +header
                                        }
                                    }
                                }
                            }

                            tbody {
                                bidsArray.forEachIndexed { index, bid ->
                                    tr {
                                        key = Key(index.toString())

                                        className =
                                            ClassName(
                                                "border-b border-white/5 hover:bg-white/5 transition-colors"
                                            )

                                        td {
                                            className = ClassName("p-4 font-medium text-white")
                                            +(bid.vendor as String)
                                        }

                                        td {
                                            className = ClassName("p-4 text-[#c7cbe0]")
                                            +(bid.vessel as String)
                                        }

                                        td {
                                            className = ClassName("p-4 font-bold text-white")
                                            +"$${bid.rate}"
                                        }

                                        td {
                                            className = ClassName("p-4 text-[#c7cbe0]")
                                            +(bid.eta as String)
                                        }

                                        td {
                                            className = ClassName("p-4")

                                            val score = bid.score as Int

                                            span {
                                                className =
                                                    ClassName(
                                                        "px-3 py-1 rounded-full text-xs font-bold border ${
                                                            if (score > 90) {
                                                                "text-emerald-400 border-emerald-400/30 bg-emerald-400/10"
                                                            } else {
                                                                "text-yellow-400 border-yellow-400/30 bg-yellow-400/10"
                                                            }
                                                        }"
                                                    )

                                                +"$score"
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    div {
                        className = ClassName("p-8 rounded-2xl border border-orange-400/30 bg-orange-400/5 text-center")

                        h3 {
                            className = ClassName("text-orange-300 font-bold text-xl mb-2")
                            +"No Vessel Match"
                        }

                        p {
                            className = ClassName("text-[#c7cbe0]")
                            +(auction.recommendation as String)
                        }
                    }
                }
            }
        }

        if (showNotification && auctionData != null) {
            val auction = auctionData.unsafeCast<dynamic>()
            val bestBid =
                (auction.bids as Array<dynamic>).firstOrNull()

            if (bestBid != null) {
                div {
                    className =
                        ClassName(
                            "fixed bottom-10 right-6 sm:right-10 z-[200] flex items-center gap-4 p-4 pr-6 rounded-2xl border border-emerald-500/40 bg-[#0a1130]/95 backdrop-blur-xl shadow-[0_20px_50px_rgba(0,0,0,0.5),0_0_30px_rgba(16,185,129,0.15)]"
                        )

                    style =
                        styleOf(
                            "animation" to
                                "veronica-toast-slide 4s cubic-bezier(0.22, 1, 0.36, 1) forwards"
                        )

                    div {
                        className =
                            ClassName(
                                "flex-shrink-0 w-12 h-12 rounded-full bg-emerald-500/20 border border-emerald-500/30 flex items-center justify-center text-emerald-400"
                            )

                        svg {
                            className = ClassName("w-6 h-6")
                            viewBox = "0 0 24 24"
                            fill = "currentColor"

                            path {
                                d =
                                    "M12 2a10 10 0 100 20 10 10 0 000-20zm4.03 7.03l-4.5 4.5a.75.75 0 01-1.06 0l-2.5-2.5a.75.75 0 111.06-1.06l1.97 1.97 3.97-3.97a.75.75 0 111.06 1.06z"
                            }
                        }
                    }

                    div {
                        h4 {
                            className = ClassName("text-white font-bold text-sm")
                            +"Operations Notified"
                        }

                        p {
                            className = ClassName("text-[#8fa6ff] text-xs mt-1")
                            +"Secured ${bestBid.vessel} at $${bestBid.rate}/MT. Workflow initiated."
                        }
                    }
                }
            }
        }
    }
}