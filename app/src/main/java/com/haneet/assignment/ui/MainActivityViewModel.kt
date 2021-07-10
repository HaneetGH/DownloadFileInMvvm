package com.haneet.assignment.ui

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.os.IBinder
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haneet.assignment.base.BaseViewModel
import com.haneet.assignment.data.repository.MainActivityRepository
import com.haneet.assignment.utils.manager.DownloadManager
import com.haneet.assignment.utils.service.DownloadService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@InternalCoroutinesApi
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: MainActivityRepository
) : ViewModel() {

    var boundService: DownloadService? = null
    var isBound = false
    var fileName=""
    val REQUEST_CODE_WRITE_STORAGE_PERMISION = 105
    var
            _uiStateFeatured: MutableLiveData<Int> =
        MutableLiveData()


    private val _downloading: MutableLiveData<Boolean> = MutableLiveData()
    val downloading: LiveData<Boolean> = _downloading
    fun setStateEvent(mainStateEvent: MainStateEvent) {
        viewModelScope.launch {
            when (mainStateEvent) {


                is MainStateEvent.DownloadIt -> {
                    checkStoragePermissions(mainStateEvent.context, mainStateEvent.url)
                }

            }

        }
    }


    fun listBound() {
        boundService?.getNumberObserver()
            ?.observeForever { _uiStateFeatured.value = it }
    }

    fun stopBound(activity: Activity) {
        if (isBound) {
            activity.unbindService(boundServiceConnection);
            isBound = false;


        }
    }

    private fun checkStoragePermissions(activity: Activity, url: String) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CODE_WRITE_STORAGE_PERMISION
                    )
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CODE_WRITE_STORAGE_PERMISION
                    )
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                if (ContextCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_CODE_WRITE_STORAGE_PERMISION
                        )
                    }
                }
            } else {
                val folder = File(
                    Environment.getExternalStorageDirectory().toString() + "/" + "Downloads/mypdfs"
                )
                if (!folder.exists()) {
                    folder.mkdirs()
                }
                val extension: String = url.substring(url.lastIndexOf("."))
                fileName = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date()) + extension
                DownloadManager.initDownload(
                    activity,
                    url,
                    folder.absolutePath,
                    fileName,
                    boundServiceConnection
                )

            }
        }
    }

    fun openFolder(activity: Activity,file:String) {
        // location = "/sdcard/my_folder";
        // location = "/sdcard/my_folder";

        val fileFol =
            File(Environment.getExternalStorageDirectory()
                .toString() + "/" + "Downloads/mypdfs/"+file)



        val uri = FileProvider.getUriForFile(
            activity,
            "com.backbase.assignment.provider",
            fileFol
        );
        val mime = MimeTypeMap.getSingleton()
        val extension: String =
            file.substring(
                file.lastIndexOf(".")
            )
        val type = mime.getMimeTypeFromExtension(
            extension.removePrefix(".")
        )
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setDataAndType(uri, type)

       // val intent = Intent(Intent.ACTION_GET_CONTENT)


        activity.startActivity(intent)
    }

    val boundServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binderBridge: DownloadService.MyBinder = service as DownloadService.MyBinder
            boundService = binderBridge.service
            isBound = true
            listBound()

        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
            boundService = null
        }
    }
}

sealed class MainStateEvent {

    data class DownloadIt(var url: String, var context: Activity) : MainStateEvent()
    object None : MainStateEvent()
}

