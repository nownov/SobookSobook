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
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction

// 독후감 상세보기 화면 프래그먼트
class DetailFragment : Fragment() {

    // DB 관련 변수
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    // 버튼
    lateinit var btnModify: Button  // 수정
    lateinit var btnDone: Button    // 확인
    lateinit var btnDelete: Button  // 삭제

    var str_date: String = ""
    var str_title: String =""
    var nowPage: Int = 0
    var str_sentence: String =""
    var str_think: String =""

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        var textViewNumber: TextView    // 오늘 읽은 페이지 수
        var likeSentence: TextView  // 마음에 든 문장
        var myThink: TextView   // 나의 생각
        var dailyMemoTitle: TextView    // 날짜

        textViewNumber = view.findViewById(R.id.textViewNumber)
        likeSentence = view.findViewById(R.id.likeSentence)
        myThink = view.findViewById(R.id.myThink)
        dailyMemoTitle = view.findViewById(R.id.dailyMemoTitle)

        btnModify = view.findViewById(R.id.btnModify)
        btnDone = view.findViewById(R.id.btnDone)
        btnDelete = view.findViewById(R.id.btnDelete)

        // BookReportListFragment에서 전달한 날짜, 책 제목 받기
        if(arguments != null) {
            str_date = arguments?.getString("dDate").toString()
            str_title = arguments?.getString("title").toString()
        }

        dbManager = DBManager(activity, "bookDB", null, 1)  // bookDB 데이터베이스 불러오기
        sqlitedb = dbManager.writableDatabase

        var cursor: Cursor

        // 전달받은 택 제목과 날짜에 해당하는 데이터 조회
        cursor = sqlitedb.rawQuery("SELECT * FROM writeDB WHERE dTitle = '" + str_title +"' and dDate = '" + str_date +"';", null)

        // 커서를 이용해서 오늘 읽은 페이지 수, 마음에 든 문장, 나의 생각을 가져오기
        if (cursor.moveToNext()){
            str_sentence = cursor.getString(cursor.getColumnIndex("dSentence")).toString()
            nowPage = cursor.getInt(cursor.getColumnIndex("dNowPage"))
            str_sentence = cursor.getString(cursor.getColumnIndex("dSentence")).toString()
            str_think = cursor.getString(cursor.getColumnIndex("dThink")).toString()
        }

        dailyMemoTitle.text = str_date
        likeSentence.text = str_sentence
        textViewNumber.text = "" + nowPage
        myThink.text = str_think + "\n"

        // 수정 버튼 - 책 제목과 날짜를 담아 수정 프래그먼트(DetailModifyFragment)로 화면을 전환
        btnModify.setOnClickListener {
            var title = str_title
            var dDate = str_date
            var bundle = Bundle()
            bundle.putString("dTitle", title)
            bundle.putString("dDate", dDate)

            val ft : FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()

            var detailModifyFragment = DetailModifyFragment()
            detailModifyFragment.arguments = bundle
            ft.replace(R.id.container, detailModifyFragment).commit()
        }

        // 확인 버튼 - 독후감 리스트를 보여주는 프래그먼트(BookReportListFragment)로 화면을 전환
        btnDone.setOnClickListener {
            var title = str_title
            var dDate = str_date
            var bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("dDate", dDate)
            val ft : FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
            var bookReportListFragment = BookReportListFragment()
            bookReportListFragment.arguments = bundle
            ft.replace(R.id.container, bookReportListFragment).commit()
        }

        // 삭제 버튼 - 해당 독후감 삭제 후 독후감 리스트를 보여주는 프래그먼트(BookReportListFragment)로 화면을 전환
        btnDelete.setOnClickListener {
            var title = str_title
            var dDate = str_date
            var nowPage = nowPage
            var bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("dDate", dDate)

            cursor = sqlitedb.rawQuery("SELECT * FROM bookDB WHERE title = '" + str_title +"';", null)  // 전달받은 택 제목에 해당하는 데이터 조회

            // 커서를 이용해서 오늘 읽은 페이지 수, 마음에 든 문장, 나의 생각을 가져오기
            while (cursor.moveToNext()){
                var accum_page = cursor.getInt(cursor.getColumnIndex("accumPage"))  // 현재까지 읽은 총 페이지 수
                accum_page = accum_page - nowPage
                sqlitedb.execSQL("UPDATE bookDB SET accumPage = '" + accum_page + "'  WHERE title = '" + str_title + "';")
            }

            sqlitedb.execSQL("DELETE FROM writeDB WHERE dtitle = '" + str_title +"' and dDate = '" + dDate +"';")

            val ft : FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
            var bookReportListFragment = BookReportListFragment()
            bookReportListFragment.arguments = bundle
            ft.replace(R.id.container, bookReportListFragment).commit()
        }

        return view
    }

}