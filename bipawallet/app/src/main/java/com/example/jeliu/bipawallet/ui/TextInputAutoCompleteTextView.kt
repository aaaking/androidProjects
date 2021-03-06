package com.example.jeliu.bipawallet.ui

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

/**
 * Created by 周智慧 on 2018/9/18.
 */
class TextInputAutoCompleteTextView : AppCompatAutoCompleteTextView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val ic = super.onCreateInputConnection(outAttrs) ?: return null

//        if (outAttrs.hintText == null) {
//            // If we don't have a hint and our parent is a TextInputLayout, use it's hint for the
//            // EditorInfo. This allows us to display a hint in 'extract mode'.
//            val parent = parent
//            if (parent is TextInputLayout) {
//                outAttrs.hintText = parent.hint
//            }
//        }
        return ic
    }
}