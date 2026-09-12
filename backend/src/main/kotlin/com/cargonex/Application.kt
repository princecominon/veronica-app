package com.cargonex

import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File

val client = HttpClient(OkHttp)

val env = dotenv {
    ignoreIfMissing = true
}

private val logger = LoggerFactory.getLogger("CargonexBackend")

@Serializable
data class VendorVessel(
    val name: String,
    val type: String,
    val capacity: String,
    val eta: String,
    val compatibility: String,
    val rate: String,
    var isAwarded: Boolean = false
)

@Serializable
data class VendorProfile(
    val name: String,
    val rating: Double,
    val compliance: String,
    val onTimePerformance: String,
    val vessels: MutableList<VendorVessel>
)

@Serializable
data class AwardRequest(
    val vesselName: String
)

@Serializable
data class BidItem(
    val vendor: String,
    val vessel: String,
    val rate: Double,
    val eta: String,
    val score: Int
)

@Serializable
data class FreightAuctionResponse(
    val cargoRequirement: String,
    val quantityMt: Int,
    val cargoType: String,
    val origin: String,
    val destination: String,
    val route: String,
    val recommendation: String,
    val routeInsight: String,
    val bids: List<BidItem>
)

val vendorDatabase = mutableListOf(
    VendorProfile(
        name = "Oceanic Shipping Pvt Ltd",
        rating = 4.7,
        compliance = "98%",
        onTimePerformance = "94%",
        vessels = mutableListOf(
            VendorVessel(
                name = "VERONICA",
                type = "Smart Container Carrier",
                capacity = "14,000 TEU",
                eta = "2026-10-12",
                compatibility = "Dry/Standard",
                rate = "$3,240/day"
            ),
            VendorVessel(
                name = "EVER GIVEN",
                type = "Ultra Large Container",
                capacity = "23,000 TEU",
                eta = "2026-10-15",
                compatibility = "Dry/Oversized",
                rate = "$3,680/day"
            ),
            VendorVessel(
                name = "OCEANIC DISCOVERER",
                type = "Bulk Carrier",
                capacity = "80,000 DWT",
                eta = "2026-10-16",
                compatibility = "Dry/Bulk",
                rate = "$2,900/day"
            ),
            VendorVessel(
                name = "OCEANIC PIONEER",
                type = "Bulk Carrier",
                capacity = "82,000 DWT",
                eta = "2026-10-18",
                compatibility = "Dry/Bulk",
                rate = "$2,950/day"
            ),
            VendorVessel(
                name = "SEA EXPLORER",
                type = "Ro-Ro",
                capacity = "6,500 CEU",
                eta = "2026-10-20",
                compatibility = "Vehicles",
                rate = "$4,500/day"
            ),
            VendorVessel(
                name = "MARITIME VANGUARD",
                type = "Container Ship",
                capacity = "10,000 TEU",
                eta = "2026-10-22",
                compatibility = "Dry/Standard",
                rate = "$2,800/day"
            ),
            VendorVessel(
                name = "ASTRAL VOYAGER",
                type = "Liquid Tanker",
                capacity = "150,000 DWT",
                eta = "2026-10-25",
                compatibility = "Liquid/Chemical",
                rate = "$5,100/day"
            )
        )
    ),

    VendorProfile(
        name = "TransGlobal Bulk Logistics",
        rating = 4.9,
        compliance = "99%",
        onTimePerformance = "97%",
        vessels = mutableListOf(
            VendorVessel(
                name = "MSC OSCAR",
                type = "Mega Container Ship",
                capacity = "23,000 TEU",
                eta = "2026-10-10",
                compatibility = "Refrigerated",
                rate = "$4,120/day"
            ),
            VendorVessel(
                name = "COSCO SHIPPING",
                type = "Global Container Line",
                capacity = "21,000 TEU",
                eta = "2026-10-14",
                compatibility = "Dry/Standard",
                rate = "$4,890/day"
            ),
            VendorVessel(
                name = "BULK TITAN",
                type = "Capesize Bulker",
                capacity = "180,000 DWT",
                eta = "2026-10-11",
                compatibility = "Dry/Ore",
                rate = "$6,200/day"
            ),
            VendorVessel(
                name = "GLOBAL TRADER",
                type = "Panamax Bulker",
                capacity = "75,000 DWT",
                eta = "2026-10-13",
                compatibility = "Dry/Grain",
                rate = "$3,100/day"
            ),
            VendorVessel(
                name = "IRON LEVIATHAN",
                type = "Ore Carrier",
                capacity = "250,000 DWT",
                eta = "2026-10-19",
                compatibility = "Dry/Ore",
                rate = "$7,500/day"
            ),
            VendorVessel(
                name = "PACIFIC HORIZON",
                type = "Container Ship",
                capacity = "12,000 TEU",
                eta = "2026-10-21",
                compatibility = "Dry/Standard",
                rate = "$3,050/day"
            ),
            VendorVessel(
                name = "TRANSGLOBAL ALPHA",
                type = "LNG Carrier",
                capacity = "170,000 CBM",
                eta = "2026-10-28",
                compatibility = "Liquid/Gas",
                rate = "$8,500/day"
            ),
            VendorVessel(
                name = "TRANSGLOBAL BETA",
                type = "LNG Carrier",
                capacity = "174,000 CBM",
                eta = "2026-10-30",
                compatibility = "Liquid/Gas",
                rate = "$8,600/day"
            )
        )
    ),

    VendorProfile(
        name = "Maersk Freight Solutions",
        rating = 4.8,
        compliance = "100%",
        onTimePerformance = "96%",
        vessels = mutableListOf(
            VendorVessel(
                name = "MAERSK MCKINNEY",
                type = "Ultra Mega Carrier",
                capacity = "23,000 TEU",
                eta = "2026-10-11",
                compatibility = "Hazmat",
                rate = "$5,240/day"
            ),
            VendorVessel(
                name = "HMM ALGECIRAS",
                type = "Advanced Container",
                capacity = "23,000 TEU",
                eta = "2026-10-18",
                compatibility = "Dry/Standard",
                rate = "$5,680/day"
            ),
            VendorVessel(
                name = "MAERSK KINLOSS",
                type = "Container Ship",
                capacity = "8,500 TEU",
                eta = "2026-10-12",
                compatibility = "Refrigerated",
                rate = "$2,600/day"
            ),
            VendorVessel(
                name = "MAERSK KENSINGTON",
                type = "Container Ship",
                capacity = "8,500 TEU",
                eta = "2026-10-14",
                compatibility = "Dry/Standard",
                rate = "$2,550/day"
            ),
            VendorVessel(
                name = "MAERSK KAWASAKI",
                type = "Container Ship",
                capacity = "8,500 TEU",
                eta = "2026-10-17",
                compatibility = "Dry/Standard",
                rate = "$2,550/day"
            ),
            VendorVessel(
                name = "NORDIC GIANT",
                type = "Heavy Lift Vessel",
                capacity = "50,000 DWT",
                eta = "2026-10-20",
                compatibility = "Oversized/Project",
                rate = "$9,200/day"
            ),
            VendorVessel(
                name = "ARCTIC RUNNER",
                type = "Ice-Class Bulker",
                capacity = "60,000 DWT",
                eta = "2026-10-24",
                compatibility = "Dry/Bulk",
                rate = "$4,100/day"
            ),
            VendorVessel(
                name = "BALTIC TIDE",
                type = "Chemical Tanker",
                capacity = "25,000 DWT",
                eta = "2026-10-27",
                compatibility = "Liquid/Chemical",
                rate = "$3,800/day"
            )
        )
    )
)



@Serializable
data class GeminiPart(val text: String? = null)

@Serializable
data class GeminiContent(val parts: List<GeminiPart>? = null)

@Serializable
data class GeminiRequest(val contents: List<GeminiContent>)

@Serializable
data class GeminiCandidate(val content: GeminiContent? = null)

@Serializable
data class GeminiResponse(val candidates: List<GeminiCandidate>? = null)
private val registeredVendors = mutableListOf(
    VendorSubmission(
        vendorName = "Oceanic Shipping Pvt Ltd",
        vesselName = "VERONICA",
        vesselType = "Container",
        charterRate = 3240.0,
        eta = "18 Oct",
        reliability = 94.0,
        compliance = 98.0,
        vesselQuality = 95.0,
        cargoCompatibility = "Dry Bulk & Containers",
        availabilityWindow = "7-12 Days"
    )
)

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {

    install(ContentNegotiation) {
        json()
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }

    routing {

        get("/api/vessel/{imo}") {
            val imo = call.parameters["imo"]
            if (imo.isNullOrBlank()) {
                call.respondText(
                    """{"error":"IMO cannot be empty","code":"INVALID_IMO"}""",
                    ContentType.Application.Json
                )
                return@get
            }
            if (!Regex("^\\d{7}$").matches(imo)) {
                call.respondText(
                    """{"error":"IMO must be 7 digits","code":"INVALID_IMO_FORMAT"}""",
                    ContentType.Application.Json
                )
                return@get
            }

            val isEverGiven = imo == "9811000"

            val mockJson = """
            {
                "vesselPosition": {
                    "vessel_name": "${if (isEverGiven) "EVER GIVEN" else "VERONICA VOYAGER"}",
                    "imo": "$imo",
                    "mmsi": "${if (isEverGiven) "353136000" else "419000123"}",
                    "latitude": 20.2654,
                    "longitude": 86.6763,
                    "timestamp": "2026-09-11T14:30:00Z",
                    "nav_status": 0,
                    "sog": 14.5,
                    "cog": 85.2,
                    "heading": 86,
                    "vessel_class": "${if (isEverGiven) "Ultra Large Container" else "Panamax"}",
                    "dwt": "${if (isEverGiven) "199,629 mt" else "82,000 mt"}",
                    "loa": "${if (isEverGiven) "399.9m" else "229m"}",
                    "beam": "${if (isEverGiven) "58.8m" else "32m"}",
                    "cargo_capacity": "${if (isEverGiven) "20,124 TEU" else "4,500 TEU"}",
                    "fuel_consumption": "${if (isEverGiven) "120 mt/day" else "35 mt/day"}",
                    "est_freight": "${if (isEverGiven) "\$5,680 / TEU" else "\$4,120 / TEU"}",
                    "port_compatibility": "${if (isEverGiven) "Draft Restricted (>16m)" else "Verified (Draft OK)"}"
                }
            }
            """.trimIndent()

            call.respondText(mockJson, ContentType.Application.Json, HttpStatusCode.OK)
        }

        get("/api/vessels") {
            val vessels = listOf(
                VesselInfo(
                    "VERONICA",
                    "Smart Container Carrier",
                    "14,000 TEU"
                ),
                VesselInfo(
                    "EVER GIVEN",
                    "Ultra Large Container",
                    "23,000 TEU"
                ),
                VesselInfo(
                    "MSC OSCAR",
                    "Mega Container Ship",
                    "23,000 TEU"
                ),
                VesselInfo(
                    "COSCO SHIPPING",
                    "Global Container Line",
                    "21,000 TEU"
                ),
                VesselInfo(
                    "MAERSK MCKINNEY",
                    "Ultra Mega Carrier",
                    "23,000 TEU"
                ),
                VesselInfo(
                    "HMM ALGECIRAS",
                    "Advanced Container",
                    "23,000 TEU"
                )
            )

            call.respond(VesselList(vessels))
        }

        get("/api/rates") {
            val rates = mapOf(
                "Container" to RateInfo(
                    "Container",
                    "$3,240",
                    "20ft & 40ft standard containers"
                ),
                "Bulk" to RateInfo(
                    "Bulk",
                    "$2,890",
                    "Dry bulk commodities transport"
                ),
                "Tanker" to RateInfo(
                    "Tanker",
                    "$4,120",
                    "Liquid cargo & chemicals"
                ),
                "RoRo" to RateInfo(
                    "RoRo",
                    "$3,680",
                    "Roll-on/Roll-off vehicles"
                ),
                "LNG" to RateInfo(
                    "LNG",
                    "$5,240",
                    "Liquefied natural gas carriers"
                ),
                "Project" to RateInfo(
                    "Project",
                    "$6,890",
                    "Oversized heavy cargo"
                )
            )

            call.respond(RatesResponse(rates))
        }

        get("/api/rates/forecast") {
            try {
                val port =
                    call.request.queryParameters["port"] ?: "Paradip"

                val region =
                    call.request.queryParameters["region"]
                        ?: "Indian East Coast"

                val rawRecords = loadBdiCsv()

                if (rawRecords.isEmpty()) {
                    call.respondText(
                        """{"error":"No BDI records found in resources"}""",
                        ContentType.Application.Json
                    )
                    return@get
                }

                val modifier = when (port) {
                    "Dhamra" -> 1.05
                    "Vizag" -> 0.95
                    "Gangavaram" -> 0.98
                    "Gopalpur" -> 1.02
                    "Haldia" -> 1.08
                    else -> 1.0
                }

                val records = rawRecords.map {
                    it.copy(
                        price = it.price * modifier,
                        high = it.high * modifier,
                        low = it.low * modifier
                    )
                }

                val latest = records.last()

                val previous =
                    if (records.size > 1) {
                        records[records.size - 2]
                    } else {
                        latest
                    }

                val dailyChange =
                    if (previous.price != 0.0) {
                        ((latest.price - previous.price) /
                            previous.price) * 100.0
                    } else {
                        0.0
                    }

                val historical =
                    records.takeLast(90)

                val forecast = buildForecast(
                    historical.map { it.price },
                    14
                )

                val currentTrend =
                    if (dailyChange >= 0) {
                        "RISING"
                    } else {
                        "FALLING"
                    }

                val insight = when {
                    dailyChange > 2.0 ->
                        "Freight momentum is rising strongly for $port. Monitor charter costs closely."

                    dailyChange > 0.0 ->
                        "Freight momentum is increasing for $port. The market is showing upward movement."

                    dailyChange < -2.0 ->
                        "Freight momentum is falling sharply for $port. Waiting may provide better charter conditions."

                    else ->
                        "Freight market movement for $port is relatively stable."
                }

                val response =
                    RateForecastResponse(
                        market = "$port Freight Market",
                        indexName = "Baltic Dry Index",
                        currentRate = latest.price,
                        latestDate = latest.date,
                        dailyChange = dailyChange,
                        marketTrend = currentTrend,
                        high = historical.maxOf {
                            it.high
                        },
                        low = historical.minOf {
                            it.low
                        },
                        dataPoints = historical.size,
                        routeContext = region,
                        insight = insight,
                        historical = historical,
                        forecast = forecast
                    )

                call.respond(response)

            } catch (e: Exception) {
                logger.error(
                    "Failed to load freight CSV: ${e.message}",
                    e
                )

                call.respondText(
                    """{"error":"Failed to load Baltic Dry Index CSV","details":"${escapeJson(e.message ?: "Unknown error")}"}""",
                    ContentType.Application.Json
                )
            }
        }

        get("/api/risk/{destination}") {

            val rawDestination =
                call.parameters["destination"]
                    ?: "Paradip"

            val apiQueryLocation =
                if (
                    rawDestination.equals(
                        "Vizag",
                        ignoreCase = true
                    )
                ) {
                    "Visakhapatnam"
                } else {
                    rawDestination
                }

            val weatherKey =
                env["OpenWeatherMap_API_KEY"]

            val newsKey =
                env["NEWSDATA_API_KEY"]

            if (
                weatherKey.isNullOrBlank() ||
                newsKey.isNullOrBlank()
            ) {
                call.respondText(
                    """{"error":"Missing API Keys"}""",
                    ContentType.Application.Json,
                    HttpStatusCode.InternalServerError
                )
                return@get
            }

            val lenientJson = Json {
                ignoreUnknownKeys = true
            }

            try {

                val weatherRes = client.get(
                    "https://api.openweathermap.org/data/2.5/weather?q=$apiQueryLocation&appid=$weatherKey&units=metric"
                )

                val weatherData =
                    lenientJson.decodeFromString<WeatherResponse>(
                        weatherRes.bodyAsText()
                    )

                val windSpeed =
                    weatherData.wind?.speed ?: 0.0

                val humidity =
                    weatherData.main?.humidity ?: 50

                val newsRes = client.get(
                    "https://newsdata.io/api/1/latest?apikey=$newsKey&q=$apiQueryLocation"
                )

                val newsData =
                    lenientJson.decodeFromString<NewsResponse>(
                        newsRes.bodyAsText()
                    )

                val newsCount =
                    newsData.totalResults

                val rawWeatherProb =
                    (windSpeed * 0.8) +
                        (humidity * 0.05)

                val weatherProb =
                    rawWeatherProb
                        .toInt()
                        .coerceIn(1, 10)

                val weatherImpact = 8

                val weatherScore =
                    weatherProb * weatherImpact

                val regionalFactor =
                    rawDestination.length % 3

                val opsProb =
                    if (newsCount > 5) {
                        8
                    } else if (newsCount > 0) {
                        5
                    } else {
                        (2 + regionalFactor)
                            .coerceIn(1, 10)
                    }

                val opsImpact = 7

                val opsScore =
                    opsProb * opsImpact

                val bdiRecords =
                    loadBdiCsv()

                if (bdiRecords.size < 2) {
                    throw IllegalStateException(
                        "At least two BDI records are required to calculate market risk"
                    )
                }

                val latestBdi =
                    bdiRecords.last()

                val prevBdi =
                    bdiRecords[
                        bdiRecords.size - 2
                    ]

                val dailyChange =
                    if (prevBdi.price != 0.0) {
                        (
                            (
                                latestBdi.price -
                                    prevBdi.price
                                ) /
                                prevBdi.price
                            ) * 100.0
                    } else {
                        0.0
                    }

                val portCongestionModifier =
                    when (rawDestination) {
                        "Dhamra" -> 1
                        "Vizag" -> 2
                        "Haldia" -> 3
                        else -> 0
                    }

                val marketProb =
                    (
                        if (
                            kotlin.math.abs(
                                dailyChange
                            ) > 4.0
                        ) {
                            8
                        } else if (
                            kotlin.math.abs(
                                dailyChange
                            ) > 1.5
                        ) {
                            5
                        } else {
                            3
                        }
                    ) +
                        portCongestionModifier

                val finalMarketProb =
                    marketProb.coerceIn(1, 10)

                val marketImpact = 8

                val marketScore =
                    finalMarketProb *
                        marketImpact

                fun getLevel(
                    score: Int
                ): String {
                    return when {
                        score >= 60 ->
                            "Critical"

                        score >= 40 ->
                            "High"

                        score >= 20 ->
                            "Medium"

                        else ->
                            "Low"
                    }
                }

                fun getColor(
                    score: Int
                ): String {
                    return when {
                        score >= 60 ->
                            "text-red-400 bg-red-400/10 border-red-400/20"

                        score >= 40 ->
                            "text-orange-400 bg-orange-400/10 border-orange-400/20"

                        score >= 20 ->
                            "text-yellow-400 bg-yellow-400/10 border-yellow-400/20"

                        else ->
                            "text-emerald-400 bg-emerald-400/10 border-emerald-400/20"
                    }
                }

                val factors =
                    listOf(
                        RiskItem(
                            name =
                                "Weather (Wind: ${windSpeed}m/s, Hum: ${humidity}%)",
                            probability =
                                weatherProb,
                            impact =
                                weatherImpact,
                            level =
                                getLevel(weatherScore),
                            score =
                                weatherScore,
                            colorClass =
                                getColor(weatherScore)
                        ),
                        RiskItem(
                            name =
                                "Operational (Alerts: $newsCount)",
                            probability =
                                opsProb,
                            impact =
                                opsImpact,
                            level =
                                getLevel(opsScore),
                            score =
                                opsScore,
                            colorClass =
                                getColor(opsScore)
                        ),
                        RiskItem(
                            name = "Market Volatility (BDI: ${
                                String.format(
                                    "%.2f",
                                    dailyChange
                                )
                            }%)",
                            probability =
                                finalMarketProb,
                            impact =
                                marketImpact,
                            level =
                                getLevel(marketScore),
                            score =
                                marketScore,
                            colorClass =
                                getColor(marketScore)
                        )
                    )

                val aggregate =
                    factors.sumOf {
                        it.score
                    } /
                        factors.size

                val summaryText =
                    if (aggregate > 50) {
                        "High risk detected near $rawDestination. Expected delays possible due to current conditions."
                    } else {
                        "Conditions are stable near $rawDestination. No immediate severe risks detected."
                    }

                val finalResponse =
                    RiskMatrixResponse(
                        currentRoute =
                            "Australia → $rawDestination",
                        aggregateScore =
                            aggregate,
                        summary =
                            summaryText,
                        risks =
                            factors
                    )

                call.respond(
                    finalResponse
                )

            } catch (e: Exception) {

                call.respondText(
                    """{"error":"Failed to aggregate risk data: ${escapeJson(e.message ?: "Unknown error")}"}""",
                    ContentType.Application.Json,
                    HttpStatusCode.InternalServerError
                )
            }
        }

        post("/api/cargo-match") {

            try {

                val request =
                    call.receive<CargoRequest>()

                val availableVessels =
                    listOf(
                        mapOf(
                            "name" to "VERONICA",
                            "type" to "Panamax",
                            "mt" to 75000,
                            "eta" to "18 Oct"
                        ),
                        mapOf(
                            "name" to "EVER GIVEN",
                            "type" to "Capesize",
                            "mt" to 120000,
                            "eta" to "14 Oct"
                        ),
                        mapOf(
                            "name" to "MSC OSCAR",
                            "type" to "Panamax",
                            "mt" to 70000,
                            "eta" to "20 Oct"
                        ),
                        mapOf(
                            "name" to "COSCO SHIPPING",
                            "type" to "Supramax",
                            "mt" to 55000,
                            "eta" to "16 Oct"
                        ),
                        mapOf(
                            "name" to "MAERSK MCKINNEY",
                            "type" to "Capesize",
                            "mt" to 180000,
                            "eta" to "25 Oct"
                        ),
                        mapOf(
                            "name" to "HMM ALGECIRAS",
                            "type" to "Panamax",
                            "mt" to 82000,
                            "eta" to "19 Oct"
                        )
                    )

                val matches =
                    availableVessels
                        .mapNotNull { vessel ->

                            val capacityMt =
                                vessel["mt"] as Int

                            val eta =
                                vessel["eta"] as String

                            var score = 100

                            if (
                                capacityMt <
                                request.quantityMt
                            ) {
                                return@mapNotNull null
                            }

                            val unusedCapacity =
                                capacityMt -
                                    request.quantityMt

                            if (
                                unusedCapacity >
                                10000
                            ) {
                                score -= 15
                            }

                            if (
                                unusedCapacity >
                                50000
                            ) {
                                score -= 20
                            }

                            MatchResult(
                                vesselName =
                                    vessel["name"]
                                        as String,
                                vesselType =
                                    vessel["type"]
                                        as String,
                                capacity =
                                    "%,d MT".format(
                                        capacityMt
                                    ),
                                eta =
                                    eta,
                                suitability =
                                    score.coerceIn(
                                        0,
                                        100
                                    )
                            )
                        }
                        .sortedByDescending {
                            it.suitability
                        }

                call.respond(
                    matches
                )

            } catch (e: Exception) {

                logger.error(
                    "Cargo matching request failed: ${e.message}",
                    e
                )

                call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid request format"
                )
            }
        }

        get("/api/vendors") {
            call.respond(
                vendorDatabase
            )
        }

        get("/api/auction/bids") {

            val cargoType =
                call.request.queryParameters["cargoType"]
                    ?.trim()
                    ?.ifBlank { "Dry Bulk" }
                    ?: "Dry Bulk"

            val quantityMt =
                call.request.queryParameters["quantityMt"]
                    ?.toIntOrNull()
                    ?.coerceIn(1000, 250000)
                    ?: 75000

            val origin =
                call.request.queryParameters["origin"]
                    ?.trim()
                    ?.ifBlank { "Australia" }
                    ?: "Australia"

            val destination =
                call.request.queryParameters["destination"]
                    ?.trim()
                    ?.ifBlank { "Paradip" }
                    ?: "Paradip"

            val route = "$origin → $destination"

            val availableVessels =
                listOf(
                    Triple("VERONICA", "Panamax", 75000),
                    Triple("EVER GIVEN", "Capesize", 120000),
                    Triple("MSC OSCAR", "Panamax", 70000),
                    Triple("COSCO SHIPPING", "Supramax", 55000),
                    Triple("MAERSK MCKINNEY", "Capesize", 180000),
                    Triple("HMM ALGECIRAS", "Panamax", 82000)
                )

            fun cargoFit(vesselType: String): Double {
                return when (cargoType.lowercase()) {
                    "containers", "container" ->
                        when (vesselType) {
                            "Panamax", "Supramax" -> 1.0
                            else -> 0.82
                        }
                    "ore" ->
                        when (vesselType) {
                            "Capesize" -> 1.0
                            "Panamax" -> 0.72
                            else -> 0.65
                        }
                    "grain" ->
                        when (vesselType) {
                            "Panamax" -> 1.0
                            "Supramax" -> 0.94
                            "Capesize" -> 0.78
                            else -> 0.65
                        }
                    else ->
                        when (vesselType) {
                            "Panamax", "Capesize" -> 1.0
                            "Supramax" -> 0.92
                            else -> 0.8
                        }
                }
            }

            fun routeFactor(port: String): Double {
                return when {
                    port.equals("Paradip", true) -> 1.00
                    port.equals("Vizag", true) -> 0.97
                    port.equals("Dhamra", true) -> 1.03
                    port.equals("Haldia", true) -> 1.06
                    port.equals("Gangavaram", true) -> 0.99
                    else -> 1.02
                }
            }

            fun originFactor(port: String): Double {
                return when {
                    port.equals("Australia", true) -> 1.00
                    port.equals("Indonesia", true) -> 0.94
                    port.equals("Brazil", true) -> 1.08
                    port.equals("South Africa", true) -> 1.05
                    port.equals("Singapore", true) -> 0.98
                    else -> 1.01
                }
            }

            val routeRateFactor =
                originFactor(origin) * routeFactor(destination)

            val bids =
                availableVessels
                    .mapIndexedNotNull { index, vessel ->
                        val capacity = vessel.third

                        if (capacity < quantityMt) {
                            null
                        } else {
                            val utilization =
                                quantityMt.toDouble() / capacity.toDouble()

                            val baseRate =
                                15.8 +
                                    (index * 0.62) +
                                    (1.0 - utilization) * 2.8

                            val adjustedRate =
                                kotlin.math.round(
                                    baseRate *
                                        routeRateFactor *
                                        (1.10 - cargoFit(vessel.second) * 0.10) *
                                        100
                                ) / 100.0

                            val score =
                                kotlin.math.round(
                                    (
                                        76.0 +
                                            cargoFit(vessel.second) * 16.0 +
                                            utilization * 6.0 -
                                            if (routeRateFactor > 1.02) 4.0 else 0.0
                                    )
                                )
                                    .toInt()
                                    .coerceIn(0, 99)

                            BidItem(
                                vendor = "Vendor ${'A' + index}",
                                vessel = vessel.first,
                                rate = adjustedRate,
                                eta = "${17 + index} Oct",
                                score = score
                            )
                        }
                    }
                    .sortedWith(
                        compareByDescending<BidItem> { it.score }
                            .thenBy { it.rate }
                    )

            val bestBid = bids.firstOrNull()

            val routeInsight =
                when {
                    destination.equals("Haldia", true) ->
                        "Route factor is elevated for $destination, so the recommendation prioritizes vessel efficiency and lower exposure."
                    destination.equals("Dhamra", true) ->
                        "$destination is priced with a moderate route premium; a right-sized vessel helps control voyage cost."
                    destination.equals("Vizag", true) ->
                        "$destination currently carries a slightly lower route factor, improving the case for a cost-efficient match."
                    else ->
                        "Recommendation is recalculated using the selected cargo volume, cargo type, vessel capacity and destination."
                }

            val recommendation =
                if (bestBid != null) {
                    val bestCapacity =
                        availableVessels
                            .first { it.first == bestBid.vessel }
                            .third

                    val utilization =
                        kotlin.math.round(
                            quantityMt.toDouble() / bestCapacity.toDouble() * 100.0
                        ).toInt()

                    "For ${"%,d".format(quantityMt)} MT of $cargoType from $origin to $destination, ${bestBid.vessel} is the best fit at $${bestBid.rate}/MT with ${utilization}% capacity utilization and a ${bestBid.score}/100 suitability score. ${if (routeRateFactor > 1.02) "The selected route adds cost pressure, so avoiding excess capacity is recommended." else "The selected route supports a balanced cost and capacity match."}"
                } else {
                    "No listed vessel can safely cover ${"%,d".format(quantityMt)} MT of $cargoType on the selected route. Reduce the cargo limit or choose a larger vessel option."
                }

            val response =
                FreightAuctionResponse(
                    cargoRequirement = "%,d MT".format(quantityMt),
                    quantityMt = quantityMt,
                    cargoType = cargoType,
                    origin = origin,
                    destination = destination,
                    route = route,
                    recommendation = recommendation,
                    routeInsight = routeInsight,
                    bids = bids
                )

            call.respond(response)
        }


        post("/api/auction/award") {

            try {

                val req =
                    call.receive<AwardRequest>()

                vendorDatabase
                    .flatMap {
                        it.vessels
                    }
                    .find {
                        it.name ==
                            req.vesselName
                    }
                    ?.isAwarded = true

                call.respondText(
                    """{"status":"success"}""",
                    ContentType.Application.Json
                )

            } catch (e: Exception) {

                logger.error(
                    "Failed to award contract: ${e.message}",
                    e
                )

                call.respondText(
                    """{"status":"error","message":"Invalid award request"}""",
                    ContentType.Application.Json,
                    HttpStatusCode.BadRequest
                )
            }
        }

        post("/api/vendor/upload") {

            try {

                val uploadDirectory =
                    File("build")

                if (!uploadDirectory.exists()) {
                    uploadDirectory.mkdirs()
                }

                val multipart =
                    call.receiveMultipart()

                multipart.forEachPart { part ->

                    if (part is PartData.FileItem) {

                        val fileName =
                            part.originalFileName

                        if (
                            !fileName.isNullOrBlank()
                        ) {

                            val fileBytes =
                                part
                                    .streamProvider()
                                    .readBytes()

                            File(
                                uploadDirectory,
                                fileName
                            ).writeBytes(
                                fileBytes
                            )
                        }
                    }

                    part.dispose()
                }

                call.respondText(
                    """{"status":"uploaded"}""",
                    ContentType.Application.Json
                )

            } catch (e: Exception) {

                logger.error(
                    "Vendor document upload failed: ${e.message}",
                    e
                )

                call.respondText(
                    """{"status":"error","message":"Upload failed"}""",
                    ContentType.Application.Json,
                    HttpStatusCode.BadRequest
                )
            }
        }

        post("/api/vendors/submit") {

            try {

                val submission =
                    call.receive<VendorSubmission>()

                registeredVendors.add(
                    submission
                )

                call.respond(
                    mapOf(
                        "status" to
                            "success",
                        "message" to
                            "Vessel and profile successfully submitted"
                    )
                )

            } catch (e: Exception) {

                logger.error(
                    "Vendor submission failed: ${e.message}",
                    e
                )

                call.respondText(
                    """{"status":"error","message":"Invalid vendor submission format"}""",
                    ContentType.Application.Json,
                    HttpStatusCode.BadRequest
                )
            }
        }

        get("/api/health") {

            call.respondText(
                """{"status":"ok","service":"Cargonex API","version":"1.0.0"}""",
                ContentType.Application.Json
            )
        }
    }
}

private fun loadBdiCsv(): List<BdiRecord> {

    val stream =
        object {}.javaClass
            .classLoader
            .getResourceAsStream(
                "Baltic Dry Index Historical Data.csv"
            )
            ?: throw IllegalStateException(
                "Baltic Dry Index Historical Data.csv not found in src/main/resources"
            )

    return stream
        .bufferedReader()
        .useLines { lines ->

            lines
                .drop(1)
                .mapNotNull { line ->
                    parseCsvLine(line)
                }
                .toList()
                .sortedBy {
                    it.date
                }
        }
}

private fun parseCsvLine(
    line: String
): BdiRecord? {

    val values =
        splitCsvLine(line)

    if (values.size < 6) {
        return null
    }

    return try {

        BdiRecord(
            date =
                normalizeDate(
                    values[0]
                ),
            price =
                parseNumber(
                    values[1]
                ),
            open =
                parseNumber(
                    values[2]
                ),
            high =
                parseNumber(
                    values[3]
                ),
            low =
                parseNumber(
                    values[4]
                ),
            volume =
                values
                    .getOrNull(5)
                    ?.trim()
                    .orEmpty(),
            changePercent =
                parsePercent(
                    values
                        .getOrNull(6)
                        .orEmpty()
                )
        )

    } catch (_: Exception) {
        null
    }
}

private fun splitCsvLine(
    line: String
): List<String> {

    val result =
        mutableListOf<String>()

    val current =
        StringBuilder()

    var insideQuotes =
        false

    for (character in line) {

        when {

            character == '"' -> {
                insideQuotes =
                    !insideQuotes
            }

            character == ',' &&
                !insideQuotes -> {

                result.add(
                    current.toString()
                )

                current.clear()
            }

            else -> {
                current.append(
                    character
                )
            }
        }
    }

    result.add(
        current.toString()
    )

    return result
}

private fun parseNumber(
    value: String
): Double {

    return value
        .trim()
        .replace(",", "")
        .replace("\"", "")
        .toDoubleOrNull()
        ?: 0.0
}

private fun parsePercent(
    value: String
): Double {

    return value
        .trim()
        .replace("%", "")
        .replace("\"", "")
        .toDoubleOrNull()
        ?: 0.0
}

private fun normalizeDate(
    value: String
): String {

    return value
        .trim()
        .replace("\"", "")
}

private fun buildForecast(
    prices: List<Double>,
    days: Int
): List<Double> {

    if (prices.isEmpty()) {
        return emptyList()
    }

    val window =
        prices.takeLast(14)

    if (window.size < 2) {
        return List(days) {
            prices.last()
        }
    }

    val first =
        window.first()

    val last =
        window.last()

    val slope =
        (last - first) /
            (window.size - 1)

    return (1..days).map { day ->
        (last + slope * day)
            .coerceAtLeast(0.0)
    }
}

private fun escapeJson(
    value: String
): String {

    return value
        .replace(
            "\\",
            "\\\\"
        )
        .replace(
            "\"",
            "\\\""
        )
        .replace(
            "\n",
            "\\n"
        )
        .replace(
            "\r",
            "\\r"
        )
}

@Serializable
data class WeatherResponse(
    val wind: WindInfo? = null,
    val main: MainInfo? = null
)

@Serializable
data class WindInfo(
    val speed: Double = 0.0
)

@Serializable
data class MainInfo(
    val temp: Double = 0.0,
    val humidity: Int = 0
)

@Serializable
data class NewsResponse(
    val totalResults: Int = 0
)

@Serializable
data class RiskItem(
    val name: String,
    val probability: Int,
    val impact: Int,
    val level: String,
    val score: Int,
    val colorClass: String
)

@Serializable
data class RiskMatrixResponse(
    val currentRoute: String,
    val aggregateScore: Int,
    val summary: String,
    val risks: List<RiskItem>
)

@Serializable
data class CargoRequest(
    val cargo: String,
    val quantityMt: Int,
    val origin: String,
    val destination: String,
    val laycanStart: String,
    val laycanEnd: String
)

@Serializable
data class MatchResult(
    val vesselName: String,
    val vesselType: String,
    val capacity: String,
    val eta: String,
    val suitability: Int
)

@Serializable
data class VendorSubmission(
    val vendorName: String,
    val vesselName: String,
    val vesselType: String,
    val charterRate: Double,
    val eta: String,
    val reliability: Double,
    val compliance: Double,
    val vesselQuality: Double,
    val cargoCompatibility: String,
    val availabilityWindow: String
)

@Serializable
data class BdiRecord(
    val date: String,
    val price: Double,
    val open: Double,
    val high: Double,
    val low: Double,
    val volume: String,
    val changePercent: Double
)

@Serializable
data class RateForecastResponse(
    val market: String,
    val indexName: String,
    val currentRate: Double,
    val latestDate: String,
    val dailyChange: Double,
    val marketTrend: String,
    val high: Double,
    val low: Double,
    val dataPoints: Int,
    val routeContext: String,
    val insight: String,
    val historical: List<BdiRecord>,
    val forecast: List<Double>
)

@Serializable
data class VesselInfo(
    val name: String,
    val type: String,
    val capacity: String
)

@Serializable
data class VesselList(
    val vessels: List<VesselInfo>
)

@Serializable
data class RateInfo(
    val name: String,
    val price: String,
    val description: String
)

@Serializable
data class RatesResponse(
    val rates: Map<String, RateInfo>
)