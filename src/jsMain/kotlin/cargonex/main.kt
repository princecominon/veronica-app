package cargonex

import react.StrictMode
import react.create
import react.dom.client.createRoot
import web.dom.ElementId
import web.dom.document

/**
 * Entry point — equivalent of `src/main.jsx`.
 * Mounts `<StrictMode><App /></StrictMode>` into `#root`.
 */
fun main() {
    val container = document.getElementById(ElementId("root")) ?: error("Missing #root element")

    createRoot(container).render(
        StrictMode.create {
            children = App.create()
        },
    )
}
