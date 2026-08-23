plugins {
    id("com.possible-triangle.common")
}

val moonlight_version: String by extra
val quark_file_id: String by extra

dependencies {
    modCompileOnly("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")

    modCompileOnly("curse.maven:quark-243121:${quark_file_id}")
}
