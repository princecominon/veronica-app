package veronica.components

import kotlinx.browser.window
import js.coroutines.awaitCancellation
import react.ChildrenBuilder
import react.FC
import react.Key
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.useEffectOnce
import react.useRef
import react.useState
import veronica.core.addScrollListener
import veronica.core.cls
import veronica.core.currentScrollY
import veronica.core.removeScrollListener
import veronica.core.styleOf
import web.cssom.ClassName
import web.cssom.Color
import web.cssom.opacity
import kotlin.js.Date

data class NavItem(
    val label: String,
    val hasDropdown: Boolean,
    val items: List<DropdownItem> = emptyList(),
)

data class DropdownItem(
    val label: String,
    val desc: String,
)

private val NAV_LINKS = listOf(
    NavItem(
        label = "Platform",
        hasDropdown = true,
        items = listOf(
            DropdownItem(
                "Vessel Engine",
                "Match cargo to vessels instantly"
            ),
            DropdownItem(
                "Rate Tracker",
                "Real-time freight rate monitoring"
            ),
            DropdownItem(
                "Risk Matrix",
                "Vendor risk scoring & alerts"
            ),
            DropdownItem(
                "Route Optimization",
                "Compare. Choose. Optimise routes."
            )
        ),
    ),
    NavItem(
        label = "Procurement",
        hasDropdown = true,
        items = listOf(
            DropdownItem(
                "Cargo Matching",
                "AI-powered cargo-vessel pairing"
            ),
            DropdownItem(
                "Freight Auction",
                "Competitive rate bidding"
            ),
            DropdownItem(
                "Vendor Portal",
                "Centralized vendor management & submissions"
            ),
            DropdownItem(
                "Berth Scheduler",
                "Port slot optimization"
            ),
        ),
    ),
    NavItem(
        "Insights",
        hasDropdown = false
    ),
    NavItem(
        "Company",
        hasDropdown = false
    )
)

external interface NavbarProps : Props {
    var onNavigate: ((String) -> Unit)?
}

external interface DropdownPanelProps : Props {
    var items: List<DropdownItem>
    var onClose: () -> Unit
    var onNavigate: ((String) -> Unit)?
}

private val DropdownPanel = FC<DropdownPanelProps> { props ->

    div {
        className = ClassName(
            "absolute top-[calc(100%+6px)] left-0 w-64 z-50 overflow-hidden"
        )

        style = styleOf(
            "transformOrigin" to "top center",
            "animation" to
                "dd-pop-in 0.22s cubic-bezier(0.22, 1, 0.36, 1) forwards",
        )

        div {
            className = ClassName(
                "rounded-2xl overflow-hidden " +
                    "bg-gradient-to-br from-[#0c1130] via-[#0f1745] to-[#1520a0] " +
                    "shadow-[0_16px_48px_-8px_rgba(0,0,0,0.55),0_4px_16px_-4px_rgba(61,91,254,0.25),inset_0_1px_0_rgba(255,255,255,0.08)] " +
                    "border border-[#3b5bfe]/40 " +
                    "p-1.5"
            )

            props.items.forEachIndexed { index, item ->

                button {
                    key = Key(index.toString())

                    onClick = {

                        if (item.label == "Vessel Engine") {
                            props.onNavigate?.invoke(
                                "vessel_engine"
                            )

                        } else if (item.label == "Rate Tracker") {
                            props.onNavigate?.invoke(
                                "Rate Tracker"
                            )

                        } else if (item.label == "Risk Matrix") {
                            props.onNavigate?.invoke(
                                "risk_matrix"
                            )

                        } else if (item.label == "Cargo Matching") {
                            props.onNavigate?.invoke(
                                "cargo_matching"
                            )

                        } else if (item.label == "Route Optimization") {
                            props.onNavigate?.invoke(
                                "route_optimization"
                            )

                        } else if (item.label == "Freight Auction") {
                            props.onNavigate?.invoke(
                                "freight_auction"
                            )

                        } else if (item.label == "Vendor Portal") {
                            props.onNavigate?.invoke(
                                "vendor_portal"
                            )
                        }

                        props.onClose()
                    }

                    className = ClassName(
                        "group w-full text-left px-4 py-3.5 rounded-xl " +
                            "hover:bg-white/10 transition-all duration-150 " +
                            "flex items-center justify-between cursor-pointer " +
                            if (index < props.items.size - 1) {
                                "border-b border-white/5"
                            } else {
                                ""
                            }
                    )

                    div {
                        div {
                            className = ClassName(
                                "text-[14px] font-medium text-[#e8edff] " +
                                    "group-hover:text-white transition-colors duration-150"
                            )

                            +item.label
                        }

                        div {
                            className = ClassName(
                                "text-[12px] text-[#8fa6ff]/70 mt-0.5 leading-tight"
                            )

                            +item.desc
                        }
                    }

                    div {
                        className = ClassName(
                            "text-[#6d8bff]/60 group-hover:text-[#7fa8ff] " +
                                "group-hover:translate-x-0.5 transition-all duration-150 " +
                                "opacity-0 group-hover:opacity-100 flex-shrink-0"
                        )

                        ArrowRight {
                            size = 13.0
                        }
                    }
                }
            }
        }
    }
}

val Navbar = FC<NavbarProps> { props ->

    val (scrolled, setScrolled) =
        useState(false)

    val (openDropdown, setOpenDropdown) =
        useState<String?>(null)

    val (isMobileMenuOpen, setIsMobileMenuOpen) = useState(false)

    val (userEmail, setUserEmail) = useState<String?>(null)

    val closeTimeoutRef =
        useRef<Int>(null)

    useEffectOnce {

        val onScroll: () -> Unit = {
            setScrolled(
                currentScrollY() > 60.0
            )
        }

        addScrollListener(
            onScroll
        )

        awaitCancellation {
            removeScrollListener(
                onScroll
            )
        }
    }

    val handleMouseEnter =
        { label: String ->

            closeTimeoutRef.current?.let {
                js("window").clearTimeout(it)
            }

            setOpenDropdown(label)
        }

    val handleMouseLeave = {

        closeTimeoutRef.current =
            js("window").setTimeout(
                {
                    setOpenDropdown(null)
                },
                250
            ) as Int
    }

    val textColor =
        if (scrolled) {
            "text-[#4b4f66]"
        } else {
            "text-[#c7cbe0]"
        }

    val textHover =
        if (scrolled) {
            "hover:text-[#12142b] hover:bg-[#f8f9fc]"
        } else {
            "hover:text-white hover:bg-white/5"
        }

    val wordmarkColor =
        if (scrolled) {
            "text-[#12142b]"
        } else {
            "text-white"
        }

    div {

        className = cls(
            "fixed top-0 left-0 right-0 z-50 transition-all duration-300",
            if (scrolled) {
                "bg-white/95 backdrop-blur-md border-b border-black/5 shadow-sm"
            } else {
                "bg-transparent"
            },
        )

        div {

            className = ClassName(
                "max-w-7xl mx-auto px-6 h-16 flex items-center justify-between"
            )

            div {

                className = ClassName(
                    "flex items-center gap-2.5 cursor-pointer"
                )

                onClick = {
                    props.onNavigate?.invoke(
                        "home"
                    )
                }

                ShipLogo()

                span {

                    className = cls(
                        "font-semibold text-lg tracking-tight",
                        wordmarkColor
                    )

                    +"Veronica"
                }
            }

            div {
                className = cls(
                    // Desktop styles forced to match original perfectly
                    "md:flex md:static md:w-auto md:bg-none md:border-none md:shadow-none md:p-0 items-center gap-0.5",
                    if (isMobileMenuOpen) {
                        // Mobile styles only
                        "flex flex-row flex-wrap justify-around absolute top-20 left-2 right-2 p-3 z-50 " +
                        "bg-gradient-to-br from-[#0c1130]/95 via-[#0f1745]/95 to-[#1520a0]/95 " +
                        "backdrop-blur-xl rounded-2xl border border-[#3b5bfe]/40 " +
                        "shadow-[0_20px_50px_rgba(0,0,0,0.5),0_0_20px_rgba(59,91,254,0.3)]"
                    } else {
                        "hidden"
                    }
                )

                NAV_LINKS.forEach { link ->

                    div {

                        key = Key(
                            link.label
                        )

                        className =
                            ClassName(
                                "relative"
                            )

                        onMouseEnter = {

                            if (link.hasDropdown) {
                                handleMouseEnter(
                                    link.label
                                )
                            }
                        }

                        onMouseLeave = {
                            handleMouseLeave()
                        }

                        button {

                            className = cls(
                                "flex items-center gap-1 font-medium rounded-lg transition-colors duration-150",
                                "px-2 py-2 text-[13px]", // Mobile specific size
                                "md:px-4 md:py-2 md:text-[15px]", // Desktop original size
                                textColor,
                                textHover,
                                if (
                                    openDropdown ==
                                    link.label
                                ) {

                                    if (scrolled) {
                                        "text-[#12142b] bg-[#f8f9fc]"
                                    } else {
                                        "text-white bg-white/10"
                                    }

                                } else {
                                    ""
                                },
                            )

                            +link.label

                            if (
                                link.hasDropdown
                            ) {

                                ChevronDown {

                                    size = 14.0

                                    className = cls(
                                        "transition-transform duration-200 opacity-60 hidden md:block", // Hidden on mobile, original on desktop
                                        if (
                                            openDropdown ==
                                            link.label
                                        ) {
                                            "rotate-180"
                                        } else {
                                            ""
                                        },
                                    )
                                }
                            }
                        }

                        if (
                            link.hasDropdown &&
                            openDropdown ==
                            link.label
                        ) {

                            DropdownPanel {

                                items =
                                    link.items

                                onClose = {
                                    handleMouseEnter(
                                        link.label
                                    )
                                }

                                onNavigate =
                                    props.onNavigate
                            }
                        }
                    }
                }
            }

            div {

                className = ClassName(
                    "flex items-center gap-3"
                )

                button {
                    className = cls(
                        "md:hidden p-2 text-sm font-bold transition-colors",
                        textColor
                    )
                    onClick = { setIsMobileMenuOpen(!isMobileMenuOpen) }

                    if (isMobileMenuOpen) {
                        +"Close"
                    } else {
                        +"Menu ☰"
                    }
                }

                button {
                    className = cls(
                        "hidden sm:block text-sm font-medium px-4 py-2 rounded-lg transition-colors cursor-pointer",
                        textColor,
                        if (scrolled) {
                            "hover:bg-black/5"
                        } else {
                            "hover:bg-white/10"
                        }
                    )

                    onClick = {
                        if (userEmail == null) {
                            val promise = window.asDynamic().loginWithGoogle()
                            promise.then { result: dynamic ->
                                val email = result.user.email as String
                                setUserEmail(email)
                                window.alert("Welcome, $email!")
                            }.catch { error: dynamic ->
                                window.alert("Login failed: ${error.message}")
                            }
                        } else {
                            window.asDynamic().logoutFirebase()
                            setUserEmail(null)
                            window.alert("Logged out successfully!")
                        }
                    }

                    if (userEmail != null) {
                        +"Sign out"
                    } else {
                        +"Sign in"
                    }
                }
    }

    injectStyle(
        """
        @keyframes dd-pop-in {
            0% {
                opacity: 0;
                transform: translateY(-8px) scaleY(0.88);
            }

            100% {
                opacity: 1;
                transform: translateY(0) scaleY(1);
            }
        }
        """.trimIndent()
    )
}

private fun ChildrenBuilder.injectStyle(
    css: String
) {
    react.dom.html.ReactHTML.style {
        +css
    }
}