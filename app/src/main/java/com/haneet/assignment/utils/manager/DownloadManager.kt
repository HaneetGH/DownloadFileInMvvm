package com.haneet.assignment.utils.manager

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import com.testapp.downloadmanager.data_model.FileToDownload
import com.haneet.assignment.utils.service.DownloadService
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.ArrayList

class DownloadManager() {
    // file path is folder path
    // file name must be given with extension

    @InternalCoroutinesApi
    companion object {
        var indexToDownload: Int = 0
        var listOfFilesToBeDownloaded: ArrayList<FileToDownload?>? = ArrayList()
        var isNotificationShowing = false
        // calling start service again doesnot create new instance if service is already running

        fun initDownload(
            context: Context,
            fileUrl: String,
            filePath: String,
            fileName: String,
            boundServiceConnection: ServiceConnection
        ) {
            val fileToDownload = FileToDownload()
            fileToDownload.url = fileUrl
            fileToDownload.filePath = filePath
            fileToDownload.fileName = fileName
            fileToDownload.isDownloaded = false
            listOfFilesToBeDownloaded?.add(fileToDownload)
            isNotificationShowing = listOfFilesToBeDownloaded?.size!! > 1
            val intent = Intent(context, DownloadService::class.java)
            context.bindService(intent, boundServiceConnection, Context.BIND_AUTO_CREATE)
            context.startService(intent)
        }
    }
}