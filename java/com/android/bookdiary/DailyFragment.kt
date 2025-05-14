/*
어시슈트 - 소북소북 코드입니다.

정보보호학과 2020111323 김지원
정보보호학과 2021111325 김해린
정보보호학과 2021111336 송다은(팀 대표)
정보보호학과 2021111694 이가연

 */

package com.android.bookdiary

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView



class DailyFragment : Fragment(), DailyClickHandler {

    lateinit var dailyRecycler: RecyclerView // 리사이클러뷰
    var dailyChoiceList: ArrayList<DailyChoiceData> = ArrayList() // 리사이클러뷰에 이용할 리스트

    // DB 관련 변수
    lateinit var dbManager : DBManager
    lateinit var sqlitedb : SQLiteDatabase

    // DB에서 받아올 변수 선언
    lateinit var bookColor : String
    lateinit var bookTitle : String
    lateinit var id : String
    lateinit var date : String
    lateinit var accumPageString : String
    var accumPage : Int = 0
    var totalPage : Int = 0

    @SuppressLint("UseRequireInsteadOfGet", "Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DailyFragment의 레이아웃을 fragment_daily로 설정
        val view = inflater.inflate(R.layout.fragment_daily, container, false)

        dbManager = DBManager(activity, "bookDB", null, 1) //bookDB 데이터베이스 불러오기
        sqlitedb = dbManager.readableDatabase

        var cursor : Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM bookDB;", null) //bookDB 테이블 정보 불러오기

        dailyRecycler = view.findViewById(R.id.dailyRecycler!!) as RecyclerView // 리사이클러 뷰 연결하기
        dailyRecycler.layoutManager = GridLayoutManager(requireContext(), 3) // 1행에 3열씩 보이도록 설정
        dailyRecycler.adapter = DailyChoiceAdapter(requireContext(), dailyChoiceList, this) // dailyChoiceList에 저장된 data 어댑터로 연결

        // cursor에 값이 0개일 때, 불러올 책 목록이 없다는 팝업 띄우기
        if(cursor.count == 0){
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.daily_zero_dialog, null, false)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)
                .setTitle("책을 불러올 수 없어요")
            val  mAlertDialog = mBuilder.show()
            val parent = mDialogView.parent as ViewGroup
            val btn = mDialogView.findViewById<Button>(R.id.dialogBtn)
            btn.setOnClickListener {
                parent.removeView(mDialogView)
                mAlertDialog.dismiss()
                val mainFragment = MainFragment()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
                transaction.replace(R.id.container, mainFragment)
                transaction.commit()
            }
        }

        // bookDB에 값이 있는 동안 책 정보 불러와서 화면에 띄우기
        while (cursor.moveToNext()) {
            totalPage = cursor.getInt(cursor.getColumnIndex("totalPage"))
            accumPageString = cursor.getString(cursor.getColumnIndex("accumPage"))
            if (accumPageString == "null") { // 이전까지 읽은 페이지의 합이 null이면 accumPage 0으로 정하기
                accumPage = 0
            }
            else { // 이전까지 읽은 페이지의 합이 null이 아니라면 Int 타입으로 변환
                accumPage = accumPageString.toInt()
            }

            if (totalPage != accumPage) { // 아직 다 읽지 못한 책만 목록에 띄우기

                bookColor = cursor.getString(cursor.getColumnIndex("color"))
                bookTitle = cursor.getString(cursor.getColumnIndex("title"))
                date = arguments?.getString("date").toString() // MainFragment로부터 날짜 정보 받아오기
                if (date == "null") { // 만약 DailyMemoFragment에서 넘어왔을 경우에는 DailyMemoFragment에서 날짜 정보 받아오기
                    date = arguments?.getString("dDate").toString()
                }

                // DailyChoiceData에 정보 채워서 dailyChoiceList에 추가하기
                var data =
                    DailyChoiceData(bookColor, bookTitle, date, totalPage, accumPage)
                dailyChoiceList.add(data)

            }
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        return view
    }

    // 책 목록에서 책 선택했을 때 DailyMemoFragment로 정보 넘기기
    override fun clickedBookItem(book: DailyChoiceData) {

        // dailyChoiceList에서 클릭된 책의 정보 불러오기
        var dTitle = book.bookTitle //책 제목
        var dColor = book.bookColor //책 색깔
        var dTotalPage : String = book.totalPage.toString() //총 페이지 수
        var dAccumPage : Int = book.accumPage // 현재까지 읽은 페이지 수

        // 정보를 DailyMemoFragment로 전달하기 위한 bundle 코드
        var bundle = Bundle()
        bundle.putString("dTitle", dTitle)
        bundle.putString("dColor", dColor)
        bundle.putString("dTotalPage", dTotalPage)
        bundle.putString("dDate", date)
        bundle.putInt("dAccumPage", dAccumPage)

        // dailyMemoFragment로 프래그먼트 이동시키기
        val ft : FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
        var dailyMemoFragment = DailyMemoFragment()
        dailyMemoFragment.arguments = bundle
        ft.replace(R.id.container, dailyMemoFragment).commit()
        Toast.makeText(activity, dTitle, Toast.LENGTH_SHORT).show() // 날짜 정보 토스트 메세지 띄우기
    }

}