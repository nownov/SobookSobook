/*
어시슈트 - 소북소북 코드입니다.

정보보호학과 2020111323 김지원
정보보호학과 2021111325 김해린
정보보호학과 2021111336 송다은(팀 대표)
정보보호학과 2021111694 이가연

 */

package com.android.bookdiary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

// 특정 책을 클릭해서 들어갔을 때 해당 책의 독후감 리스트를 모두 보여주는 어댑터
class BookReportListAdapter(private val context: Context, val bookReportListDataArray: ArrayList<BookReportListData>, private val clickBookReportHandler: BookReportListHandler) :
    RecyclerView.Adapter<BookReportListAdapter.ViewHolder>() {

    // 뷰 홀더 클래스 객체를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.report_item, parent,false)
        return ViewHolder(view)
    }

    // 어댑터에 설정된 아이템 리스트 (bookReportListDataArray)의 크기를 반환하여 목록에 보여줄 아이템의 개수를 설정
    override fun getItemCount(): Int = bookReportListDataArray.size

    // 어댑터 생성자에 넘어온 데이터를 뷰 홀더에 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data: BookReportListData = bookReportListDataArray[position]
        holder.bind(data)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        // 프래그먼트 내의 위젯 연결
        private var dDate: TextView = itemView.findViewById(R.id.dDate)

        init {
            view.setOnClickListener(this)
        }

        fun bind(item: BookReportListData) {

            // DB에서 가져온 값을 프래그먼트 내의 위젯에 연결
            dDate.text = item.dDate
            }

        // 날짜 클릭 시 clickBookReportHandler 작동
        override fun onClick(v: View?) {
            val position = bookReportListDataArray[position]
            clickBookReportHandler.clickedBookReportList(position)
        }


    }
}