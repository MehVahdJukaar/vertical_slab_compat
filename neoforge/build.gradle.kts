plugins {
    id("com.possible-triangle.neoforge")
}

neoforge {
    dependOn(project(":common"))
}

val moonlight_version: String by extra
val quark_file_id: String by extra
val zeta_version: String by extra

dependencies {
    modImplementation("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")

    modCompileOnly("curse.maven:quark-243121:${quark_file_id}")
    // quark ships without zeta, but VerticalSlabBlock implements its interfaces
    modCompileOnly("org.violetmoon.zeta:Zeta:${zeta_version}")

    // dev run only. quark (+ its deps) so the quark slab path runs, plus a mod with plain
    // slabs and no vertical ones so there is actually something to generate
    modRuntimeOnly("curse.maven:quark-243121:${quark_file_id}")
    modRuntimeOnly("curse.maven:zeta-968868:7980010") // v1.1-40
    modRuntimeOnly("curse.maven:biolith-852512:7074761") // v3.0.10
    modRuntimeOnly("curse.maven:buildersaddition-389697:8155184") // v2.1.2
    modRuntimeOnly("curse.maven:absent-by-design-305840:8030393") // v1.9.2, adds a pile of slabs
    modRuntimeOnly("curse.maven:flib-661261:8611586") // v0.2.9, absent by design dep
}
