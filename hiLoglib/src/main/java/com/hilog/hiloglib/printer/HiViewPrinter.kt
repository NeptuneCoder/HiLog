package com.hilog.hiloglib.printer

import android.app.Application
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hilog.hiloglib.HiLogConfig
import com.hilog.hiloglib.HiLogType
import com.hilog.hiloglib.R


class HiViewPrinter(val app: Application) : HiLogPrinter {
    val recyclerView: RecyclerView
    val adapter: LogAdapter
    val hiLogPrinterProvider: HiLogPrinterProvider

    init {
        //todo  全局添加，而不是基于某一个activity
        recyclerView = RecyclerView(app)
        recyclerView.setBackgroundColor(Color.BLACK)
        val layoutmanager = LinearLayoutManager(app)
        recyclerView.layoutManager = layoutmanager
        adapter = LogAdapter(recyclerView)
        recyclerView.adapter = adapter
        hiLogPrinterProvider = HiLogPrinterProvider(app, recyclerView)
        hiLogPrinterProvider.showFloatingView()

    }


    override fun print(config: HiLogConfig, level: Int, tag: String, printString: String) {
        //todo 将日志打印到面板上
        val model = HiLogModel(System.currentTimeMillis(), level, tag, printString)
        adapter.addLog(model)

    }

    class LogAdapter(val recyclerView: RecyclerView) : RecyclerView.Adapter<LogViewHolder>() {
        val logs = mutableListOf<HiLogModel>()
        fun addLog(model: HiLogModel) {
            logs.add(model)

            notifyItemChanged(logs.size - 1)//刷新最后一条
            recyclerView.smoothScrollToPosition(logs.size - 1)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.hilog_item, parent, false)
            return LogViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return logs.size
        }

        override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
            val data = logs.get(position)
            val color = getHighlightColor(data.level)
            holder.tagView.setTextColor(color)
            holder.msgView.setTextColor(color)

            holder.tagView.setText(data.getPlattened())
            holder.msgView.setText(data.log)
        }

        fun getHighlightColor(logLevel: Int): Int {
            return when (logLevel) {
                HiLogType.V -> 0xffbbbbbb.toInt()
                HiLogType.D -> 0xffffffff.toInt()
                HiLogType.I -> 0xff6a8759.toInt()
                HiLogType.W -> 0xffbbb529.toInt()
                HiLogType.E -> 0xffffff00.toInt()
                HiLogType.A -> 0xffffff00.toInt()
                else -> 0x333333
            }
        }


    }

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagView = itemView.findViewById<TextView>(R.id.tag)
        val msgView = itemView.findViewById<TextView>(R.id.content)

    }
}