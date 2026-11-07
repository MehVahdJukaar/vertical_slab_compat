plugins {
    id("com.possible-triangle.common")
}

common {
    neoformVersion = "1.21.1-20240808.144430"
}

val moonlight_version: String by extra

dependencies {
    modCompileOnly("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}@jar")
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")
}
