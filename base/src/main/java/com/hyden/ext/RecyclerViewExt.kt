package com.hyden.ext

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hyden.base.BaseRecyclerView
import java.util.Collections.replaceAll

@BindingAdapter(value = ["bindItems"])
fun RecyclerView.bindItems(items : List<Any>?) {
    items?.let {
        (adapter as? BaseRecyclerView.Adapter<Any,*>)?.run {
//            replaceAll(items)
            updateItems(items)
        }
    }
}