package com.haneet.assignment.data.repository


import android.content.Context
import android.util.Log
import com.haneet.assignment.constant.Task
import com.haneet.assignment.domain.DataState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject


class MainActivityRepository @Inject constructor(
    @ApplicationContext context: Context
) : BaseRepository() {
    private val appContext = context.applicationContext


    suspend fun downloadIr(p0: Array<String?>): Flow<DataState> {
        return flow {
            var count: Int = 0
            try {
                val url =
                    URL(p0[0])
                val connection: HttpURLConnection? = url.openConnection() as HttpURLConnection?
                connection?.requestMethod = "GET"
                connection?.readTimeout = 20000
                connection?.connectTimeout = 20000
                connection?.setRequestProperty("Accept-Encoding", "identity")
                connection?.useCaches = false
                connection?.connect()
                // getting file length

                if (connection != null && connection.responseCode == 200) {
                    val lengthOfFile: Int? = connection.contentLength

                    // input stream to read file - with 8k buffer
                    val input: BufferedInputStream? = BufferedInputStream(url.openStream());

                    // Output stream to write file
                    val output = FileOutputStream(p0[1] + "/" + p0[2])
                    val outputStream = BufferedOutputStream(output)

                    val data = ByteArray(1024)

                    var total: Long = 0

                    while (run {
                            count = input?.read(data)!!
                            count
                        } != -1) {
                        total += count.toLong()
                        // publishing the progress....
                        // After this onProgressUpdate will be called
//                print("" + (total * 100 / lengthOfFile!!).toInt())
                        Log.d(
                            "Main Activity",
                            "Progress: " + (total * 100 / lengthOfFile!!).toInt()
                        )

                        emit(
                            DataState.Progress(
                                (total * 100 / lengthOfFile!!).toInt(),
                                Task.DOWNLOAD
                            )
                        )
                        // writing data to file
                        outputStream.write(data, 0, count)
                    }

                    // flushing output
                    outputStream.flush()

                    // closing streams
                    outputStream.close()
                    input?.close()
                    connection.disconnect()
                    emit(DataState.Success(true, Task.DOWNLOAD))
                }

            } catch (e: Exception) {
//            Log.e("Error: ", e.message)
                print("Error: " + e.message)
                emit(DataState.Error(e, Task.DOWNLOAD))
            }
        }.flowOn(Dispatchers.IO) // Use the IO thread for this Flow
    }










}

