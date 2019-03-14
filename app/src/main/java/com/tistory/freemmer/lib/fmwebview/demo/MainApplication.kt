package com.tistory.freemmer.lib.fmwebview.demo

import android.app.Application
import com.tistory.freemmer.lib.fmwebview.demo.webinterface.CommonWebInterface
import com.tistory.freemmer.lib.fmwebview.webinterface.FMWebInterfaceManager
import com.tistory.freemmer.lib.libfm.LibFM
import com.tistory.freemmer.lib.libfm.logger.FMILog
import com.tistory.freemmer.lib.libfm.logger.impl.FMLogCatImpl
import com.tistory.freemmer.lib.libfm.platform.FMBeanManager

/**
 * Created by freemmer on 12/03/2019.
 * History
 *    - 12/03/2019 Create file
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FMWebInterfaceManager.addClass(CommonWebInterface::class.java)

        if (BuildConfig.DEBUG) {
            LibFM.initialize().enableDebugLog()
            FMBeanManager.registerClass(FMILog::class.java, FMLogCatImpl.build(FMILog.LEVEL.DEBUG
                , this.resources.getString(R.string.app_name), null))
            val log: FMILog? = FMBeanManager.getClass(FMILog::class.java)
            log?.printDeviceInfo(this)
            log?.printMemory()
        }
    }
}