Flyweight Design Pattern Implementation
GeoDash Marker Rendering System

1. Problem Overview
The GeoDash map system renders a large number of markers on a map.
Each marker has:
geographic location
label
visual style
Example marker:
M-101 @ (12.9342, 77.6111)
style = PIN|RED|12|F

A marker style consists of:
shape
color
size
filled / outline

Example:
PIN | RED | 12 | FILLED

When rendering thousands of markers, many markers share identical styles.
Example:
Marker 1 → PIN RED 12 FILLED
Marker 2 → PIN RED 12 FILLED
Marker 3 → PIN RED 12 FILLED

However, if each marker creates its own style object, the system wastes memory.
Example scenario:
30,000 markers
30,000 MarkerStyle objects

Even though only about 96 unique style combinations are possible.

2. Original System Structure
Project folder structure:
flyweight-markers/src
└── com/example/map
    ├── App.java
    ├── MapDataSource.java
    ├── MapMarker.java
    ├── MapRenderer.java
    ├── MarkerStyle.java
    ├── MarkerStyleFactory.java
    └── QuickCheck.java

File Responsibilities
App.java
Program entry point.
int n = 30_000;
MapDataSource ds = new MapDataSource();
List<MapMarker> markers = ds.loadMarkers(n);
new MapRenderer().render(markers);


MapDataSource.java
Simulates loading marker data.
Generates:
random coordinates
labels
random marker styles

MapMarker.java
Represents a single marker on the map.
Stores:
lat
lng
label
style


MapRenderer.java
Simulates rendering markers.
Prints sample markers to console.

MarkerStyle.java
Represents the visual style of markers.
Example:
PIN|RED|12|F


MarkerStyleFactory.java
Creates and reuses shared MarkerStyle objects.

QuickCheck.java
Validation tool that counts unique style instances used by markers.

3. Problem in the Naive Design
If every marker creates its own style:
new MarkerStyle(shape, color, size, filled)

Memory usage becomes:
Markers: 30,000
Style objects: 30,000

Even though many styles are identical.
Example duplicates:
PIN|RED|12|F
PIN|RED|12|F
PIN|RED|12|F

This causes unnecessary memory consumption.

4. Flyweight Design Pattern
The Flyweight Pattern reduces memory usage by sharing common objects.
It separates state into two categories.

Intrinsic State (Shared)
Data that is common across many objects.
MarkerStyle
    shape
    color
    size
    filled

This state is shared between markers.

Extrinsic State (Per Object)
Data unique to each marker.
MapMarker
    lat
    lng
    label

This state cannot be shared.

Final Structure
MapMarker
 ├── lat
 ├── lng
 ├── label
 └── MarkerStyle (shared flyweight)

Many markers reference the same style object.

5. Refactored Implementation
The Flyweight pattern was implemented using:
MarkerStyle (Flyweight)
MarkerStyleFactory (Flyweight Factory)
MapMarker (Context)


6. MarkerStyle – Flyweight Object
MarkerStyle represents the shared style data.
The class is designed to be immutable.
public class MarkerStyle {

    private final String shape;
    private final String color;
    private final int size;
    private final boolean filled;

    public MarkerStyle(String shape, String color, int size, boolean filled) {
        this.shape = shape;
        this.color = color;
        this.size = size;
        this.filled = filled;
    }
}

Why Immutable?
Flyweight objects must be immutable so that shared objects cannot be modified by one marker and affect others.
Example style format:
PIN|RED|12|F


7. MarkerStyleFactory – Flyweight Factory
The factory ensures only one instance exists per unique style.
private final Map<String, MarkerStyle> cache = new HashMap<>();

Key generation:
String key = shape + "|" + color + "|" + size + "|" + (filled ? "F" : "O");

Object retrieval logic:
MarkerStyle style = cache.get(key);

if (style == null) {
    style = new MarkerStyle(shape, color, size, filled);
    cache.put(key, style);
}

return style;

Result
If the same style is requested again:
PIN|RED|12|F

The existing object is reused.

8. MapMarker – Context Object
Each marker stores extrinsic data plus a reference to a shared style.
public class MapMarker {

    private final double lat;
    private final double lng;
    private final String label;
    private final MarkerStyle style;

    public MapMarker(double lat, double lng, String label, MarkerStyle style) {
        this.lat = lat;
        this.lng = lng;
        this.label = label;
        this.style = style;
    }
}

Markers do not create styles themselves.
They simply receive a shared style object.

9. MapDataSource – Using the Factory
The data source generates markers and requests styles from the factory.
MarkerStyle style = styleFactory.get(shape, color, size, filled);

out.add(new MapMarker(lat, lng, label, style));

Now identical styles reuse the same object.

10. MapRenderer – Rendering Markers
The renderer prints markers to the console.
Example output:
Rendering 30000 markers...
M-0 @ (12.9342, 77.6121) style=PIN|RED|12|F
M-1 @ (12.9123, 77.5902) style=CIRCLE|BLUE|10|O
M-2 @ (12.9211, 77.5888) style=SQUARE|GREEN|16|F
...

Rendering logic does not change with Flyweight.

11. QuickCheck Validation
The QuickCheck program verifies that styles are shared.
It counts unique style object references.
identities.add(System.identityHashCode(m.getStyle()));

Output example:
Markers: 20000
Unique style instances (by identity): 96
Expected after Flyweight: <= 96


12. Maximum Number of Styles
The number of possible styles is:
Shapes  = 3
Colors  = 4
Sizes   = 4
Filled  = 2

Total combinations:
3 × 4 × 4 × 2 = 96

So even with 30,000 markers, the system creates at most 96 style objects.

13. Memory Optimization
Before Flyweight
Markers: 30,000
Style objects: 30,000

After Flyweight
Markers: 30,000
Style objects: ≤ 96

Memory usage is reduced dramatically.

14. Benefits of the Flyweight Implementation
Memory Efficiency
Large reduction in duplicate style objects.

Object Reuse
Shared objects reduce creation overhead.

Immutable Design
Ensures safe sharing across markers.

Scalability
The system can render hundreds of thousands of markers efficiently.

15. Conclusion
The Flyweight pattern was successfully applied to the GeoDash marker system.
The refactored design:
separates intrinsic and extrinsic state
shares immutable style objects
reduces memory usage dramatically
keeps the rendering system unchanged
As a result, the system can efficiently render large numbers of map markers while using minimal memory.

