plugins {
    id("com.possible-triangle.fabric")
}

fabric {
    dependOn(project(":common"))
}

val moonlight_version: String by extra

dependencies {
    modImplementation("net.mehvahdjukaar:moonlight-fabric:${moonlight_version}")
}
