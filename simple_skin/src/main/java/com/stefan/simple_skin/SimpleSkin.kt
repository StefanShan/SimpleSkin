package com.stefan.simple_skin

import android.app.Application
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatDelegate
import com.stefan.simple_skin.activityLifeCallback.SkinActivityLifeCallback
import java.util.*

@Keep
object SimpleSkin {

    internal val mInnerObservable: InnerObservable = InnerObservable()

    fun init(application: Application){
        application.registerActivityLifecycleCallbacks(SkinActivityLifeCallback())
    }

    fun changeMode(){
        mInnerObservable.notifyObservers()
    }
}

internal class InnerObservable: Observable() {
    override fun notifyObservers() {
        super.setChanged()
        super.notifyObservers()
    }
}