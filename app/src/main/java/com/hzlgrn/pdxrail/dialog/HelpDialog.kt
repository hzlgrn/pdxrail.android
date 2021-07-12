package com.hzlgrn.pdxrail.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.hzlgrn.pdxrail.activity.R

class HelpDialog : DialogFragment() {

    private lateinit var mDialogView: View

    var onDismissListener: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mDialogView = activity?.layoutInflater?.inflate(R.layout.dialog_help, container, false)!!
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)
        return mDialogView
    }

    override fun onStart() {
        super.onStart()
        val params = dialog?.window?.attributes?.apply {
            width = android.view.WindowManager.LayoutParams.MATCH_PARENT
            height = android.view.WindowManager.LayoutParams.WRAP_CONTENT
        }
        dialog?.window?.attributes = params as android.view.WindowManager.LayoutParams
        dialog?.findViewById<ImageView>(R.id.image_arrival)?.startAnimation(
            RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.4f)
                .apply {
                    interpolator = LinearInterpolator()
                    repeatCount = Animation.INFINITE
                    duration = 1400
                })
        dialog?.findViewById<View>(R.id.dialog_help_action_sweet)?.setOnClickListener {
            dialog?.dismiss()
        }
        dialog?.setOnDismissListener {
            dialog?.dismiss()
            onDismissListener?.invoke()
        }
    }

    override fun onPause() {
        super.onPause()
        dialog?.findViewById<ImageView>(R.id.image_arrival)?.animation?.cancel()
    }

}