package com.shinriyo.fvmplugin

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.ui.Messages
import org.jdom.Element
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import java.awt.BorderLayout
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class FvmPathConfigurable(private val project: Project) : Configurable {

    private var mainPanel: JPanel? = null

    override fun createComponent(): JComponent? {
        // Flutterの設定パネルを取得
        val flutterConfigurableClass = Class.forName("io.flutter.FlutterSettingsConfigurable")
        val flutterConfigurable = flutterConfigurableClass.getConstructor(Project::class.java).newInstance(project) as Configurable
        val flutterComponent = flutterConfigurable.createComponent()

        // "Set FVM Path"ボタンを追加
        val setFvmPathButton = JButton("Set FVM Path").apply {
            addActionListener { setFvmPath() }
        }

        // メインパネルに追加
        mainPanel = JPanel(BorderLayout())
        mainPanel?.add(flutterComponent, BorderLayout.CENTER)
        mainPanel?.add(setFvmPathButton, BorderLayout.SOUTH)

        return mainPanel!!
    }

    private fun setFvmPath() {
        val fvmPath = "${project.basePath}/.fvm/flutter_sdk"
        val sdkName = "Flutter SDK"

        var existingSdk = ProjectJdkTable.getInstance().findJdk(sdkName)
        if (existingSdk == null) {
            val flutterSdk = SdkConfigurationUtil.createAndAddSDK(fvmPath, object : SdkType("Flutter SDK") {
                override fun suggestHomePath(): String? = null
                override fun isValidSdkHome(path: String): Boolean = File(path).exists() && File(path, "bin/flutter").exists()
                override fun suggestSdkName(currentSdkName: String?, sdkHome: String): String = "Flutter SDK"

                override fun createAdditionalDataConfigurable(
                    sdkModel: SdkModel,
                    sdkModificator: SdkModificator
                ): AdditionalDataConfigurable? = null

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

                override fun saveAdditionalData(additionalData: SdkAdditionalData, additional: Element) {
                    // ここで追加データを保存
                    if (additionalData is CustomSdkAdditionalData) {
                        additional.setAttribute("version", additionalData.version)
                    }
                }

                override fun loadAdditionalData(additional: Element): SdkAdditionalData? {
                    // ここで追加データをロード
                    val version = additional.getAttributeValue("version")
                    return if (version != null) CustomSdkAdditionalData(version) else null
                }

                override fun getPresentableName(): String = "Flutter SDK"
            })
            if (flutterSdk != null) {
                ProjectJdkTable.getInstance().addJdk(flutterSdk)
                existingSdk = flutterSdk
            }
        } else {
            (existingSdk as ProjectJdkImpl).homePath = fvmPath
        }

        if (existingSdk != null) {
            SdkConfigurationUtil.setDirectoryProjectSdk(project, existingSdk)
            Messages.showInfoMessage(project, "Flutter SDK path set to .fvm/flutter_sdk", "Success")
        } else {
            Messages.showErrorDialog(project, "Failed to set Flutter SDK path", "Error")
        }
    }

    override fun isModified(): Boolean = false

    override fun apply() {}

    override fun getDisplayName(): String = "Flutter SDK"
}

class CustomSdkAdditionalData(val version: String) : SdkAdditionalData
