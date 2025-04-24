package com.ip_tv.ipsat.presentation.dialogs

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.databinding.UpdateBottomSheetBinding
import com.ip_tv.ipsat.domain.model.AppUpdate
import com.ip_tv.ipsat.utils.SpoilerPlugin
import com.ip_tv.ipsat.utils.gone
import com.ip_tv.ipsat.utils.snackString
import com.ip_tv.ipsat.utils.visible
import io.noties.markwon.Markwon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class UpdateActivity : AppCompatActivity() {
    companion object {
        var _update: AppUpdate? = null
        fun newIntent(context: Context, update: AppUpdate): Intent {
            _update = update
            return Intent(context, UpdateActivity::class.java)
        }
    }

    private val update get() = _update!!

    private lateinit var binding: UpdateBottomSheetBinding
    private val vm: UpdateViewModel by viewModels()
    private val askNotif = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) vm.startDownload(this, update.appLink ?: "")
        else snackString("Notification permission denied")
    }

    private val askUnknown =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && packageManager.canRequestPackageInstalls()) vm.installApk(
                this
            )
            else snackString("Cannot install from unknown sources")
        }

    private val askInstall =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) finish()
            else snackString("Installation cancelled")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UpdateBottomSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        renderMarkdown(update.changeLog)
        observeVm()

        binding.updateBtn.setOnClickListener {
            val link = update.appLink ?: return@setOnClickListener
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                askNotif.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else vm.startDownload(this, link)
        }
    }

    override fun onStart() {
        super.onStart()
        vm.registerReceiver(this)
    }

    override fun onStop() {
        vm.unregisterReceiver(this)
        super.onStop()
    }

    private fun renderMarkdown(md: String?) {
        val mw = Markwon.builder(this)
            .usePlugin(io.noties.markwon.html.HtmlPlugin.create { it.excludeDefaults(true) })
            .usePlugin(SpoilerPlugin()).build()
        mw.setMarkdown(binding.markdownText, md ?: "")
    }

    private fun observeVm() {
        vm.uiState.observe(this) { st ->
            when (st) {
                is UpdateViewModel.UiState.Idle -> {
                    binding.progressView1.gone()
                    binding.bottomSheerCustomTitle.text = "Update Available"
                    binding.updateBtn.apply { text = "Update Now"; visible() }
                }

                is UpdateViewModel.UiState.Downloading -> {
                    binding.progressView1.visible()
                    binding.bottomSheerCustomTitle.text = "Downloading…"
                    binding.updateBtn.gone()
                    binding.progressView1.apply {
                        progress = st.progress.toFloat()
                        labelText = "${st.progress}%"
                    }
                }

                is UpdateViewModel.UiState.DownloadComplete -> {
                    binding.progressView1.gone()
                    binding.bottomSheerCustomTitle.text = "Download Complete!"
                    binding.updateBtn.apply {
                        text = "Install Now"
                        visible()
                        setOnClickListener { vm.installApk(this@UpdateActivity) }
                    }
                }

                is UpdateViewModel.UiState.DownloadFailed -> {
                    binding.progressView1.gone()
                    binding.bottomSheerCustomTitle.text = "Download Failed"
                    binding.updateBtn.apply {
                        text = "Try Again"
                        visible()
                        setOnClickListener {
                            vm.startDownload(this@UpdateActivity, update.appLink ?: "")
                        }
                    }
                    snackString(st.error)
                }
            }
        }

        vm.installEvent.observe(this) { ev ->
            when (ev) {
                is UpdateViewModel.InstallEvent.RequestUnknownSources -> {
                    val i = Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                        .setData(Uri.parse("package:$packageName"))
                    askUnknown.launch(i)
                }

                is UpdateViewModel.InstallEvent.StartInstall -> askInstall.launch(ev.intent)
                is UpdateViewModel.InstallEvent.Error -> snackString(ev.message)
            }
        }
    }
}

class UpdateViewModel : ViewModel() {

    sealed class UiState {
        data object Idle : UiState()
        data class Downloading(val progress: Int) : UiState()
        data object DownloadComplete : UiState()
        data class DownloadFailed(val error: String) : UiState()
    }

    sealed class InstallEvent {
        object RequestUnknownSources : InstallEvent()
        data class StartInstall(val intent: Intent) : InstallEvent()
        data class Error(val message: String) : InstallEvent()
    }

    private var downloadId: Long = -1L
    private var downloadManager: DownloadManager? = null
    private var progressJob: Job? = null
    private val _uiState = MutableLiveData<UiState>(UiState.Idle)
    val uiState: LiveData<UiState> = _uiState
    private val _installEvent = MutableLiveData<InstallEvent>()
    val installEvent: LiveData<InstallEvent> = _installEvent

    private val onDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id == downloadId) checkDownloadStatus()
        }
    }

    private val uninstallReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, i: Intent) {
            if (i.action == Intent.ACTION_PACKAGE_REMOVED &&
                i.data?.schemeSpecificPart == ctx.packageName
            ) {
                // Old copy removed → start fresh install
                installApk(ctx)
            }
        }
    }

    fun startDownload(context: Context, apkUrl: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(intent)
            return
        }
        _uiState.value = UiState.Downloading(0)
        downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloads =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val destinationDir = File(downloads, "IPSAT")
        destinationDir.listFiles { _, name -> name.startsWith("app_update") && name.endsWith(".apk") }
            ?.forEach { file ->
                file.delete()
            }

        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }

        val destination = "IPSAT/app_update.apk"
        val request = DownloadManager.Request(Uri.parse(apkUrl)).apply {
            setTitle("IPSAT Update")
            setDescription("Downloading new version…")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, destination)
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setAllowedOverRoaming(false)
        }

        downloadId = downloadManager?.enqueue(request) ?: -1L
        trackProgress()
    }

    @SuppressLint("InlinedApi")       // RECV_… konstantalar inline
    fun registerReceiver(ctx: Context) {

        // Download tugaganini eshitish
        val downloadFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE /* 34 */) {
            ctx.registerReceiver(
                onDownloadComplete,
                downloadFilter,
                Context.RECEIVER_EXPORTED          // ← tashqaridan keladigan system broadcast
            )
        } else {
            @Suppress("DEPRECATION")
            ctx.registerReceiver(onDownloadComplete, downloadFilter)
        }

        // (Agar uninstallReceiver ham bo‘lsa)
        val uninstallFilter = IntentFilter(Intent.ACTION_PACKAGE_REMOVED).apply {
            addDataScheme("package")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ctx.registerReceiver(
                uninstallReceiver,
                uninstallFilter,
                Context.RECEIVER_EXPORTED
            )
        } else {
            @Suppress("DEPRECATION")
            ctx.registerReceiver(uninstallReceiver, uninstallFilter)
        }
    }

    fun unregisterReceiver(ctx: Context) {
        runCatching { ctx.unregisterReceiver(onDownloadComplete) }
        runCatching { ctx.unregisterReceiver(uninstallReceiver) }
    }


    private fun trackProgress() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch(Dispatchers.Main) {
            while (isActive) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                downloadManager?.query(query)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        when (cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))) {
                            DownloadManager.STATUS_RUNNING -> {
                                val done =
                                    cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                val total =
                                    cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                                if (total > 0) {
                                    val progress = ((done * 100L) / total).toInt()
                                    _uiState.postValue(UiState.Downloading(progress))
                                }
                            }

                            DownloadManager.STATUS_PENDING -> {
                                _uiState.postValue(UiState.Downloading(0))
                            }

                            else -> return@use
                        }
                    }
                }
                delay(500)
            }
        }
    }

    private fun checkDownloadStatus() {
        progressJob?.cancel()
        val query = DownloadManager.Query().setFilterById(downloadId)
        downloadManager?.query(query)?.use { cursor ->
            if (cursor.moveToFirst()) {
                when (cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        _uiState.postValue(UiState.DownloadComplete)
                    }

                    DownloadManager.STATUS_FAILED -> {
                        val reason =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                        _uiState.postValue(UiState.DownloadFailed("Download failed. Reason code: $reason"))
                    }
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun installApk(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !context.packageManager.canRequestPackageInstalls()) {
                _installEvent.postValue(InstallEvent.RequestUnknownSources)
                return
            }

            val downloads =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destinationDir = File(downloads, "IPSAT")

            // Eng so'nggi app_update*.apk faylini topish
            val apkFile = destinationDir.listFiles { _, name ->
                name.startsWith("app_update") && name.endsWith(".apk")
            }?.maxByOrNull { it.lastModified() } // Eng so'nggi o'zgartirilgan faylni tanlash

            Log.d(
                "UpdateViewModel",
                "APK Path: ${apkFile?.absolutePath}, Exists: ${apkFile?.exists()}"
            )

            if (apkFile == null || !apkFile.exists()) {
                _installEvent.postValue(InstallEvent.Error("Downloaded APK not found in ${destinationDir.absolutePath}."))
                return
            }

            val authority = "${context.packageName}.provider" // <- SAME STRING AS MANIFEST
            val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, authority, apkFile)
            } else {
                Uri.fromFile(apkFile)
            }

            Log.d("UpdateViewModel", "APK URI: $uri")

            val intent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
                data = uri
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Intent.EXTRA_RETURN_RESULT, true)
            }

            Log.d(
                "UpdateViewModel",
                "Intent Resolution: ${intent.resolveActivity(context.packageManager)}"
            )
            if (intent.resolveActivity(context.packageManager) != null) {
                _installEvent.postValue(InstallEvent.StartInstall(intent))
            } else {
                _installEvent.postValue(InstallEvent.Error("No installer found on device."))
            }
        } catch (e: Exception) {
            Log.e("UpdateViewModel", "Install APK Error: ${e.message}", e)
            _installEvent.postValue(InstallEvent.Error("Installation failed: ${e.message}"))
        }
    }

    private fun deleteRecursively(file: File): Boolean {
        if (file.isDirectory) file.listFiles()?.forEach { deleteRecursively(it) }
        return file.delete()
    }

    override fun onCleared() {
        progressJob?.cancel()
        super.onCleared()
    }
}