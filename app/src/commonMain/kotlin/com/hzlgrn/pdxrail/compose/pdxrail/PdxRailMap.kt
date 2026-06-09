package com.hzlgrn.pdxrail.compose.pdxrail

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hzlgrn.pdxrail.data.geo.LatLon
import com.hzlgrn.pdxrail.data.help.PdxRailSystemHelper
import com.hzlgrn.pdxrail.generated.resources.Res
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival_blue
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival_green
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival_orange
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival_red
import com.hzlgrn.pdxrail.generated.resources.marker_max_arrival_yellow
import com.hzlgrn.pdxrail.generated.resources.marker_max_stop
import com.hzlgrn.pdxrail.generated.resources.marker_select
import com.hzlgrn.pdxrail.generated.resources.marker_streetcar_a_loop
import com.hzlgrn.pdxrail.generated.resources.marker_streetcar_b_loop
import com.hzlgrn.pdxrail.generated.resources.marker_streetcar_ns_line
import com.hzlgrn.pdxrail.generated.resources.marker_streetcar_stop
import com.hzlgrn.pdxrail.theme.LocalAppDimensions
import com.hzlgrn.pdxrail.viewmodel.MapBounds
import com.hzlgrn.pdxrail.viewmodel.MapViewport
import com.hzlgrn.pdxrail.viewmodel.PdxRailViewModel
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemArrivals
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapItem
import com.hzlgrn.pdxrail.viewmodel.railsystem.RailSystemMapState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.take
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.compose.resources.imageResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.eq
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.value.NumberValue
import org.maplibre.compose.expressions.value.StringValue
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.location.rememberDefaultLocationProvider
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds

private const val STYLE_LIGHT = "https://tiles.openfreemap.org/styles/liberty"
private const val STYLE_DARK = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"

private const val EMPTY_FC_JSON = """{"type":"FeatureCollection","features":[]}"""
private val EMPTY_FC = GeoJsonData.JsonString(EMPTY_FC_JSON)

private const val IMG_ARRIVAL_DEFAULT = "arrival-default"
private const val IMG_ARRIVAL_BLUE = "arrival-blue"
private const val IMG_ARRIVAL_GREEN = "arrival-green"
private const val IMG_ARRIVAL_ORANGE = "arrival-orange"
private const val IMG_ARRIVAL_RED = "arrival-red"
private const val IMG_ARRIVAL_YELLOW = "arrival-yellow"
private const val IMG_ARRIVAL_NS = "arrival-ns"
private const val IMG_ARRIVAL_A_LOOP = "arrival-a-loop"
private const val IMG_ARRIVAL_B_LOOP = "arrival-b-loop"

private val MAX_BLUE_COLOR   = Color(0xFF0069AA)
private val MAX_GREEN_COLOR  = Color(0xFF008752)
private val MAX_ORANGE_COLOR = Color(0xFFD15F27)
private val MAX_RED_COLOR    = Color(0xFFD11242)
private val MAX_YELLOW_COLOR = Color(0xFFFFC425)
private val WES_COLOR        = Color(0xFF231F20)
private val SC_A_COLOR       = Color(0xFFDD288E)
private val SC_B_COLOR       = Color(0xFF0091B2)
private val SC_NS_COLOR      = Color(0xFF8CC63E)

@Composable
@Suppress("MissingPermission")
fun PdxRailMap(pdxRailViewModel: PdxRailViewModel) {
    val railSystemMap by pdxRailViewModel.railSystemMap.collectAsState()
    val railSystemArrivals by pdxRailViewModel.railSystemArrivals.collectAsState()
    val isMyLocationEnabled by pdxRailViewModel.isMyLocationEnabled.collectAsState()
    val isDarkTheme = isSystemInDarkTheme()
    val dimens = LocalAppDimensions.current

    val cameraState = rememberCameraState(
        CameraPosition(
            zoom = PdxRailSystemHelper.CAMERA.ZOOM.toDouble(),
            target = Position(
                longitude = PdxRailSystemHelper.CAMERA.TARGET_LNG,
                latitude = PdxRailSystemHelper.CAMERA.TARGET_LAT,
            ),
        )
    )
    val styleState = rememberStyleState()

    val maxStopBitmap       = imageResource(Res.drawable.marker_max_stop)
    val streetcarStopBitmap = imageResource(Res.drawable.marker_streetcar_stop)
    val selectBitmap        = imageResource(Res.drawable.marker_select)
    val arrivalDefaultBitmap = imageResource(Res.drawable.marker_max_arrival)
    val arrivalBlueBitmap   = imageResource(Res.drawable.marker_max_arrival_blue)
    val arrivalGreenBitmap  = imageResource(Res.drawable.marker_max_arrival_green)
    val arrivalOrangeBitmap = imageResource(Res.drawable.marker_max_arrival_orange)
    val arrivalRedBitmap    = imageResource(Res.drawable.marker_max_arrival_red)
    val arrivalYellowBitmap = imageResource(Res.drawable.marker_max_arrival_yellow)
    val arrivalNsBitmap     = imageResource(Res.drawable.marker_streetcar_ns_line)
    val arrivalALoopBitmap  = imageResource(Res.drawable.marker_streetcar_a_loop)
    val arrivalBLoopBitmap  = imageResource(Res.drawable.marker_streetcar_b_loop)

    val selectedStopPosition by pdxRailViewModel.selectedStopPosition.collectAsState()

    LaunchedEffect(Unit) { pdxRailViewModel.onMapLoaded() }

    LaunchedEffect(selectedStopPosition) {
        selectedStopPosition?.let { pos ->
            cameraState.animateTo(
                CameraPosition(
                    target = Position(longitude = pos.lon, latitude = pos.lat),
                    zoom = cameraState.position.zoom,
                )
            )
        }
    }

    LaunchedEffect(cameraState) {
        snapshotFlow { cameraState.position }
            .sample(200.milliseconds)
            .collect { position ->
                val bb = cameraState.projection?.queryVisibleBoundingBox()
                pdxRailViewModel.updateMapViewport(
                    MapViewport(
                        center = LatLon(
                            lat = position.target.latitude,
                            lon = position.target.longitude,
                        ),
                        zoom = position.zoom,
                        bounds = bb?.let {
                            MapBounds(north = it.north, south = it.south, east = it.east, west = it.west)
                        },
                    )
                )
            }
    }

    val mapDisplay: RailSystemMapState.Display? = railSystemMap as? RailSystemMapState.Display
    val arrivalsDisplay = railSystemArrivals as? RailSystemArrivals.Display

    // Capture stop lists for click handler lambdas
    val maxStopData      = mapDisplay?.maxStopData      ?: emptyList()
    val commuterStopData = mapDisplay?.commuterStopData ?: emptyList()
    val streetcarStopData = mapDisplay?.streetcarStopData ?: emptyList()

    val selectedStopFC: GeoJsonData = selectedStopPosition?.let { pos ->
        GeoJsonData.JsonString("""{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[${pos.lon},${pos.lat}]},"properties":{}}]}""")
    } ?: EMPTY_FC

    Box(modifier = Modifier.fillMaxSize()) {
    MaplibreMap(
        modifier = Modifier.fillMaxSize(),
        baseStyle = BaseStyle.Uri(if (isDarkTheme) STYLE_DARK else STYLE_LIGHT),
        cameraState = cameraState,
        styleState = styleState,
        zoomRange = PdxRailSystemHelper.BOUNDS.ZOOM_MIN..20f,
        boundingBox = BoundingBox(
            west  = PdxRailSystemHelper.BOUNDS.WEST,
            south = PdxRailSystemHelper.BOUNDS.SOUTH,
            east  = PdxRailSystemHelper.BOUNDS.EAST,
            north = PdxRailSystemHelper.BOUNDS.NORTH,
        ),
        options = MapOptions(ornamentOptions = OrnamentOptions(isLogoEnabled = false, isAttributionEnabled = false, scaleBarAlignment = Alignment.TopCenter)),
        onMapClick = { _, _ ->
            pdxRailViewModel.clearSelectedStop()
            ClickResult.Pass
        },
    ) {
            val outlineColor = if (isDarkTheme) Color.DarkGray else Color.White

            val blueSrc        = rememberGeoJsonSource(mapDisplay?.blueFC ?: EMPTY_FC)
            val greenSrc       = rememberGeoJsonSource(mapDisplay?.greenFC ?: EMPTY_FC)
            val orangeSrc      = rememberGeoJsonSource(mapDisplay?.orangeFC ?: EMPTY_FC)
            val redSrc         = rememberGeoJsonSource(mapDisplay?.redFC ?: EMPTY_FC)
            val yellowSrc      = rememberGeoJsonSource(mapDisplay?.yellowFC ?: EMPTY_FC)
            val wesSrc         = rememberGeoJsonSource(mapDisplay?.wesFC ?: EMPTY_FC)
            val blueGreenSrc   = rememberGeoJsonSource(mapDisplay?.blueGreenFC ?: EMPTY_FC)
            val blueRedSrc     = rememberGeoJsonSource(mapDisplay?.blueRedFC ?: EMPTY_FC)
            val greenOrangeSrc = rememberGeoJsonSource(mapDisplay?.greenOrangeFC ?: EMPTY_FC)
            val greenYellowSrc = rememberGeoJsonSource(mapDisplay?.greenYellowFC ?: EMPTY_FC)
            val bgrSrc         = rememberGeoJsonSource(mapDisplay?.bgrFC ?: EMPTY_FC)
            val bgrySrc        = rememberGeoJsonSource(mapDisplay?.bgryFC ?: EMPTY_FC)
            val scASrc         = rememberGeoJsonSource(mapDisplay?.scAFC ?: EMPTY_FC)
            val scBSrc         = rememberGeoJsonSource(mapDisplay?.scBFC ?: EMPTY_FC)
            val scNsSrc        = rememberGeoJsonSource(mapDisplay?.scNsFC ?: EMPTY_FC)
            val scAbSrc        = rememberGeoJsonSource(mapDisplay?.scAbFC ?: EMPTY_FC)
            val scNsaSrc       = rememberGeoJsonSource(mapDisplay?.scNsaFC ?: EMPTY_FC)
            val scNsbSrc       = rememberGeoJsonSource(mapDisplay?.scNsbFC ?: EMPTY_FC)
            val scNsabSrc      = rememberGeoJsonSource(mapDisplay?.scNsabFC ?: EMPTY_FC)
            val scMaxAbOrSrc   = rememberGeoJsonSource(mapDisplay?.scMaxAbOrFC ?: EMPTY_FC)
            val basicSrc       = rememberGeoJsonSource(mapDisplay?.basicFC ?: EMPTY_FC)

            val maxStopSource       = rememberGeoJsonSource(mapDisplay?.maxStops ?: EMPTY_FC)
            val streetcarStopSource = rememberGeoJsonSource(mapDisplay?.streetcarStops ?: EMPTY_FC)
            val selectedStopSource  = rememberGeoJsonSource(selectedStopFC)

            val arrivalBlueSrc    = rememberGeoJsonSource(arrivalsDisplay?.arrivalBlueFeatures ?: EMPTY_FC)
            val arrivalGreenSrc   = rememberGeoJsonSource(arrivalsDisplay?.arrivalGreenFeatures ?: EMPTY_FC)
            val arrivalOrangeSrc  = rememberGeoJsonSource(arrivalsDisplay?.arrivalOrangeFeatures ?: EMPTY_FC)
            val arrivalRedSrc     = rememberGeoJsonSource(arrivalsDisplay?.arrivalRedFeatures ?: EMPTY_FC)
            val arrivalYellowSrc  = rememberGeoJsonSource(arrivalsDisplay?.arrivalYellowFeatures ?: EMPTY_FC)
            val arrivalNSSrc      = rememberGeoJsonSource(arrivalsDisplay?.arrivalNSFeatures ?: EMPTY_FC)
            val arrivalALoopSrc   = rememberGeoJsonSource(arrivalsDisplay?.arrivalALoopFeatures ?: EMPTY_FC)
            val arrivalBLoopSrc   = rememberGeoJsonSource(arrivalsDisplay?.arrivalBLoopFeatures ?: EMPTY_FC)
            val arrivalDefaultSrc = rememberGeoJsonSource(arrivalsDisplay?.arrivalDefaultFeatures ?: EMPTY_FC)

            // ── Outline layers (drawn beneath all color layers) ──────────────────────
            LineLayer(id = "lo-blue",         source = blueSrc,        color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-green",        source = greenSrc,       color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-orange",       source = orangeSrc,      color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-red",          source = redSrc,         color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-yellow",       source = yellowSrc,      color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-wes",          source = wesSrc,         color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-blue-green",   source = blueGreenSrc,   color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-blue-red",     source = blueRedSrc,     color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-green-orange", source = greenOrangeSrc, color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-green-yellow", source = greenYellowSrc, color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-bgr",          source = bgrSrc,         color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-bgry",         source = bgrySrc,        color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-sc-a",         source = scASrc,         color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-sc-b",         source = scBSrc,         color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-sc-ns",        source = scNsSrc,        color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-sc-ab",        source = scAbSrc,        color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-sc-nsa",       source = scNsaSrc,       color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-sc-nsb",       source = scNsbSrc,       color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-sc-nsab",      source = scNsabSrc,      color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-sc-max-ab-or", source = scMaxAbOrSrc,   color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))
            LineLayer(id = "lo-basic",        source = basicSrc,       color = const(outlineColor), width = const(dimens.mapLineOutlineWidth))

            // ── Single-color lines ───────────────────────────────────────────────────
            LineLayer(id = "lc-blue",   source = blueSrc,   color = const(MAX_BLUE_COLOR),   width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-green",  source = greenSrc,  color = const(MAX_GREEN_COLOR),  width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-orange", source = orangeSrc, color = const(MAX_ORANGE_COLOR), width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-red",    source = redSrc,    color = const(MAX_RED_COLOR),    width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-yellow", source = yellowSrc, color = const(MAX_YELLOW_COLOR), width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-wes",    source = wesSrc,    color = const(WES_COLOR),        width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-sc-a",   source = scASrc,    color = const(SC_A_COLOR),       width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-sc-b",   source = scBSrc,    color = const(SC_B_COLOR),       width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-sc-ns",  source = scNsSrc,   color = const(SC_NS_COLOR),      width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-basic",  source = basicSrc,  color = const(Color.Gray),       width = const(dimens.mapLineWidth))

            // ── Dual-color lines — solid base + dashed overlay [4,4] ────────────────
            LineLayer(id = "lc-blue-green-a",  source = blueGreenSrc,  color = const(MAX_BLUE_COLOR),   width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-blue-green-b",  source = blueGreenSrc,  color = const(MAX_GREEN_COLOR),  width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(4, 4)))
            LineLayer(id = "lc-blue-red-a",    source = blueRedSrc,    color = const(MAX_BLUE_COLOR),   width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-blue-red-b",    source = blueRedSrc,    color = const(MAX_RED_COLOR),    width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(4, 4)))
            LineLayer(id = "lc-green-orange-a",source = greenOrangeSrc,color = const(MAX_GREEN_COLOR),  width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-green-orange-b",source = greenOrangeSrc,color = const(MAX_ORANGE_COLOR), width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(4, 4)))
            LineLayer(id = "lc-green-yellow-a",source = greenYellowSrc,color = const(MAX_GREEN_COLOR),  width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-green-yellow-b",source = greenYellowSrc,color = const(MAX_YELLOW_COLOR), width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(4, 4)))
            LineLayer(id = "lc-sc-ab-a",       source = scAbSrc,       color = const(SC_A_COLOR),       width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-sc-ab-b",       source = scAbSrc,       color = const(SC_B_COLOR),       width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(4, 4)))
            LineLayer(id = "lc-sc-nsa-a",      source = scNsaSrc,      color = const(SC_NS_COLOR),      width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-sc-nsa-b",      source = scNsaSrc,      color = const(SC_A_COLOR),       width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(4, 4)))
            LineLayer(id = "lc-sc-nsb-a",      source = scNsbSrc,      color = const(SC_NS_COLOR),      width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-sc-nsb-b",      source = scNsbSrc,      color = const(SC_B_COLOR),       width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(4, 4)))

            // ── Triple-color lines — solid base + [6,3] + [3,6] ─────────────────────
            LineLayer(id = "lc-bgr-a",          source = bgrSrc,       color = const(MAX_BLUE_COLOR),   width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-bgr-b",          source = bgrSrc,       color = const(MAX_GREEN_COLOR),  width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(6, 3)))
            LineLayer(id = "lc-bgr-c",          source = bgrSrc,       color = const(MAX_RED_COLOR),    width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(3, 6)))
            LineLayer(id = "lc-sc-nsab-a",      source = scNsabSrc,    color = const(SC_NS_COLOR),      width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-sc-nsab-b",      source = scNsabSrc,    color = const(SC_A_COLOR),       width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(6, 3)))
            LineLayer(id = "lc-sc-nsab-c",      source = scNsabSrc,    color = const(SC_B_COLOR),       width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(3, 6)))
            LineLayer(id = "lc-sc-max-ab-or-a", source = scMaxAbOrSrc, color = const(MAX_ORANGE_COLOR), width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-sc-max-ab-or-b", source = scMaxAbOrSrc, color = const(SC_A_COLOR),       width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(6, 3)))
            LineLayer(id = "lc-sc-max-ab-or-c", source = scMaxAbOrSrc, color = const(SC_B_COLOR),       width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(3, 6)))

            // ── Quad-color line — solid base + [6,2] + [4,4] + [2,6] ────────────────
            LineLayer(id = "lc-bgry-a", source = bgrySrc, color = const(MAX_BLUE_COLOR),   width = const(dimens.mapLineWidth))
            LineLayer(id = "lc-bgry-b", source = bgrySrc, color = const(MAX_GREEN_COLOR),  width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(6, 2)))
            LineLayer(id = "lc-bgry-c", source = bgrySrc, color = const(MAX_RED_COLOR),    width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(4, 4)))
            LineLayer(id = "lc-bgry-d", source = bgrySrc, color = const(MAX_YELLOW_COLOR), width = const(dimens.mapLineWidth), dasharray = const(listOf<Number>(2, 6)))

            // ── Arrival markers ──────────────────────────────────────────────────────
            // MapLibre match expressions cannot have image() as output values, so each
            // arrival type gets its own SymbolLayer. All layers are always present;
            // empty source data when no stop is selected.
            @Suppress("UNCHECKED_CAST")
            val iconKey = feature.get("icon") as org.maplibre.compose.expressions.ast.Expression<StringValue>
            @Suppress("UNCHECKED_CAST")
            val rotation = feature.get("rotation") as org.maplibre.compose.expressions.ast.Expression<NumberValue<Number>>

            SymbolLayer(id = "arrivals-default", source = arrivalDefaultSrc, iconImage = image(arrivalDefaultBitmap), iconRotate = rotation, iconAllowOverlap = const(true), iconIgnorePlacement = const(true), filter = iconKey eq const(IMG_ARRIVAL_DEFAULT))
            SymbolLayer(id = "arrivals-blue",    source = arrivalBlueSrc,    iconImage = image(arrivalBlueBitmap),    iconRotate = rotation, iconAllowOverlap = const(true), iconIgnorePlacement = const(true), filter = iconKey eq const(IMG_ARRIVAL_BLUE))
            SymbolLayer(id = "arrivals-green",   source = arrivalGreenSrc,   iconImage = image(arrivalGreenBitmap),   iconRotate = rotation, iconAllowOverlap = const(true), iconIgnorePlacement = const(true), filter = iconKey eq const(IMG_ARRIVAL_GREEN))
            SymbolLayer(id = "arrivals-orange",  source = arrivalOrangeSrc,  iconImage = image(arrivalOrangeBitmap),  iconRotate = rotation, iconAllowOverlap = const(true), iconIgnorePlacement = const(true), filter = iconKey eq const(IMG_ARRIVAL_ORANGE))
            SymbolLayer(id = "arrivals-red",     source = arrivalRedSrc,     iconImage = image(arrivalRedBitmap),     iconRotate = rotation, iconAllowOverlap = const(true), iconIgnorePlacement = const(true), filter = iconKey eq const(IMG_ARRIVAL_RED))
            SymbolLayer(id = "arrivals-yellow",  source = arrivalYellowSrc,  iconImage = image(arrivalYellowBitmap),  iconRotate = rotation, iconAllowOverlap = const(true), iconIgnorePlacement = const(true), filter = iconKey eq const(IMG_ARRIVAL_YELLOW))
            SymbolLayer(id = "arrivals-ns",      source = arrivalNSSrc,      iconImage = image(arrivalNsBitmap),      iconRotate = rotation, iconAllowOverlap = const(true), iconIgnorePlacement = const(true), filter = iconKey eq const(IMG_ARRIVAL_NS))
            SymbolLayer(id = "arrivals-a-loop",  source = arrivalALoopSrc,   iconImage = image(arrivalALoopBitmap),   iconRotate = rotation, iconAllowOverlap = const(true), iconIgnorePlacement = const(true), filter = iconKey eq const(IMG_ARRIVAL_A_LOOP))
            SymbolLayer(id = "arrivals-b-loop",  source = arrivalBLoopSrc,   iconImage = image(arrivalBLoopBitmap),   iconRotate = rotation, iconAllowOverlap = const(true), iconIgnorePlacement = const(true), filter = iconKey eq const(IMG_ARRIVAL_B_LOOP))

            // ── Stop markers ─────────────────────────────────────────────────────────
            SymbolLayer(
                id = "max-stops-layer",
                source = maxStopSource,
                iconImage = image(maxStopBitmap),
                iconAllowOverlap = const(true),
                onClick = { features ->
                    features.firstOrNull()?.properties?.let { props ->
                        val id = (props["id"] as? JsonPrimitive)?.content ?: return@let
                        when ((props["type"] as? JsonPrimitive)?.content) {
                            "max" -> maxStopData.firstOrNull { it.uniqueId.uniqueIdString == id }
                                ?.let { pdxRailViewModel.onClickMaxStop(it) }
                            "commuter" -> commuterStopData.firstOrNull { it.uniqueId.uniqueIdString == id }
                                ?.let { pdxRailViewModel.onClickCommuterStop(it) }
                        }
                        pdxRailViewModel.openDrawer(true)
                    }
                    ClickResult.Consume
                },
            )
            SymbolLayer(
                id = "streetcar-stops-layer",
                source = streetcarStopSource,
                iconImage = image(streetcarStopBitmap),
                iconAllowOverlap = const(true),
                onClick = { features ->
                    features.firstOrNull()?.properties?.let { props ->
                        val id = (props["id"] as? JsonPrimitive)?.content ?: return@let
                        streetcarStopData.firstOrNull { it.uniqueId.uniqueIdString == id }
                            ?.let { pdxRailViewModel.onClickStreetcarStop(it) }
                        pdxRailViewModel.openDrawer(true)
                    }
                    ClickResult.Consume
                },
            )

            // ── User location ────────────────────────────────────────────────────────
            // LocationPuck is avoided here: it uses GeoJsonData.Features internally which
            // crashes on the spatialk polymorphic serializer (same bug as all other sources).
            if (isMyLocationEnabled) {
                val locationProvider = rememberDefaultLocationProvider()

                LaunchedEffect(locationProvider) {
                    locationProvider.location
                        .filterNotNull()
                        .take(1)
                        .collect { loc ->
                            val lat = loc.position.latitude
                            val lon = loc.position.longitude
                            if (lat in PdxRailSystemHelper.BOUNDS.SOUTH..PdxRailSystemHelper.BOUNDS.NORTH &&
                                lon in PdxRailSystemHelper.BOUNDS.WEST..PdxRailSystemHelper.BOUNDS.EAST
                            ) {
                                cameraState.animateTo(
                                    CameraPosition(
                                        target = Position(longitude = lon, latitude = lat),
                                        zoom = PdxRailSystemHelper.CAMERA.ZOOM.toDouble(),
                                    )
                                )
                            }
                        }
                }

                val userLocation by locationProvider.location.collectAsState()
                val userLocationFC: GeoJsonData = userLocation?.let { loc ->
                    GeoJsonData.JsonString("""{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[${loc.position.longitude},${loc.position.latitude}]},"properties":{}}]}""")
                } ?: EMPTY_FC
                val userLocationSource = rememberGeoJsonSource(userLocationFC)
                CircleLayer(
                    id = "user-location-dot",
                    source = userLocationSource,
                    radius = const(dimens.mapLocationDotRadius),
                    color = const(Color(0xFF2196F3)),
                    strokeColor = const(Color.White),
                    strokeWidth = const(dimens.mapLocationDotStroke),
                )
            }

            // ── Selected stop overlay ────────────────────────────────────────────────
            SymbolLayer(
                id = "selected-stop-layer",
                source = selectedStopSource,
                iconImage = image(selectBitmap),
                iconAllowOverlap = const(true),
                iconIgnorePlacement = const(true),
            )
        }
        if (isDarkTheme) {
            Text(
                text = "© CARTO",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = dimens.paddingXSmall, bottom = dimens.paddingXSmall),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
            )
        }
    }
}

fun List<RailSystemMapItem.Marker.Arrival>?.toGeoJsonString(): String {
    val features = (this ?: emptyList()).joinToString(",") { arrival ->
        val icon = when (arrival) {
            is RailSystemMapItem.Marker.Arrival.MaxBlue   -> IMG_ARRIVAL_BLUE
            is RailSystemMapItem.Marker.Arrival.MaxGreen  -> IMG_ARRIVAL_GREEN
            is RailSystemMapItem.Marker.Arrival.MaxOrange -> IMG_ARRIVAL_ORANGE
            is RailSystemMapItem.Marker.Arrival.MaxRed    -> IMG_ARRIVAL_RED
            is RailSystemMapItem.Marker.Arrival.MaxYellow -> IMG_ARRIVAL_YELLOW
            is RailSystemMapItem.Marker.Arrival.NSLine    -> IMG_ARRIVAL_NS
            is RailSystemMapItem.Marker.Arrival.ALoop     -> IMG_ARRIVAL_A_LOOP
            is RailSystemMapItem.Marker.Arrival.BLoop     -> IMG_ARRIVAL_B_LOOP
            else                                          -> IMG_ARRIVAL_DEFAULT
        }
        """{"type":"Feature","geometry":{"type":"Point","coordinates":[${arrival.position.lon},${arrival.position.lat}]},"properties":{"icon":"$icon","rotation":${arrival.heading}}}"""
    }
    return """{"type":"FeatureCollection","features":[$features]}"""
}
