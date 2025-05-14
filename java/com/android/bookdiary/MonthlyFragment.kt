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
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.Dimension
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.fragment_monthly.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class MonthlyFragment : Fragment() {

    private lateinit var switch1: Switch    // 스위치 변수

    private lateinit var myBookRecord1: TextView    // 통계 화면 첫 줄 텍스트 변수
    private lateinit var myBookRecord2: TextView    // 통계 화면 두번쨰 줄 텍스트 변수
    private lateinit var myBookRecord3: TextView    // 통계 화면 세번째 줄 텍스트 변수
    private lateinit var one: TextView     // 통계 화면 지난달 / 이번달 텍스트 표시 변수
    private lateinit var two: TextView     // 통계 화면 평균 / 나 텍스트 표시 변수수
    private lateinit var graphJj: ImageView    // 이번달, 지난달 독서량이 없는 경우 사용할 이미지 변수
    private lateinit var switchText: TextView    // 스위치 이름 텍스트 변수

    lateinit var myHelper: DBManager    // DB 관련 변수
    lateinit var sqlDB: SQLiteDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_monthly, container, false)
        var barChart1 = view.findViewById<BarChart>(R.id.barChart1)    // 이번달 독서 통계의 그래프. 레이아웃의 barChart와 연결
        var barChart2 = view.findViewById<BarChart>(R.id.barChart2)    // 나의 독서 통계 그래프

        myBookRecord1 = view.findViewById<TextView>(R.id.myBookRecord1)    // 레이아웃의 TextView와 연결
        myBookRecord2 = view.findViewById<TextView>(R.id.myBookRecord2)    // 레이아웃의 TextView와 연결
        myBookRecord3 = view.findViewById<TextView>(R.id.myBookRecord3)    // 레이아웃의 TextView와 연결
        one = view.findViewById(R.id.one)    // 레이아웃의 TextView와 연결
        two = view.findViewById(R.id.two)    // 레이아웃의 TextView와 연결
        graphJj = view.findViewById(R.id.graphJj)    // 레이아웃의 ImageView와 연결
        switchText = view.findViewById(R.id.switchText)    // 레이아웃의 TextView와 연결

        myHelper = DBManager(activity, "bookDB", null, 1)    // DBManager와 연결

        switch1 = view.findViewById<Switch>(R.id.switch1)    // 통계 화면의 스위치 이름 변수. 레이아웃의 스위치와 연결

        switch1.setOnClickListener {    // 스위치가 on일 경우 성인 평균 데이터와 나의 독서량 비교 그래프 출력

            if (switch1.isChecked == true){
                barChart1.visibility = View.INVISIBLE    // 이번달 독서 통계 그래프 안 보이게 설정
                barChart2.visibility = View.VISIBLE    // 나의 독서 통계 그래프 보이게 설정
                myBookRecord1.visibility = View.INVISIBLE    // 텍스트 변수 안 보이게 설정
                myBookRecord2.visibility = View.VISIBLE    // 텍스트 변수 보이게 설정
                myBookRecord3.visibility = View.INVISIBLE    // 텍스트 변수 안 보이게 설정
                one.visibility = View.INVISIBLE    // 텍스트 변수 안 보이게 설정
                two.visibility = View.VISIBLE    // 텍스트 변수 보이게 설정
                setWeek2(barChart2)    // 나의 독서 통계 그래프 출력
                graphJj.visibility = View.INVISIBLE    // 소북소북 아이콘 이미지 숨김

            } else {
                barChart2.visibility = View.INVISIBLE    // 나의 독서 통계 그래프 안 보이게 설정
                barChart1.visibility = View.VISIBLE    // 이번달 독서 통계 그래프 보이게 설정
                myBookRecord1.visibility = View.VISIBLE    // 텍스트 변수 보이게 설정
                myBookRecord2.visibility = View.VISIBLE    // 텍스트 변수 보이게 설정
                myBookRecord3.visibility = View.VISIBLE    // 텍스트 변수 보이게 설정
                one.visibility = View.VISIBLE    // 텍스트 변수 보이게 설정
                two.visibility = View.INVISIBLE    // 텍스트 변수 안 보이게 설정
                setWeek(barChart1)    // 이번달 독서 통계 그래프 출력
            }
        }

        setWeek(barChart1)    // 이번달 독서 통계 그래프 출력

        return view
    }


    // barChart 출력 메소드
    private fun setWeek(barChart: BarChart) {

        initBarChart(barChart)    // barChart의 색상, 크기 지정

        barChart.setScaleEnabled(false)    // Zoom In/Out

        val valueList = ArrayList<Int>()    // 그래프의 데이터 값이 들어갈 리스트
        val entries: ArrayList<BarEntry> = ArrayList()
        val title = "지난달과 이번 달 독서량 비교"    // 그래프 제목 설정

        sqlDB = myHelper.readableDatabase   // DB를 읽기 가능한 모드로 가져옴

        var cursor: Cursor

        val now = System.currentTimeMillis()    // 시스템의 현재 시간 가져오기
        val nowDate = Date(now)    // 현재 시간 가져와서 nowData에 저장
        val nowFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss E", Locale("ko", "KR"))    // 날짜, 시간을 가져오고 싶은 형태로 선언
        val  strNow = nowFormat.format(nowDate)    // 현재 시간을 nowFormat에 선언한 형태의 String으로 변환 '2023-01-27 13:26:59 금'

        var subMonth = strNow.substring(6 until 7)    // 월 정보만 추출
        subMonth = "0" + subMonth    // 월 값 앞에 0 붙이기 ex) 01

        var lastMonth = subMonth.toInt() - 1    // 지난달 월 값 구하기
        if (lastMonth == 0) {    // lastMonth 값이 0일 경우
            lastMonth = 12    // 12월로 설정
        }

        var maxPage = 0    // 지난달 독서량 변수

        cursor = sqlDB.rawQuery("SELECT dNowPage FROM writeDB WHERE dDate LIKE '%${lastMonth}월%';", null)    // 지난달 모든 독서 기록에서 읽은 페이지 수의 정보를 불러옴

        while (cursor.moveToNext()){
            maxPage += cursor.getString(0).toInt()    // 지난달 읽은 페이지 수 전체 덧셈 연산
        }

        valueList.add(maxPage)    // 지난달 독서량을 그래프에 입력

        var cMaxPage = 0    // 이번달 독서량 변수

        cursor = sqlDB.rawQuery("SELECT dNowPage FROM writeDB WHERE dDate LIKE '%${subMonth}월%';", null)    // 이번달 모든 독서 기록에서 읽은 페이지 수의 정보를 불러옴

        while (cursor.moveToNext()){
            cMaxPage += cursor.getString(0).toInt()    // 이번달 읽은 페이지 수 전체 덧셈 연산
        }

        valueList.add(cMaxPage)    // 이번달 독서량을 그래프에 입력

        var monthRecord = 0    // 기록 일수 변수

        cursor = sqlDB.rawQuery("SELECT dDate FROM writeDB WHERE dDate LIKE '%${subMonth}월%';", null)    // 이번달 모든 기록을 불러옴

        while (cursor.moveToNext()){
            monthRecord = cursor.count    // 기록 개수를 저장
        }

        myBookRecord3.text = "이번달에 " + monthRecord + "번이나 기록했어요!"    // 이번달 기록 개수 출력
        myBookRecord3.setTextSize(Dimension.SP, 16f)    // 텍스트 크기 수정
        one.setTextSize(Dimension.SP, 16f)    // 텍스트 크기 수정
        two.setTextSize(Dimension.SP, 16f)    // 텍스트 크기 수정
        switchText.setTextSize(Dimension.SP, 16f)    // 텍스트 크기 수정

        myBookRecord1.text = "이번달에 " + cMaxPage + "쪽 읽었어요."    // 이번달 읽은 총 페이지 수 출력
        myBookRecord1.setTextSize(Dimension.SP, 16f)    // 텍스트 크기 수정

        var charRead = cMaxPage - maxPage    // 이번달과 지난달의 페이지수에 따른 독서량 차이 값 저장

        if (charRead < 0) {    // 지난달과 이번달 독서량 차이에 따른 문자열 변화
            myBookRecord2.text = "앞으로 " + abs(charRead) + "쪽 더 읽으면 지난달보다 많이 읽을 수 있어요!"
        } else if (charRead == 0) {
            myBookRecord2.text = "지난달만큼 읽었어요!"
        } else {
            myBookRecord2.text = "지난달보다 " + charRead + "쪽 더 읽었어요!"
            myBookRecord2.setTextSize(Dimension.SP, 16f)    // 텍스트 크기 수정
        }

        cursor = sqlDB.rawQuery("SELECT dDate FROM writeDB WHERE dDate LIKE '%${subMonth}월%' OR '%${lastMonth}월%';", null)  // 이번달과 지난달 독서 기록 정보 불러옴

        if (cursor.count == 0) {    // 이번달과 지난달 모두 기록하지 않은 경우
            myBookRecord1.visibility = View.INVISIBLE
            myBookRecord2.text = "아직 기록을 작성하지 않았어요!"    // 텍스트 수정
            myBookRecord2.setTextSize(Dimension.SP, 20f)    // 텍스트 크기 수정
            myBookRecord3.visibility = View.INVISIBLE
            barChart.visibility = View.INVISIBLE    // 이번달 독서 통계 그래프 숨김
            one.visibility = View.INVISIBLE    // 이번달 독서 통계의 x축 값인 지난달, 이번달 값 숨김
            graphJj.visibility = View.VISIBLE    // 소북소북 아이콘 이미지 보임
        } else {
            graphJj.visibility = View.INVISIBLE    // 소북소북 아이콘 이미지 숨김
            barChart.visibility = View.VISIBLE    // 이번달 독서 통계 그래프 보임
        }

        cursor.close()    // cursor 닫기
        sqlDB.close()    // sqlDB 닫기

        for (i in 0 until valueList.size) {    //  리스트 사이즈만큼 그래프에 데이터 입력
            val barEntry = BarEntry(i.toFloat(), valueList[i].toFloat())  // float 타입으로 valueList에 지난달, 이번달 읽은 페이지수 입력
            entries.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, title)    // 그래프와 제목을 결합
        val data = BarData(barDataSet)
        barChart.data = data
        barChart.invalidate()

    }


    // barChart2 출력 메소드
    @SuppressLint("Range")
    private fun setWeek2(barChart: BarChart) {

        initBarChart(barChart)    // barChart의 색상, 크기 지정

        barChart.setScaleEnabled(false)    // Zoom In/Out

        val valueList = ArrayList<Int>()    // 그래프의 데이터 값이 들어갈 리스트
        val entries: ArrayList<BarEntry> = ArrayList()
        val title = "연간 성인 평균 독서량과 내 독서량 비교"    // 그래프 제목 설정

        sqlDB = myHelper.readableDatabase       // DB를 읽기 가능한 모드로 가져옴
        var cursor: Cursor

        cursor = sqlDB.rawQuery("SELECT readAvg FROM readingDB WHERE category = '소계';", null)    // 연간 성인 평균 독서량을 불러옴
        var readAvg = 0    // 연간 성인 평균 독서량 저장 변수
        while (cursor.moveToNext()){
            readAvg = cursor.getInt(0)    // 연간 성인 평균 독서량 값 저장
        }

        valueList.add(readAvg)    // 연간 평균 성인 독서량을 데이터 리스트에 입력

        cursor = sqlDB.rawQuery("SELECT * FROM bookDB;", null)    // 나의 독서량을 데이터 리스트에 입력
    //    var recordCounter = cursor.count.toString()
        var recordCount = cursor.count
        while(cursor.moveToNext()){
            var accumPage = cursor.getInt(cursor.getColumnIndex("accumPage"))    // accumPage 값이 0 -> 읽지 않은 책이므로
            if(accumPage == 0){
                recordCount--    // 등록된 책 개수에서 1씩 마이너스
            }
        }
        var recordCounter = recordCount.toString()
        myBookRecord1.text = "이번 달에 " + recordCounter + "권 읽었어요!"
        valueList.add(recordCounter.toInt())

        var comparAvg = readAvg.toInt() - recordCounter.toInt()    // 연간 성인 평균 독서량과 나의 독서량의 차이 값 저장

        if (comparAvg < 0) {    // 연간 성인 평균 독서량보다 많이 읽었을 경우
            myBookRecord2.text = "연간 성인 평균 독서량보다 " + abs(comparAvg) + "권 더 읽었어요!"    // 텍스트 변경
            myBookRecord2.setTextSize(Dimension.SP, 20f)    // 텍스트 크기 설정
        } else if (comparAvg == 0) {    // 연간 성인 평균 독서량만큼 읽었을 경우
            myBookRecord2.text = "연간 성인 평균 독서량인 " + recordCounter + "권 읽었어요!"    // 텍스트 변경
            myBookRecord2.setTextSize(Dimension.SP, 20f)    // 텍스트 크기 설정
        } else {    // 연간 성인 평균 독서량보다 적게 읽었을 경우
            myBookRecord2.text = "" + comparAvg + "권 더 읽으면 연간 성인 평균 독서량 이상이에요!"    // 텍스트 변경
            myBookRecord2.setTextSize(Dimension.SP, 20f)    // 텍스트 크기 설정
        }

        if (cursor.count == 0) {    // 등록한 책이 없는 경우
            myBookRecord2.visibility = View.VISIBLE    // 텍스트 숨김
            myBookRecord2.text = "아직 책을 읽지 않았어요."    // 텍스트 변경
            myBookRecord2.setTextSize(Dimension.SP, 20f)    // 텍스트 크기 수정
        }

        cursor.close()    // cursor 닫기
        sqlDB.close()    // sqlDB 닫기

        for (i in 0 until valueList.size) {    // 리스트 사이즈만큼 그래프에 데이터 입력
            val barEntry = BarEntry(i.toFloat(), valueList[i].toFloat())  // float 타입으로 valueList에 지난달, 이번달 읽은 페이지수 입력
            entries.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, title)    // 그래프와 제목을 결합
        val data = BarData(barDataSet)
        barChart.data = data
        barChart.invalidate()

    }


    // barChart의 색상, 위치, 제목 설정 메소드
    private fun initBarChart(barChart: BarChart) {

        barChart.setDrawGridBackground(false)    // 차트의 회색 배경 숨기기, 설정되지 않은 경우 기본 false
        barChart.setDrawBarShadow(false)    // 막대 그림자 제거, 설정되지 않은 경우 기본 false
        barChart.setDrawBorders(false)    // 차트의 테두리 제거, 설정되지 않은 경우 기본 false

        val description = Description()    // 오른쪽 아래 모서리에 있는 설명 레이블 텍스트를 제거

        description.setEnabled(false)
        barChart.setDescription(description)

        barChart.animateY(1000)    // X, Y 바의 애니메이션 효과
        barChart.animateX(1000)

        val xAxis: XAxis = barChart.getXAxis()    // 바텀 좌표 값

        xAxis.position = XAxis.XAxisPosition.BOTTOM    // x축의 위치를 하단으로 변경
        xAxis.granularity = 1f    // 격자선의 수평 거리 설정 (하단 값)
        xAxis.textColor = Color.WHITE    // 하단 값의 색상
        xAxis.setDrawAxisLine(false)    // x축 선 숨기기, 설정되지 않은 경우 기본 true
        xAxis.setDrawGridLines(false)    // 수직 그리드 선 숨기기, 설정되지 않은 경우 기본 true

        val leftAxis: YAxis = barChart.getAxisLeft()    // 좌측 값 y축 선 숨기기, 설정되지 않은 경우 기본 true

        leftAxis.setDrawAxisLine(false)
        leftAxis.textColor = Color.BLACK    // 좌측 값 색상 설정

        val rightAxis: YAxis = barChart.getAxisRight()    //우측 값 y축 선 숨기기, 설정되지 않은 경우 기본 true

        rightAxis.setDrawAxisLine(false)
        rightAxis.textColor = Color.WHITE    // 우측 값 색상 설정

        val legend: Legend = barChart.getLegend()    // barChart의 타이틀

        legend.form = Legend.LegendForm.LINE    // 기준 양식의 모양을 선으로 설정, 기본 사각형 모양
        legend.textSize = 13f    // 그래프 제목의 텍스트 크기, 색상 설정
        legend.textColor = Color.rgb(81, 99, 177)
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP    // 그래프 제목 정렬 설정
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL    // 쌓기 방향 설정
        legend.setDrawInside(false)    // 차트 외부에 기준 위치 설정, 설정되지 않은 경우 기본 false

    }

}
