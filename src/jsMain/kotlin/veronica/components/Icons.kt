package veronica.components

import react.ChildrenBuilder
import react.FC
import react.Props
import react.dom.svg.ReactSVG.animate
import react.dom.svg.ReactSVG.animateTransform
import react.dom.svg.ReactSVG.circle
import react.dom.svg.ReactSVG.defs
import react.dom.svg.ReactSVG.ellipse
import react.dom.svg.ReactSVG.feDropShadow
import react.dom.svg.ReactSVG.filter as svgFilterTag
import react.dom.svg.ReactSVG.g
import react.dom.svg.ReactSVG.line
import react.dom.svg.ReactSVG.linearGradient
import react.dom.svg.ReactSVG.path
import react.dom.svg.ReactSVG.polygon
import react.dom.svg.ReactSVG.polyline
import react.dom.svg.ReactSVG.radialGradient
import react.dom.svg.ReactSVG.stop
import react.dom.svg.ReactSVG.svg
import react.dom.svg.StrokeLinecap
import react.dom.svg.StrokeLinejoin
import react.useId
import web.cssom.ClassName
import web.dom.ElementId
import web.svg.RepeatCount
import web.svg.indefinite

external interface IconProps : Props {
    var size: Double?
    var className: ClassName?
    var strokeWidth: Double?
}

private fun ChildrenBuilder.lucide(
    props: IconProps?,
    content: ChildrenBuilder.() -> Unit,
) {
    svg {
        width = props?.size ?: 24.0
        height = props?.size ?: 24.0
        viewBox = "0 0 24 24"
        fill = "none"
        stroke = "currentColor"
        strokeWidth = props?.strokeWidth ?: 2.0
        strokeLinecap = StrokeLinecap.round
        strokeLinejoin = StrokeLinejoin.round
        className = props?.className
        content()
    }
}

val ArrowRight = FC<IconProps> { props ->
    lucide(props) {
        line { x1 = 5.0; y1 = 12.0; x2 = 19.0; y2 = 12.0 }
        polyline { points = "12 5 19 12 12 19" }
    }
}

val Play = FC<IconProps> { props ->
    lucide(props) {
        polygon { points = "6 3 20 12 6 21 6 3" }
    }
}

val TrendingUp = FC<IconProps> { props ->
    lucide(props) {
        polyline { points = "23 6 13.5 15.5 8.5 10.5 1 18" }
        polyline { points = "17 6 23 6 23 12" }
    }
}

val Ship = FC<IconProps> { props ->
    lucide(props) {
        path { d = "M2 21c.6.5 1.2 1 2.5 1 2.5 0 2.5-2 5-2 1.3 0 1.9.5 2.5 1s1.2 1 2.5 1c2.5 0 2.5-2 5-2 1.3 0 1.9.5 2.5 1" }
        path { d = "M19.38 20A11.6 11.6 0 0 0 21 14l-9-4-9 4c0 2.9.94 5.34 2.81 7.76" }
        path { d = "M19 13V7a2 2 0 0 0-2-2H7a2 2 0 0 0-2 2v6" }
        path { d = "M12 10v4" }
        path { d = "M12 2v3" }
    }
}

val Anchor = FC<IconProps> { props ->
    lucide(props) {
        circle { cx = 12.0; cy = 5.0; r = 3.0 }
        line { x1 = 12.0; y1 = 22.0; x2 = 12.0; y2 = 8.0 }
        path { d = "M5 12H2a10 10 0 0 0 20 0h-3" }
    }
}

val Clock3 = FC<IconProps> { props ->
    lucide(props) {
        circle { cx = 12.0; cy = 12.0; r = 10.0 }
        polyline { points = "12 6 12 12 16.5 12" }
    }
}

val AlertTriangle = FC<IconProps> { props ->
    lucide(props) {
        path { d = "m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z" }
        path { d = "M12 9v4" }
        path { d = "M12 17h.01" }
    }
}

val CheckCircle = FC<IconProps> { props ->
    lucide(props) {
        path { d = "M22 11.08V12a10 10 0 1 1-5.93-9.14" }
        path { d = "m9 11 3 3L22 4" }
    }
}

val ChevronUp = FC<IconProps> { props ->
    lucide(props) {
        path { d = "m18 15-6-6-6 6" }
    }
}

val ChevronDown = FC<IconProps> { props ->
    lucide(props) {
        path { d = "m6 9 6 6 6-6" }
    }
}

external interface ShipLogoProps : Props {
    var size: Double?
    var tone: String?
}

private data class ShipPalette(
    val hullTop: String,
    val hullMid: String,
    val hullBottom: String,
    val hullShadeTop: String,
    val hullShadeBottom: String,
    val sailTop: String,
    val sailBottom: String,
    val glow: String,
)

private val SHIP_PALETTE_BRAND = ShipPalette(
    hullTop = "#8fa6ff", hullMid = "#3b5bfe", hullBottom = "#1526b0",
    hullShadeTop = "#2338c9", hullShadeBottom = "#0e1a80",
    sailTop = "#c7d3ff", sailBottom = "#6d8bff",
    glow = "#3b5bfe",
)

private val SHIP_PALETTE_ACCENT = ShipPalette(
    hullTop = "#d6ddff", hullMid = "#6d8bff", hullBottom = "#3c4c96",
    hullShadeTop = "#5a71d9", hullShadeBottom = "#2c3970",
    sailTop = "#eef1ff", sailBottom = "#a9bdff",
    glow = "#6d8bff",
)

val ShipLogo = FC<ShipLogoProps> { props ->
    val displaySize = props.size ?: 34.0
    val tone = props.tone ?: "brand"
    val palette = if (tone == "accent") SHIP_PALETTE_ACCENT else SHIP_PALETTE_BRAND
    
    // FIX: Append tone to ID so the browser recalculates gradients on scroll
    val uid = useId().toString().replace(":", "") + "-$tone"
    
    val hullGrad = ElementId("shipHull-$uid")
    val hullShadeGrad = ElementId("shipHullShade-$uid")
    val sailGrad = ElementId("shipSail-$uid")
    val glowGrad = ElementId("shipGlow-$uid")
    val dropShadow = ElementId("shipShadow-$uid")

    svg {
        width = displaySize
        height = displaySize
        viewBox = "0 0 40 40"
        fill = "none"
        xmlns = "http://www.w3.org/2000/svg"
        className = ClassName("overflow-visible")
        
        defs {
            linearGradient {
                id = hullGrad
                x1 = 0.0; y1 = 0.0; x2 = 0.0; y2 = 1.0
                stop { offset = "0%"; stopColor = palette.hullTop }
                stop { offset = "45%"; stopColor = palette.hullMid }
                stop { offset = "100%"; stopColor = palette.hullBottom }
            }
            linearGradient {
                id = hullShadeGrad
                x1 = 0.0; y1 = 0.0; x2 = 0.0; y2 = 1.0
                stop { offset = "0%"; stopColor = palette.hullShadeTop }
                stop { offset = "100%"; stopColor = palette.hullShadeBottom }
            }
            linearGradient {
                id = sailGrad
                x1 = 0.0; y1 = 0.0; x2 = 1.0; y2 = 1.0
                stop { offset = "0%"; stopColor = palette.sailTop }
                stop { offset = "100%"; stopColor = palette.sailBottom }
            }
            radialGradient {
                id = glowGrad
                cx = 0.5; cy = 0.5; r = 0.5
                stop { offset = "0%"; stopColor = palette.glow; stopOpacity = 0.35 }
                stop { offset = "100%"; stopColor = palette.glow; stopOpacity = 0.0 }
            }
            svgFilterTag {
                id = dropShadow
                x = -0.6; y = -0.6; width = 2.2; height = 2.2
                feDropShadow {
                    dx = 0.0; dy = 0.9; stdDeviation = "0.9"
                    floodColor = "#1526b0"; floodOpacity = "0.45"
                }
            }
        }
        
        ellipse {
            cx = 20.0; cy = 30.0; rx = 15.0; ry = 5.0
            fill = "url(#$glowGrad)"
            animate {
                attributeName = "opacity"
                values = "0.5;0.9;0.5"
                dur = "3.2s"
                repeatCount = RepeatCount.indefinite
            }
        }
        
        g {
            filter = "url(#$dropShadow)"
            animateTransform {
                attributeName = "transform"
                attributeType = "XML"
                type = "translate"
                values = "0 0; 0 -1.1; 0 0"
                dur = "2.6s"
                repeatCount = RepeatCount.indefinite
            }
            
            path {
                d = "M6 26 Q20 21.5 34 26 L30 32 Q20 29 10 32 Z"
                fill = "url(#$hullShadeGrad)"
                stroke = "#0e1a80"
                strokeWidth = 0.6
                strokeLinejoin = StrokeLinejoin.round
            }
            path {
                d = "M7 26 Q20 22.6 33 26 L31 29.5 Q20 26.4 9 29.5 Z"
                fill = "url(#$hullGrad)"
                strokeLinejoin = StrokeLinejoin.round
            }
            path {
                d = "M8.5 25.6 Q20 22.3 31.5 25.6"
                stroke = "#e4e9ff"
                strokeWidth = 0.8
                opacity = 0.55
            }
            path {
                d = "M10 26 L12 20 L28 20 L30 26 Z"
                fill = "url(#$hullGrad)"
                stroke = "#1d2bd8"
                strokeWidth = 0.5
            }
            path {
                d = "M12 20 L28 20 L26.5 22 L13.5 22 Z"
                fill = "#c7d3ff"
                opacity = 0.35
            }
            path {
                d = "M20 20 L20 12"
                stroke = "#1d2bd8"
                strokeWidth = 1.4
                strokeLinecap = StrokeLinecap.round
            }
            path {
                d = "M20 12 L24.5 15.2 L20 13.6 Z"
                fill = "url(#$sailGrad)"
                stroke = "#3b5bfe"
                strokeWidth = 0.6
                strokeLinejoin = StrokeLinejoin.round
            }
            circle { cx = 15.5; cy = 24.5; r = 0.9; fill = "#c7d3ff"; opacity = 0.85 }
            circle { cx = 20.0; cy = 24.5; r = 0.9; fill = "#c7d3ff"; opacity = 0.85 }
            circle { cx = 24.5; cy = 24.5; r = 0.9; fill = "#c7d3ff"; opacity = 0.85 }
        }
        
        path {
            d = "M4 34 Q12 32 20 34 Q28 36 36 34"
            stroke = "#3b5bfe"
            strokeWidth = 1.2
            strokeLinecap = StrokeLinecap.round
            strokeDasharray = "1 4"
            opacity = 0.6
            animate {
                attributeName = "stroke-dashoffset"
                values = "0;-20"
                dur = "1.6s"
                repeatCount = RepeatCount.indefinite
            }
        }
        path {
            d = "M4 37 Q12 35 20 37 Q28 39 36 37"
            stroke = "#3b5bfe"
            strokeWidth = 1.0
            strokeLinecap = StrokeLinecap.round
            strokeDasharray = "1 5"
            opacity = 0.4
            animate {
                attributeName = "stroke-dashoffset"
                values = "0;-24"
                dur = "2s"
                repeatCount = RepeatCount.indefinite
            }
        }
    }
}