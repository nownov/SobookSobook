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
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction

// 저자 수정 프래그먼트
class BookUpdateAuthorFragment : Fragment() {

    // DB 관련 변수
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var tvTitle: TextView  // 책 제목
    lateinit var edtAuthor: EditText    // 저자(사용자의 입력을 받아옴)
    lateinit var tvPage: TextView   // 총 페이지 수
    lateinit var btnUpdate: Button  // 수정 버튼

    // 색상 정보
    lateinit var rg_Color: RadioGroup
    lateinit var rb_red: RadioButton
    lateinit var rb_orange: RadioButton
    lateinit var rb_yellow: RadioButton
    lateinit var rb_green: RadioButton
    lateinit var rb_blue: RadioButton
    lateinit var rb_navy: RadioButton
    lateinit var rb_purple: RadioButton
    lateinit var rb_pink: RadioButton

    lateinit var str_title: String
    lateinit var str_author: String
    lateinit var page: String
    lateinit var str_color: String

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_update_author, container, false)

        tvTitle = view.findViewById(R.id.tvTitle)
        edtAuthor = view.findViewById(R.id.edtAuthor)
        tvPage = view.findViewById(R.id.tvPage)

        rg_Color = view.findViewById(R.id.radioGroup)
        rb_red = view.findViewById(R.id.rbRed)
        rb_orange = view.findViewById(R.id.rbOrange)
        rb_yellow = view.findViewById(R.id.rbYellow)
        rb_green = view.findViewById(R.id.rbGreen)
        rb_blue = view.findViewById(R.id.rbBlue)
        rb_navy = view.findViewById(R.id.rbNavy)
        rb_purple = view.findViewById(R.id.rbPurple)
        rb_pink = view.findViewById(R.id.rbPink)

        dbManager = DBManager(activity, "bookDB", null, 1)  // bookDB 데이터베이스 불러오기
        sqlitedb = dbManager.writableDatabase

        // BookReportListFragment에서 전달한 책 제목, 총 페이지 수, 책 컬러 받기
        if(arguments != null) {
            str_title = arguments?.getString("title").toString()
            page = arguments?.getInt("page").toString()
            str_color = arguments?.getString("color").toString()
        }

        tvTitle.setText(str_title)
        tvPage.setText(page)
        if (str_color == "RED"){
            rb_red.setChecked(true)
        }
        if (str_color == "ORANGE"){
            rb_orange.setChecked(true)
        }
        if (str_color == "YELLOW"){
            rb_yellow.setChecked(true)
        }
        if (str_color == "GREEN"){
            rb_green.setChecked(true)
        }
        if (str_color == "BLUE"){
            rb_blue.setChecked(true)
        }
        if (str_color == "NAVY"){
            rb_navy.setChecked(true)
        }
        if (str_color == "PURPLE"){
            rb_purple.setChecked(true)
        }
        if (str_color == "PINK"){
            rb_pink.setChecked(true)
        }

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM bookDB WHERE title = '" + str_title +"';", null)  // 전달받아 온 책 제목과 일치하는 데이터를 조회

        // 커서를 이용하여 조건에 맞는 책의 저자 데이터를 가져오기
        if (cursor.moveToNext()){
            str_author = cursor.getString(cursor.getColumnIndex("author"))
        }

        edtAuthor.text = Editable.Factory.getInstance().newEditable(str_author)

        btnUpdate = view.findViewById(R.id.btnUpdate)

        // 수정 버튼을 눌렀을 때
        btnUpdate.setOnClickListener {
            var author = edtAuthor.text.toString()

            // 만약 저자 입력 부분에 아무 것도 입력된 것이 없다면 대화상자 띄우기
            if (author == "") {
                val mDialogView =
                    LayoutInflater.from(context).inflate(R.layout.daily_author_dialog, null, false)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)
                    .setTitle("완료할 수 없어요")
                val mAlertDialog = mBuilder.show()
                val parent = mDialogView.parent as ViewGroup
                val btn = mDialogView.findViewById<Button>(R.id.dialogBtn)
                btn.setOnClickListener {
                    parent.removeView(mDialogView)
                    mAlertDialog.dismiss()
                }

            // 수정 후 책 리스트를 보여주는 프래그먼트(listFragment)로 화면 전환
            } else {
                sqlitedb.execSQL("UPDATE bookDB SET author = '" + edtAuthor.text + "'  WHERE title = '" + str_title + "';")

                sqlitedb.close()

                Toast.makeText(context, "수정됨", Toast.LENGTH_SHORT).show()

                var title = str_title
                var bundle = Bundle()
                bundle.putString("title", title)    // 책 제목 전달
                val ft: FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()

                var listFragment = ListFragment()
                listFragment.arguments = bundle
                ft.replace(R.id.container, listFragment).commit()
            }
        }

        return view
    }

}