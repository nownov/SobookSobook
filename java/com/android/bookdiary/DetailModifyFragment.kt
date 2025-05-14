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
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_daily_memo.*
import kotlinx.android.synthetic.main.note_item.*
import kotlinx.android.synthetic.main.report_item.*

class DetailModifyFragment : Fragment() {

    //DB 관련 변수
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    // DB로부터 전달받은 값을 저장할 d~ 변수
    var dNowPage: Int = 0
    var dAccumPage : Int = 0
    var dTotalPage : Int = 0
    lateinit var dSentence: String
    lateinit var dThink: String
    lateinit var dTitle: String
    lateinit var dDate: String

    @SuppressLint("UseRequireInsteadOfGet", "Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_detail_modify, container, false) // DetailModifyFragment.kt와 fragment_detail_modity.xml 연결

        // fragment_detail_modify.xml에 존재하는 위젯 연결
        val pageEdit = view.findViewById<EditText>(R.id.editTextNumber)
        val sentenceEdit = view.findViewById<EditText>(R.id.likeSentence)
        val thinkEdit = view.findViewById<EditText>(R.id.myThink)

        // DetailFragment로부터 전달받은 값 저장
        dTitle = arguments?.getString("dTitle").toString()
        dDate = arguments?.getString("dDate").toString()

        // DB 연결
        dbManager = DBManager(activity, "bookDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM writeDB WHERE dTitle = '" + dTitle +"' and dDate = '" + dDate +"';", null) // 책 제목과 날짜를 이용해 해당 날짜의 독후감 정보 찾기

        // 찾은 db로부터 마음에 든 문장, 읽은 페이지 수, 내 생각 정보 가져오기
        if (cursor.moveToNext()){
            dSentence = cursor.getString(cursor.getColumnIndex("dSentence")).toString()
            dNowPage = cursor.getInt(cursor.getColumnIndex("dNowPage"))
            dThink = cursor.getString(cursor.getColumnIndex("dThink")).toString()
        }

        // 책 제목을 이용하여 bookDB로부터 정보 찾기
        cursor = sqlitedb.rawQuery("SELECT * FROM bookDB WHERE title = '" + dTitle +"' ;", null)

        // 찾은 db로 부터 정보 가져오기
        if (cursor.moveToNext()){
            dAccumPage = cursor.getInt(cursor.getColumnIndex("accumPage"))
            dTotalPage = cursor.getInt(cursor.getColumnIndex("totalPage"))
        }

        sqlitedb.close()
        dbManager.close()

        dAccumPage -= dNowPage //현재까지의 페이지 합에서 기록되어 있던 페이지 수 빼기

        // 기본에 적혀있던 정보 editText창에 불러오기
        pageEdit.text = Editable.Factory.getInstance().newEditable(dNowPage.toString())
        sentenceEdit.text = Editable.Factory.getInstance().newEditable(dSentence)
        thinkEdit.text = Editable.Factory.getInstance().newEditable(dThink)

        // DetailFragment로 돌아가기
        val dailyBackBtn = view.findViewById<Button>(R.id.doneBtn)
        dailyBackBtn.setOnClickListener {

            // 변경된 값을 저장할 m~ 변수들
            var mPageString = editTextNumber.text.toString() // 비어있는지 아닌지 판단하기 위해 string으로 변수 정의
            var mSentence: String = likeSentence.text.toString()
            var mThink: String = myThink.text.toString()
            var mAccumPage : Int = dAccumPage
            var mNowPage : Int = 0

            // 페이지 수가 비어있지 않으면 Int로 변환한 다음 지금까지 읽은 페이지 수의 합 변경하기
            if (mPageString != "") {
                mNowPage = mPageString.toInt()
                mAccumPage += mNowPage
            }

            // 페이지 수가 비어있지 않고, 지금까지 읽은 페이지 수의 합이 총 페이지보다 클 경우에는 변경 불가능 팝업창 띄우기
            if (mPageString != "" && mAccumPage > dTotalPage) {
                val mDialogView = LayoutInflater.from(context).inflate(R.layout.daily_page_dialog, null, false)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)
                    .setTitle("완료할 수 없어요")
                val  mAlertDialog = mBuilder.show()
                val parent = mDialogView.parent as ViewGroup
                val btn = mDialogView.findViewById<Button>(R.id.dialogBtn)
                btn.setOnClickListener {
                    parent.removeView(mDialogView)
                    mAlertDialog.dismiss()
                }

            }

            // 페이지 수 입력시에만 DB로 입력 값 전달하기
            else if (mPageString != "") {
                dbManager = DBManager(activity, "bookDB", null, 1)
                sqlitedb = dbManager.writableDatabase


                // 날짜와 제목을 이용하여 writeDB에 변경된 값 업데이트하고, 책 제목을 이용하여 총 읽은 페이지 수 업데이트 하기
                sqlitedb.execSQL("UPDATE writeDB SET dNowPage = '" + mNowPage + "', dSentence = '" + mSentence + "', dThink = '" + mThink + "' WHERE dTitle = '" + dTitle +"' and dDate = '" + dDate +"';")

                // 책 제목을 이용하여 현재까지 읽은 페이지 수와 최근에 읽은 페이지 수 업데이트 하기
                sqlitedb.execSQL("UPDATE bookDB SET accumPage = '" + mAccumPage + "', nowPage = '" + mNowPage + "' WHERE title = '" + dTitle +"';")

                sqlitedb.close()
                dbManager.close()

                // DetailFragment로 전달할 값 bundle에 저장하고 프래그먼트 이동하기
                var title = dTitle
                var dDate = dDate
                var bundle = Bundle()
                bundle.putString("dDate", dDate)
                bundle.putString("title", title)
                val ft : FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
                var detailFragment = DetailFragment()
                detailFragment.arguments = bundle
                ft.replace(R.id.container, detailFragment).commit()
                Toast.makeText(activity, dDate, Toast.LENGTH_SHORT).show() // 날짜 정보 토스트 메세지 띄우기
            }

            // 페이지 수 미입력시 팝업 띄우기
            else {
                val mDialogView = LayoutInflater.from(context).inflate(R.layout.daily_null_dialog, null, false)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)
                    .setTitle("완료할 수 없어요")
                val  mAlertDialog = mBuilder.show()
                val parent = mDialogView.parent as ViewGroup
                val btn = mDialogView.findViewById<Button>(R.id.dialogBtn)
                btn.setOnClickListener {
                    parent.removeView(mDialogView)
                    mAlertDialog.dismiss()
                }
            }
        }
        return view
    }
}