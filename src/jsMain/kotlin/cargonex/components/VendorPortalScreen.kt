package cargonex.components

import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData
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
import web.html.InputType
import kotlin.js.JSON
import kotlin.js.json

external interface VendorPortalProps : Props {
    var onNavigate: ((String) -> Unit)?
}

val VendorPortalScreen = FC<VendorPortalProps> { props ->

    val (vendors, setVendors) = useState<Array<dynamic>>(emptyArray())
    val (isLoading, setIsLoading) = useState(true)
    val (activeTab, setActiveTab) = useState("dashboard")

    fun loadVendors() {
        MainScope().launch {
            try {
                val requestOptions =
                    json("method" to "GET").unsafeCast<RequestInit>()

                val response = window.fetch(
                    "http://localhost:8080/api/vendors",
                    requestOptions
                ).await()

                if (response.ok) {
                    val data =
                        JSON.parse<Array<dynamic>>(
                            response.text().await()
                        )

                    setVendors(data)
                }
            } catch (e: Throwable) {
                console.log(
                    "Failed to fetch vendors: ${e.message}"
                )
            } finally {
                setIsLoading(false)
            }
        }
    }

    useEffect(emptyList<Any?>()) {
        loadVendors()
    }

    div {
        className = ClassName(
            "min-h-screen bg-[#070b20] pt-24 px-6 font-sans " +
                "text-white relative overflow-hidden"
        )

        style = styleOf(
            "background" to
                "radial-gradient(circle at 50% 10%, rgba(59,91,254,0.15), transparent 40%), " +
                "linear-gradient(180deg, #080d28 0%, #070b20 100%)"
        )

        button {
            className = ClassName(
                "absolute top-24 left-6 z-[100] sm:left-12 " +
                    "flex items-center gap-2 px-4 py-2 rounded-xl " +
                    "bg-[#0c1130]/80 hover:bg-[#3b5bfe]/40 " +
                    "text-[#8fa6ff] hover:text-white backdrop-blur-md " +
                    "transition-all text-sm font-medium " +
                    "border border-[#3b5bfe]/30 shadow-lg cursor-pointer"
            )

            onClick = {
                props.onNavigate?.invoke("home")
            }

            +"Back to Home"
        }

        div {
            className = ClassName(
                "max-w-6xl mx-auto pt-16"
            )

            div {
                className = ClassName(
                    "mb-10 text-center"
                )

                h2 {
                    className = ClassName(
                        "text-4xl font-bold tracking-tight mb-4"
                    )

                    +"Vendor & Broker Portal"
                }

                p {
                    className = ClassName(
                        "text-[#8fa6ff] max-w-2xl mx-auto"
                    )

                    +"Manage fleet availability, submit documentation, and participate in Freight Auctions."
                }
            }

            div {
                className = ClassName(
                    "flex justify-center gap-4 mb-8 " +
                        "border-b border-[#3b5bfe]/20 pb-4"
                )

                listOf(
                    "dashboard" to "Vendor Dashboard",
                    "uploads" to "Document Uploads",
                    "admin" to "Admin: Compare Vendors"
                ).forEach { (tabId, label) ->

                    button {
                        key = Key(tabId)

                        className = ClassName(
                            "px-6 py-2 rounded-lg font-semibold " +
                                "transition-all cursor-pointer " +
                                if (activeTab == tabId) {
                                    "bg-[#3b5bfe] text-white " +
                                        "shadow-lg shadow-[#3b5bfe]/30"
                                } else {
                                    "text-[#8fa6ff] hover:bg-[#3b5bfe]/10"
                                }
                        )

                        onClick = {
                            setActiveTab(tabId)
                        }

                        +label
                    }
                }
            }

            if (isLoading) {

                div {
                    className = ClassName(
                        "text-center text-[#8fa6ff] py-20 animate-pulse"
                    )

                    +"Loading vendor network data..."
                }

            } else if (vendors.isNotEmpty()) {

                val primaryVendor = vendors.first()

                when (activeTab) {

                    "dashboard" -> {

                        div {
                            className = ClassName(
                                "grid grid-cols-2 md:grid-cols-4 gap-4 mb-8"
                            )

                            listOf(
                                "Rating" to "${primaryVendor.rating}/5",
                                "Fleet" to
                                    "${(primaryVendor.vessels as Array<dynamic>).size} Vessels",
                                "On-Time" to
                                    "${primaryVendor.onTimePerformance}",
                                "Compliance" to
                                    "${primaryVendor.compliance}"
                            ).forEach { (label, value) ->

                                div {
                                    key = Key(label)

                                    className = ClassName(
                                        "p-5 rounded-2xl bg-[#0c1130]/80 " +
                                            "border border-[#3b5bfe]/30 " +
                                            "backdrop-blur-md text-center"
                                    )

                                    p {
                                        className = ClassName(
                                            "text-xs text-[#8fa6ff] " +
                                                "uppercase tracking-wider mb-1"
                                        )

                                        +label
                                    }

                                    p {
                                        className = ClassName(
                                            "text-2xl font-bold text-white"
                                        )

                                        +value
                                    }
                                }
                            }
                        }

                        div {
                            className = ClassName(
                                "rounded-xl border border-white/10 " +
                                    "bg-[#0c1130]/50 overflow-hidden"
                            )

                            div {
                                className = ClassName(
                                    "p-5 border-b border-white/10 bg-white/5"
                                )

                                h3 {
                                    className = ClassName(
                                        "text-lg font-bold"
                                    )

                                    +"My Fleet Availability"
                                }
                            }

                            table {
                                className = ClassName(
                                    "w-full text-left border-collapse"
                                )

                                thead {
                                    tr {
                                        listOf(
                                            "Vessel",
                                            "Type & Capacity",
                                            "ETA",
                                            "Rate/Day",
                                            "Cargo Compat.",
                                            "Status"
                                        ).forEach { header ->

                                            th {
                                                className = ClassName(
                                                    "p-4 text-xs font-semibold " +
                                                        "text-[#8fa6ff] uppercase"
                                                )

                                                +header
                                            }
                                        }
                                    }
                                }

                                tbody {

                                    (primaryVendor.vessels as Array<dynamic>)
                                        .forEachIndexed { index, vessel ->

                                            tr {
                                                key = Key(
                                                    index.toString()
                                                )

                                                className = ClassName(
                                                    "border-b border-white/5 " +
                                                        "hover:bg-white/5"
                                                )

                                                td {
                                                    className = ClassName(
                                                        "p-4 font-bold"
                                                    )

                                                    +(vessel.name as String)
                                                }

                                                td {
                                                    className = ClassName(
                                                        "p-4 text-sm text-[#c7cbe0]"
                                                    )

                                                    +"${vessel.type} (${vessel.capacity})"
                                                }

                                                td {
                                                    className = ClassName(
                                                        "p-4 text-sm text-emerald-400"
                                                    )

                                                    +(vessel.eta as String)
                                                }

                                                td {
                                                    className = ClassName(
                                                        "p-4 font-mono"
                                                    )

                                                    +(vessel.rate as String)
                                                }

                                                td {
                                                    className = ClassName(
                                                        "p-4 text-sm"
                                                    )

                                                    +(vessel.compatibility as String)
                                                }

                                                td {
                                                    className = ClassName(
                                                        "p-4 text-xs font-bold"
                                                    )

                                                    if (vessel.isAwarded == true) {
                                                        span {
                                                            className = ClassName(
                                                                "text-emerald-400"
                                                            )

                                                            +"Contract Awarded"
                                                        }
                                                    } else {
                                                        span {
                                                            className = ClassName(
                                                                "text-[#8fa6ff]"
                                                            )

                                                            +"Available"
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }

                    "uploads" -> {

                        div {
                            className = ClassName(
                                "max-w-2xl mx-auto rounded-2xl " +
                                    "border border-[#3b5bfe]/30 " +
                                    "bg-[#0c1130]/80 p-8"
                            )

                            h3 {
                                className = ClassName(
                                    "text-xl font-bold mb-6"
                                )

                                +"Required Certifications"
                            }

                            listOf(
                                "Vessel Registration Certificate",
                                "Protection & Indemnity (P&I) Insurance",
                                "Safety Management Certificate (SMC)",
                                "Cargo Capability Manifest"
                            ).forEach { document ->

                                div {
                                    key = Key(document)

                                    className = ClassName(
                                        "flex items-center justify-between p-4 " +
                                            "mb-4 rounded-xl bg-black/20 " +
                                            "border border-white/10"
                                    )

                                    div {

                                        p {
                                            className = ClassName(
                                                "font-medium text-white"
                                            )

                                            +document
                                        }

                                        p {
                                            className = ClassName(
                                                "text-xs text-[#8fa6ff] mt-1"
                                            )

                                            +"PDF, JPG up to 10MB"
                                        }
                                    }

                                    input {
                                        type =
                                            "file".unsafeCast<InputType>()

                                        className = ClassName(
                                            "text-xs text-[#8fa6ff] " +
                                                "file:mr-4 file:py-2 file:px-4 " +
                                                "file:rounded-lg file:border-0 " +
                                                "file:text-xs file:font-semibold " +
                                                "file:bg-[#3b5bfe]/20 " +
                                                "file:text-[#8fa6ff] " +
                                                "hover:file:bg-[#3b5bfe] " +
                                                "hover:file:text-white " +
                                                "cursor-pointer"
                                        )

                                        onChange = { e ->

                                            val target =
                                                e.target.asDynamic()

                                            if (
                                                target.files != null &&
                                                target.files.length > 0
                                            ) {

                                                val uploadedFile =
                                                    target.files[0]

                                                val fileName =
                                                    uploadedFile.name as String

                                                MainScope().launch {

                                                    try {

                                                        val formData =
                                                            FormData()

                                                        formData
                                                            .asDynamic()
                                                            .append(
                                                                "file",
                                                                uploadedFile,
                                                                fileName
                                                            )

                                                        val req =
                                                            json(
                                                                "method" to "POST",
                                                                "body" to formData
                                                            ).unsafeCast<RequestInit>()

                                                        val response =
                                                            window.fetch(
                                                                "http://localhost:8080/api/vendor/upload",
                                                                req
                                                            ).await()

                                                        if (response.ok) {

                                                            window.alert(
                                                                "$fileName securely uploaded to backend!"
                                                            )

                                                        } else {

                                                            window.alert(
                                                                "Upload failed for $fileName"
                                                            )
                                                        }

                                                    } catch (
                                                        error: Throwable
                                                    ) {

                                                        window.alert(
                                                            "Upload failed: ${error.message}"
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            button {
                                className = ClassName(
                                    "w-full mt-4 py-3 rounded-xl " +
                                        "bg-[#3b5bfe] text-white font-bold " +
                                        "hover:shadow-[0_0_20px_rgba(59,91,254,0.4)] " +
                                        "transition-all cursor-pointer"
                                )

                                onClick = {
                                    window.alert(
                                        "Select and upload all required documents above."
                                    )
                                }

                                +"Submit Compliance Package"
                            }
                        }
                    }

                    "admin" -> {

                        div {
                            className = ClassName(
                                "rounded-xl border border-white/10 " +
                                    "bg-[#0c1130]/50 overflow-hidden"
                            )

                            div {
                                className = ClassName(
                                    "p-5 border-b border-white/10 bg-white/5"
                                )

                                h3 {
                                    className = ClassName(
                                        "text-lg font-bold"
                                    )

                                    +"Freight Auction: Vendor Comparison"
                                }
                            }

                            table {
                                className = ClassName(
                                    "w-full text-left border-collapse"
                                )

                                thead {
                                    tr {

                                        listOf(
                                            "Vendor Name",
                                            "Rating",
                                            "On-Time Perf.",
                                            "Compliance",
                                            "Vessel",
                                            "Rate",
                                            "Action"
                                        ).forEach { header ->

                                            th {
                                                className = ClassName(
                                                    "p-4 text-xs font-semibold " +
                                                        "text-[#8fa6ff] uppercase"
                                                )

                                                +header
                                            }
                                        }
                                    }
                                }

                                tbody {

                                    vendors.forEachIndexed { vendorIndex, vendor ->

                                        val vendorVessels =
                                            vendor.vessels as Array<dynamic>

                                        vendorVessels
                                            .forEachIndexed { vesselIndex, vessel ->

                                                tr {
                                                    key = Key(
                                                        "${vendorIndex}-${vesselIndex}"
                                                    )

                                                    className = ClassName(
                                                        "border-b border-white/5 " +
                                                            "hover:bg-white/5"
                                                    )

                                                    td {
                                                        className = ClassName(
                                                            "p-4 font-bold text-white"
                                                        )

                                                        +(vendor.name as String)
                                                    }

                                                    td {
                                                        className = ClassName(
                                                            "p-4 text-yellow-400 font-bold"
                                                        )

                                                        +"★ ${vendor.rating}"
                                                    }

                                                    td {
                                                        className = ClassName(
                                                            "p-4 text-sm text-[#c7cbe0]"
                                                        )

                                                        +(vendor.onTimePerformance as String)
                                                    }

                                                    td {
                                                        className = ClassName(
                                                            "p-4 text-sm text-emerald-400"
                                                        )

                                                        +(vendor.compliance as String)
                                                    }

                                                    td {
                                                        className = ClassName(
                                                            "p-4"
                                                        )

                                                        div {
                                                            className = ClassName(
                                                                "flex flex-col"
                                                            )

                                                            span {
                                                                className =
                                                                    ClassName(
                                                                        "font-bold text-white"
                                                                    )

                                                                +(vessel.name as String)
                                                            }

                                                            span {
                                                                className =
                                                                    ClassName(
                                                                        "text-xs text-[#8fa6ff]"
                                                                    )

                                                                +(vessel.capacity as String)
                                                            }
                                                        }
                                                    }

                                                    td {
                                                        className = ClassName(
                                                            "p-4 font-mono text-sm"
                                                        )

                                                        +(vessel.rate as String)
                                                    }

                                                    td {
                                                        className = ClassName(
                                                            "p-4"
                                                        )

                                                        if (
                                                            vessel.isAwarded == true
                                                        ) {

                                                            span {
                                                                className =
                                                                    ClassName(
                                                                        "text-emerald-400 font-bold text-xs"
                                                                    )

                                                                +"Contract Awarded ✓"
                                                            }

                                                        } else {

                                                            button {
                                                                className =
                                                                    ClassName(
                                                                        "px-4 py-2 rounded-lg " +
                                                                            "bg-[#3b5bfe] text-white " +
                                                                            "text-xs font-bold " +
                                                                            "hover:bg-[#6d8bff] " +
                                                                            "transition-colors " +
                                                                            "cursor-pointer"
                                                                    )

                                                                onClick = {

                                                                    MainScope()
                                                                        .launch {

                                                                            try {

                                                                                val req =
                                                                                    json(
                                                                                        "method" to "POST",
                                                                                        "headers" to json(
                                                                                            "Content-Type" to
                                                                                                "application/json"
                                                                                        ),
                                                                                        "body" to
                                                                                            JSON.stringify(
                                                                                                json(
                                                                                                    "vesselName" to
                                                                                                        vessel.name
                                                                                                )
                                                                                            )
                                                                                    ).unsafeCast<RequestInit>()

                                                                                val response =
                                                                                    window.fetch(
                                                                                        "http://localhost:8080/api/auction/award",
                                                                                        req
                                                                                    ).await()

                                                                                if (
                                                                                    response.ok
                                                                                ) {

                                                                                    window.alert(
                                                                                        "Contract awarded for ${vessel.name}!"
                                                                                    )

                                                                                    setIsLoading(
                                                                                        true
                                                                                    )

                                                                                    loadVendors()

                                                                                } else {

                                                                                    window.alert(
                                                                                        "Failed to award contract for ${vessel.name}"
                                                                                    )
                                                                                }

                                                                            } catch (
                                                                                error: Throwable
                                                                            ) {

                                                                                window.alert(
                                                                                    "Award request failed: ${error.message}"
                                                                                )
                                                                            }
                                                                        }
                                                                }

                                                                +"Award Contract"
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

            } else {

                div {
                    className = ClassName(
                        "text-center text-[#8fa6ff] py-20"
                    )

                    +"No vendor data available."
                }
            }
        }
    }
}