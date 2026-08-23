plugins {
    id("com.possible-triangle.common")
}

val moonlight_version: String by extra
val quark_file_id: String by extra
val zeta_version: String by extra

dependencies {
    modCompileOnly("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")

    modCompileOnly("curse.maven:quark-243121:${quark_file_id}")
    // quark ships without zeta, but VerticalSlabBlock implements its interfaces
    modCompileOnly("org.violetmoon.zeta:Zeta:${zeta_version}")
}
