package com.shinriyo.fvmplugin

import com.intellij.openapi.projectRoots.*
import org.jdom.Element
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class CustomSdkType : SdkType("Flutter SDK") {

    override fun suggestHomePath(): String? {
        return null
    }

    override fun isValidSdkHome(path: String): Boolean {
        return path.isNotEmpty() && File(path).isDirectory
    }

    override fun suggestSdkName(currentSdkName: String?, sdkHome: String): String {
        return "Flutter SDK"
    }

    override fun createAdditionalDataConfigurable(
        sdkModel: SdkModel,
        sdkModificator: SdkModificator
    ): AdditionalDataConfigurable? {
        TODO("Not yet implemented")
    }

    override fun getVersionString(sdk: Sdk): String? {
        val sdkHome = sdk.homePath ?: return null
        val flutterExecutable = File(sdkHome, "bin/flutter")
        if (!flutterExecutable.exists()) return null

        return try {
            val process = ProcessBuilder(flutterExecutable.absolutePath, "--version")
                .redirectErrorStream(true)
                .start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val versionLine = reader.readLine()
            process.waitFor()
            versionLine?.substringAfter("Flutter ")?.substringBefore(" ")
        } catch (e: Exception) {
            null
        }
    }

    override fun getPresentableName(): String {
        return "Flutter SDK"
    }

    override fun saveAdditionalData(additionalData: SdkAdditionalData, additional: Element) {
        // 必要に応じて追加データを保存するロジックをここに追加
    }

    override fun loadAdditionalData(additional: Element): SdkAdditionalData? {
        // 必要に応じて追加データをロードするロジックをここに追加
        return null
    }
}
