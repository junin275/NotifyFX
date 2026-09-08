// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.25" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "1.5.13" apply false
    id("com.google.dagger.hilt.android") version "2.51" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}