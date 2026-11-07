plugins {
    id("com.possible-triangle.common")
}

common {
    neoformVersion = "26.1.2-1"
}

val moonlight_version: String by extra

dependencies {
    modCompileOnly("net.mehvahdjukaar:moonlight-common:${moonlight_version}") { isTransitive = false }
    accessTransformers("net.mehvahdjukaar:moonlight-common:${moonlight_version}")

    modCompileOnly("maven.modrinth:enchanted-vertical-slabs:EUlKZFNN")
}
