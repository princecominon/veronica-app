package veronica.components

import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import react.FC
import react.Key
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.style
import react.useEffect
import react.useState
import veronica.core.styleOf
import web.cssom.ClassName
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Headers
import kotlin.js.JSON
import kotlin.js.json

private data class MatchResultUi(
    val vesselName: String,
    val type: String,
    val capacity: String,
    val eta: String,
    val score: Int
)

external interface CargoMatchingProps : Props {
    var onNavigate: ((String) -> Unit)?
}

val CargoMatchingScreen = FC<CargoMatchingProps> { props ->
    val (cargoType, setCargoType) = useState("Iron Ore")
    val (quantity, setQuantity) = useState("72000")
    val (origin, setOrigin) = useState("Australia")
    val (destination, setDestination) = useState("Paradip")
    val (laycanStart, setLaycanStart) = useState("15 Oct")
    val (laycanEnd, setLaycanEnd) = useState("22 Oct")
    
    val (isProcessing, setIsProcessing) = useState(false)
    val (matches, setMatches) = useState<List<MatchResultUi>>(emptyList())
    
    // Naye State variables Charter Modal ke liye
    val (selectedCharter, setSelectedCharter) = useState<MatchResultUi?>(null)
    val (isSecuring, setIsSecuring) = useState(false)

    val handleMatch = {
        MainScope().launch {
            setIsProcessing(true)
            setMatches(emptyList())
            try {
                val requestBody = json(
                    "cargo" to cargoType,
                    "quantityMt" to (quantity.toIntOrNull() ?: 0),
                    "origin" to origin,
                    "destination" to destination,
                    "laycanStart" to laycanStart,
                    "laycanEnd" to laycanEnd
                )

                val headers = Headers()
                headers.append("Content-Type", "application/json")

                val requestOptions = json(
                    "method" to "POST",
                    "headers" to headers,
                    "body" to JSON.stringify(requestBody)
                ).unsafeCast<RequestInit>()

                val response = window.fetch(
                    "http://localhost:8080/api/cargo-match",
                    requestOptions
                ).await()

                if (response.ok) {
                    val textResponse = response.text().await()
                    val data = JSON.parse<Array<dynamic>>(textResponse)
                    val realMatches = data.map { item ->
                        MatchResultUi(
                            vesselName = item.vesselName as String,
                            type = item.vesselType as String,
                            capacity = item.capacity as String,
                            eta = item.eta as String,
                            score = item.suitability as Int
                        )
                    }
                    setMatches(realMatches)
                } else {
                    console.log("Error from server: ${response.status}")
                }
            } catch (e: Throwable) {
                console.log("Network error: ${e.message}")
            } finally {
                setIsProcessing(false)
            }
        }
    }

    style {
        +"""
            @keyframes scan-line {
                0% { top: 0%; opacity: 0; }
                10% { opacity: 1; }
                90% { opacity: 1; }
                100% { top: 100%; opacity: 0; }
            }
            @keyframes pulse-glow {
                0%, 100% { box-shadow: 0 0 20px rgba(59,91,254,0.2); }
                50% { box-shadow: 0 0 50px rgba(59,91,254,0.6); }
            }
            @keyframes float-3d {
                0%, 100% { transform: translateY(0px) rotateX(2deg) rotateY(-2deg); }
                50% { transform: translateY(-12px) rotateX(-2deg) rotateY(2deg); }
            }
            @keyframes orb-spin {
                0% { transform: rotate(0deg) scale(1); }
                50% { transform: rotate(180deg) scale(1.1); }
                100% { transform: rotate(360deg) scale(1); }
            }
        """.trimIndent()
    }

    div {
        className = ClassName(
            "min-h-screen bg-[#050816] text-white relative overflow-hidden font-sans pb-24"
        )

        div {
            className = ClassName(
                "absolute inset-0 pointer-events-none opacity-20"
            )
            style = styleOf(
                "backgroundImage" to "linear-gradient(rgba(59,91,254,0.2) 1px, transparent 1px), linear-gradient(90deg, rgba(59,91,254,0.2) 1px, transparent 1px)",
                "backgroundSize" to "80px 80px",
                "transform" to "perspective(1000px) rotateX(60deg) translateY(-100px) translateZ(-200px)",
                "transformOrigin" to "top center"
            )
        }

        div {
            className = ClassName(
                "absolute top-[10%] left-[20%] w-96 h-96 bg-[#3b5bfe]/20 rounded-full blur-[120px] pointer-events-none"
            )
            style = styleOf(
                "animation" to "orb-spin 20s infinite linear"
            )
        }

        div {
            className = ClassName(
                "absolute bottom-[10%] right-[10%] w-[500px] h-[500px] bg-[#6d8bff]/15 rounded-full blur-[150px] pointer-events-none"
            )
            style = styleOf(
                "animation" to "orb-spin 25s infinite reverse linear"
            )
        }

        button {
            className = ClassName(
                "absolute top-24 left-6 sm:left-12 z-50 flex items-center gap-2 px-5 py-2.5 rounded-xl " +
                    "bg-[#0c1130]/80 hover:bg-[#3b5bfe]/30 text-[#8fa6ff] hover:text-white " +
                    "backdrop-blur-xl transition-all border border-[#3b5bfe]/30 shadow-lg cursor-pointer"
            )
            onClick = {
                props.onNavigate?.invoke("home")
            }
            +"Back to Home"
        }

        div {
            className = ClassName(
                "relative z-10 max-w-6xl mx-auto pt-32 px-6"
            )

            div {
                className = ClassName("text-center mb-16")
                p {
                    className = ClassName(
                        "text-[#6d8bff] text-xs uppercase tracking-[0.4em] font-bold mb-3"
                    )
                    +"AI Routing Module"
                }
                h1 {
                    className = ClassName(
                        "text-5xl md:text-6xl font-extrabold tracking-tight " +
                            "bg-gradient-to-r from-white via-[#dfe5ff] to-[#6d8bff] " +
                            "bg-clip-text text-transparent " +
                            "drop-shadow-[0_0_30px_rgba(109,139,255,0.3)]"
                    )
                    +"Cargo Matching Engine"
                }
                p {
                    className = ClassName(
                        "mt-4 text-[#8fa6ff]/80 text-lg max-w-2xl mx-auto"
                    )
                    +"Instantly calculate 5-dimensional compatibility across Cargo, Route, Vessel, Port Draft, and Time."
                }
            }

            div {
                className = ClassName(
                    "flex flex-col lg:flex-row gap-10 lg:gap-16 items-start"
                )

                // Input Parameters Panel
                div {
                    className = ClassName(
                        "w-full lg:w-[45%] rounded-[32px] border border-[#3b5bfe]/30 " +
                            "bg-[#0a1130]/60 backdrop-blur-2xl p-8 " +
                            "shadow-[0_20px_60px_rgba(0,0,0,0.5),inset_0_0_40px_rgba(59,91,254,0.05)] " +
                            "relative overflow-hidden group"
                    )
                    style = styleOf(
                        "animation" to "float-3d 8s ease-in-out infinite"
                    )

                    div {
                        className = ClassName(
                            "absolute left-0 right-0 h-[2px] bg-gradient-to-r " +
                                "from-transparent via-[#7fa8ff] to-transparent opacity-0 " +
                                "group-hover:opacity-100"
                        )
                        style = styleOf(
                            "animation" to "scan-line 3s linear infinite"
                        )
                    }

                    h2 {
                        className = ClassName(
                            "text-2xl font-bold text-white mb-8 flex items-center gap-3"
                        )
                        div {
                            className = ClassName(
                                "w-2 h-8 bg-[#3b5bfe] rounded-full shadow-[0_0_15px_#3b5bfe]"
                            )
                        }
                        +"Cargo Parameters"
                    }

                    div {
                        className = ClassName("space-y-6")

                        div {
                            className = ClassName(
                                "grid grid-cols-2 gap-4"
                            )
                            div {
                                p {
                                    className = ClassName(
                                        "text-[10px] uppercase tracking-wider text-[#8fa6ff]/70 mb-2 font-semibold"
                                    )
                                    +"Commodity"
                                }
                                input {
                                    className = ClassName(
                                        "w-full px-4 py-3.5 rounded-xl border border-[#3b5bfe]/20 " +
                                            "bg-black/40 text-white focus:outline-none " +
                                            "focus:border-[#7fa8ff] transition-all shadow-inner"
                                    )
                                    value = cargoType
                                    onChange = {
                                        setCargoType(it.target.value)
                                    }
                                }
                            }
                            div {
                                p {
                                    className = ClassName(
                                        "text-[10px] uppercase tracking-wider text-[#8fa6ff]/70 mb-2 font-semibold"
                                    )
                                    +"Quantity (MT)"
                                }
                                input {
                                    className = ClassName(
                                        "w-full px-4 py-3.5 rounded-xl border border-[#3b5bfe]/20 " +
                                            "bg-black/40 text-white focus:outline-none " +
                                            "focus:border-[#7fa8ff] transition-all shadow-inner"
                                    )
                                    value = quantity
                                    onChange = {
                                        setQuantity(it.target.value)
                                    }
                                }
                            }
                        }

                        div {
                            className = ClassName(
                                "grid grid-cols-2 gap-4"
                            )
                            div {
                                p {
                                    className = ClassName(
                                        "text-[10px] uppercase tracking-wider text-[#8fa6ff]/70 mb-2 font-semibold"
                                    )
                                    +"Origin"
                                }
                                input {
                                    className = ClassName(
                                        "w-full px-4 py-3.5 rounded-xl border border-[#3b5bfe]/20 " +
                                            "bg-black/40 text-white focus:outline-none " +
                                            "focus:border-[#7fa8ff] transition-all shadow-inner"
                                    )
                                    value = origin
                                    onChange = {
                                        setOrigin(it.target.value)
                                    }
                                }
                            }
                            div {
                                p {
                                    className = ClassName(
                                        "text-[10px] uppercase tracking-wider text-[#8fa6ff]/70 mb-2 font-semibold"
                                    )
                                    +"Destination"
                                }
                                input {
                                    className = ClassName(
                                        "w-full px-4 py-3.5 rounded-xl border border-[#3b5bfe]/20 " +
                                            "bg-black/40 text-white focus:outline-none " +
                                            "focus:border-[#7fa8ff] transition-all shadow-inner"
                                    )
                                    value = destination
                                    onChange = {
                                        setDestination(it.target.value)
                                    }
                                }
                            }
                        }

                        div {
                            className = ClassName(
                                "grid grid-cols-2 gap-4"
                            )
                            div {
                                p {
                                    className = ClassName(
                                        "text-[10px] uppercase tracking-wider text-[#8fa6ff]/70 mb-2 font-semibold"
                                    )
                                    +"Laycan Start"
                                }
                                input {
                                    className = ClassName(
                                        "w-full px-4 py-3.5 rounded-xl border border-[#3b5bfe]/20 " +
                                            "bg-black/40 text-white focus:outline-none " +
                                            "focus:border-[#7fa8ff] transition-all shadow-inner"
                                    )
                                    value = laycanStart
                                    onChange = {
                                        setLaycanStart(it.target.value)
                                    }
                                }
                            }
                            div {
                                p {
                                    className = ClassName(
                                        "text-[10px] uppercase tracking-wider text-[#8fa6ff]/70 mb-2 font-semibold"
                                    )
                                    +"Laycan End"
                                }
                                input {
                                    className = ClassName(
                                        "w-full px-4 py-3.5 rounded-xl border border-[#3b5bfe]/20 " +
                                            "bg-black/40 text-white focus:outline-none " +
                                            "focus:border-[#7fa8ff] transition-all shadow-inner"
                                    )
                                    value = laycanEnd
                                    onChange = {
                                        setLaycanEnd(it.target.value)
                                    }
                                }
                            }
                        }

                        button {
                            className = ClassName(
                                "w-full mt-6 py-4 rounded-xl font-bold text-lg text-white " +
                                    "transition-all cursor-pointer relative overflow-hidden " +
                                    if (isProcessing) {
                                        "bg-[#1a2b82]"
                                    } else {
                                        "bg-gradient-to-r from-[#1d2bd8] to-[#3b5bfe] " +
                                            "hover:shadow-[0_0_30px_rgba(59,91,254,0.6)]"
                                    }
                            )
                            onClick = {
                                handleMatch()
                            }
                            disabled = isProcessing

                            if (isProcessing) {
                                span {
                                    className = ClassName(
                                        "flex items-center justify-center gap-3"
                                    )
                                    div {
                                        className = ClassName(
                                            "w-5 h-5 border-2 border-white/30 " +
                                                "border-t-white rounded-full animate-spin"
                                        )
                                    }
                                    +"Synchronizing Dimensions..."
                                }
                            } else {
                                +"Run Matching Engine"
                            }
                        }
                    }
                }

                // Results Panel
                div {
                    className = ClassName(
                        "w-full lg:w-[55%] flex flex-col gap-6"
                    )

                    if (isProcessing) {
                        div {
                            className = ClassName(
                                "flex-1 min-h-[400px] flex flex-col items-center justify-center " +
                                    "rounded-[32px] border border-[#3b5bfe]/10 " +
                                    "bg-[#0a1130]/30 backdrop-blur-md"
                            )
                            div {
                                className = ClassName(
                                    "relative w-32 h-32 mb-8"
                                )
                                style = styleOf(
                                    "animation" to "pulse-glow 2s infinite"
                                )
                                div {
                                    className = ClassName(
                                        "absolute inset-0 rounded-full border-4 " +
                                            "border-[#3b5bfe] border-l-transparent " +
                                            "animate-[spin_1.5s_linear_infinite]"
                                    )
                                }
                                div {
                                    className = ClassName(
                                        "absolute inset-4 rounded-full border-4 " +
                                            "border-[#7fa8ff] border-r-transparent " +
                                            "animate-[spin_2s_linear_infinite_reverse]"
                                    )
                                }
                                div {
                                    className = ClassName(
                                        "absolute inset-10 rounded-full bg-gradient-to-tr " +
                                            "from-[#3b5bfe] to-[#8fa6ff] animate-pulse " +
                                            "shadow-[0_0_40px_#3b5bfe]"
                                    )
                                }
                            }
                            h3 {
                                className = ClassName(
                                    "text-xl font-bold text-[#8fa6ff] animate-pulse"
                                )
                                +"Calculating suitability matrix..."
                            }
                            p {
                                className = ClassName(
                                    "text-sm text-white/40 mt-2"
                                )
                                +"Evaluating capacity, route drafts, and temporal alignment."
                            }
                        }
                    } else if (matches.isNotEmpty()) {
                        h2 {
                            className = ClassName(
                                "text-2xl font-bold text-white px-2 flex justify-between items-end"
                            )
                            +"Best Matches Found"
                            span {
                                className = ClassName(
                                    "text-[12px] font-normal text-[#6d8bff] " +
                                        "bg-[#3b5bfe]/10 px-3 py-1 rounded-full"
                                )
                                +"${matches.size} Results"
                            }
                        }

                        matches.forEachIndexed { index, match ->
                            div {
                                key = Key("match-$index")
                                className = ClassName(
                                    "relative rounded-2xl border border-[#3b5bfe]/30 " +
                                        "bg-gradient-to-br from-[#0c1439]/90 to-[#080d28]/90 " +
                                        "p-6 shadow-xl backdrop-blur-xl transition-all duration-300 " +
                                        "hover:-translate-y-2 " +
                                        "hover:shadow-[0_20px_50px_rgba(59,91,254,0.25)]"
                                )
                                style = styleOf(
                                    "transformStyle" to "preserve-3d",
                                    "animation" to "veronica-ship-enter 0.5s ease-out forwards",
                                    "animationDelay" to "${index * 0.15}s"
                                )

                                asDynamic().onMouseMove = { e: dynamic ->
                                    val target = e.currentTarget
                                    val rect = target.getBoundingClientRect()
                                    val px =
                                        (e.clientX.unsafeCast<Double>() - rect.left.unsafeCast<Double>()) /
                                            rect.width.unsafeCast<Double>()
                                    val py =
                                        (e.clientY.unsafeCast<Double>() - rect.top.unsafeCast<Double>()) /
                                            rect.height.unsafeCast<Double>()
                                    val rx = (0.5 - py) * 12
                                    val ry = (px - 0.5) * 12

                                    target.style.transform =
                                        "perspective(1000px) rotateX(${rx}deg) rotateY(${ry}deg) translateY(-8px)"
                                    Unit
                                }

                                asDynamic().onMouseLeave = { e: dynamic ->
                                    val target = e.currentTarget
                                    target.style.transform =
                                        "perspective(1000px) rotateX(0deg) rotateY(0deg) translateY(0px)"
                                    Unit
                                }

                                div {
                                    className = ClassName(
                                        "flex items-center justify-between"
                                    )
                                    div {
                                        h3 {
                                            className = ClassName(
                                                "text-2xl font-bold text-white"
                                            )
                                            +match.vesselName
                                        }
                                        p {
                                            className = ClassName(
                                                "text-[#8fa6ff] text-sm font-medium mt-1"
                                            )
                                            +match.type
                                        }
                                    }
                                    div {
                                        className = ClassName(
                                            "relative w-20 h-20 flex items-center justify-center " +
                                                "rounded-full bg-black/40 border border-[#3b5bfe]/50 " +
                                                "shadow-[inset_0_0_20px_rgba(59,91,254,0.3)]"
                                        )
                                        style = styleOf(
                                            "transform" to "translateZ(30px)"
                                        )
                                        div {
                                            className = ClassName(
                                                "absolute inset-0 rounded-full blur-md opacity-50 " +
                                                    if (match.score > 90) {
                                                        "bg-emerald-500"
                                                    } else {
                                                        "bg-[#3b5bfe]"
                                                    }
                                            )
                                        }
                                        div {
                                            className = ClassName(
                                                "relative text-center"
                                            )
                                            p {
                                                className = ClassName(
                                                    "text-2xl font-black " +
                                                        if (match.score > 90) {
                                                            "text-emerald-400"
                                                        } else {
                                                            "text-[#7fa8ff]"
                                                        }
                                                )
                                                +"${match.score}%"
                                            }
                                            p {
                                                className = ClassName(
                                                    "text-[8px] uppercase tracking-widest text-white/50 -mt-1"
                                                )
                                                +"Match"
                                            }
                                        }
                                    }
                                }

                                div {
                                    className = ClassName(
                                        "grid grid-cols-2 gap-4 mt-6 pt-6 border-t border-[#3b5bfe]/20"
                                    )
                                    style = styleOf(
                                        "transform" to "translateZ(15px)"
                                    )
                                    div {
                                        p {
                                            className = ClassName(
                                                "text-[10px] uppercase text-[#8fa6ff]/60 mb-1"
                                            )
                                            +"Capacity"
                                        }
                                        p {
                                            className = ClassName(
                                                "text-white font-semibold"
                                            )
                                            +match.capacity
                                        }
                                    }
                                    div {
                                        p {
                                            className = ClassName(
                                                "text-[10px] uppercase text-[#8fa6ff]/60 mb-1"
                                            )
                                            +"Est. Time of Arrival"
                                        }
                                        p {
                                            className = ClassName(
                                                "text-white font-semibold"
                                            )
                                            +match.eta
                                        }
                                    }
                                }

                                button {
                                    className = ClassName(
                                        "mt-6 w-full py-3 rounded-lg border border-[#3b5bfe] " +
                                            "text-[#7fa8ff] hover:bg-[#3b5bfe] hover:text-white " +
                                            "transition-all font-bold tracking-wide cursor-pointer"
                                    )
                                    style = styleOf(
                                        "transform" to "translateZ(20px)"
                                    )
                                    // YAHAN CLICK HONE PAR MODAL OPEN HOGA
                                    onClick = {
                                        setSelectedCharter(match)
                                    }
                                    +"Proceed to Charter"
                                }
                            }
                        }
                    } else {
                        div {
                            className = ClassName(
                                "flex-1 flex flex-col items-center justify-center opacity-30"
                            )
                            div {
                                className = ClassName(
                                    "w-32 h-32 rounded-full border-2 border-dashed " +
                                        "border-[#6d8bff] mb-6 animate-[spin_20s_linear_infinite]"
                                )
                            }
                            h3 {
                                className = ClassName(
                                    "text-xl font-bold text-white"
                                )
                                +"Engine Standby"
                            }
                            p {
                                className = ClassName(
                                    "text-sm text-white/60 mt-2 text-center max-w-sm"
                                )
                                +"Enter your cargo parameters and run the engine to discover optimal vessels."
                            }
                        }
                    }
                }
            }
        }
        
        // ========================================================
        // NAYA CHARTER MODAL (POPUP) 
        // Ye tabhi dikhega jab koi ship select hogi
        // ========================================================
        if (selectedCharter != null) {
            div {
                className = ClassName("fixed inset-0 z-[200] flex items-center justify-center bg-black/70 backdrop-blur-md p-4")
                div {
                    className = ClassName("bg-[#0c1130] border border-[#3b5bfe]/50 rounded-3xl p-6 md:p-8 max-w-2xl w-full shadow-[0_0_60px_rgba(59,91,254,0.25)] relative overflow-hidden")
                    style = styleOf("animation" to "veronica-ship-enter 0.3s ease-out forwards")

                    // Background Glow Effect
                    div {
                        className = ClassName("absolute -top-20 -right-20 w-48 h-48 bg-[#3b5bfe]/20 blur-3xl rounded-full pointer-events-none")
                    }

                    // Close Button
                    button {
                        className = ClassName("absolute top-5 right-5 text-white/40 hover:text-white transition-colors cursor-pointer text-xl font-bold")
                        onClick = { setSelectedCharter(null) }
                        +"✕"
                    }

                    h2 {
                        className = ClassName("text-3xl font-extrabold text-white mb-6 border-b border-white/10 pb-4")
                        +"Charter Agreement Review"
                    }

                    div {
                        className = ClassName("grid grid-cols-1 md:grid-cols-2 gap-6 mb-8")
                        
                        // Vessel Block
                        div {
                            className = ClassName("p-4 rounded-xl bg-white/5 border border-white/10")
                            p { className = ClassName("text-[10px] uppercase tracking-wider text-[#8fa6ff]/80 mb-1"); +"Selected Vessel" }
                            p { className = ClassName("text-xl font-bold text-white"); +selectedCharter.vesselName }
                            p { className = ClassName("text-xs font-semibold text-emerald-400 mt-1"); +"${selectedCharter.score}% AI Match Fit" }
                        }
                        
                        // Route Block
                        div {
                            className = ClassName("p-4 rounded-xl bg-white/5 border border-white/10")
                            p { className = ClassName("text-[10px] uppercase tracking-wider text-[#8fa6ff]/80 mb-1"); +"Route Overview" }
                            p { className = ClassName("text-lg font-bold text-white"); +"$origin → $destination" }
                        }
                        
                        // Cargo Block
                        div {
                            className = ClassName("p-4 rounded-xl bg-white/5 border border-white/10")
                            p { className = ClassName("text-[10px] uppercase tracking-wider text-[#8fa6ff]/80 mb-1"); +"Cargo Specification" }
                            p { className = ClassName("text-lg font-bold text-white"); +"$quantity MT" }
                            p { className = ClassName("text-xs text-[#c7cbe0] mt-1"); +"Commodity: $cargoType" }
                        }
                        
                        // Schedule Block
                        div {
                            className = ClassName("p-4 rounded-xl bg-white/5 border border-white/10")
                            p { className = ClassName("text-[10px] uppercase tracking-wider text-[#8fa6ff]/80 mb-1"); +"Vessel Schedule" }
                            p { className = ClassName("text-lg font-bold text-white"); +"Laycan: $laycanStart - $laycanEnd" }
                            p { className = ClassName("text-xs text-[#c7cbe0] mt-1"); +"Estimated ETA: ${selectedCharter.eta}" }
                        }
                    }

                    button {
                        className = ClassName("w-full py-4 rounded-xl font-bold text-white transition-all cursor-pointer shadow-lg " +
                            if (isSecuring) "bg-[#3b5bfe]/50 cursor-not-allowed" 
                            else "bg-emerald-500 hover:bg-emerald-400 hover:shadow-[0_0_25px_rgba(16,185,129,0.5)] text-[#070b20]"
                        )
                        disabled = isSecuring
                        onClick = {
                            MainScope().launch {
                                setIsSecuring(true)
                                try {
                                    val req = json(
                                        "method" to "POST",
                                        "headers" to json("Content-Type" to "application/json"),
                                        "body" to JSON.stringify(json("vesselName" to selectedCharter.vesselName))
                                    ).unsafeCast<RequestInit>()
                                    
                                    val response = window.fetch("http://localhost:8080/api/auction/award", req).await()
                                    
                                    if (response.ok) {
                                        window.alert("Success! Charter contract officially secured for ${selectedCharter.vesselName}.\n\nCargo: $quantity MT $cargoType\nRoute: $origin to $destination")
                                        setSelectedCharter(null) // Modal band kardo
                                    } else {
                                        window.alert("Error securing charter on the backend.")
                                    }
                                } catch(e: Throwable) {
                                    window.alert("Network Error: ${e.message}")
                                } finally {
                                    setIsSecuring(false)
                                }
                            }
                        }
                        if (isSecuring) {
                            +"Securing Contract via Smart Contract..."
                        } else {
                            +"Confirm & Sign Charter"
                        }
                    }
                }
            }
        }
        // ========================================================
    }
}