package com.android.bookdiary

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentTransaction

import kotlinx.android.synthetic.main.fragment_book_reg.*

// 책을 새로 등록하는 프래그먼트
class BookRegFragment : Fragment() {
    // DB 관련 변수
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var btnRegister: Button    // 등록 버튼

    // 사용자에게서 입력 받은 정보를 담을 변수
    lateinit var edtTitle: EditText // 책 제목
    lateinit var edtAuthor: EditText    // 저자
    lateinit var edtTotalPage: EditText // 총 페이지 수

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

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_book_reg, container, false)

        edtTitle = view.findViewById(R.id.edtTitle)
        edtAuthor = view.findViewById(R.id.edtAuthor)
        edtTotalPage = view.findViewById(R.id.edtTotalPage)

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

        btnRegister = view.findViewById(R.id.btnRegister)

        // 등록 버튼을 눌렀을 때
        btnRegister.setOnClickListener {
            var str_title: String = edtTitle.text.toString()
            var str_author: String = edtAuthor.text.toString()
            var int_totalPage = Integer.parseInt(edtTotalPage.text.toString())
            var str_color = ""

            var int_nowPage = arguments?.getInt("nowPage")
            var int_accumPage = 0
            if (int_nowPage != null) {
                int_accumPage += int_nowPage    // 오늘 읽은 페이지 수를 나타내는 nowPage를 받아와 현재까지 읽은 페이지 수를 나타내는 accumPage에 더하기
            }

            if(rg_Color.checkedRadioButtonId == R.id.rbRed) {
                str_color = "RED"
            }
            if(rg_Color.checkedRadioButtonId == R.id.rbOrange) {
                str_color = "ORANGE"
            }
            if(rg_Color.checkedRadioButtonId == R.id.rbYellow) {
                str_color = "YELLOW"
            }
            if(rg_Color.checkedRadioButtonId == R.id.rbGreen) {
                str_color = "GREEN"
            }
            if(rg_Color.checkedRadioButtonId == R.id.rbBlue) {
                str_color = "BLUE"
            }
            if(rg_Color.checkedRadioButtonId == R.id.rbNavy) {
                str_color = "NAVY"
            }
            if(rg_Color.checkedRadioButtonId == R.id.rbPurple) {
                str_color = "PURPLE"
            }
            if(rg_Color.checkedRadioButtonId == R.id.rbPink) {
                str_color = "PINK"
            }

            sqlitedb = dbManager.writableDatabase

            // bookDB에 사용자에게서 입력 받은 책 컬러, 제목, 저자, 총 페이지 수, 그리고 오늘 읽은 페이지 수와 현재까지 읽은 페이지 수를 계산하여 기록
            sqlitedb.execSQL("INSERT INTO bookDB VALUES ('"+str_color+"', '"+str_title+"', '"+str_author+"', "+int_totalPage+", "+int_nowPage+", "+int_accumPage+");")
            sqlitedb.close()

            // 책 리스트를 보여주는 프래그먼트(listFragment)로 전환
            val listFragment = ListFragment()
            val transaction : FragmentTransaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.container, listFragment)
            transaction.commit()
        }
        return view
    }
}