plugins {
    id("com.possible-triangle.neoforge")
}

neoforge {
    dependOn(project(":common"))
}

val moonlight_version: String by extra

dependencies {
    modImplementation("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}") { isTransitive = false }
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")

    //modCompileOnly("curse.maven:quark-243121:8146177")
    //modCompileOnly("org.violetmoon.zeta:Zeta:1.1-40-SNAPSHOT")
    modCompileOnly("maven.modrinth:enchanted-vertical-slabs:EUlKZFNN")
    //modCompileOnly("curse.maven:buildersaddition-389697:8155184")

    //TMP modRuntimeOnly("curse.maven:quark-243121:8146177")
    //TMP modRuntimeOnly("curse.maven:zeta-968868:7980010") // v1.1-40
    modRuntimeOnly("maven.modrinth:biolith:whieXuL5") // v3.6.0
    //modRuntimeOnly("curse.maven:buildersaddition-389697:8155184") // v2.1.2
    modRuntimeOnly("maven.modrinth:absent-by-design:Ydkgd65V") // v1.9.2, adds a pile of slabs
    modRuntimeOnly("maven.modrinth:flib:uMEW9ig6") // v0.2.11, absent by design dep
    modRuntimeOnly("maven.modrinth:enchanted-vertical-slabs:EUlKZFNN") // v2.6.6, vanilla vertical slabs
    //modRuntimeOnly("curse.maven:architects-palette-433862:6861008") // v1.4.0-beta.5, own vertical slabs
}
