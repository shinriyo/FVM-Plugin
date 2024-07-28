package com.shinriyo.fvmplugin

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.ui.Messages
import io.flutter.sdk.FlutterSdkUtil
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class FvmPathConfigurable(private val project: Project) : Configurable {

    private var mainPanel: JPanel? = null

    override fun createComponent(): JComponent? {
        mainPanel = JPanel(BorderLayout())

        // Flutterの設定パネルを取得
        val flutterConfigurable = FlutterSdkUtil.getConfigurable(project)
        val flutterComponent = flutterConfigurable?.createComponent()

        val setFvmPathButton = JButton("Set FVM Path")
        setFvmPathButton.addActionListener {
            setFvmPath()
        }

        mainPanel?.add(flutterComponent, BorderLayout.CENTER)
        mainPanel?.add(setFvmPathButton, BorderLayout.SOUTH)

        return mainPanel!!
    }

    private fun setFvmPath() {
        val fvmPath = "${project.basePath}/.fvm/flutter_sdk"
        val sdkName = "Flutter SDK"

        var existingSdk = ProjectJdkTable.getInstance().findJdk(sdkName)
        if (existingSdk == null) {
            val flutterSdk = SdkConfigurationUtil.createAndAddSDK(fvmPath, CustomSdkType())
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
