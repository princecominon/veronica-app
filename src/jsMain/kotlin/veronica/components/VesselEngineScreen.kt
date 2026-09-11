package veronica.components

import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.fetch.RequestInit
import kotlin.js.JSON
import kotlin.js.json
import js.coroutines.awaitCancellation
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
import react.useEffect
import react.useState
import veronica.core.styleOf
import web.cssom.ClassName

private val SHIP_NAMES = listOf(
    "VERONICA",
    "EVER GIVEN",
    "MSC OSCAR",
    "COSCO SHIPPING",
    "MAERSK MCKINNEY",
    "HMM ALGECIRAS",
)

private val SHIP_IMAGES = listOf(
    "https://images.unsplash.com/photo-1713127563336-666289e1bd5f?auto=format&fit=crop&w=1200&q=85",
    "https://images.unsplash.com/photo-1746481594808-b2b0ddbe5e9e?auto=format&fit=crop&w=1200&q=85",
    "https://images.unsplash.com/photo-1783780163537-c5bda11d6a5c?auto=format&fit=crop&w=1200&q=85",
    "https://images.unsplash.com/photo-1784913110523-f1636e5dbb6d?auto=format&fit=crop&w=1200&q=85",
    "https://images.unsplash.com/photo-1780382880605-ea7b0dbb6765?auto=format&fit=crop&w=1200&q=85",
    "https://images.unsplash.com/photo-1494412574643-ff11b0e5f6c9?auto=format&fit=crop&w=1200&q=85",
)

private val SHIP_DESCRIPTIONS = listOf(
    "Smart Container Carrier • 14,000 TEU",
    "Ultra Large Container • 23,000 TEU",
    "Mega Container Ship • 23,000 TEU",
    "Global Container Line • 21,000 TEU",
    "Ultra Mega Carrier • 23,000 TEU",
    "Advanced Container • 23,000 TEU",
)

private val SHIP_RATES = listOf(
    "$3,240",
    "$3,680",
    "$4,120",
    "$4,890",
    "$5,240",
    "$5,680",
)

private const val AUTO_ADVANCE_MS = 4500

private data class FloatingObject(
    val left: Double,
    val top: Double,
    val size: Double,
    val duration: Double,
    val delay: Double,
    val driftX: Double,
    val driftY: Double,
    val rotateX: Double,
    val rotateY: Double,
    val rotateZ: Double,
    val opacity: Double,
    val type: String,
)

private val FLOATING_OBJECTS = listOf(
    FloatingObject(3.0, 10.0, 102.0, 18.0, -2.0, 120.0, 150.0, 180.0, 130.0, 220.0, 0.30, "cube"),
    FloatingObject(10.0, 31.0, 72.0, 22.0, -8.0, 150.0, -120.0, 260.0, 180.0, 100.0, 0.24, "cube"),
    FloatingObject(18.0, 60.0, 84.0, 20.0, -5.0, 110.0, 140.0, 120.0, 240.0, 70.0, 0.28, "diamond"),
    FloatingObject(27.0, 82.0, 126.0, 24.0, -12.0, -140.0, -110.0, 210.0, 140.0, 300.0, 0.22, "bar"),
    FloatingObject(36.0, 14.0, 78.0, 20.0, -10.0, 130.0, 100.0, 320.0, 70.0, 190.0, 0.26, "cube"),
    FloatingObject(46.0, 43.0, 96.0, 19.0, -3.0, 160.0, -130.0, 160.0, 280.0, 120.0, 0.27, "diamond"),
    FloatingObject(55.0, 8.0, 84.0, 23.0, -16.0, -130.0, 150.0, 240.0, 110.0, 250.0, 0.23, "bar"),
    FloatingObject(64.0, 66.0, 114.0, 21.0, -7.0, 120.0, -150.0, 140.0, 220.0, 40.0, 0.29, "cube"),
    FloatingObject(73.0, 25.0, 72.0, 17.0, -9.0, -120.0, 160.0, 300.0, 90.0, 210.0, 0.30, "diamond"),
    FloatingObject(83.0, 79.0, 108.0, 26.0, -6.0, -150.0, -120.0, 200.0, 160.0, 280.0, 0.23, "cube"),
    FloatingObject(91.0, 40.0, 78.0, 19.0, -11.0, 110.0, 130.0, 260.0, 120.0, 160.0, 0.27, "bar"),
    FloatingObject(95.0, 12.0, 66.0, 15.0, -4.0, -90.0, 140.0, 340.0, 100.0, 50.0, 0.29, "diamond"),
    FloatingObject(5.0, 48.0, 66.0, 14.0, -1.0, 130.0, -90.0, 180.0, 300.0, 140.0, 0.22, "bar"),
    FloatingObject(22.0, 50.0, 78.0, 27.0, -18.0, -110.0, 140.0, 280.0, 180.0, 200.0, 0.21, "cube"),
    FloatingObject(68.0, 50.0, 72.0, 13.0, -14.0, -80.0, -140.0, 240.0, 140.0, 320.0, 0.31, "diamond"),
    FloatingObject(87.0, 58.0, 96.0, 18.0, -20.0, 140.0, 100.0, 150.0, 270.0, 90.0, 0.26, "cube"),
    FloatingObject(14.0, 20.0, 90.0, 25.0, -15.0, 105.0, 125.0, 200.0, 175.0, 40.0, 0.25, "cube"),
    FloatingObject(30.0, 38.0, 70.0, 16.0, -21.0, -125.0, 105.0, 290.0, 145.0, 180.0, 0.28, "diamond"),
    FloatingObject(42.0, 74.0, 120.0, 28.0, -9.0, 145.0, -105.0, 170.0, 250.0, 315.0, 0.24, "bar"),
    FloatingObject(58.0, 28.0, 88.0, 21.0, -13.0, -115.0, 135.0, 235.0, 160.0, 75.0, 0.27, "cube"),
    FloatingObject(76.0, 68.0, 102.0, 23.0, -17.0, 125.0, -130.0, 315.0, 190.0, 145.0, 0.23, "diamond"),
    FloatingObject(81.0, 15.0, 78.0, 19.0, -5.0, -105.0, 115.0, 155.0, 285.0, 225.0, 0.29, "bar"),
    FloatingObject(2.0, 76.0, 84.0, 22.0, -10.0, 115.0, -100.0, 225.0, 155.0, 305.0, 0.25, "cube"),
    FloatingObject(93.0, 72.0, 114.0, 24.0, -7.0, -135.0, -115.0, 185.0, 230.0, 95.0, 0.22, "diamond"),
)

private external interface FloatingObjectProps : Props {
    var floatingObject: FloatingObject?
}

private val FloatingObject3D = FC<FloatingObjectProps> { props ->
    val item = props.floatingObject ?: FLOATING_OBJECTS.first()

    div {
        className = ClassName(
            "absolute pointer-events-none will-change-transform"
        )

        style = styleOf(
            "left" to "${item.left}%",
            "top" to "${item.top}%",
            "width" to "${item.size}px",
            "height" to "${item.size}px",
            "opacity" to item.opacity,
            "perspective" to "900px",
            "transformStyle" to "preserve-3d",
            "animation" to "veronica-object-float ${item.duration}s ease-in-out ${item.delay}s infinite alternate"
        )

        when (item.type) {
            "cube" -> {
                div {
                    className = ClassName(
                        "relative w-full h-full"
                    )

                    style = styleOf(
                        "transformStyle" to "preserve-3d",
                        "animation" to "veronica-object-spin ${item.duration * 1.4}s linear ${item.delay}s infinite"
                    )

                    div {
                        className = ClassName(
                            "absolute inset-0 border border-[#d8dee8]/55 bg-[#d8dee8]/[0.055]"
                        )

                        style = styleOf(
                            "transform" to "translateZ(${item.size / 2}px)"
                        )
                    }

                    div {
                        className = ClassName(
                            "absolute inset-0 border border-[#b8c2d0]/40 bg-[#b8c2d0]/[0.035]"
                        )

                        style = styleOf(
                            "transform" to "rotateY(180deg) translateZ(${item.size / 2}px)"
                        )
                    }

                    div {
                        className = ClassName(
                            "absolute inset-0 border border-[#e2e7ef]/48 bg-[#e2e7ef]/[0.04]"
                        )

                        style = styleOf(
                            "transform" to "rotateY(90deg) translateZ(${item.size / 2}px)"
                        )
                    }

                    div {
                        className = ClassName(
                            "absolute inset-0 border border-[#a9b3c2]/34 bg-[#a9b3c2]/[0.03]"
                        )

                        style = styleOf(
                            "transform" to "rotateY(-90deg) translateZ(${item.size / 2}px)"
                        )
                    }

                    div {
                        className = ClassName(
                            "absolute inset-0 border border-[#eef1f5]/45 bg-[#eef1f5]/[0.03]"
                        )

                        style = styleOf(
                            "transform" to "rotateX(90deg) translateZ(${item.size / 2}px)"
                        )
                    }

                    div {
                        className = ClassName(
                            "absolute inset-0 border border-[#a2adbd]/34 bg-[#a2adbd]/[0.03]"
                        )

                        style = styleOf(
                            "transform" to "rotateX(-90deg) translateZ(${item.size / 2}px)"
                        )
                    }
                }
            }

            "diamond" -> {
                div {
                    className = ClassName(
                        "absolute inset-0 rotate-45 border border-[#e2e7ee]/55 bg-[#c8d0db]/[0.05]"
                    )

                    style = styleOf(
                        "transform" to "rotateX(${item.rotateX}deg) rotateY(${item.rotateY}deg) rotateZ(45deg)",
                        "boxShadow" to "0 0 36px rgba(210,218,229,0.16)"
                    )
                }
            }

            else -> {
                div {
                    className = ClassName(
                        "absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 rounded-full border border-[#d6dde7]/48 bg-gradient-to-br from-white/[0.08] to-transparent"
                    )

                    style = styleOf(
                        "width" to "100%",
                        "height" to "38%",
                        "transform" to "rotateX(68deg) rotateZ(${item.rotateZ}deg)",
                        "boxShadow" to "0 0 40px rgba(205,214,227,0.14)"
                    )
                }

                div {
                    className = ClassName(
                        "absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 rounded-full border border-[#b6c0cf]/38"
                    )

                    style = styleOf(
                        "width" to "75%",
                        "height" to "28%",
                        "transform" to "rotateX(68deg) rotateZ(${-item.rotateZ}deg)"
                    )
                }
            }
        }
    }
}

// 1. Defined an interface so we can pass a navigation function
external interface VesselEngineScreenProps : Props {
    var onNavigate: ((String) -> Unit)?
}

// 2. Updated to use VesselEngineScreenProps
val VesselEngineScreen = FC<VesselEngineScreenProps> { props ->
    val (shipIndex, setShipIndex) = useState(0)
    val (dragStartX, setDragStartX) = useState(0.0)
    val (translateX, setTranslateX) = useState(0.0)
    val (isDragging, setIsDragging) = useState(false)

    val (imoInput, setImoInput) = useState("")
    val (isLoading, setIsLoading) = useState(false)
    val (errorMessage, setErrorMessage) = useState<String?>(null)
    val (vesselData, setVesselData) = useState<dynamic>(null)

    val currentShip = SHIP_NAMES[shipIndex]
    val currentShipDesc = SHIP_DESCRIPTIONS[shipIndex]
    val currentRate = SHIP_RATES[shipIndex]

    useEffect(shipIndex, isDragging) {
        if (isDragging) {
            awaitCancellation { }
        } else {
            val timeoutId = js("window").setTimeout({
                setShipIndex((shipIndex + 1) % SHIP_NAMES.size)
            }, AUTO_ADVANCE_MS) as Int

            awaitCancellation {
                js("window").clearTimeout(timeoutId)
            }
        }
    }

    react.dom.html.ReactHTML.style {
        +"""
            @keyframes veronica-orbit {
                0% {
                    transform: rotate(0deg) scaleX(1);
                    opacity: 0.32;
                }

                50% {
                    transform: rotate(180deg) scaleX(1.08);
                    opacity: 0.55;
                }

                100% {
                    transform: rotate(360deg) scaleX(1);
                    opacity: 0.32;
                }
            }

            @keyframes veronica-orbit-reverse {
                0% {
                    transform: rotate(360deg) scaleY(1);
                    opacity: 0.22;
                }

                50% {
                    transform: rotate(180deg) scaleY(1.12);
                    opacity: 0.42;
                }

                100% {
                    transform: rotate(0deg) scaleY(1);
                    opacity: 0.22;
                }
            }

            @keyframes veronica-pulse {
                0%, 100% {
                    box-shadow: 0 0 30px rgba(59,91,254,0.22);
                }

                50% {
                    box-shadow: 0 0 55px rgba(59,91,254,0.42);
                }
            }

            @keyframes veronica-float {
                0%, 100% {
                    transform: translateY(0px) rotateX(0deg) rotateY(0deg);
                }

                50% {
                    transform: translateY(-10px) rotateX(1.2deg) rotateY(-1.5deg);
                }
            }

            @keyframes veronica-ship-enter {
                0% {
                    opacity: 0;
                    transform: translateY(28px) scale(0.88) rotateX(7deg);
                    filter: blur(12px);
                }

                60% {
                    opacity: 1;
                    filter: blur(0px);
                }

                100% {
                    opacity: 1;
                    transform: translateY(0) scale(1) rotateX(0deg);
                    filter: blur(0px);
                }
            }

            @keyframes veronica-image-drift {
                0% {
                    transform: scale(1.03) translate3d(0, 0, 0);
                }

                50% {
                    transform: scale(1.10) translate3d(-1.5%, -1%, 0);
                }

                100% {
                    transform: scale(1.06) translate3d(1%, 1%, 0);
                }
            }

            @keyframes veronica-sheen {
                0% {
                    transform: translateX(-120%) skewX(-18deg);
                    opacity: 0;
                }

                20% {
                    opacity: 0.16;
                }

                45% {
                    opacity: 0.03;
                }

                100% {
                    transform: translateX(160%) skewX(-18deg);
                    opacity: 0;
                }
            }

            @keyframes veronica-grid {
                0%, 100% {
                    transform: perspective(500px) rotateX(70deg) translateY(0);
                    opacity: 0.14;
                }

                50% {
                    transform: perspective(500px) rotateX(70deg) translateY(10px);
                    opacity: 0.23;
                }
            }

            @keyframes veronica-glow-text {
                0%, 100% {
                    text-shadow: 0 0 18px rgba(109,139,255,0.15);
                }

                50% {
                    text-shadow: 0 0 30px rgba(109,139,255,0.35);
                }
            }

            @keyframes veronica-object-float {
                0% {
                    transform: translate3d(0, 0, 0);
                }

                35% {
                    transform: translate3d(24px, -30px, 90px);
                }

                70% {
                    transform: translate3d(-18px, 22px, -40px);
                }

                100% {
                    transform: translate3d(36px, -12px, 60px);
                }
            }

            @keyframes veronica-object-spin {
                0% {
                    transform: rotateX(0deg) rotateY(0deg) rotateZ(0deg);
                }

                50% {
                    transform: rotateX(180deg) rotateY(220deg) rotateZ(90deg);
                }

                100% {
                    transform: rotateX(360deg) rotateY(440deg) rotateZ(180deg);
                }
            }
        """.trimIndent()
    }

    // MAIN CONTAINER (Global Background applied here)
    div {
        className = ClassName(
            "w-full min-h-screen relative flex flex-col bg-[#070b20] overflow-hidden select-none"
        )
        
        style = styleOf(
            "background" to "radial-gradient(circle at 50% 38%, rgba(59,91,254,0.24), transparent 34%), linear-gradient(180deg, #080d28 0%, #111a66 38%, #070b20 100%)",
            "perspective" to "1400px"
        )

        // ==========================================
        // NEW BACK BUTTON
        // ==========================================
        div {
            className = ClassName("absolute top-24 left-6 z-[100] sm:left-12")
            button {
                className = ClassName(
                    "flex items-center gap-2 px-4 py-2 rounded-xl bg-[#0c1130]/80 hover:bg-[#3b5bfe]/40 text-[#8fa6ff] hover:text-white backdrop-blur-md transition-all text-sm font-medium border border-[#3b5bfe]/30 shadow-lg cursor-pointer"
                )
                onClick = {
                    if (props.onNavigate != null) {
                        props.onNavigate?.invoke("home")
                    } else {
                        window.location.reload()
                    }
                }
                span { +"←" }
                span { +"Back to Home" }
            }
        }

        // ==========================================
        // 3D BACKGROUND LAYER (Spans the whole file)
        // ==========================================
        div {
            className = ClassName(
                "absolute inset-0 pointer-events-none overflow-hidden z-[1]"
            )

            FLOATING_OBJECTS.forEachIndexed { index, item ->
                FloatingObject3D {
                    key = Key("bg-object-$index")
                    floatingObject = item
                }
            }

            div {
                className = ClassName(
                    "absolute left-1/2 top-[40%] w-[1200px] h-[500px] -translate-x-1/2 -translate-y-1/2 border border-[#6d8bff]/10 rounded-[50%]"
                )
                style = styleOf(
                    "animation" to "veronica-orbit 18s linear infinite"
                )
            }

            div {
                className = ClassName(
                    "absolute left-1/2 top-[40%] w-[900px] h-[340px] -translate-x-1/2 -translate-y-1/2 border border-[#3b5bfe]/20 rounded-[50%]"
                )
                style = styleOf(
                    "animation" to "veronica-orbit-reverse 12s linear infinite"
                )
            }

            div {
                className = ClassName(
                    "absolute left-1/2 top-[45%] w-[900px] h-[320px] -translate-x-1/2 opacity-20"
                )
                style = styleOf(
                    "backgroundImage" to "linear-gradient(rgba(109,139,255,0.16) 1px, transparent 1px), linear-gradient(90deg, rgba(109,139,255,0.16) 1px, transparent 1px)",
                    "backgroundSize" to "50px 50px",
                    "maskImage" to "linear-gradient(to bottom, transparent, black 25%, transparent 100%)",
                    "animation" to "veronica-grid 8s ease-in-out infinite"
                )
            }
        }

        // ==========================================
        // FOREGROUND BLOCK 1: CAROUSEL
        // ==========================================
        div {
            className = ClassName(
                "min-h-screen relative z-20 pt-20 shrink-0"
            )

            div {
                className = ClassName(
                    "text-center"
                )

                p {
                    className = ClassName(
                        "text-[#8fa6ff] text-xs uppercase tracking-[0.35em] font-semibold"
                    )
                    style = styleOf(
                        "animation" to "veronica-glow-text 4s ease-in-out infinite"
                    )
                    +"Vessel Engine"
                }
            }

            div {
                className = ClassName(
                    "relative mx-auto mt-5 w-full max-w-5xl h-[650px] overflow-visible"
                )

                onMouseDown = { event ->
                    setIsDragging(true)
                    setDragStartX(event.clientX.toDouble())
                }
                onMouseMove = { event ->
                    if (isDragging) {
                        setTranslateX(
                            event.clientX.toDouble() - dragStartX
                        )
                    }
                }
                onMouseUp = { event ->
                    if (isDragging) {
                        val delta = event.clientX.toDouble() - dragStartX
                        if (delta > 90) {
                            setShipIndex((shipIndex - 1 + SHIP_NAMES.size) % SHIP_NAMES.size)
                        } else if (delta < -90) {
                            setShipIndex((shipIndex + 1) % SHIP_NAMES.size)
                        }
                        setTranslateX(0.0)
                        setIsDragging(false)
                    }
                }
                onMouseLeave = {
                    setTranslateX(0.0)
                    setIsDragging(false)
                }
                onTouchStart = { event ->
                    if ((event.touches?.length ?: 0) > 0) {
                        setIsDragging(true)
                        setDragStartX(event.touches!![0].clientX.toDouble())
                    }
                }
                onTouchMove = { event ->
                    if (isDragging && (event.touches?.length ?: 0) > 0) {
                        setTranslateX(event.touches!![0].clientX.toDouble() - dragStartX)
                    }
                }
                onTouchEnd = {
                    if (isDragging) {
                        if (translateX > 80) {
                            setShipIndex((shipIndex - 1 + SHIP_NAMES.size) % SHIP_NAMES.size)
                        } else if (translateX < -80) {
                            setShipIndex((shipIndex + 1) % SHIP_NAMES.size)
                        }
                        setTranslateX(0.0)
                        setIsDragging(false)
                    }
                }

                div {
                    className = ClassName(
                        "absolute inset-0 flex items-center justify-center"
                    )
                    style = styleOf(
                        "transform" to "translateX(${translateX}px) translateY(-4px)",
                        "transition" to if (isDragging) "none" else "transform 420ms cubic-bezier(0.22,1,0.36,1)"
                    )

                    div {
                        key = Key(shipIndex.toString())
                        className = ClassName(
                            "relative w-[440px] h-[440px] sm:w-[500px] sm:h-[500px] rounded-[42px] overflow-hidden border border-[#6d8bff]/30 bg-[#0b1238]/80 backdrop-blur-xl"
                        )
                        style = styleOf(
                            "animation" to "veronica-ship-enter 0.7s cubic-bezier(0.22,1,0.36,1) both, veronica-float 5s ease-in-out infinite 0.7s, veronica-pulse 4s ease-in-out infinite",
                            "boxShadow" to "0 40px 120px rgba(4,8,35,0.7), inset 0 0 60px rgba(59,91,254,0.12)"
                        )

                        div {
                            className = ClassName("absolute inset-0")
                            style = styleOf(
                                "backgroundImage" to "url(${SHIP_IMAGES[shipIndex]})",
                                "backgroundSize" to "cover",
                                "backgroundPosition" to "center",
                                "animation" to "veronica-image-drift 14s ease-in-out infinite alternate"
                            )
                        }
                        div {
                            className = ClassName("absolute inset-0 bg-gradient-to-t from-[#050814] via-transparent to-[#1525a8]/25")
                        }
                        div {
                            className = ClassName("absolute inset-y-0 -left-1/3 w-1/3 bg-gradient-to-r from-transparent via-white/20 to-transparent pointer-events-none")
                            style = styleOf("animation" to "veronica-sheen 5s ease-in-out infinite")
                        }
                        div {
                            className = ClassName("absolute top-5 left-5 right-5 flex items-center justify-between")
                            span {
                                className = ClassName("px-3 py-1.5 rounded-full border border-white/10 bg-black/25 backdrop-blur-md text-[10px] uppercase tracking-[0.22em] text-white/65")
                                +"Live Vessel"
                            }
                            span {
                                className = ClassName("px-3 py-1.5 rounded-full border border-[#6d8bff]/25 bg-[#0c1130]/50 backdrop-blur-md text-[10px] tracking-[0.18em] text-[#b5c4ff]")
                                +"${shipIndex + 1} / ${SHIP_NAMES.size}"
                            }
                        }
                        div {
                            className = ClassName("absolute bottom-8 left-8 right-8 text-center")
                            p {
                                className = ClassName("text-3xl sm:text-4xl font-bold text-white tracking-tight")
                                style = styleOf("textShadow" to "0 4px 25px rgba(0,0,0,0.6)")
                                +currentShip
                            }
                            p {
                                className = ClassName("text-sm sm:text-base text-white/65 mt-2")
                                +currentShipDesc
                            }
                        }
                    }
                }

                if (shipIndex > 0 || SHIP_NAMES.size > 1) {
                    div {
                        className = ClassName("absolute left-5 top-1/2 -translate-y-1/2 w-14 h-14 rounded-full border border-white/10 bg-white/8 backdrop-blur-xl flex items-center justify-center cursor-pointer hover:bg-white/15 transition-all duration-200 z-30")
                        onClick = { setShipIndex((shipIndex - 1 + SHIP_NAMES.size) % SHIP_NAMES.size) }
                        span {
                            className = ClassName("text-white text-2xl leading-none")
                            +"←"
                        }
                    }
                }
                div {
                    className = ClassName("absolute right-5 top-1/2 -translate-y-1/2 w-14 h-14 rounded-full border border-white/10 bg-white/8 backdrop-blur-xl flex items-center justify-center cursor-pointer hover:bg-white/15 transition-all duration-200 z-30")
                    onClick = { setShipIndex((shipIndex + 1) % SHIP_NAMES.size) }
                    span {
                        className = ClassName("text-white text-2xl leading-none")
                        +"→"
                    }
                }

                div {
                    className = ClassName("absolute bottom-24 left-1/2 -translate-x-1/2 flex items-center gap-2 z-30")
                    SHIP_NAMES.forEachIndexed { index, _ ->
                        div {
                            key = Key(index.toString())
                            className = ClassName(
                                "h-1.5 rounded-full cursor-pointer transition-all duration-300 " +
                                    if (index == shipIndex) "w-8 bg-white" else "w-1.5 bg-white/30 hover:bg-white/55"
                            )
                            onClick = { setShipIndex(index) }
                        }
                    }
                }

                div {
                    className = ClassName("absolute bottom-4 left-1/2 -translate-x-1/2 text-center z-30")
                    p {
                        className = ClassName("text-[9px] uppercase tracking-[0.28em] text-[#8fa6ff]/65")
                        +"Swipe or drag to explore vessels"
                    }
                }
            }

            div {
                className = ClassName("absolute bottom-8 left-1/2 -translate-x-1/2 z-40 flex flex-col items-center gap-3")
                div {
                    className = ClassName("flex items-center gap-2 px-4 py-2 rounded-xl border border-[#6d8bff]/20 bg-[#080d28]/90 backdrop-blur-xl shadow-[0_15px_50px_rgba(0,0,0,0.45)]")
                    span {
                        className = ClassName("text-[8px] uppercase tracking-[0.18em] text-white/45")
                        +"Estimated Freight"
                    }
                    span {
                        className = ClassName("text-lg font-bold text-white leading-none")
                        +currentRate
                    }
                    span {
                        className = ClassName("text-[9px] text-white/45")
                        +"/ TEU"
                    }
                }

                div {
                    className = ClassName("flex items-center gap-3")
                    div {
                        className = ClassName("px-7 py-3 rounded-xl font-semibold text-sm transition-all duration-200 cursor-pointer bg-[#3b5bfe] text-white hover:bg-[#6d8bff] hover:shadow-lg hover:shadow-[#3b5bfe]/30")
                        style = styleOf("boxShadow" to "0 8px 35px rgba(59,91,254,0.28)")
                        +"Request Quote"
                    }
                    div {
                        className = ClassName("w-10 h-10 rounded-xl border border-[#6d8bff]/25 bg-[#0c1130]/70 backdrop-blur-md flex items-center justify-center cursor-pointer hover:bg-[#1525a8]/40 transition-all duration-200")
                        span {
                            className = ClassName("text-white text-sm")
                            +"⋮"
                        }
                    }
                }
            }

            h1 {
                className = ClassName("absolute bottom-1 left-0 right-0 text-center text-4xl sm:text-5xl lg:text-6xl font-bold tracking-tight pointer-events-none")
                style = styleOf(
                    "background" to "linear-gradient(135deg, #fff 0%, #3b5bfe 50%, #6d8bff 100%)",
                    "backgroundClip" to "text",
                    "-webkitBackgroundClip" to "text",
                    "WebkitTextFillColor" to "transparent",
                    "opacity" to 0.09
                )
                +"Vessel Engine"
            }
        }

        // ==========================================
        // FOREGROUND BLOCK 2: SEARCH ENGINE
        // ==========================================
        div {
            className = ClassName(
                "relative z-20 py-24 flex flex-col items-center justify-center shrink-0 border-t border-[#3b5bfe]/20 px-6"
            )

            h2 {
                className = ClassName("text-3xl font-bold text-white mb-8")
                +"Search Vessel by IMO"
            }

            div {
                className = ClassName("flex flex-col sm:flex-row items-center gap-4 justify-center w-full max-w-lg")

                input {
                    className = ClassName("px-4 py-3.5 rounded-xl border border-[#3b5bfe]/40 bg-[#1d2bd8]/10 text-white placeholder-[#c7cbe0]/50 focus:outline-none focus:border-[#7fa8ff] transition-colors w-full sm:w-64")
                    placeholder = "Enter IMO..."
                    value = imoInput
                    onChange = { event ->
                        setImoInput(event.target.value)
                        setErrorMessage(null)
                    }
                }

                button {
                    className = ClassName("btn-primary text-white font-semibold px-7 py-3.5 rounded-xl flex items-center gap-2 text-base group hover:shadow-lg hover:shadow-[#3b5bfe]/30 transition-all w-full sm:w-auto justify-center")

                    onClick = {
                        MainScope().launch {
                            setIsLoading(true)
                            setErrorMessage(null)
                            setVesselData(null)

                            try {
                                if (imoInput.isBlank()) {
                                    setErrorMessage("Please enter an IMO number.")
                                    return@launch
                                }

                                val requestOptions = json("method" to "GET").unsafeCast<RequestInit>()
                                val response = window.fetch("http://localhost:8080/api/vessel/$imoInput", requestOptions).await()
                                val textResponse = response.text().await()

                                if (!response.ok) {
                                    setErrorMessage("Error ${response.status}: $textResponse")
                                } else {
                                    try {
                                        val data = JSON.parse<dynamic>(textResponse)
                                        setVesselData(data)
                                    } catch (e: Throwable) {
                                        setErrorMessage("Invalid JSON from server: $textResponse")
                                    }
                                }
                            } catch (e: Throwable) {
                                setErrorMessage("Network request failed: ${e.message}")
                            } finally {
                                setIsLoading(false)
                            }
                        }
                    }

                    // 3. Changed button text to GET DETAILS
                    +"GET DETAILS"
                }
            }

            div {
                className = ClassName("mt-8 flex flex-col items-center w-full max-w-lg text-center")

                if (isLoading) {
                    p {
                        className = ClassName("text-[#7fa8ff]")
                        +"Fetching forecast data..."
                    }
                }

                if (errorMessage != null) {
                    div {
                        className = ClassName("p-4 rounded-xl bg-red-500/10 border border-red-500/30 text-red-400 w-full")
                        +errorMessage!!
                    }
                }

                if (vesselData != null) {
                    div {
                        className = ClassName("p-6 rounded-2xl bg-[#0c1130]/90 backdrop-blur-md border border-[#3b5bfe]/30 text-left w-full max-w-2xl mx-auto")
                        
                        val positionData = vesselData.vesselPosition ?: (if (js("Array.isArray(vesselData)") as Boolean) vesselData[0] else vesselData)
                        
                        val vName = positionData?.vessel_name?.toString() ?: positionData?.name?.toString() ?: "Unknown Vessel"
                        val imo = positionData?.imo?.toString() ?: "N/A"
                        val mmsi = positionData?.mmsi?.toString() ?: "N/A"
                        val lat = positionData?.latitude?.toString() ?: "0.0"
                        val lon = positionData?.longitude?.toString() ?: "0.0"
                        val timestamp = positionData?.timestamp?.toString() ?: "N/A"
                        val processedTime = positionData?.processed_timestamp?.toString() ?: "N/A"
                        val cog = positionData?.cog?.toString() ?: "0"
                        val heading = positionData?.heading?.toString() ?: "0"
                        val speed = positionData?.sog?.toString() ?: "0.0"
                        val glitch = positionData?.suspected_glitch?.toString() ?: "false"
                        val rawStatus = positionData?.nav_status as? Number
                        
                        val vStatus = when (rawStatus?.toInt()) {
                            0 -> "Under way using engine"
                            1 -> "At anchor"
                            2 -> "Not under command"
                            3 -> "Restricted maneuverability"
                            5 -> "Moored"
                            null -> "Unknown"
                            else -> "Status Code: $rawStatus"
                        }
                        
                        val vesselClass = positionData?.vessel_class?.toString() ?: "Panamax"
                        val dwt = positionData?.dwt?.toString() ?: "82,000 mt"
                        val loa = positionData?.loa?.toString() ?: "229m"
                        val beam = positionData?.beam?.toString() ?: "32m"
                        val capacity = positionData?.cargo_capacity?.toString() ?: "4,500 TEU"
                        val fuelCons = positionData?.fuel_consumption?.toString() ?: "35 mt/day"
                        val estFreight = positionData?.est_freight?.toString() ?: "$4,120 / TEU"
                        val portCompat = positionData?.port_compatibility?.toString() ?: "Verified (Draft OK)"
                        
                        val formattedTimestamp = if (timestamp != "N/A" && timestamp.contains("T")) {
                            timestamp.substringBefore("T") + " " + timestamp.substringAfter("T").substringBefore("Z")
                        } else {
                            timestamp
                        }
                        
                        val details = listOf(
                            "Vessel Class" to vesselClass,
                            "IMO Number" to imo,
                            "MMSI" to mmsi,
                            "DWT (Deadweight)" to dwt,
                            "Max Capacity" to capacity,
                            "LOA x Beam" to "$loa x $beam",
                            "Current Draft" to "12.4m",
                            "Speed (SOG)" to "$speed knots",
                            "Course / Heading" to "$cog° / $heading°",
                            "Est. Fuel Cons." to fuelCons,
                            "Port Compatibility" to portCompat,
                            "Est. Freight Cost" to estFreight,
                            "Last Signal" to formattedTimestamp
                        )
                        
                        div {
                            className = ClassName("flex flex-col sm:flex-row sm:items-end justify-between border-b border-[#3b5bfe]/20 pb-4 mb-5")
                            h3 {
                                className = ClassName("text-2xl font-bold text-white")
                                +vName
                            }
                            span {
                                className = ClassName("text-[#6d8bff] text-sm mt-2 sm:mt-0")
                                +"Status: $vStatus"
                            }
                        }
                        
                        div {
                            className = ClassName("grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-y-6 gap-x-4")
                            details.forEach { (label, value) ->
                                div {
                                    key = Key(label)
                                    p {
                                        className = ClassName("text-[#8fa6ff]/60 text-[10px] uppercase tracking-wider mb-1 font-semibold")
                                        +label
                                    }
                                    p {
                                        className = ClassName("text-[#e8edff] font-medium text-sm")
                                        +value
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