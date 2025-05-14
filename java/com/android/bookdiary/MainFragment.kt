/*
어시슈트 - 소북소북 코드입니다.

정보보호학과 2020111323 김지원
정보보호학과 2021111325 김해린
정보보호학과 2021111336 송다은(팀 대표)
정보보호학과 2021111694 이가연

 */

package com.android.bookdiary

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.bookdiary.databinding.FragmentMainBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class MainFragment : Fragment(), OnitemListener {

    private lateinit var binding : FragmentMainBinding //   바인딩 할 때 사용하는 변수
    lateinit var dbManager : DBManager
    lateinit var sqlitedb : SQLiteDatabase
    val calendarDataArry : ArrayList<CalendarData> = ArrayList() // CalendarAdapter에 전달할 list


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CalendarUtil.selectedDate = LocalDate.now()//   현재 날짜 가져오기
        }
        setMonthView() //   달력 표시

        binding.preBtn.setOnClickListener {//   이전달 버튼
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusMonths(1)
            }
            setMonthView()
        }

        binding.nextBtn.setOnClickListener {//  다음달 버튼
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusMonths(1)
            }
            setMonthView()
        }

        return binding.root

    }

    @SuppressLint("UseRequireInsteadOfGet", "Range")
    private fun setMonthView() {//  달력 표시 함수

        binding.monthyearText.text = yearmonthFromDate(CalendarUtil.selectedDate) // 년 월 가져오기

        val dayList = dayInMonthArray(CalendarUtil.selectedDate) // 날짜 생성 후 리스트에 담음

        dbManager = DBManager(activity, "bookDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM writeDB;", null)//    일일 독후감을 저장한 데이터베이스 가져오기


        val adapter = CalendarAdapter(dayList, this, calendarDataArry)//    어댑터 초기화

        var manager : RecyclerView.LayoutManager = GridLayoutManager(activity, 7)// 그리드 레이아웃으로 열 7개 설정

        binding.recyclerView.layoutManager = manager // 레이아웃 적용

        binding.recyclerView.adapter = adapter//    어댑터 적용


        while(cursor.moveToNext()){
            var str_date = cursor.getString(cursor.getColumnIndex("dDate"))//   writeDB에서 독후감 등록한 날짜 가져오기
            var str_color = cursor.getString(cursor.getColumnIndex("dColor"))// writeDB에서 독후감 책에 해당하는 색 가져오기
            var totalPage = cursor.getInt(cursor.getColumnIndex("dTotalPage"))//    writeDB에서 독후감 책의 총 페이지 수 가져오기
            var nowPage = cursor.getInt(cursor.getColumnIndex("dNowPage"))//    writeDB에서 오늘 읽은 페이지 수 가져오기
            var ratioPageFloat : Float = (nowPage.toFloat() / totalPage.toFloat())//    전체 중 오늘 읽은 비율 계산하기 위해 float으로 계산
            var ratioPage : Int = (ratioPageFloat * 100).toInt()//  비율 int로 변환
            var data : CalendarData = CalendarData(str_date, str_color, ratioPage)//    CalendarAdapter로 전달할 데이터 담기
            calendarDataArry.add(data)//    CalendarAdapter에 전달하기 위해 calendarDataArry에 담기

        }
        cursor.close()
        sqlitedb.close()
        dbManager.close()


    }

    private fun yearmonthFromDate(date: LocalDate) : String{//  날짜 타입을 년도, 월로 표시하기 (ex: 2023년 11월)
        var formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("yyyy년 MM월")
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        return date.format(formatter)// 받아온 날짜 해당 포맷으로 바꾸기
    }

    private fun dayInMonthArray(date: LocalDate) : ArrayList<LocalDate?>{
        var dayList = ArrayList<LocalDate?>()

        var yearMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            YearMonth.from(date)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        var lastDay = yearMonth.lengthOfMonth() //  해당 월 마지막 날짜 가져오기 (ex: 28, 30, 31)
        var firstDay = CalendarUtil.selectedDate.withDayOfMonth(1) //   해당 월 첫번째 날짜 가져오기 (ex: 3월 1일)
        var dayOfweek = firstDay.dayOfWeek.value // 첫번째 날 요일 가져오기 (ex: 월->1, 일->7)
       for(i in 1..41) {
            if(i <= dayOfweek || i > (lastDay + dayOfweek)){//  첫번째 날 요일에 해당하는 수보다 작거나 / 월의 일 수 + 앞에서 빈칸으로 추가한 일 수 보다 크면
                dayList.add(null)// 빈칸으로 추가
            }else{//    아니라면
                dayList.add(LocalDate.of(CalendarUtil.selectedDate.year,
                CalendarUtil.selectedDate.monthValue, i-dayOfweek))//   날짜 추가
            }
        }

        return dayList//    추가한 날짜 리스트 리턴
    }

    override fun onItemClick(dayText: LocalDate) {// 아이템 클릭 이벤트

        var str_day : String = dayText.toString()// 클릭한 날짜 타입 String으로 바꿔서 저장

        var sub_year : String = str_day.substring(0 until 4)// 2023-02-01 중 2023
        var sub_month : String = str_day.substring(5 until 7)// 2023-02-01 중 02
        var sub_day : String = str_day.substring(8 until 10)// 2023-02-01 중 01
        str_day = sub_year + "년 " + sub_month + "월 " + sub_day + "일"//  날짜 형식에 맞게 문자열 저장
        var bundle = Bundle()// bundle에 전달할 데이터 담기
        bundle.putString("date", str_day)//  키 값을 key로 하여 전달
        val ft : FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()

        var DailyFragment = DailyFragment()
        DailyFragment.arguments = bundle//  dailtfragment로 전달

        ft.replace(R.id.container, DailyFragment).commit() //   daliyfragment 화면으로 전환
        Toast.makeText(activity, str_day, Toast.LENGTH_SHORT).show()//  해당 날짜 토스트로 표시

    }

}


