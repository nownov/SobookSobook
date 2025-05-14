/*
어시슈트 - 소북소북 코드입니다.

정보보호학과 2020111323 김지원
정보보호학과 2021111325 김해린
정보보호학과 2021111336 송다은(팀 대표)
정보보호학과 2021111694 이가연

 */

package com.android.bookdiary

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class CalendarAdapter(private val dayList: ArrayList<LocalDate?>,
                      private val onItemListener: OnitemListener,
                      val calendarDataArray: ArrayList<CalendarData> ):
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val dayText: TextView = itemView.findViewById<TextView>(R.id.dayText)
        val dayImg: ImageView = itemView.findViewById(R.id.dayImg)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var num: Int = calendarDataArray.size// 받아온 데이터 사이즈 저장
        var day = dayList[holder.position] //   날짜 변수에 담기
        var data: CalendarData//    CalendarData 타입의 변수 선언
        var colorDate = arrayOfNulls<String>(num) //    받아온 데이터 중 날짜를 가져오기위한 배열(db에 저장된 날짜)
        var colorYear = arrayOfNulls<String>(num) //    날짜 중 년도
        var colorMonth = arrayOfNulls<String>(num) //   날짜 중 월
        var colorDay = arrayOfNulls<String>(num) // 날짜 중 일
        var colorIntDay = arrayOfNulls<Int>(num)//  일 타입을 int 형식으로 바꾸기
        var selectedColor = arrayOfNulls<String>(num) //    해당 날짜의 색깔
        var colorRatio = arrayOfNulls<Int>(num) //  얼마나 읽었는지의 비율


        for (i in 1..num - 1) {//   받아온 데이터 사이즈만큼 for문 동작

            data = calendarDataArray[i]

            colorDate[i] = data.date.toString() //  2023년 01월 11일
            colorYear[i] = colorDate[i]?.substring(0 until 4) //    2023
            colorMonth[i] = colorDate[i]?.substring(6 until 8) //   01
            if(colorMonth[i] != "10" && colorMonth[i] != "11" && colorMonth[i] != "12") { //    한 자리 수면
                colorMonth[i] = colorMonth[i]?.substring(1 until 2) //  앞의 0 자르기
            }
            colorDay[i] = colorDate[i]?.substring(10 until 12) //   11
            colorIntDay[i] = colorDay[i]!!.toInt() //   일은 int형으로 형변환

            selectedColor[i] = data.color.toString() // 색 받아오기
            colorRatio[i] = data.ratioPage //   비율 받아오기

        }

        if (day == null) { //   날짜가 없다면

            holder.dayText.text = "" // 아무것도 표시 하지 않음

        } else { // 날짜가 있다면
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.dayText.text = day.dayOfMonth.toString() // 해당하는 날짜 보이기
            }
            holder.dayImg.setImageResource(R.drawable.ic_round_menu_book_24)
            for (i in 1..num - 1) { //  받아온 데이터 사이즈만큼 for문 동작
                if(colorIntDay[i]== day.dayOfMonth && colorMonth[i] == day.monthValue.toString() && colorYear[i] == day.year.toString()){// 선택한 아이템의 년도, 월, 일이 같다면
                    if(selectedColor[i] == "NAVY") { // 책에 해당하는 컬러가 남색일 때
                        if(colorRatio[i]!! >= 50){ //   그 날 읽은 분량이 50% 이상이라면
                            holder.dayImg.setColorFilter(Color.parseColor("#A0C4FF")) //    색을 진하게
                        } else { // 읽은 분량이 50% 미만이라면
                            holder.dayImg.setColorFilter(Color.parseColor("#A0C4FF"))
                            holder.dayImg.setAlpha(0.5f) // 투명도를 낮춰서 색을 연하게 표시
                        }
                    }
                    if(selectedColor[i] == "RED") {//   빨간색일 때
                        if (colorRatio[i]!! >= 50) {
                            holder.dayImg.setColorFilter(Color.parseColor("#FFADAD"))
                        }else{
                            holder.dayImg.setAlpha(0.5f)
                            holder.dayImg.setColorFilter(Color.parseColor("#FFADAD"))

                        }
                    }
                    if(selectedColor[i] == "PINK") {//  분홍색일 때
                        if (colorRatio[i]!! >= 50) {
                            holder.dayImg.setColorFilter(Color.parseColor("#FFC6FF"))
                        }else{
                            holder.dayImg.setAlpha(0.5f)
                            holder.dayImg.setColorFilter(Color.parseColor("#FFC6FF"))

                        }
                    }
                    if(selectedColor[i] == "GREEN") {// 초록색일 때
                        if (colorRatio[i]!! >= 50) {
                            holder.dayImg.setColorFilter(Color.parseColor("#CAFFBF"))
                        }else{
                            holder.dayImg.setAlpha(0.5f)
                            holder.dayImg.setColorFilter(Color.parseColor("#CAFFBF"))

                        }
                    }
                    if(selectedColor[i] == "ORANGE") {//    주황색일 때
                        if (colorRatio[i]!! >= 50) {
                            holder.dayImg.setColorFilter(Color.parseColor("#FFD6A5"))
                        }else{
                            holder.dayImg.setAlpha(0.5f)
                            holder.dayImg.setColorFilter(Color.parseColor("#FFD6A5"))

                        }
                    }
                    if(selectedColor[i] == "YELLOW") {//    노란색일 때
                        if (colorRatio[i]!! >= 50) {
                            holder.dayImg.setColorFilter(Color.parseColor("#FDFFB6"))
                        }else{
                            holder.dayImg.setAlpha(0.5f)
                            holder.dayImg.setColorFilter(Color.parseColor("#FDFFB6"))

                        }
                    }
                    if(selectedColor[i] == "PURPLE") {//    보라색일 때
                        if (colorRatio[i]!! >= 50) {
                            holder.dayImg.setColorFilter(Color.parseColor("#BDB2FF"))
                        }else{
                            holder.dayImg.setAlpha(0.5f)
                            holder.dayImg.setColorFilter(Color.parseColor("#BDB2FF"))

                        }
                    }
                    if(selectedColor[i] == "BLUE") {//  파란색일 때
                        if (colorRatio[i]!! >= 50) {
                            holder.dayImg.setColorFilter(Color.parseColor("#9BF6FF"))
                        }else{
                            holder.dayImg.setAlpha(0.5f)
                            holder.dayImg.setColorFilter(Color.parseColor("#9BF6FF"))

                        }

                    }

                }

            }

            if (position % 7 == 6) { // 토요일은 파랑, 일요일은 빨강
                holder.dayText.setTextColor(Color.parseColor("#A0C4FF"))
            } else if (position == 0 || position % 7 == 0) {
                holder.dayText.setTextColor(Color.parseColor("#FFADAD"))
            }

            holder.itemView.setOnClickListener {//  날짜 클릭시 이벤트 발생
                onItemListener.onItemClick(day) //  인터페이스를 통해 날짜 넘겨줌

            }
        }
    }
}




