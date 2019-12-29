package com.hyden.base

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hyden.util.CommonUtil.Companion.CLICK_THROTTLE
import com.hyden.util.ItemClickListener
import com.hyden.util.ItemLongClickListener
import com.hyden.util.RecyclerDiffUtil

class BaseRecyclerView {
    abstract class SimpleAdapter<B : ViewDataBinding, T>(
        private val layoutId: Int,
        private val listItem: List<T>,
        private val bindingVariableId: Int?
    ) : RecyclerView.Adapter<ViewHolder<B>>() {

        private var list = listItem

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<B> =
            object : ViewHolder<B>(layoutId, parent, bindingVariableId) { }

        override fun getItemCount(): Int = list.size
        override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) =
            holder.onBind(list[position])

    }

    abstract class Adapter<ITEM : Any, B : ViewDataBinding>(
        private val layoutId: Int,
        private val bindingVariableId: Int?,
        private val clickItemEvent: ItemClickListener? = null,
        private val longClickItemEvent: ItemLongClickListener? = null
    ) : RecyclerView.Adapter<ViewHolder<B>>() {

        private var list = listOf<ITEM>()
        private var CLICK_LAST_TIME = 0L

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<B> {
            val holder = object : ViewHolder<B>(
                layoutId,
                parent,
                bindingVariableId
            ) {}
            holder.itemView.apply {
                setOnClickListener {
                    if (!(SystemClock.elapsedRealtime() - CLICK_LAST_TIME < CLICK_THROTTLE)) {
                        clickItemEvent?.onItemClick(list[holder.adapterPosition])
                    }
                }
                setOnLongClickListener {
                    longClickItemEvent?.onItemLongClick(list[holder.adapterPosition]) ?: false
                }
                CLICK_LAST_TIME = SystemClock.elapsedRealtime()

            }
            return holder
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) =
            holder.onBind(list[position])

        fun replaceAll(items: List<ITEM>) {
            list = items
            notifyDataSetChanged()
        }

        fun updateItems(items: List<ITEM>) {
            RecyclerDiffUtil(list, items).apply {
                list = items
                DiffUtil.calculateDiff(this).let {
                    it.dispatchUpdatesTo(this@Adapter)
                }
            }
        }
    }

    abstract class ViewHolder<B : ViewDataBinding>(
        private val layoutId: Int,
        private val parent: ViewGroup,
        private val bindingVariableId: Int?
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
    ) {

        val binding = DataBindingUtil.bind<B>(itemView)


        fun onBind(item: Any?) {
            bindingVariableId?.let {
                binding?.setVariable(it, item)
            }
        }
    }
}