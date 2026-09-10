package veronica

import react.FC
import react.dom.html.ReactHTML.div
import react.useState
import veronica.components.*
import web.cssom.ClassName

val App = FC {
    val (currentPage, setCurrentPage) = useState("home")

    div {
        className = ClassName("min-h-screen")

        Navbar {
            onNavigate = { pageName ->
                setCurrentPage(pageName)
            }
        }

        if (currentPage == "home") {
            FloatingShips()
            Hero {
                onNavigate = { pageName ->
                    setCurrentPage(pageName)
                }
            }
            Features()
            Dashboard()
        } else if (currentPage == "vessel_engine") {
            VesselEngineScreen {
                onNavigate = { pageName ->
                    setCurrentPage(pageName)
                }
            }
        } else if (currentPage == "Rate Tracker") {
            RateTrackerScreen {
                onNavigate = { pageName ->
                    setCurrentPage(pageName)
                }
            }
        } else if (currentPage == "risk_matrix") {
            RiskMatrixScreen {
                onNavigate = { pageName ->
                    setCurrentPage(pageName)
                }
            }
        } else if (currentPage == "freight_auction") {
            FreightAuctionScreen {
                onNavigate = { pageName ->
                    setCurrentPage(pageName)
                }
            }
        } else if (currentPage == "cargo_matching") {
            CargoMatchingScreen {
                onNavigate = { pageName ->
                    setCurrentPage(pageName)
                }
            }
        } else if (currentPage == "vendor_portal") {
            VendorPortalScreen {
                onNavigate = { pageName ->
                    setCurrentPage(pageName)
                }
            }
        } else if (currentPage == "forecast_dashboard") {
            ForecastDashboardScreen {
                onNavigate = { pageName ->
                    setCurrentPage(pageName)
                }
            }
        } else if (currentPage == "route_optimization") {
            RouteOptimizationScreen {
                onNavigate = { pageName ->
                    setCurrentPage(pageName)
                }
            }
        }
    }
}