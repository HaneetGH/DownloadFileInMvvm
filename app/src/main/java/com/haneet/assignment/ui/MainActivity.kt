package com.haneet.assignment.ui

import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.haneet.assignment.R
import com.haneet.assignment.base.BaseClass
import com.haneet.assignment.databinding.ActivityMainBinding
import com.haneet.assignment.utils.manager.DownloadManager
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@InternalCoroutinesApi
class MainActivity : BaseClass() {
    @InternalCoroutinesApi
    private val viewModel by viewModels<MainActivityViewModel>()
    private var page = 1;
    private lateinit var binding: ActivityMainBinding


    var inflater: LayoutInflater? = null
    private var mProgressDialog: ProgressDialog? = null
    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.btn.setOnClickListener(View.OnClickListener {
            viewModel.setStateEvent(
                MainStateEvent.DownloadIt(
                    "https://www.eurofound.europa.eu/sites/default/files/ef_publication/field_ef_document/ef1710en.pdf",
                    this
                )
            )
            // viewModel.listBound(this)
        })

        binding.btnOpen.setOnClickListener(View.OnClickListener { viewModel.openFolder(this,viewModel.fileName) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onStop() {
        super.onStop()
        viewModel?.stopBound(this)
    }

    override fun attachViewModel() {
        viewModel._uiStateFeatured.observe(this, Observer { parse(it) })


    }

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private val NOTIFICATION_CHANNEL = "downloads"
    private var NOTIFICATION_ID = -1
    private fun parse(it: Int?) {
        if (it != null) {


            binding.txt.text = it.toString()
            binding.progressCircular.progress = it
            showNotifications(it)


            if (it as Int == 100) {

                binding.txt.text = "Complete"
                binding.btnOpen.visibility = View.VISIBLE

            }

        }

    }

    private fun showNotifications(it: Int?) {
        NOTIFICATION_ID = DownloadManager.indexToDownload
        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_back)
            .setContentText("please Complete...")
            .setContentTitle("Downloaded")
            .setAutoCancel(false)
            .setOngoing(true)
            .setChannelId(NOTIFICATION_CHANNEL)
        notificationBuilder?.setOngoing(true)
        if (it != null) {
            notificationBuilder?.setProgress(100, it, false)
        }

        notificationBuilder?.setContentText("$it%")
        notificationManager?.notify(
            NOTIFICATION_ID,
            notificationBuilder?.build()
        )
    }
    val fileName="";
    @InternalCoroutinesApi
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            viewModel.REQUEST_CODE_WRITE_STORAGE_PERMISION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val folder = File(
                        Environment.getExternalStorageDirectory()
                            .toString() + "/" + "Downloads/mypdfs"
                    )
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }
                    var url =
                        "https://www.eurofound.europa.eu/sites/default/files/ef_publication/field_ef_document/ef1710en.pdf";
                    val extension: String = url.substring(url.lastIndexOf("."))
                    val fileName =
                        SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date()) + extension
                    DownloadManager.initDownload(
                        this,
                        url,
                        folder.absolutePath,
                        fileName,
                        viewModel.boundServiceConnection
                    )
                }
            }

        }
    }

}
