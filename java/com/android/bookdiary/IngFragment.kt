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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// 나의 책장에서 독서를 진행 중인 책 리스트를 보여주는 프래그먼트
class IngFragment : Fragment(), BookListHandler {

    //DB 관련 변수
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var recyclerView: RecyclerView // 리사이클러뷰

    @SuppressLint("UseRequireInsteadOfGet", "Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ing, container, false)

        var bookListDataArray: ArrayList<BookListData> = ArrayList()    // 리사이클러뷰에 이용할 리스트

        dbManager = DBManager(activity, "bookDB", null, 1)  // bookDB 데이터베이스 불러오기
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM bookDB WHERE totalPage <> accumPage and accumPage <> 0;", null)   // bookDB에서 독서를 진행 중인 책의 데이터만 조회

        // bookDB에 값이 있는 동안 책 정보 불러와서 화면에 띄우기
        while (cursor.moveToNext()) {
            var str_title = cursor.getString(cursor.getColumnIndex("title"))    // 책 제목
            var total_page = cursor.getInt(cursor.getColumnIndex("totalPage"))  // 책의 전체 페이지
            var str_color = cursor.getString(cursor.getColumnIndex("color"))    // 책의 컬러
            var accum_page = cursor.getInt(cursor.getColumnIndex("accumPage"))  // 현재까지 읽은 총 페이지 수
            var percent = accum_page.toFloat() / total_page.toFloat() * 100 // 해당 책의 독서 진행도

            var data: BookListData = BookListData(str_title, accum_page, total_page, str_color, percent)
            bookListDataArray.add(data) // 리사이클러뷰에 반영할 데이터
        }

        recyclerView = view.findViewById(R.id.recyclerView!!) as RecyclerView   // 리사이클러뷰 연결하기
        recyclerView.layoutManager = LinearLayoutManager(requireContext())  // 리사이클러뷰의 아이템이 수직 방향으로 보이도록 리니어 레이아웃 매니저를 사용
        recyclerView.adapter = BookListAdapter(requireContext(), bookListDataArray, this)   // bookListDataArray에 저장된 data 어댑터로 연결

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        return view

    }

    // BookListHandler 인터페이스의 메소드 오버라이딩 - 책장의 책 아이템을 클릭하면 해당 책의 독후감 리스트 프래그먼트(BookReportFragment)로 전환
    override fun clickedBookList(book: BookListData) {
        var title = book.title
        var bundle = Bundle()
        bundle.putString("title", title)    // 책 제목 전달
        val ft : FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()

        var bookReportListFragment = BookReportListFragment()
        bookReportListFragment.arguments = bundle
        ft.replace(R.id.container, bookReportListFragment).commit()
        Toast.makeText(activity, title, Toast.LENGTH_SHORT).show()
    }

}