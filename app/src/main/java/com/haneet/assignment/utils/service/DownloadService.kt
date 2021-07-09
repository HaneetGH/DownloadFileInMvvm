package com.haneet.assignment.utils.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import com.haneet.assignment.R
import com.haneet.assignment.data.repository.MainActivityRepository
import com.haneet.assignment.domain.DataState
import com.haneet.assignment.utils.manager.DownloadManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.io.File


@InternalCoroutinesApi
class DownloadService : Service() {
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private val NOTIFICATION_CHANNEL = "downloads"
    private var NOTIFICATION_ID = -1


    val
            _uiStateFeatured: MutableLiveData<Int> =
        MutableLiveData()


    lateinit var myRepository: MainActivityRepository

    private val localBinder: IBinder = MyBinder()
    override fun onBind(p0: Intent?): IBinder? {
        return localBinder
    }

    override fun onLowMemory() {
        super.onLowMemory()
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val scope = CoroutineScope(Dispatchers.Main)
        myRepository = MainActivityRepository(this)
        scope.launch { initDownload() }

        createPendingNotification()
        return START_STICKY
    }

    private suspend fun initDownload() {
        val list = DownloadManager.listOfFilesToBeDownloaded
        if (list != null && list.size > 0) {
            if (DownloadManager.indexToDownload < list.size) {
                val fileToDownload = list.get(DownloadManager.indexToDownload)
                if (fileToDownload != null && !fileToDownload.isDownloaded) {
                    fileToDownload.isDownloaded = true
                    NOTIFICATION_ID = DownloadManager.indexToDownload
                    notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_back)
                        .setContentText("please wait...")
                        .setContentTitle("Downloading")
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setChannelId(NOTIFICATION_CHANNEL)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            NOTIFICATION_CHANNEL,
                            NOTIFICATION_CHANNEL,
                            NotificationManager.IMPORTANCE_LOW
                        )
                        channel.description = "no sound"
                        channel.setSound(null, null)
                        channel.enableLights(false);
                        channel.setLightColor(Color.BLUE);
                        channel.enableVibration(false);
                        notificationManager?.createNotificationChannel(channel)
                    }
                    notificationManager?.notify(NOTIFICATION_ID, notificationBuilder?.build())

                    myRepository.downloadIr(
                        arrayOf(
                            fileToDownload.url,
                            fileToDownload.filePath,
                            fileToDownload.fileName
                        )
                    ).collect {

                        if (it != null) {
                            when (it) {
                                is DataState.Success<*> -> {
                                    if (it?.data != null) {
                                        fileToDownload.isDownloaded = false
                                        notificationManager?.cancel(999)
                                        if (it.data as Boolean) {


                                            val file =
                                                File(fileToDownload.filePath + "/" + fileToDownload.fileName)
                                            //  val uri = Uri.fromFile(file)
                                            val uri = FileProvider.getUriForFile(
                                                this,
                                                "com.backbase.assignment.provider",
                                                file
                                            );
                                            val mime = MimeTypeMap.getSingleton()
                                            val extension: String =
                                                fileToDownload.fileName.substring(
                                                    fileToDownload.fileName.lastIndexOf(".")
                                                )
                                            val type = mime.getMimeTypeFromExtension(
                                                extension.removePrefix(".")
                                            )
                                            val intent = Intent(Intent.ACTION_VIEW)
                                            intent.setDataAndType(uri, type)

                                            // startActivity(intent);
                                            // startActivity(intent);
                                            val pendingIntent = PendingIntent.getActivity(
                                                this,
                                                0,
                                                intent,
                                                PendingIntent.FLAG_CANCEL_CURRENT
                                            )

                                            notificationBuilder?.setOngoing(false)
                                            notificationBuilder?.setContentTitle("Download Complete")
                                            notificationBuilder?.setContentText("ready...")
                                            notificationBuilder?.setContentIntent(pendingIntent)
                                            notificationBuilder?.setContentTitle(fileToDownload.fileName)
                                            notificationManager?.notify(
                                                NOTIFICATION_ID,
                                                notificationBuilder?.build()
                                            )
                                            notificationManager = null
                                            notificationBuilder = null
                                            val newIndexToDownload =
                                                DownloadManager.indexToDownload + 1
                                            if (newIndexToDownload < DownloadManager.listOfFilesToBeDownloaded?.size!!) {
                                                DownloadManager.indexToDownload = newIndexToDownload
                                                initDownload()
                                            } else {
                                                DownloadManager.indexToDownload = 0
                                                DownloadManager.listOfFilesToBeDownloaded =
                                                    ArrayList()
                                                stopSelf()
                                            }

                                        } else {
                                            notificationManager?.cancel(999)
                                            notificationBuilder?.setOngoing(false)
                                            notificationBuilder?.setContentText("")
                                            notificationBuilder?.setContentTitle("Download Error")
                                            notificationManager?.notify(
                                                NOTIFICATION_ID,
                                                notificationBuilder?.build()
                                            )
                                            notificationManager = null
                                            notificationBuilder = null
                                            DownloadManager.indexToDownload = 0
                                            DownloadManager.listOfFilesToBeDownloaded = ArrayList()
                                            stopSelf()
                                        }

                                    }

                                }
                                is DataState.Error -> {
                                    fileToDownload.isDownloaded = false
                                    Log.d("Api Response", "ERROR ${it.exception.toString()}");
                                }
                                is DataState.Loading -> {
                                    Log.d("Api Response", "LOADING");
                                }
                                is DataState.Progress<*> -> {



                                    _uiStateFeatured.postValue(it.data as Int)

                                }
                                else -> {
                                }
                            }
                        }

                    }


                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationBuilder?.setOngoing(false)
        notificationBuilder?.setContentText("")
        notificationBuilder?.setContentTitle("Download Error")
        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder?.build())
        notificationManager = null
        notificationBuilder = null
    }

    // when any error occurs stop the service by calling stop self
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        notificationManager?.cancel(999)
        notificationBuilder?.setOngoing(false)
        notificationBuilder?.setContentText("")
        notificationBuilder?.setContentTitle("Download Error")
        notificationManager?.notify(NOTIFICATION_ID, notificationBuilder?.build())
        notificationManager = null
        notificationBuilder = null
        DownloadManager.indexToDownload = 0
        DownloadManager.listOfFilesToBeDownloaded = ArrayList()
        stopSelf()
    }


    fun createPendingNotification() {
        if (DownloadManager.isNotificationShowing) {
            notificationBuilder?.setOngoing(false)
            notificationBuilder?.setContentText("Added in queue for download")
            notificationBuilder?.setContentTitle("Pending")
            notificationBuilder?.setProgress(0, 0, false)
            notificationManager?.notify(999, notificationBuilder?.build())
        }
    }

    fun getNumberObserver(): MutableLiveData<Int> {
        return _uiStateFeatured
    }

    inner class MyBinder : Binder() {
        val service: DownloadService
            get() = this@DownloadService
    }
}

