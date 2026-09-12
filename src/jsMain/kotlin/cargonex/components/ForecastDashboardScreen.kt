package cargonex.components

import react.FC
import react.Key
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import react.useEffect
import react.useState
import cargonex.core.styleOf
import web.cssom.ClassName
import kotlin.random.Random

external interface ForecastDashboardProps : Props {
    var onNavigate: ((String) -> Unit)?
}

val ForecastDashboardScreen = FC<ForecastDashboardProps> { props ->

    // 1. State Variables for Inputs
    val (origin, setOrigin) = useState("Australia")
    val (destination, setDestination) = useState("Paradip")
    val (cargo, setCargo) = useState("Iron Ore")
    val (quantity, setQuantity) = useState("70,000 MT")
    val (vesselType, setVesselType) = useState("Panamax")
    val (timeframe, setTimeframe) = useState("30D")

    // 2. State Variables for Dynamic Output Data
    val (currentRate, setCurrentRate) = useState(17.80)
    val (forecastRate, setForecastRate) = useState(16.90)
    val (trendPercent, setTrendPercent) = useState("-5.0")
    val (isBearish, setIsBearish) = useState(true)
    val (graphData, setGraphData) = useState(listOf(90, 85, 80, 75, 70, 60, 50, 45, 40, 35, 45, 55, 60))
    
    val (cargoDemand, setCargoDemand) = useState("2.7")
    val (demandTrend, setDemandTrend) = useState("+12.5")
    val (vesselFit, setVesselFit) = useState(94)
    val (potentialSaving, setPotentialSaving) = useState("63")

    // 3. Dynamic Calculation Engine (Runs when inputs or timeframe change)
    fun runAiForecast(tf: String) {
        // Generate pseudo-random realistic data based on inputs
        val baseRate = 12.0 + Random.nextDouble() * 12.0 // Between 12 and 24
        val trend = (Random.nextDouble() * 16.0) - 8.0 // Between -8% and +8%
        val futureRate = baseRate * (1.0 + (trend / 100.0))
        
        setCurrentRate(kotlin.math.round(baseRate * 100) / 100.0)
        setForecastRate(kotlin.math.round(futureRate * 100) / 100.0)
        
        // FIX: Added outer brackets before .toString()
        setTrendPercent((kotlin.math.round(trend * 10) / 10.0).toString())
        
        setIsBearish(trend < 0)

        // Update Graph Bars based on Timeframe
        val barCount = when (tf) { "7D" -> 7; "90D" -> 30; else -> 15 }
        val newGraph = List(barCount) { Random.nextInt(30, 95) }
        setGraphData(newGraph)

        // Update other metrics
        setVesselFit(Random.nextInt(65, 99))
        setCargoDemand((kotlin.math.round((1.5 + Random.nextDouble() * 2.0) * 10) / 10.0).toString())
        
        val dTrend = (Random.nextDouble() * 20.0) - 5.0
        setDemandTrend((kotlin.math.round(dTrend * 10) / 10.0).toString())
        
        setPotentialSaving((Random.nextInt(15, 120)).toString())
    }

    // AI Logic Strings based on the calculated trend
    val aiDecision = if (isBearish) "WAIT 7-10 DAYS" else "CHARTER NOW"
    val aiReason = if (isBearish) 
        "Rates are projected to decline by ${kotlin.math.abs(trendPercent.toDouble())}% over the next $timeframe. Wait for the market to soften." 
    else 
        "Rates are rising rapidly. Secure the $vesselType immediately to prevent further cost increases on the $origin to $destination route."

    div {
        className = ClassName("min-h-screen bg-[#070b20] pt-24 px-6 font-sans text-white relative overflow-y-auto pb-20")
        style = styleOf(
            "background" to "radial-gradient(circle at 50% 10%, rgba(59,91,254,0.15), transparent 40%), linear-gradient(180deg, #080d28 0%, #070b20 100%)"
        )

        // Back Button
        button {
            className = ClassName("absolute top-24 left-6 z-[100] sm:left-12 flex items-center gap-2 px-4 py-2 rounded-xl bg-[#0c1130]/80 hover:bg-[#3b5bfe]/40 text-[#8fa6ff] hover:text-white backdrop-blur-md transition-all text-sm font-medium border border-[#3b5bfe]/30 shadow-lg cursor-pointer")
            onClick = { props.onNavigate?.invoke("home") }
            +"Back to Home"
        }

        div {
            className = ClassName("max-w-6xl mx-auto pt-16")

            // HEADER
            div {
                className = ClassName("flex flex-col md:flex-row justify-between items-start md:items-end mb-8")
                div {
                    h2 {
                        className = ClassName("text-4xl font-bold tracking-tight mb-2 bg-gradient-to-r from-white to-[#8fa6ff] bg-clip-text text-transparent")
                        +"AI Freight Forecast"
                    }
                    p {
                        className = ClassName("text-[#8fa6ff] text-lg")
                        +"Predict freight rates, cargo demand, and market movement before you charter."
                    }
                }
                div {
                    className = ClassName("mt-4 md:mt-0 flex items-center gap-2 px-4 py-2 rounded-lg bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-sm font-medium")
                    div { className = ClassName("w-2 h-2 rounded-full bg-emerald-400 animate-ping") }
                    +"Live Forecast Engine Ready"
                }
            }

            // 1. ROUTE & CARGO SELECTION (Dynamic Inputs)
            div {
                className = ClassName("grid grid-cols-2 md:grid-cols-5 gap-4 mb-4")
                
                // Origin Input
                div {
                    className = ClassName("p-3 rounded-xl bg-[#0c1130]/60 border border-white/10 backdrop-blur-sm")
                    p { className = ClassName("text-[10px] uppercase tracking-wider text-[#8fa6ff]"); +"Origin" }
                    input {
                        className = ClassName("w-full bg-transparent text-white font-bold text-sm outline-none border-b border-white/10 focus:border-[#3b5bfe] py-1 mt-1")
                        value = origin
                        onChange = { setOrigin(it.target.value) }
                    }
                }

                // Destination Input
                div {
                    className = ClassName("p-3 rounded-xl bg-[#0c1130]/60 border border-white/10 backdrop-blur-sm")
                    p { className = ClassName("text-[10px] uppercase tracking-wider text-[#8fa6ff]"); +"Destination" }
                    input {
                        className = ClassName("w-full bg-transparent text-white font-bold text-sm outline-none border-b border-white/10 focus:border-[#3b5bfe] py-1 mt-1")
                        value = destination
                        onChange = { setDestination(it.target.value) }
                    }
                }

                // Cargo Input
                div {
                    className = ClassName("p-3 rounded-xl bg-[#0c1130]/60 border border-white/10 backdrop-blur-sm")
                    p { className = ClassName("text-[10px] uppercase tracking-wider text-[#8fa6ff]"); +"Cargo" }
                    input {
                        className = ClassName("w-full bg-transparent text-white font-bold text-sm outline-none border-b border-white/10 focus:border-[#3b5bfe] py-1 mt-1")
                        value = cargo
                        onChange = { setCargo(it.target.value) }
                    }
                }

                // Quantity Input
                div {
                    className = ClassName("p-3 rounded-xl bg-[#0c1130]/60 border border-white/10 backdrop-blur-sm")
                    p { className = ClassName("text-[10px] uppercase tracking-wider text-[#8fa6ff]"); +"Quantity" }
                    input {
                        className = ClassName("w-full bg-transparent text-white font-bold text-sm outline-none border-b border-white/10 focus:border-[#3b5bfe] py-1 mt-1")
                        value = quantity
                        onChange = { setQuantity(it.target.value) }
                    }
                }

                // Vessel Type Input
                div {
                    className = ClassName("p-3 rounded-xl bg-[#0c1130]/60 border border-white/10 backdrop-blur-sm")
                    p { className = ClassName("text-[10px] uppercase tracking-wider text-[#8fa6ff]"); +"Vessel Type" }
                    input {
                        className = ClassName("w-full bg-transparent text-white font-bold text-sm outline-none border-b border-white/10 focus:border-[#3b5bfe] py-1 mt-1")
                        value = vesselType
                        onChange = { setVesselType(it.target.value) }
                    }
                }
            }

            // Recalculate Button
            div {
                className = ClassName("flex justify-end mb-8")
                button {
                    className = ClassName("px-6 py-2.5 rounded-lg bg-[#3b5bfe] text-white font-bold hover:bg-[#6d8bff] hover:shadow-[0_0_15px_rgba(59,91,254,0.4)] transition-all cursor-pointer")
                    onClick = { runAiForecast(timeframe) }
                    +"Recalculate Forecast"
                }
            }

            // MIDDLE SECTION (Rate + Forecast Graph)
            div {
                className = ClassName("grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8")

                // 2. CURRENT FREIGHT RATE
                div {
                    className = ClassName("p-6 rounded-2xl bg-gradient-to-br from-[#0c1130] to-[#080d28] border border-[#3b5bfe]/30 shadow-lg relative overflow-hidden")
                    p { className = ClassName("text-xs font-semibold uppercase tracking-widest text-[#8fa6ff] mb-2"); +"Current Freight Rate" }

                    div {
                        className = ClassName("flex items-end gap-3 mb-6 relative z-10")
                        h3 { className = ClassName("text-5xl font-extrabold text-white"); +"$$currentRate" }
                        span { className = ClassName("text-xl text-[#8fa6ff] mb-1"); +"/ MT" }
                    }

                    div {
                        className = ClassName("space-y-3 relative z-10")

                        div {
                            className = ClassName("flex justify-between items-center p-3 rounded-lg bg-white/5")
                            span { className = ClassName("text-sm text-[#c7cbe0]"); +"$timeframe Forecast" }
                            span { 
                                className = ClassName("text-sm font-bold ${if (isBearish) "text-emerald-400" else "text-red-400"}")
                                +"${if (isBearish) "↓" else "↑"} ${kotlin.math.abs(trendPercent.toDouble())}%"
                            }
                        }

                        div {
                            className = ClassName("flex justify-between items-center p-3 rounded-lg bg-white/5")
                            span { className = ClassName("text-sm text-[#c7cbe0]"); +"Expected Rate" }
                            span { className = ClassName("text-sm font-bold text-white"); +"$$forecastRate" }
                        }
                    }
                }

                // 3. FREIGHT FORECAST GRAPH (Dynamic Hero Component)
                div {
                    className = ClassName("lg:col-span-2 p-6 rounded-2xl bg-[#0c1130]/80 border border-[#3b5bfe]/30 shadow-lg flex flex-col")

                    div {
                        className = ClassName("flex justify-between items-center mb-6")

                        h3 {
                            className = ClassName("text-lg font-bold text-white")
                            +"Freight Rate Forecast"
                        }

                        div {
                            className = ClassName("flex bg-black/40 rounded-lg p-1 border border-white/10")

                            listOf("7D", "30D", "90D").forEach { time ->
                                button {
                                    key = Key(time)
                                    className = ClassName("px-4 py-1 text-xs font-bold rounded-md transition-colors cursor-pointer ${if(timeframe == time) "bg-[#3b5bfe] text-white" else "text-[#8fa6ff] hover:text-white"}")
                                    onClick = { 
                                        setTimeframe(time)
                                        runAiForecast(time)
                                    }
                                    +time
                                }
                            }
                        }
                    }

                    // Dynamic Graph Representation
                    div {
                        className = ClassName("flex-1 relative flex items-end gap-1 mt-4 h-48 border-b border-l border-white/10 pl-2 pb-2")
                        
                        graphData.forEachIndexed { i, h ->
                            val isForecast = i > (graphData.size / 2)

                            div {
                                key = Key(i.toString())
                                className = ClassName("flex-1 rounded-t-sm transition-all duration-500 hover:opacity-100 ${if (isForecast) "bg-gradient-to-t from-[#5567d8]/30 to-[#8294ff]/70 opacity-60 border-t border-dashed border-[#8294ff]" else "bg-gradient-to-t from-[#2638ad] to-[#536cf4] opacity-80"}")
                                style = styleOf("height" to "$h%")
                            }
                        }

                        // X-axis labels
                        div {
                            className = ClassName("absolute -bottom-6 left-0 right-0 flex justify-between text-[10px] text-[#8fa6ff]/60")
                            span { +"Past" }
                            span { +"Today" }
                            span { +"Next $timeframe" }
                        }

                        // Y-axis label
                        div {
                            className = ClassName("absolute left-4 top-0 text-[10px] text-[#8fa6ff]/60")
                            +"$$currentRate/MT"
                        }
                    }
                }
            }

            // BOTTOM GRID (4 Columns)
            div {
                className = ClassName("grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6")

                // 4. CARGO DEMAND (Dynamic)
                div {
                    className = ClassName("p-6 rounded-2xl bg-[#0c1130]/60 border border-white/10")

                    h3 {
                        className = ClassName("text-sm font-bold uppercase tracking-wider text-[#8fa6ff] mb-4")
                        +"Cargo Demand"
                    }

                    div {
                        className = ClassName("flex items-center gap-4 mb-4")

                        div {
                            p { className = ClassName("text-2xl font-bold text-white"); +"${cargoDemand}M" }
                            p { className = ClassName("text-xs text-[#c7cbe0]"); +"$timeframe Forecast (MT)" }
                        }

                        div {
                            val isPos = demandTrend.toDouble() > 0

                            span { 
                                className = ClassName("px-2 py-1 rounded text-xs font-bold ${if(isPos) "bg-emerald-500/20 text-emerald-400" else "bg-red-500/20 text-red-400"}")
                                +"${if(isPos) "↑ +" else "↓ "}$demandTrend%"
                            }
                        }
                    }

                    p {
                        className = ClassName("text-xs text-[#c7cbe0]/80 leading-relaxed border-t border-white/10 pt-4")
                        +"$cargo shipments are shifting. Monitor vessel demand closely on the $origin corridor."
                    }
                }

                // 5. MARKET SIGNALS
                div {
                    className = ClassName("p-6 rounded-2xl bg-[#0c1130]/60 border border-white/10")

                    h3 {
                        className = ClassName("text-sm font-bold uppercase tracking-wider text-[#8fa6ff] mb-4")
                        +"Market Signals"
                    }

                    div {
                        className = ClassName("space-y-2")

                        val signals = listOf(
                            "Vessel Supply" to "↓ 8%",
                            "Cargo Demand" to "↑ 12%",
                            "Bunker Cost" to "↑ 4%",
                            "Port Congestion" to "↓ 3%"
                        )

                        signals.forEach { (label, value) ->
                            div {
                                key = Key(label)
                                className = ClassName("flex justify-between items-center text-sm")
                                span { className = ClassName("text-[#c7cbe0]"); +label }
                                span {
                                    className = ClassName("font-bold ${if(value.contains("↑")) "text-emerald-400" else "text-red-400"}")
                                    +value
                                }
                            }
                        }
                    }
                }

                // 6. VESSEL FIT & PORT IMPACT (Dynamic)
                div {
                    className = ClassName("p-6 rounded-2xl bg-[#0c1130]/60 border border-white/10")

                    h3 {
                        className = ClassName("text-sm font-bold uppercase tracking-wider text-[#8fa6ff] mb-4")
                        +"Vessel & Port Fit"
                    }

                    div {
                        className = ClassName("mb-4 pb-4 border-b border-white/10")

                        p {
                            className = ClassName("text-lg font-bold text-white")
                            +"$vesselType ($vesselFit% Fit)"
                        }

                        p {
                            className = ClassName("text-xs text-[#c7cbe0] mt-1")
                            +"Best balance of capacity & route economics."
                        }
                    }

                    div {
                        p {
                            className = ClassName("text-sm text-white mb-1 flex justify-between")
                            span { +"Origin Wait" }
                            span {
                                className = ClassName("text-emerald-400 font-bold")
                                +"Low"
                            }
                        }

                        p {
                            className = ClassName("text-sm text-white flex justify-between")
                            span { +"Dest. Wait ($destination)" }
                            span {
                                className = ClassName("text-yellow-400 font-bold")
                                +"18% (6.2 hrs)"
                            }
                        }
                    }
                }

                // 7. FINAL AI DECISION CARD (Dynamic Climax)
                div {
                    className = ClassName("p-6 rounded-2xl bg-gradient-to-br from-[#1d2bd8]/40 to-[#3b5bfe]/20 border border-[#6d8bff]/50 shadow-[0_0_30px_rgba(59,91,254,0.2)] relative overflow-hidden")

                    div {
                        className = ClassName("absolute -right-10 -top-10 w-32 h-32 bg-[#6d8bff]/20 blur-2xl rounded-full pointer-events-none")
                    }

                    h3 {
                        className = ClassName("text-xs font-bold uppercase tracking-widest text-[#a5b5ff] mb-2")
                        +"AI Charter Recommendation"
                    }
                    
                    div {
                        className = ClassName("mt-2 mb-4")

                        span { 
                            className = ClassName("inline-block px-4 py-1.5 rounded-lg font-extrabold text-lg tracking-wide ${if(isBearish) "bg-yellow-500 text-black" else "bg-emerald-500 text-white"}")
                            +aiDecision
                        }
                    }
                    
                    div {
                        className = ClassName("space-y-2 mb-4")

                        p {
                            className = ClassName("text-sm flex justify-between")
                            span {
                                className = ClassName("text-[#c7cbe0]")
                                +"Target Window:"
                            }
                            span {
                                className = ClassName("font-bold text-white")
                                +"Next $timeframe"
                            }
                        }

                        p {
                            className = ClassName("text-sm flex justify-between")
                            span {
                                className = ClassName("text-[#c7cbe0]")
                                +"Expected Rate:"
                            }
                            span {
                                className = ClassName("font-bold ${if(isBearish) "text-emerald-400" else "text-red-400"}")
                                +"$$forecastRate / MT"
                            }
                        }

                        if (isBearish) {
                            p {
                                className = ClassName("text-sm flex justify-between border-t border-white/20 pt-2 mt-2")
                                span {
                                    className = ClassName("text-[#c7cbe0]")
                                    +"Potential Saving:"
                                }
                                span {
                                    className = ClassName("font-bold text-emerald-400")
                                    +"~$${potentialSaving},000"
                                }
                            }
                        } else {
                            p {
                                className = ClassName("text-sm flex justify-between border-t border-white/20 pt-2 mt-2")
                                span {
                                    className = ClassName("text-[#c7cbe0]")
                                    +"Risk Exposure:"
                                }
                                span {
                                    className = ClassName("font-bold text-red-400")
                                    +"HIGH"
                                }
                            }
                        }
                    }

                    p {
                        className = ClassName("text-[11px] text-[#c7cbe0]/80 leading-relaxed")
                        +aiReason
                    }
                }
            }

            // 8. DATA SOURCES FOOTER
            div {
                className = ClassName("flex flex-wrap justify-center gap-6 py-6 border-t border-white/10 text-[10px] text-[#8fa6ff]/50 uppercase tracking-widest")
                span { +"Historical Data ✓" }
                span { +"Live Rates ✓" }
                span { +"Vessel Supply ✓" }
                span { +"Port Congestion ✓" }
            }
        }
    }
}