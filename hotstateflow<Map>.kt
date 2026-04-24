//In Kotlin, StateFlow does not have a built-in map operator
//that returns another StateFlow. Using the standard Flow.map
//operator on a StateFlow transforms it into a cold Flow,
//causing you to lose the value property and hot-stream behavior

//1. Converting Map Result back to StateFlow
//To transform a StateFlow while keeping it a StateFlow,
//you must use the stateIn operator after mapping


val sourceState: StateFlow<Int> = ...
val mappedState: StateFlow<String> = sourceState
    .map { it.toString() }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = sourceState.value.toString()
    )

//2. Updating a Map inside MutableStateFlow
//When a MutableStateFlow holds a Kotlin Map,
//updating the map's contents internally will
//not trigger new emissions because StateFlow
//uses equality checks (equals) to conflate values


//Wrong Way: Modifying a MutableMap instance directly will not notify collectors


//Correct Way: Use the update function to replace the entire map with a new instance


private val _mapState = MutableStateFlow<Map<String, String>>(emptyMap())

fun updateEntry(key: String, value: String) {
    _mapState.update { oldMap ->
        oldMap + (key to value) // Creates a new map instance
    }
}

//3. Key Differences
//Performance: Using mapLatest instead of map can be
//more efficient for StateFlow because it cancels
//pending transformations if the state changes again quickly


//Conflation: StateFlow always conflates updates;
//if you map a value to itself (e.g., 5 -> 5), no new emission will occur


//Lifecycle Safety: When collecting a transformed flow
//in Android, always use repeatOnLifecycle to
//avoid processing updates while the UI is in the background


//Use a MutableStateFlow to hold a Map<String, WeatherInfo>,
// where the key is the city name. To ensure the UI updates,
// you must provide a new map instance whenever data changes


class WeatherViewModel : ViewModel() {
    // A hot flow holding our "source of truth" map
    private val _cityWeatherMap = MutableStateFlow<Map<String, WeatherInfo>>(emptyMap())
    val cityWeatherMap: StateFlow<Map<String, WeatherInfo>> = _cityWeatherMap.asStateFlow()

    // Example: Updating a specific city's weather
    fun updateCityWeather(cityName: String, info: WeatherInfo) {
        _cityWeatherMap.update { currentMap ->
            // Create a new map instance with the updated entry
            currentMap + (cityName to info)
        }
    }
}

//transform stateflow for UI
val rainyCities: StateFlow<List<String>> = cityWeatherMap
    .map { map ->
        map.filter { it.value.condition == "Rain" }.keys.toList()
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Resource-safe
        initialValue = emptyList()
    )