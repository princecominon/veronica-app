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
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.th
import react.dom.html.ReactHTML.thead
import react.dom.html.ReactHTML.tr
import react.useEffect
import react.useState
import cargonex.core.styleOf
import web.cssom.ClassName
import kotlin.js.JSON
import kotlin.js.json

external interface RiskMatrixProps : Props {
    var onNavigate: ((String) -> Unit)?
}

val RiskMatrixScreen = FC<RiskMatrixProps> { props ->
    val (isLoading, setIsLoading) = useState(true)
    val (errorMessage, setErrorMessage) = useState<String?>(null)
    val (riskData, setRiskData) = useState<dynamic>(null)

    val (selectedPort, setSelectedPort) = useState("Paradip")
    val availablePorts = listOf("Paradip", "Dhamra", "Vizag", "Haldia")

    useEffect(selectedPort) {
        MainScope().launch {
            setIsLoading(true)
            setErrorMessage(null)

            try {
                val requestOptions = json("method" to "GET").unsafeCast<RequestInit>()

                val response = window.fetch("https://veronica-backend-zyyf.onrender.com/api/risk/$selectedPort", requestOptions).await()

                if (response.ok) {
                    val textResponse = response.text().await()
                    val parsedData = JSON.parse<dynamic>(textResponse)

                    if (parsedData.error != null) {
                        setErrorMessage(parsedData.error.toString())
                        setRiskData(null)
                    } else {
                        setRiskData(parsedData)
                    }
                } else {
                    setErrorMessage("Error fetching data: ${response.status}")
                    setRiskData(null)
                }
            } catch (e: Throwable) {
                setErrorMessage("Failed to connect to backend: ${e.message}")
                setRiskData(null)
            } finally {
                setIsLoading(false)
            }
        }
    }

    div {
        className = ClassName("min-h-screen bg-[#070b20] pt-24 px-6 font-sans text-white relative")

        style = styleOf(
            "background" to "radial-gradient(circle at 50% 38%, rgba(59,91,254,0.15), transparent 50%), linear-gradient(180deg, #080d28 0%, #070b20 100%)"
        )

        button {
            className = ClassName(
                "absolute top-24 left-6 z-[100] sm:left-12 flex items-center gap-2 px-4 py-2 rounded-xl " +
                    "bg-[#0c1130]/80 hover:bg-[#3b5bfe]/40 text-[#8fa6ff] hover:text-white " +
                    "backdrop-blur-md transition-all text-sm font-medium border border-[#3b5bfe]/30 " +
                    "shadow-lg cursor-pointer"
            )

            onClick = {
                props.onNavigate?.invoke("home")
            }

            +"Back to Home"
        }

        div {
            className = ClassName("max-w-5xl mx-auto pt-16")

            div {
                className = ClassName("mb-12 text-center")

                h2 {
                    className = ClassName("text-4xl font-bold tracking-tight mb-4")
                    +"Risk Matrix Intelligence"
                }

                p {
                    className = ClassName("text-[#8fa6ff] max-w-2xl mx-auto")
                    +"Real-time analysis of weather, geopolitical, market, and operational risks affecting your cargo."
                }
            }

            div {
                className = ClassName("flex flex-wrap justify-center gap-4 mb-8")

                availablePorts.forEach { port ->
                    button {
                        key = Key(port)

                        className = ClassName(
                            "px-6 py-2.5 rounded-xl border text-sm font-semibold transition-all duration-200 " +
                                if (selectedPort == port) {
                                    "bg-[#3b5bfe] text-white border-[#3b5bfe] shadow-[0_0_20px_rgba(59,91,254,0.4)]"
                                } else {
                                    "bg-[#0c1130]/50 text-[#8fa6ff] border-[#3b5bfe]/30 hover:bg-[#3b5bfe]/20"
                                }
                        )

                        onClick = {
                            setSelectedPort(port)
                        }

                        +port
                    }
                }
            }

            if (isLoading) {
                div {
                    className = ClassName("text-center text-[#8fa6ff] py-20 animate-pulse")
                    +"Analyzing live data for $selectedPort..."
                }
            } else if (errorMessage != null) {
                div {
                    className = ClassName(
                        "p-6 text-center text-red-400 bg-red-400/10 rounded-2xl border border-red-400/20"
                    )

                    +errorMessage!!
                }
            } else if (riskData != null) {
                val currentRoute = riskData.currentRoute as String
                val aggregateScore = riskData.aggregateScore as Int
                val summary = riskData.summary as String
                val risksArray = riskData.risks as Array<dynamic>

                div {
                    className = ClassName(
                        "mb-10 p-6 rounded-2xl border border-[#3b5bfe]/30 " +
                            "bg-[#0c1130]/80 backdrop-blur-md shadow-lg"
                    )

                    div {
                        className = ClassName(
                            "flex flex-col md:flex-row justify-between items-start md:items-center gap-6"
                        )

                        div {
                            span {
                                className = ClassName(
                                    "text-xs font-semibold tracking-widest uppercase " +
                                        "text-[#6d8bff] mb-1 block"
                                )

                                +"Current Route Risk"
                            }

                            h3 {
                                className = ClassName("text-2xl font-bold text-white mb-2")
                                +currentRoute
                            }

                            p {
                                className = ClassName("text-sm text-[#c7cbe0]/80")
                                +summary
                            }
                        }

                        div {
                            className = ClassName("flex flex-col items-end")

                            div {
                                className = ClassName(
                                    "text-4xl font-bold ${
                                        if (aggregateScore > 50) {
                                            "text-red-400"
                                        } else {
                                            "text-emerald-400"
                                        }
                                    }"
                                )

                                +"$aggregateScore"

                                span {
                                    className = ClassName("text-lg text-white/40 ml-1")
                                    +"/ 100"
                                }
                            }

                            span {
                                className = ClassName("text-xs text-white/50 mt-1")
                                +"Aggregate Risk Score"
                            }
                        }
                    }
                }

                div {
                    className = ClassName(
                        "overflow-hidden rounded-xl border border-white/10 " +
                            "bg-[#0c1130]/50 backdrop-blur-sm"
                    )

                    table {
                        className = ClassName("w-full text-left border-collapse")

                        thead {
                            className = ClassName("bg-white/5 border-b border-white/10")

                            tr {
                                listOf(
                                    "Risk Factor",
                                    "Probability",
                                    "Impact",
                                    "Level"
                                ).forEach { header ->
                                    th {
                                        className = ClassName(
                                            "p-4 text-xs font-semibold text-[#8fa6ff] " +
                                                "uppercase tracking-wider"
                                        )

                                        +header
                                    }
                                }
                            }
                        }

                        tbody {
                            risksArray.forEachIndexed { index, item ->
                                tr {
                                    key = Key(index.toString())

                                    className = ClassName(
                                        "border-b border-white/5 hover:bg-white/5 transition-colors"
                                    )

                                    td {
                                        className = ClassName("p-4 font-medium text-white")
                                        +(item.name as String)
                                    }

                                    td {
                                        className = ClassName("p-4 text-[#c7cbe0]")
                                        +"${item.probability} / 10"
                                    }

                                    td {
                                        className = ClassName("p-4 text-[#c7cbe0]")
                                        +"${item.impact} / 10"
                                    }

                                    td {
                                        className = ClassName("p-4")

                                        span {
                                            className = ClassName(
                                                "px-3 py-1 rounded-full text-xs font-bold border ${item.colorClass}"
                                            )

                                            +(item.level as String)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}