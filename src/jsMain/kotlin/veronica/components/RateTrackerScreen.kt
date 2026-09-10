package veronica.components

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.fetch.RequestInit
import react.FC
import react.Key
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.canvas
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import react.useEffect
import react.useRef
import react.useState
import veronica.core.styleOf
import web.cssom.ClassName
import web.html.HTMLCanvasElement
import web.html.HTMLDivElement

external interface RateTrackerProps : Props {
    var onNavigate: ((String) -> Unit)?
}

private val INDIAN_PORTS = listOf(
    "Paradip",
    "Dhamra",
    "Vizag",
    "Gangavaram",
    "Gopalpur",
    "Haldia"
)

private val INDIAN_REGIONS = listOf(
    "Indian East Coast",
    "Odisha Coast",
    "Andhra Coast",
    "West Bengal Coast"
)

private fun dynamicDouble(value: dynamic): Double {
    return when (value) {
        is Double -> value
        is Int -> value.toDouble()
        is Float -> value.toDouble()
        else -> value?.toString()?.toDoubleOrNull() ?: 0.0
    }
}

private fun formatNumber(value: Double): String {
    return value.toInt().toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}

private fun formatPercent(value: Double): String {
    val fixed = value.asDynamic().toFixed(2) as String
    return "${if (value >= 0) "+" else ""}$fixed%"
}

private fun encodeQueryValue(value: String): String {
    return js("encodeURIComponent(value)") as String
}

val RateTrackerScreen = FC<RateTrackerProps> { props ->
    val (isLoading, setIsLoading) = useState(true)
    val (errorMessage, setErrorMessage) = useState<String?>(null)
    val (rateData, setRateData) = useState<dynamic>(null)

    val (selectedPort, setSelectedPort) = useState("Paradip")
    val (selectedRegion, setSelectedRegion) =
        useState("Indian East Coast")
    val (selectedRange, setSelectedRange) = useState("90D")

    val (showPorts, setShowPorts) = useState(false)
    val (showRegions, setShowRegions) = useState(false)

    val heroCanvasRef = useRef<HTMLCanvasElement>(null)
    val parallaxLayerRef = useRef<HTMLDivElement>(null)

    // Cinematic 3D layer: a slowly drifting low-poly cargo ship on an
    // animated ocean plane, rendered behind all existing content, plus
    // mouse-driven parallax tilt on the main content layer. Nothing
    // above/below this effect is touched.
    useEffect(emptyList<Any?>()) {
        var mouseNormX = 0.0
        var mouseNormY = 0.0
        var animationFrameId = 0
        var resizeHandler: ((org.w3c.dom.events.Event) -> Unit)? = null
        var scene: dynamic = null
        var camera: dynamic = null
        var renderer: dynamic = null
        var shipGroup: dynamic = null
        var oceanMesh: dynamic = null
        var oceanBasePositions: dynamic = null
        var disposed = false

        val onMouseMove: (org.w3c.dom.events.Event) -> Unit = { event ->
            val e = event.asDynamic()
            mouseNormX = (e.clientX.unsafeCast<Double>() / window.innerWidth) * 2.0 - 1.0
            mouseNormY = (e.clientY.unsafeCast<Double>() / window.innerHeight) * 2.0 - 1.0

            parallaxLayerRef.current?.let { el ->
                el.style.asDynamic().transform =
                    "perspective(1400px) rotateY(${mouseNormX * 2.2}deg) rotateX(${-mouseNormY * 2.2}deg)"
            }
        }

        window.addEventListener("mousemove", onMouseMove)

        fun animate() {
            if (disposed) return

            val t = js("Date.now()").unsafeCast<Double>() * 0.001

            if (shipGroup != null) {
                shipGroup.position.x = kotlin.math.sin(t * 0.12) * 5.5
                shipGroup.position.y = kotlin.math.sin(t * 0.9) * 0.12
                shipGroup.rotation.z = kotlin.math.sin(t * 0.9) * 0.02
                shipGroup.rotation.y = 0.35 + kotlin.math.sin(t * 0.05) * 0.08
            }

            if (oceanMesh != null && oceanBasePositions != null) {
                val posAttr = oceanMesh.geometry.attributes.position
                val count = posAttr.count.unsafeCast<Int>()
                for (i in 0 until count) {
                    val bx = oceanBasePositions[i * 3].unsafeCast<Double>()
                    val by = oceanBasePositions[i * 3 + 1].unsafeCast<Double>()
                    val wave =
                        kotlin.math.sin(bx * 0.4 + t * 0.8) * 0.18 +
                            kotlin.math.sin(by * 0.35 + t * 0.6) * 0.14
                    posAttr.setZ(i, wave)
                }
                posAttr.needsUpdate = true
            }

            if (camera != null) {
                camera.position.x += (mouseNormX * 1.6 - camera.position.x.unsafeCast<Double>()) * 0.03
                camera.position.y += (2.4 - mouseNormY * 0.8 - camera.position.y.unsafeCast<Double>()) * 0.03
                camera.lookAt(0.0, 0.4, 0.0)
            }

            if (renderer != null && scene != null && camera != null) {
                renderer.render(scene, camera)
            }

            animationFrameId = window.requestAnimationFrame { animate() }
        }

        fun buildScene() {
            val THREE = window.asDynamic().THREE
            val canvasEl = heroCanvasRef.current ?: return
            if (THREE == undefined) return

            scene = js("new THREE.Scene()")
            scene.fog = js("new THREE.FogExp2(0x050816, 0.045)")

            camera = js("new THREE.PerspectiveCamera(45, 1, 0.1, 100)")
            camera.position.set(0.0, 2.4, 11.0)

            renderer = js("new THREE.WebGLRenderer({ antialias: true, alpha: true })")
            renderer.setClearColor(0x000000, 0)
            renderer.setPixelRatio(kotlin.math.min(window.devicePixelRatio, 2.0))

            val canvasDyn = canvasEl.asDynamic()
            renderer.domElement = canvasDyn
            renderer.context = canvasDyn.getContext("webgl2") ?: canvasDyn.getContext("webgl")

            val ambient = js("new THREE.AmbientLight(0x6f87ff, 0.65)")
            scene.add(ambient)

            val moonLight = js("new THREE.DirectionalLight(0x8fa5ff, 1.1)")
            moonLight.position.set(-6.0, 8.0, 4.0)
            scene.add(moonLight)

            // Ocean: a wide plane with per-vertex wave animation.
            val oceanGeo = js("new THREE.PlaneGeometry(60, 40, 90, 60)")
            val oceanMat = js(
                "new THREE.MeshStandardMaterial({ color: 0x101c4d, metalness: 0.35, roughness: 0.65, wireframe: false, transparent: true, opacity: 0.85 })"
            )
            oceanMesh = js("new THREE.Mesh(oceanGeo, oceanMat)")
            oceanMesh.rotation.x = -kotlin.math.PI / 2.0
            oceanMesh.position.y = -0.6
            scene.add(oceanMesh)

            val posAttr = oceanMesh.geometry.attributes.position
            oceanBasePositions = js("posAttr.array.slice()")

            // Low-poly cargo ship: hull, bridge, funnel and stacked containers.
            shipGroup = js("new THREE.Group()")

            val hullGeo = js("new THREE.BoxGeometry(4.2, 0.6, 1.3)")
            val hullMat = js("new THREE.MeshStandardMaterial({ color: 0x2a3568, metalness: 0.4, roughness: 0.5 })")
            val hull = js("new THREE.Mesh(hullGeo, hullMat)")
            shipGroup.add(hull)

            val bridgeGeo = js("new THREE.BoxGeometry(0.7, 0.7, 1.0)")
            val bridgeMat = js("new THREE.MeshStandardMaterial({ color: 0xdfe5ff, metalness: 0.2, roughness: 0.6 })")
            val bridge = js("new THREE.Mesh(bridgeGeo, bridgeMat)")
            bridge.position.set(-1.6, 0.65, 0.0)
            shipGroup.add(bridge)

            val funnelGeo = js("new THREE.CylinderGeometry(0.12, 0.15, 0.5, 12)")
            val funnelMat = js("new THREE.MeshStandardMaterial({ color: 0x536eff })")
            val funnel = js("new THREE.Mesh(funnelGeo, funnelMat)")
            funnel.position.set(-1.6, 1.15, 0.0)
            shipGroup.add(funnel)

            val containerColors = arrayOf(0x536eff, 0x8fa5ff, 0xff6b6b, 0x4de08a, 0xffc857)
            var cx = -0.9
            var colorIndex = 0
            while (cx < 1.9) {
                val rows = if (colorIndex % 2 == 0) 2 else 3
                for (row in 0 until rows) {
                    val cGeo = js("new THREE.BoxGeometry(0.55, 0.4, 1.0)")
                    val cMat = js("new THREE.MeshStandardMaterial({ metalness: 0.15, roughness: 0.75 })")
                    cMat.color = js("new THREE.Color()")
                    cMat.color.setHex(containerColors[colorIndex % containerColors.size])
                    val container = js("new THREE.Mesh(cGeo, cMat)")
                    container.position.set(cx, 0.5 + row.toDouble() * 0.42, 0.0)
                    shipGroup.add(container)
                }
                colorIndex += 1
                cx += 0.65
            }

            shipGroup.position.y = 0.15
            shipGroup.rotation.y = 0.35
            scene.add(shipGroup)

            fun resize() {
                val parent = canvasEl.parentElement
                val w = (parent?.asDynamic()?.clientWidth ?: window.innerWidth).unsafeCast<Double>()
                val h = (parent?.asDynamic()?.clientHeight ?: 500).unsafeCast<Double>()
                camera.aspect = w / h
                camera.updateProjectionMatrix()
                renderer.setSize(w, h, false)
            }

            resize()
            resizeHandler = { resize() }
            window.addEventListener("resize", resizeHandler)

            animate()
        }

        if (window.asDynamic().THREE != undefined) {
            buildScene()
        } else if (document.getElementById("three-js-cdn-script") == null) {
            val script = document.createElement("script").asDynamic()
            script.id = "three-js-cdn-script"
            script.src = "https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"
            script.onload = { buildScene() }
            document.head?.appendChild(script.unsafeCast<org.w3c.dom.Node>())
        } else {
            window.setTimeout({ buildScene() }, 300)
        }

        val teardown: () -> Unit = {
            disposed = true
            window.removeEventListener("mousemove", onMouseMove)
            if (animationFrameId != 0) {
                window.cancelAnimationFrame(animationFrameId)
            }
            resizeHandler?.let { window.removeEventListener("resize", it) }
            renderer?.dispose?.invoke()
        }

        teardown
    }

    useEffect(selectedPort, selectedRegion) {
        MainScope().launch {
            try {
                setIsLoading(true)
                setErrorMessage(null)

                val requestOptions: dynamic = js("({})")
                requestOptions.method = "GET"

                val portParam = encodeQueryValue(selectedPort)
                val regionParam = encodeQueryValue(selectedRegion)

                val response = window.fetch(
                    "http://localhost:8080/api/rates/forecast?port=$portParam&region=$regionParam",
                    requestOptions
                ).await()

                val textData = response.text().await()

                if (response.ok) {
                    setRateData(
                        JSON.parse<dynamic>(textData)
                    )
                } else {
                    setErrorMessage(textData)
                }
            } catch (e: Throwable) {
                setErrorMessage(
                    "Failed to connect to freight intelligence service: ${e.message}"
                )
            } finally {
                setIsLoading(false)
            }
        }
    }

    div {
        className = ClassName(
            "min-h-screen bg-[#050816] text-white relative overflow-hidden"
        )

        style = styleOf(
            "background" to """
                radial-gradient(circle at 50% 5%, rgba(79,105,255,0.25), transparent 28%),
                radial-gradient(circle at 10% 40%, rgba(38,73,255,0.12), transparent 30%),
                radial-gradient(circle at 90% 65%, rgba(78,50,255,0.10), transparent 30%),
                linear-gradient(180deg, #080d28 0%, #0a1033 42%, #050816 100%)
            """.trimIndent()
        )

        div {
            className = ClassName(
                "absolute inset-0 pointer-events-none opacity-30"
            )

            style = styleOf(
                "backgroundImage" to """
                    linear-gradient(rgba(99,125,255,0.07) 1px, transparent 1px),
                    linear-gradient(90deg, rgba(99,125,255,0.07) 1px, transparent 1px)
                """.trimIndent(),
                "backgroundSize" to "60px 60px",
                "maskImage" to "linear-gradient(to bottom, black, transparent 80%)"
            )
        }

        div {
            className = ClassName(
                "absolute -top-40 left-1/2 -translate-x-1/2 w-[700px] h-[700px] rounded-full border border-[#526dff]/10 pointer-events-none"
            )

            style = styleOf(
                "boxShadow" to "0 0 120px rgba(61,91,255,0.08), inset 0 0 100px rgba(61,91,255,0.05)"
            )
        }

        div {
            className = ClassName(
                "absolute top-24 left-[8%] w-2 h-2 rounded-full bg-[#7f9aff] shadow-[0_0_20px_#7f9aff] pointer-events-none"
            )
        }

        div {
            className = ClassName(
                "absolute top-[38%] right-[12%] w-1.5 h-1.5 rounded-full bg-[#9eb0ff] shadow-[0_0_15px_#9eb0ff] pointer-events-none"
            )
        }

        // Cinematic 3D layer: cargo ship drifting across an animated ocean,
        // rendered behind all existing content and decoration.
        div {
            className = ClassName(
                "fixed inset-0 z-[1] pointer-events-none opacity-70"
            )

            canvas {
                ref = heroCanvasRef
                className = ClassName("w-full h-full")
            }
        }

        button {
            className = ClassName(
                "fixed top-24 left-5 sm:left-10 z-50 px-4 py-2.5 rounded-xl bg-[#0a1030]/80 border border-[#536eff]/30 text-[#a5b5ff] hover:text-white hover:border-[#7187ff]/60 hover:bg-[#17235e]/80 backdrop-blur-xl transition-all duration-300 cursor-pointer shadow-[0_10px_40px_rgba(0,0,0,0.25)]"
            )

            onClick = {
                props.onNavigate?.invoke("home")
            }

            +"← Back to Home"
        }

        div {
            ref = parallaxLayerRef

            className = ClassName(
                // Changed pt-32 to pt-44 to properly clear the fixed Back button
                "relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-44 pb-16 will-change-transform"
            )

            style = styleOf(
                "transition" to "transform 0.25s ease-out",
                "transformStyle" to "preserve-3d"
            )

            if (isLoading) {
                div {
                    className = ClassName(
                        "min-h-[70vh] flex flex-col items-center justify-center"
                    )

                    div {
                        className = ClassName(
                            "relative w-24 h-24 mb-8"
                        )

                        div {
                            className = ClassName(
                                "absolute inset-0 rounded-full border border-[#5f78ff]/30 animate-ping"
                            )
                        }

                        div {
                            className = ClassName(
                                "absolute inset-3 rounded-full border border-[#6f87ff]/50 animate-spin"
                            )
                        }

                        div {
                            className = ClassName(
                                "absolute inset-7 rounded-full bg-[#536eff] shadow-[0_0_35px_rgba(83,110,255,0.8)]"
                            )
                        }
                    }

                    h2 {
                        className = ClassName(
                            "text-2xl font-semibold tracking-tight"
                        )

                        +"Initializing Freight Intelligence"
                    }

                    p {
                        className = ClassName(
                            "text-[#7f91d8] mt-3 text-sm"
                        )

                        +"Loading historical market data from backend"
                    }
                }
            } else if (errorMessage != null) {
                div {
                    className = ClassName(
                        "min-h-[70vh] flex items-center justify-center"
                    )

                    div {
                        className = ClassName(
                            "w-full max-w-lg rounded-3xl border border-red-400/20 bg-red-500/5 backdrop-blur-2xl p-8 text-center shadow-[0_30px_100px_rgba(0,0,0,0.4)]"
                        )

                        div {
                            className = ClassName(
                                "mx-auto mb-5 w-14 h-14 rounded-2xl bg-red-500/10 border border-red-400/20 flex items-center justify-center text-2xl"
                            )

                            +"!"
                        }

                        h3 {
                            className = ClassName(
                                "text-xl font-bold text-red-300 mb-3"
                            )

                            +"Freight Data Unavailable"
                        }

                        p {
                            className = ClassName(
                                "text-white/55 text-sm leading-relaxed"
                            )

                            +errorMessage!!
                        }

                        button {
                            className = ClassName(
                                "mt-6 px-6 py-3 rounded-xl bg-[#405be8] hover:bg-[#5a72ff] transition-colors cursor-pointer font-semibold"
                            )

                            onClick = {
                                window.location.reload()
                            }

                            +"Reconnect"
                        }
                    }
                }
            } else if (rateData != null) {
                val currentRate =
                    dynamicDouble(rateData.currentRate)

                val dailyChange =
                    dynamicDouble(rateData.dailyChange)

                val high =
                    dynamicDouble(rateData.high)

                val low =
                    dynamicDouble(rateData.low)

                val latestDate =
                    rateData.latestDate?.toString() ?: ""

                val marketTrend =
                    rateData.marketTrend?.toString()
                        ?: "STABLE"

                val insight =
                    rateData.insight?.toString() ?: ""

                val indexName =
                    rateData.indexName?.toString()
                        ?: "Baltic Dry Index"

                val historical =
                    (
                        rateData.historical as? Array<dynamic>
                            ?: emptyArray()
                    ).toList()

                val forecast =
                    (
                        rateData.forecast as? Array<dynamic>
                            ?: emptyArray()
                    ).toList()

                val selectedHistorical =
                    when (selectedRange) {
                        "7D" -> historical.takeLast(7)
                        "30D" -> historical.takeLast(30)
                        "90D" -> historical.takeLast(90)
                        else -> historical
                    }

                val selectedPrices = mutableListOf<Double>()

                selectedHistorical.forEach { record ->
                    selectedPrices.add(
                        dynamicDouble(record.price)
                    )
                }

                val forecastPrices = mutableListOf<Double>()

                forecast.forEach { value ->
                    forecastPrices.add(
                        dynamicDouble(value)
                    )
                }

                val allChartValues =
                    selectedPrices + forecastPrices

                val chartMax =
                    allChartValues.maxOrNull()
                        ?: currentRate

                val chartMin =
                    allChartValues.minOrNull()
                        ?: currentRate

                val chartSpread =
                    (chartMax - chartMin)
                        .coerceAtLeast(1.0)

                val trendUp =
                    dailyChange >= 0.0

                val forecastEnd =
                    forecastPrices.lastOrNull()
                        ?: currentRate

                val forecastChange =
                    if (currentRate != 0.0) {
                        ((forecastEnd - currentRate) / currentRate) * 100.0
                    } else {
                        0.0
                    }

                div {
                    className = ClassName(
                        "mb-10"
                    )

                    div {
                        className = ClassName(
                            "flex flex-col lg:flex-row lg:items-end lg:justify-between gap-6"
                        )

                        div {
                            h1 {
                                className = ClassName(
                                    "text-4xl sm:text-5xl lg:text-6xl font-bold tracking-[-0.04em] bg-gradient-to-r from-white via-[#dfe5ff] to-[#8fa5ff] bg-clip-text text-transparent"
                                )

                                +"Freight Command Center"
                            }

                            p {
                                className = ClassName(
                                    "mt-4 text-[#8292ce] text-sm sm:text-base max-w-2xl leading-relaxed"
                                )

                                +"Indian East Coast freight intelligence powered by historical market data, trend analysis and forward projection."
                            }
                        }

                        div {
                            className = ClassName(
                                "flex items-center gap-3 px-4 py-3 rounded-2xl bg-[#0c1439]/70 border border-[#5069e8]/25 backdrop-blur-xl shadow-[0_15px_50px_rgba(0,0,0,0.25)]"
                            )

                            div {
                                className = ClassName(
                                    "w-3 h-3 rounded-full " +
                                        if (trendUp) {
                                            "bg-emerald-400 shadow-[0_0_18px_#34d399]"
                                        } else {
                                            "bg-red-400 shadow-[0_0_18px_#f87171]"
                                        }
                                )
                            }

                            div {
                                p {
                                    className = ClassName(
                                        "text-[10px] uppercase tracking-[0.18em] text-white/35"
                                    )

                                    +"Market Signal"
                                }

                                p {
                                    className = ClassName(
                                        "font-bold text-sm mt-0.5"
                                    )

                                    +when (marketTrend) {
                                        "RISING" -> "Bullish Momentum"
                                        "FALLING" -> "Bearish Momentum"
                                        else -> "Market Stable"
                                    }
                                }
                            }
                        }
                    }
                }

                div {
                    className = ClassName(
                        "grid grid-cols-1 lg:grid-cols-12 gap-6 mb-6"
                    )

                    div {
                        className = ClassName(
                            "lg:col-span-8 relative rounded-[28px] border border-[#566ff0]/25 bg-[#0a1130]/70 backdrop-blur-2xl overflow-hidden shadow-[0_30px_100px_rgba(0,0,0,0.38)]"
                        )

                        style = styleOf(
                            "transform" to "perspective(1400px) rotateX(1deg)"
                        )

                        div {
                            className = ClassName(
                                "absolute -top-28 -right-28 w-72 h-72 rounded-full bg-[#4c65ff]/10 blur-3xl pointer-events-none"
                            )
                        }

                        div {
                            className = ClassName(
                                "absolute inset-0 pointer-events-none"
                            )

                            style = styleOf(
                                "background" to "linear-gradient(135deg, rgba(87,108,255,0.08), transparent 35%, transparent 70%, rgba(83,110,255,0.05))"
                            )
                        }

                        div {
                            className = ClassName(
                                "relative p-6 sm:p-8"
                            )

                            div {
                                className = ClassName(
                                    "flex flex-col sm:flex-row sm:items-start sm:justify-between gap-6"
                                )

                                div {
                                    p {
                                        className = ClassName(
                                            "text-[10px] uppercase tracking-[0.2em] text-[#7184d2] font-bold"
                                        )

                                        +indexName
                                    }

                                    div {
                                        className = ClassName(
                                            "flex items-end gap-4 mt-3"
                                        )

                                        h2 {
                                            className = ClassName(
                                                "text-6xl sm:text-7xl font-bold tracking-[-0.05em] bg-gradient-to-br from-white via-[#dbe2ff] to-[#7289ff] bg-clip-text text-transparent"
                                            )

                                            +formatNumber(currentRate)
                                        }

                                        span {
                                            className = ClassName(
                                                "pb-2 text-[#7082c6] text-sm"
                                            )

                                            +"INDEX"
                                        }
                                    }

                                    p {
                                        className = ClassName(
                                            "text-xs text-white/35 mt-3"
                                        )

                                        +"Last updated $latestDate"
                                    }
                                }

                                div {
                                    className = ClassName(
                                        "relative w-28 h-28 sm:w-32 sm:h-32 shrink-0"
                                    )

                                    div {
                                        className = ClassName(
                                            "absolute inset-0 rounded-full border border-[#7085ff]/25"
                                        )
                                    }

                                    div {
                                        className = ClassName(
                                            "absolute inset-2 rounded-full border border-[#6078ff]/20 border-dashed animate-[spin_14s_linear_infinite]"
                                        )
                                    }

                                    div {
                                        className = ClassName(
                                            "absolute inset-5 rounded-full bg-gradient-to-br from-[#6079ff]/40 to-[#182768]/60 border border-[#8194ff]/40 shadow-[inset_-10px_-10px_25px_rgba(0,0,0,0.5),0_0_45px_rgba(74,98,255,0.2)]"
                                        )
                                    }

                                    div {
                                        className = ClassName(
                                            "absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 text-center"
                                        )

                                        p {
                                            className = ClassName(
                                                "text-[9px] uppercase tracking-wider text-[#b6c2ff]"
                                            )

                                            +"BDI"
                                        }

                                        p {
                                            className = ClassName(
                                                "text-xs font-bold text-white mt-1"
                                            )

                                            +marketTrend
                                        }
                                    }
                                }
                            }

                            div {
                                className = ClassName(
                                    "grid grid-cols-2 sm:grid-cols-4 gap-3 mt-8"
                                )

                                val metrics = listOf(
                                    Triple(
                                        "Daily Change",
                                        formatPercent(dailyChange),
                                        if (trendUp) "UP" else "DOWN"
                                    ),
                                    Triple(
                                        "Market High",
                                        formatNumber(high),
                                        "HIGH"
                                    ),
                                    Triple(
                                        "Market Low",
                                        formatNumber(low),
                                        "LOW"
                                    ),
                                    Triple(
                                        "Forecast",
                                        formatPercent(forecastChange),
                                        "14 DAYS"
                                    )
                                )

                                metrics.forEachIndexed { index, metric ->
                                    div {
                                        key = Key("metric-$index")

                                        className = ClassName(
                                            "group rounded-2xl border border-white/[0.07] bg-white/[0.025] hover:bg-white/[0.05] transition-all duration-300 p-4"
                                        )

                                        style = styleOf(
                                            "transformStyle" to "preserve-3d",
                                            "willChange" to "transform"
                                        )

                                        asDynamic().onMouseMove = { e: dynamic ->
                                            val target = e.currentTarget
                                            val rect = target.getBoundingClientRect()
                                            val px = (e.clientX.unsafeCast<Double>() - rect.left.unsafeCast<Double>()) / rect.width.unsafeCast<Double>()
                                            val py = (e.clientY.unsafeCast<Double>() - rect.top.unsafeCast<Double>()) / rect.height.unsafeCast<Double>()
                                            val rotateX = (0.5 - py) * 16.0
                                            val rotateY = (px - 0.5) * 16.0
                                            target.style.transition = "transform 0.05s ease-out"
                                            target.style.transform =
                                                "perspective(700px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) translateY(-4px) scale(1.03)"
                                            Unit
                                        }

                                        asDynamic().onMouseLeave = { e: dynamic ->
                                            val target = e.currentTarget
                                            target.style.transition = "transform 0.5s cubic-bezier(0.22, 1, 0.36, 1)"
                                            target.style.transform =
                                                "perspective(700px) rotateX(0deg) rotateY(0deg) translateY(0px) scale(1)"
                                            Unit
                                        }

                                        p {
                                            className = ClassName(
                                                "text-[9px] uppercase tracking-[0.16em] text-white/30"
                                            )

                                            +metric.first
                                        }

                                        p {
                                            className = ClassName(
                                                "text-lg font-bold mt-2 " +
                                                    if (index == 0 || index == 3) {
                                                        if (metric.second.startsWith("-")) {
                                                            "text-red-300"
                                                        } else {
                                                            "text-emerald-300"
                                                        }
                                                    } else {
                                                        "text-white"
                                                    }
                                            )

                                            +metric.second
                                        }

                                        p {
                                            className = ClassName(
                                                "text-[9px] text-white/20 mt-1"
                                            )

                                            +metric.third
                                        }
                                    }
                                }
                            }
                        }
                    }

                    div {
                        className = ClassName(
                            "lg:col-span-4 relative rounded-[28px] border border-[#566ff0]/25 bg-[#0a1130]/70 backdrop-blur-2xl overflow-hidden shadow-[0_30px_100px_rgba(0,0,0,0.38)]"
                        )

                        div {
                            className = ClassName(
                                "absolute inset-0 pointer-events-none"
                            )

                            style = styleOf(
                                "background" to "radial-gradient(circle at 80% 15%, rgba(81,104,255,0.18), transparent 40%)"
                            )
                        }

                        div {
                            className = ClassName(
                                "relative p-6 sm:p-7 h-full"
                            )

                            p {
                                className = ClassName(
                                    "text-[10px] uppercase tracking-[0.2em] text-[#7184d2] font-bold"
                                )

                                +"Indian Network"
                            }

                            h3 {
                                className = ClassName(
                                    "text-2xl font-bold mt-2"
                                )

                                +"East Coast"
                            }

                            p {
                                className = ClassName(
                                    "text-xs text-white/35 mt-2"
                                )

                                +"Regional freight context"
                            }

                            div {
                                className = ClassName(
                                    "relative h-48 mt-6 rounded-2xl border border-[#5269db]/15 bg-[#070c25]/70 overflow-hidden"
                                )

                                div {
                                    className = ClassName(
                                        "absolute inset-0 opacity-40"
                                    )

                                    style = styleOf(
                                        "backgroundImage" to "radial-gradient(circle, rgba(115,135,255,0.35) 1px, transparent 1px)",
                                        "backgroundSize" to "18px 18px"
                                    )
                                }

                                div {
                                    className = ClassName(
                                        "absolute left-[20%] top-[35%] w-[60%] h-px bg-gradient-to-r from-transparent via-[#607aff] to-transparent"
                                    )
                                }

                                div {
                                    className = ClassName(
                                        "absolute left-[38%] top-[35%] w-px h-[38%] bg-gradient-to-b from-[#607aff] to-transparent"
                                    )
                                }

                                div {
                                    className = ClassName(
                                        "absolute left-[68%] top-[35%] w-px h-[24%] bg-gradient-to-b from-[#607aff] to-transparent"
                                    )
                                }

                                div {
                                    className = ClassName(
                                        "absolute left-[18%] top-[31%] w-3 h-3 rounded-full bg-[#7d94ff] shadow-[0_0_20px_#7187ff]"
                                    )
                                }

                                div {
                                    className = ClassName(
                                        "absolute left-[36%] top-[68%] w-3 h-3 rounded-full bg-[#6f86ff] shadow-[0_0_20px_#7187ff]"
                                    )
                                }

                                div {
                                    className = ClassName(
                                        "absolute left-[66%] top-[68%] w-3 h-3 rounded-full bg-[#7d94ff] shadow-[0_0_20px_#7187ff]"
                                    )
                                }

                                div {
                                    className = ClassName(
                                        "absolute left-[78%] top-[31%] w-3 h-3 rounded-full bg-[#9cafed] shadow-[0_0_20px_#7187ff]"
                                    )
                                }

                                span {
                                    className = ClassName(
                                        "absolute left-[10%] top-[12%] text-[9px] text-white/50"
                                    )

                                    +"Dhamra"
                                }

                                span {
                                    className = ClassName(
                                        "absolute left-[28%] bottom-[12%] text-[9px] text-white/50"
                                    )

                                    +"Paradip"
                                }

                                span {
                                    className = ClassName(
                                        "absolute left-[58%] bottom-[12%] text-[9px] text-white/50"
                                    )

                                    +"Vizag"
                                }

                                span {
                                    className = ClassName(
                                        "absolute right-[8%] top-[12%] text-[9px] text-white/50"
                                    )

                                    +"Haldia"
                                }

                                div {
                                    className = ClassName(
                                        "absolute left-[43%] top-[22%] text-[8px] uppercase tracking-[0.18em] text-[#6478c7]"
                                    )

                                    +"INDIA"
                                }
                            }

                            div {
                                className = ClassName(
                                    "grid grid-cols-2 gap-2 mt-4"
                                )

                                INDIAN_PORTS.take(4).forEach { port ->
                                    button {
                                        key = Key(port)

                                        className = ClassName(
                                            "px-3 py-2 rounded-xl border text-left text-[11px] transition-all duration-300 cursor-pointer " +
                                                if (selectedPort == port) {
                                                    "bg-[#435be5]/20 border-[#7085ff]/50 text-white shadow-[0_0_25px_rgba(76,98,255,0.12)]"
                                                } else {
                                                    "bg-white/[0.02] border-white/[0.06] text-white/40 hover:text-white/80 hover:border-[#536be8]/30"
                                                }
                                        )

                                        onClick = {
                                            setSelectedPort(port)
                                        }

                                        +port
                                    }
                                }
                            }
                        }
                    }
                }

                div {
                    className = ClassName(
                        "rounded-[28px] border border-[#566ff0]/25 bg-[#0a1130]/70 backdrop-blur-2xl overflow-hidden shadow-[0_30px_100px_rgba(0,0,0,0.35)] mb-6"
                    )

                    div {
                        className = ClassName(
                            "p-5 sm:p-7"
                        )

                        div {
                            className = ClassName(
                                "flex flex-col lg:flex-row lg:items-center lg:justify-between gap-5 mb-7"
                            )

                            div {
                                h3 {
                                    className = ClassName(
                                        "text-xl font-bold"
                                    )

                                    +"Freight Market Pulse"
                                }

                                p {
                                    className = ClassName(
                                        "text-xs text-white/35 mt-1"
                                    )

                                    +"Historical performance and projected movement"
                                }
                            }

                            div {
                                className = ClassName(
                                    "flex items-center gap-1 p-1 rounded-xl bg-black/25 border border-white/[0.06]"
                                )

                                listOf(
                                    "7D",
                                    "30D",
                                    "90D",
                                    "ALL"
                                ).forEach { range ->
                                    button {
                                        key = Key(range)

                                        className = ClassName(
                                            "px-4 py-2 rounded-lg text-[11px] font-bold cursor-pointer transition-all " +
                                                if (selectedRange == range) {
                                                    "bg-[#536cf4] text-white shadow-[0_5px_20px_rgba(83,108,244,0.3)]"
                                                } else {
                                                    "text-white/35 hover:text-white/70"
                                                }
                                        )

                                        onClick = {
                                            setSelectedRange(range)
                                        }

                                        +range
                                    }
                                }
                            }
                        }

                        div {
                            className = ClassName(
                                "relative h-[300px] rounded-2xl border border-white/[0.05] bg-[#070c24]/80 overflow-hidden"
                            )

                            div {
                                className = ClassName(
                                    "absolute inset-0 pointer-events-none"
                                )

                                style = styleOf(
                                    "backgroundImage" to """
                                        linear-gradient(rgba(91,111,230,0.06) 1px, transparent 1px),
                                        linear-gradient(90deg, rgba(91,111,230,0.06) 1px, transparent 1px)
                                    """.trimIndent(),
                                    "backgroundSize" to "70px 50px"
                                )
                            }

                            div {
                                className = ClassName(
                                    "absolute left-0 right-0 top-1/4 border-t border-white/[0.04]"
                                )
                            }

                            div {
                                className = ClassName(
                                    "absolute left-0 right-0 top-1/2 border-t border-white/[0.04]"
                                )
                            }

                            div {
                                className = ClassName(
                                    "absolute left-0 right-0 top-3/4 border-t border-white/[0.04]"
                                )
                            }

                            div {
                                className = ClassName(
                                    "absolute left-0 bottom-0 top-0 w-px bg-white/[0.05]"
                                )
                            }

                            div {
                                className = ClassName(
                                    "absolute right-0 bottom-0 top-0 w-px bg-white/[0.05]"
                                )
                            }

                            div {
                                className = ClassName(
                                    "absolute bottom-0 left-0 right-0 h-1/2 pointer-events-none"
                                )

                                style = styleOf(
                                    "background" to "linear-gradient(to top, rgba(70,93,255,0.12), transparent)"
                                )
                            }

                            div {
                                className = ClassName(
                                    "absolute inset-x-3 sm:inset-x-6 bottom-7 top-8 flex items-end gap-[2px]"
                                )

                                allChartValues.forEachIndexed { index, value ->
                                    val isForecast =
                                        index >= selectedPrices.size

                                    val height =
                                        (((value - chartMin) / chartSpread) * 82.0 + 8.0)
                                            .coerceIn(5.0, 96.0)

                                    div {
                                        key = Key(
                                            "bar-$index-$value"
                                        )

                                        className = ClassName(
                                            "group relative flex-1 min-w-[2px] rounded-t-md transition-all duration-500 hover:scale-y-105 origin-bottom " +
                                                if (isForecast) {
                                                    "bg-gradient-to-t from-[#5567d8]/20 via-[#8294ff]/50 to-[#b2beff]"
                                                } else {
                                                    "bg-gradient-to-t from-[#2638ad] via-[#536cf4] to-[#91a4ff]"
                                                }
                                        )

                                        style = styleOf(
                                            "height" to "${height}%"
                                        )

                                        div {
                                            className = ClassName(
                                                "absolute -top-9 left-1/2 -translate-x-1/2 z-30 whitespace-nowrap px-2.5 py-1.5 rounded-lg bg-[#050817] border border-[#647aff]/30 text-[9px] font-bold opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none shadow-xl"
                                            )

                                            if (isForecast) {
                                                +"Forecast ${formatNumber(value)}"
                                            } else {
                                                +"${selectedHistorical[index].date}: ${formatNumber(value)}"
                                            }
                                        }
                                    }
                                }
                            }

                            div {
                                className = ClassName(
                                    "absolute left-4 right-4 bottom-2 flex justify-between text-[9px] uppercase tracking-wider text-white/20 pointer-events-none"
                                )

                                span {
                                    +(
                                        selectedHistorical
                                            .firstOrNull()
                                            ?.date
                                            ?.toString()
                                            ?: ""
                                    )
                                }

                                span {
                                    className = ClassName(
                                        "text-[#788cff]"
                                    )

                                    +"Forecast →"
                                }

                                span {
                                    +(
                                        selectedHistorical
                                            .lastOrNull()
                                            ?.date
                                            ?.toString()
                                            ?: ""
                                    )
                                }
                            }
                        }

                        div {
                            className = ClassName(
                                "flex flex-wrap items-center gap-5 mt-5"
                            )

                            div {
                                className = ClassName(
                                    "flex items-center gap-2 text-[10px] text-white/35"
                                )

                                div {
                                    className = ClassName(
                                        "w-3 h-1 rounded-full bg-[#637aff]"
                                    )
                                }

                                +"Historical"
                            }

                            div {
                                className = ClassName(
                                    "flex items-center gap-2 text-[10px] text-white/35"
                                )

                                div {
                                    className = ClassName(
                                        "w-3 h-1 rounded-full bg-[#a2b0ff]"
                                    )
                                }

                                +"14-day projection"
                            }
                        }
                    }
                }

                div {
                    className = ClassName(
                        "grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6"
                    )

                    div {
                        className = ClassName(
                            "relative rounded-[26px] border border-[#566ff0]/25 bg-[#0a1130]/70 backdrop-blur-2xl p-6 overflow-hidden shadow-[0_25px_80px_rgba(0,0,0,0.28)]"
                        )

                        div {
                            className = ClassName(
                                "absolute -right-10 -bottom-10 w-32 h-32 rounded-full bg-[#5169ff]/10 blur-2xl"
                            )
                        }

                        p {
                            className = ClassName(
                                "text-[10px] uppercase tracking-[0.2em] text-[#7184d2] font-bold"
                            )

                            +"Market Selection"
                        }

                        h3 {
                            className = ClassName(
                                "text-xl font-bold mt-2"
                            )

                            +selectedPort
                        }

                        p {
                            className = ClassName(
                                "text-xs text-white/35 mt-1"
                            )

                            +selectedRegion
                        }

                        div {
                            className = ClassName(
                                "relative mt-6"
                            )

                            button {
                                className = ClassName(
                                    "w-full px-4 py-3 rounded-xl bg-black/20 border border-white/[0.08] text-left text-sm hover:border-[#637aff]/40 transition-colors cursor-pointer"
                                )

                                onClick = {
                                    setShowPorts(!showPorts)
                                }

                                +"Port: $selectedPort"

                                span {
                                    className = ClassName(
                                        "float-right text-white/30"
                                    )

                                    +"⌄"
                                }
                            }

                            if (showPorts) {
                                div {
                                    className = ClassName(
                                        "absolute top-full left-0 right-0 mt-2 z-50 rounded-xl overflow-hidden bg-[#0b1234] border border-[#637aff]/30 shadow-2xl"
                                    )

                                    INDIAN_PORTS.forEach { port ->
                                        button {
                                            key = Key(
                                                "port-menu-$port"
                                            )

                                            className = ClassName(
                                                "block w-full px-4 py-3 text-left text-xs text-white/60 hover:text-white hover:bg-[#536cf4]/15 cursor-pointer"
                                            )

                                            onClick = {
                                                setSelectedPort(port)
                                                setShowPorts(false)
                                            }

                                            +port
                                        }
                                    }
                                }
                            }

                            button {
                                className = ClassName(
                                    "w-full mt-2 px-4 py-3 rounded-xl bg-black/20 border border-white/[0.08] text-left text-sm hover:border-[#637aff]/40 transition-colors cursor-pointer"
                                )

                                onClick = {
                                    setShowRegions(!showRegions)
                                }

                                +"Region: $selectedRegion"

                                span {
                                    className = ClassName(
                                        "float-right text-white/30"
                                    )

                                    +"⌄"
                                }
                            }

                            if (showRegions) {
                                div {
                                    className = ClassName(
                                        "relative mt-2 z-40 rounded-xl overflow-hidden bg-[#0b1234] border border-[#637aff]/30 shadow-2xl"
                                    )

                                    INDIAN_REGIONS.forEach { region ->
                                        button {
                                            key = Key(
                                                "region-menu-$region"
                                            )

                                            className = ClassName(
                                                "block w-full px-4 py-3 text-left text-xs text-white/60 hover:text-white hover:bg-[#536cf4]/15 cursor-pointer"
                                            )

                                            onClick = {
                                                setSelectedRegion(region)
                                                setShowRegions(false)
                                            }

                                            +region
                                        }
                                    }
                                }
                            }
                        }
                    }

                    div {
                        className = ClassName(
                            "relative rounded-[26px] border border-[#566ff0]/25 bg-[#0a1130]/70 backdrop-blur-2xl p-6 overflow-hidden shadow-[0_25px_80px_rgba(0,0,0,0.28)]"
                        )

                        p {
                            className = ClassName(
                                "text-[10px] uppercase tracking-[0.2em] text-[#7184d2] font-bold"
                            )

                            +"Forecast Engine"
                        }

                        h3 {
                            className = ClassName(
                                "text-xl font-bold mt-2"
                            )

                            +"14-Day Outlook"
                        }

                        div {
                            className = ClassName(
                                "flex items-center gap-4 mt-6"
                            )

                            div {
                                className = ClassName(
                                    "w-14 h-14 rounded-2xl bg-[#536cf4]/10 border border-[#7187ff]/20 flex items-center justify-center"
                                )

                                span {
                                    className = ClassName(
                                        "text-xl " +
                                            if (forecastChange >= 0) {
                                                "text-emerald-300"
                                            } else {
                                                "text-red-300"
                                            }
                                    )

                                    +(if (forecastChange >= 0) "↗" else "↘")
                                }
                            }

                            div {
                                p {
                                    className = ClassName(
                                        "text-2xl font-bold"
                                    )

                                    +formatPercent(forecastChange)
                                }

                                p {
                                    className = ClassName(
                                        "text-[10px] uppercase tracking-wider text-white/30 mt-1"
                                    )

                                    +"Expected movement"
                                }
                            }
                        }

                        div {
                            className = ClassName(
                                "mt-6 h-2 rounded-full bg-white/[0.05] overflow-hidden"
                            )

                            div {
                                className = ClassName(
                                    "h-full rounded-full bg-gradient-to-r from-[#394ec3] to-[#8497ff]"
                                )

                                style = styleOf(
                                    "width" to "${(kotlin.math.abs(forecastChange) * 8.0).coerceIn(8.0, 100.0)}%"
                                )
                            }
                        }

                        p {
                            className = ClassName(
                                "text-xs text-white/35 mt-4 leading-relaxed"
                            )

                            +"Projection is derived from recent historical BDI movement."
                        }
                    }

                    div {
                        className = ClassName(
                            "relative rounded-[26px] border border-[#566ff0]/25 bg-[#0a1130]/70 backdrop-blur-2xl p-6 overflow-hidden shadow-[0_25px_80px_rgba(0,0,0,0.28)]"
                        )

                        p {
                            className = ClassName(
                                "text-[10px] uppercase tracking-[0.2em] text-[#7184d2] font-bold"
                            )

                            +"Market Intelligence"
                        }

                        h3 {
                            className = ClassName(
                                "text-xl font-bold mt-2"
                            )

                            +"Analyst Signal"
                        }

                        p {
                            className = ClassName(
                                "text-sm text-[#c8d2ff] leading-relaxed mt-5"
                            )

                            +insight
                        }

                        div {
                            className = ClassName(
                                "mt-5 px-4 py-3 rounded-xl bg-[#536cf4]/5 border border-[#637aff]/15"
                            )

                            p {
                                className = ClassName(
                                    "text-[10px] uppercase tracking-wider text-[#7184d2]"
                                )

                                +"Data Integrity"
                            }

                            p {
                                className = ClassName(
                                    "text-xs text-white/45 mt-1"
                                )

                                +"Historical values loaded directly from backend CSV"
                            }
                        }
                    }
                }

                div {
                    className = ClassName(
                        "relative rounded-[28px] border border-[#566ff0]/25 bg-gradient-to-br from-[#111b50]/80 via-[#0a1130]/75 to-[#080c24]/80 backdrop-blur-2xl p-6 sm:p-8 overflow-hidden shadow-[0_30px_100px_rgba(0,0,0,0.35)]"
                    )

                    div {
                        className = ClassName(
                            "absolute -right-24 -top-24 w-72 h-72 rounded-full bg-[#566eff]/10 blur-3xl"
                        )
                    }

                    div {
                        className = ClassName(
                            "relative flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6"
                        )

                        div {
                            div {
                                className = ClassName(
                                    "inline-flex items-center gap-2 text-[10px] uppercase tracking-[0.2em] text-[#7d90e8] font-bold"
                                )

                                div {
                                    className = ClassName(
                                        "w-1.5 h-1.5 rounded-full bg-[#7790ff] shadow-[0_0_12px_#7790ff]"
                                    )
                                }

                                +"Freight Intelligence"
                            }

                            h2 {
                                className = ClassName(
                                    "text-2xl sm:text-3xl font-bold mt-3"
                                )

                                +"Indian East Coast Market"
                            }

                            p {
                                className = ClassName(
                                    "text-sm text-white/40 mt-2 max-w-2xl leading-relaxed"
                                )

                                +"${selectedPort} • ${selectedRegion} • ${historical.size} historical observations • $indexName"
                            }
                        }

                        div {
                            className = ClassName(
                                "flex items-center gap-3"
                            )

                            div {
                                className = ClassName(
                                    "px-4 py-3 rounded-xl bg-black/20 border border-white/[0.07]"
                                )

                                p {
                                    className = ClassName(
                                        "text-[9px] uppercase tracking-wider text-white/25"
                                    )

                                    +"Data Source"
                                }

                                p {
                                    className = ClassName(
                                        "text-xs font-semibold text-white/70 mt-1"
                                    )

                                    +"Backend CSV"
                                }
                            }

                            div {
                                className = ClassName(
                                    "px-4 py-3 rounded-xl bg-[#536cf4]/10 border border-[#637aff]/20"
                                )

                                p {
                                    className = ClassName(
                                        "text-[9px] uppercase tracking-wider text-[#7e90e5]"
                                    )

                                    +"Status"
                                }

                                p {
                                    className = ClassName(
                                        "text-xs font-semibold text-emerald-300 mt-1"
                                    )

                                    +"CONNECTED"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}