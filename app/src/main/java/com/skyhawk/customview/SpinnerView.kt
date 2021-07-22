package com.skyhawk.customview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat.getDrawable
import com.skyhawk.R
import kotlinx.android.synthetic.main.indeterminate_spinner.view.indeterminate_spinner_outer as outerImageView
import kotlinx.android.synthetic.main.indeterminate_spinner.view.indeterminate_spinner_inner as innerImageView
class SpinnerView  : FrameLayout {
    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        setUp()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        setUp()
    }

    private fun setUp() {
        LayoutInflater.from(context).inflate(R.layout.indeterminate_spinner, this, true)
        setBackgroundColor(0xE5445154.toInt())

        val innerDrawable = getDrawable(context, R.drawable.animated_spinner_inner) as AnimatedVectorDrawable
        innerImageView.setImageDrawable(innerDrawable)
        innerDrawable.start()

        val outerDrawable = getDrawable(context, R.drawable.animated_spinner_outer) as AnimatedVectorDrawable
        outerImageView.setImageDrawable(outerDrawable)
        outerDrawable.start()
    }
}