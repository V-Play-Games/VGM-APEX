/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vpg.apex

import android.app.Notification
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.exoplayer.scheduler.Requirements.RequirementFlags
import net.vpg.apex.core.di.rememberDownloadManager

@OptIn(UnstableApi::class)
class ApexDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_NOTIFICATION_CHANNEL_ID,
    R.string.notification_channel_name,
    0
) {
    private val downloadNotificationHelper by lazy {
        DownloadNotificationHelper(this, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
    }

    override fun getDownloadManager() = rememberDownloadManager(this).also {
        it.addListener(
            TerminalStateNotificationHelper(
                this, downloadNotificationHelper, FOREGROUND_NOTIFICATION_ID + 1
            )
        )
    }

    override fun getScheduler() = PlatformScheduler(this, JOB_ID)

    override fun getForegroundNotification(downloads: List<Download>, notMetRequirements: @RequirementFlags Int) =
        downloadNotificationHelper.buildProgressNotification(
            this,
            R.drawable.ic_pika_chill,
            null,
            null,
            downloads,
            notMetRequirements
        )

    private class TerminalStateNotificationHelper(
        context: Context,
        private val notificationHelper: DownloadNotificationHelper,
        private var nextNotificationId: Int
    ) : DownloadManager.Listener {
        private val context = context.applicationContext

        override fun onDownloadChanged(
            downloadManager: DownloadManager, download: Download, finalException: Exception?
        ) {
            val notification: Notification?
            when (download.state) {
                Download.STATE_COMPLETED -> {
                    notification =
                        notificationHelper.buildDownloadCompletedNotification(
                            context,
                            R.drawable.ic_pika_chill,  /* contentIntent= */
                            null,
                            Util.fromUtf8Bytes(download.request.data)
                        )
                }

                Download.STATE_FAILED -> {
                    notification =
                        notificationHelper.buildDownloadFailedNotification(
                            context,
                            R.drawable.ic_pika_chill,  /* contentIntent= */
                            null,
                            Util.fromUtf8Bytes(download.request.data)
                        )
                }

                else -> {
                    return
                }
            }
            NotificationUtil.setNotification(context, nextNotificationId++, notification)
        }
    }

    companion object {
        private const val JOB_ID = 1
        private const val FOREGROUND_NOTIFICATION_ID = 1
        private const val DOWNLOAD_NOTIFICATION_CHANNEL_ID: String = "download_channel"
    }
}