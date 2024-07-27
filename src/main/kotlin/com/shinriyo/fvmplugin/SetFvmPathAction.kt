package com.shinriyo.fvmplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import org.jdom.Element
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class SetFvmPathAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        // プロジェクトのルートディレクトリを取得
        val projectRoot: VirtualFile = project.baseDir ?: return

        // .fvm フォルダのパスを取得
        val fvmPath = "${projectRoot.path}/.fvm/flutter_sdk"
        val fvmDirectory = File(fvmPath)
        if (fvmDirectory.exists() && fvmDirectory.isDirectory) {
            // SDKパスを設定
            setFlutterSdkPath(project, fvmPath)
        } else {
            // エラーメッセージを表示
            notifyError(MyPluginBundle.message("error.message.directoryNotExist"), project)
        }
    }

    private fun setFlutterSdkPath(project: Project, fvmPath: String) {
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

        // プロジェクトにSDKを設定
        if (existingSdk != null) {
            SdkConfigurationUtil.setDirectoryProjectSdk(project, existingSdk)
        }
    }

    private fun notifyError(message: String, project: Project) {
        val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("FVM Path Setter")
        val notification = notificationGroup.createNotification(message, NotificationType.ERROR)
        notification.notify(project)
    }

    class CustomSdkType : SdkType("Flutter SDK") {
        override fun suggestHomePath(): String? = null

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
}
